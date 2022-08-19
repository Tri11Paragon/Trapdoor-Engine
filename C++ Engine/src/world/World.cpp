//
// Created by brett on 29/07/22.
//

#include "world/World.h"
#include "renderer/ui/debug.h"
#include <config.h>

namespace TD {

    // ---------------{World}---------------

    extern TD::camera* activeCamera;

    World::World() {
        tlog << "World Created";
        TD::shader* shader = gBufferFbo.getSecondPassShader();
        shader->use();
        shader->setInt("cascadeCount", shadowFbo.shadowCascadeLevels.size());
        for (size_t i = 0; i < shadowFbo.shadowCascadeLevels.size(); ++i) {
            shader->setFloat("cascadePlaneDistances[" + std::to_string(i) + "]", shadowFbo.shadowCascadeLevels[i]);
        }
    }

    World::~World() {
        for (auto ptr : entityMap)
            ptr.second.free();
        for (const auto& ptr : components){
            for (auto ele : ptr.second){
                ele.second.free();
            }
        }
        for (auto ptr : systems)
            delete(ptr);
        tlog << "World Deleted";
    }

    void World::render() {
        shadowFbo.bind();
        for (auto system : systems)
            system->render(shadowFbo.getShader());
        shadowFbo.finish();
        gBufferFbo.bindFirstPass();
        for (auto system : systems) {
            system->render(gBufferFbo.getFirstPassShader());
            system->renderOnce();
        }
#ifdef DEBUG_ENABLED
        TD::Editor::renderGBuffer();
#endif
        skyboxRenderer.render();
        gBufferFbo.unbindFBO();
        // FBO is required
        //fxaaShader.use();
        shadowFbo.bindDepthTextureArray();
        gBufferFbo.bindSecondPass(*activeCamera);
    }

    void World::update() {
        for (auto system : systems)
            system->update();
    }

    void World::spawnEntity(Entity *entity) {
        if (entityMap.find(entity->getName()) == entityMap.end()) {
            dPtr<Entity> entPtr(entity);
            entityList.push_back(entPtr);
            entityMap.insert(std::pair(entity->getName(), entPtr));
            for (auto c : entity->getComponents()){
                if (components.find(c->getName()) == components.end()){
                    flat_hash_map<ID, dPtr<Component>> vtr;
                    components.insert(std::pair(c->getName(), vtr));
                }
                components.at(c->getName()).insert(std::pair(entity->getID(), c));
                for (const auto& s : c->getAssociatedSystems()){
                    // systems always exist for the lifetime of the world
                    // if the world is deleted they are deleted.
                    // therefore there is no need for them to be stored as dptrs
                    // especially since they **SHOULD ONLY** be internal to the world.
                    if (systemMap.find(s) == systemMap.end()) {
                        auto systemPtr = systemAllocators.at(s)->allocateDefault(this);
                        try {
                            systems.push_back(systemPtr);
                            systemMap.insert(std::pair(s, systemPtr));
                        } catch (exception& e){
                            elog << "Unable to create system! " << s;
                            delete(systemPtr);
                        }
                    }
                }
            }
        } else
            wlog << "Entity {" << entity->getName() << "} with name already exists, not adding to world!";
    }

    void World::addComponentToEntity(const std::string& name, Component *component) {
        if (entityMap.find(name) == entityMap.end()) {
            wlog << "Unable to find entity. Not adding component! (Component has been deleted)";
            delete(component);
            return;
        }
        dPtr<Component> compPtr(component);
        dPtr<Entity> entPtr = entityMap.at(name);
        entPtr->addComponent(compPtr);
        if (components.find(component->getName()) == components.end()){
            flat_hash_map<ID, dPtr<Component>> vtr;
            components.insert(std::pair(component->getName(), vtr));
        }
        components.at(component->getName()).insert(std::pair(entPtr->getID(), compPtr));
        for (const auto& s : component->getAssociatedSystems()) {
            if (systemMap.find(s) == systemMap.end()) {
                auto systemPtr = systemAllocators.at(s)->allocateDefault(this);
                try {
                    systems.push_back(systemPtr);
                    systemMap.insert(std::pair(s, systemPtr));
                } catch (exception& e){
                    elog << "Unable to create system! " << s;
                    delete(systemPtr);
                }
            }
        }
    }

    void World::deleteEntity(const std::string& entityName) {
        auto it = entityMap.find(entityName);
        // only remove the entity if it exists
        if (it != entityMap.end()) {
            dPtr<Entity> ptr = entityMap.at(entityName);

            // free all the component memory
            for (auto c : ptr->getComponents()){
                flat_hash_map<ID, dPtr<Component>>& mapVtr = components.at(c->getName());
                auto mapItr = mapVtr.find(ptr->getID());
                if (mapItr != mapVtr.end()){
                    mapVtr.erase(mapItr);
                }
                c.free();
            }
            // delete ent memory, which is actually pretty small now.
            entityMap.erase(it);
            ptr.free();
        }
    }

