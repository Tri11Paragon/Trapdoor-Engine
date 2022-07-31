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
            glm::mat4 trans;
            glm::rotate(trans, 1.0f, rotation);
            glm::scale(trans, scale);
            glm::translate(trans, position);
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
}

#endif //ENGINE_ENTITY_H
