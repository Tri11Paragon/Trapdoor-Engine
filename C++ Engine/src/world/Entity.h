//
// Created by brett on 30/07/22.
//

#ifndef ENGINE_ENTITY_H
#define ENGINE_ENTITY_H

#include "../glm.h"
#include "../std.h"

namespace TD {

    class Entity {
    protected:
        // TODO: replace with bullet stuff,
        glm::vec3 position = glm::vec3(0, 0, 0);
        glm::vec3 rotation = glm::vec3(0, 0, 0);
        glm::vec3 scale = glm::vec3(1, 1, 1);
        std::string modelName;
    public:
        Entity(std::string modelName) { this->modelName = modelName; }

        inline glm::mat4 getTranslationMatrix() {
            glm::mat4 trans(1.0);
            trans = glm::translate(trans, position);
            trans = glm::rotate(trans, glm::radians(rotation.x), glm::vec3(1,0,0));
            trans = glm::rotate(trans, glm::radians(rotation.y), glm::vec3(0,1,0));
            trans = glm::rotate(trans, glm::radians(rotation.z), glm::vec3(0,0,1));
            trans = glm::scale(trans, scale);
            return trans;
        }

        // called when the entity is rendered
        virtual void render() = 0;

        // called when the entity is updated, world;
        virtual void update() = 0;

        inline glm::vec3 getPosition() { return position; }

        inline glm::vec3 getRotation() { return rotation; }

        inline glm::vec3 getScale() { return scale; }

        inline std::string getModelName() { return modelName; }
    };

    class StaticEntity : public Entity {
    public:
        StaticEntity(std::string modelName, glm::vec3 pos): StaticEntity(modelName, pos, glm::vec3(0), glm::vec3(1)){}
        StaticEntity(std::string modelName, glm::vec3 pos, glm::vec3 scale): StaticEntity(modelName, pos, glm::vec3(0), scale) {}
        StaticEntity(std::string modelName, glm::vec3 pos, glm::vec3 rotation, glm::vec3 scale): Entity(modelName) {
            this->position = pos;
            this->rotation = rotation;
            this->scale = scale;
        }
        virtual void render(){

        }
        virtual void update(){

        }
    };
}

#endif //ENGINE_ENTITY_H