    void World::updateLights(std::vector<TD::Light> lights) {
        gBufferFbo.updateLights(lights);
    }

    void World::load(TAG_COMPOUND *tag) {
        auto tagList = tag->getTags();
        for (const auto& t : tagList){
            // tag representing the entity
            auto* asCompoundEntity_ = dynamic_cast<TAG_COMPOUND*>(t.get());
            tlog << "Loading entity: " << asCompoundEntity_->getName();
            // this should never be false. we aren't going to exit if this happens
            // but we will give a warning. If you see this, that is bad!
            if (asCompoundEntity_->hasTag("EntityName")){
                // create a new entity representation.
                // now yes we did store the id in the name however that isn't overly relevant.
                // the engine handles assigning the IDs, and we don't really use them
                auto* e = new Entity(asCompoundEntity_->get<TAG_STRING>("EntityName")->getPayload());
                auto* entityComponents = asCompoundEntity_->get<TAG_COMPOUND>("Components");
                for (auto c : entityComponents->getTags()){
                    tlog << "Loading component: " << c->getName();
                    // tag representing the component's data.
                    auto *asCompoundComponent_ = dynamic_cast<TAG_COMPOUND *>(c.get());
                    // the name should be the name of the component.
                    std::string name = asCompoundComponent_->getName();
                    Component *compoundUnloaded;
                    // transforms aren't allowed to be added / removed from entities, + are created when we call new Entity:
                    // so they don't exist inside the allocator table,
                    // so we need a special case from them here.
                    if (name == TRANSFORM_COMPONENT){
                        auto transform = e->getComponent(TRANSFORM_COMPONENT);
                        transform->load(asCompoundComponent_);
                    } else {
                        compoundUnloaded = componentAllocators.at(name)->allocateDefault();
                        compoundUnloaded->load(asCompoundComponent_);
                        e->addComponent(dPtr<Component>(compoundUnloaded));
                    }
                }

                spawnEntity(e);
            } else
                wlog << "Invalid entity format!";
        }
    }

    TAG_COMPOUND *World::save() {
        auto* ret = new TAG_COMPOUND("World");
        for (auto e : entityList){
            auto compList = e->getComponents();
            auto* tag = new TAG_COMPOUND("Entity_" + std::to_string(e->getID()));

            tag->put(new TAG_STRING("EntityName", e->getName()));
            auto* cmpts = new TAG_COMPOUND("Components");
            for (auto c : compList){
                cmpts->put(c->save());
            }
            tag->put(cmpts);

            ret->put(tag);
        }
        return ret;
    }

    void MeshRendererSystem::render(shader* shader) {
        auto &meshComponents = world->getComponents(MESH_RENDERER_COMPONENT);
        auto &transforms = world->getComponents(TRANSFORM_COMPONENT);
        for (auto mesh : meshComponents){
            auto meshPtr = mesh.second.get();
            if (meshPtr){
                try {
                    auto *castedMeshPtr = static_cast<MeshComponent *>(meshPtr);
                    std::string model = castedMeshPtr->getModelName();
                    // transform always exists on entity, no need to check.
                    auto *transform = static_cast<TransformComponent*>(transforms.at(meshPtr->getAssociatedEntity()).get());
                    glm::mat4 trans(1.0);
                    trans = glm::translate(trans, transform->getTranslation());
                    //tlog << meshPtr->getAssociatedEntity() << " " << transform->getTranslation().x << " " << transform->getTranslation().y << " " << transform->getTranslation().z;
                    // rotates are relatively expensive, so don't do them unless we have to.
                    glm::vec3 rotation = transform->getRotation();
                    if (rotation.x != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.x), glm::vec3(1, 0, 0));
                    if (rotation.y != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.y), glm::vec3(0, 1, 0));
                    if (rotation.z != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.z), glm::vec3(0, 0, 1));
                    trans = glm::scale(trans, transform->getScale());
                    GameRegistry::getModel(model)->draw(*shader, trans);
                } catch (std::exception& e){}
            } else {
                flog << "Mesh isn't valid! Did you fail to delete it correctly?";
                flog << "(if you are seeing this the engine has failed, please report this.)";
                flog << "(use debugger tools to give a complete report, maybe even supply your project files UwU)";
                flog << "The engine does not support furries.";
                exit(-1);
            }

        }
    }

    void MeshRendererSystem::renderOnce() {

    }

    void MeshRendererSystem::update() {

    }
}
