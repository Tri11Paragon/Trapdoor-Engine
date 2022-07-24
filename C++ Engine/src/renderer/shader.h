//
// Created by brett on 22/07/22.
//

#ifndef ENGINE_SHADER_H
#define ENGINE_SHADER_H

#include "../std.h"
#include "../glm.h"

#define SHADER_LOAD_FAILURE "SHADER_LOAD_FAILURE";

namespace TD {

    class shader {
    private:
        struct IntDefaultedToMinusOne {
            unsigned int i = -1;
        };
        unsigned int programID = 0;
        unsigned int vertexShaderID = 0;
        unsigned int fragmentShaderID = 0;
        unsigned int geometryShaderID = 0;
        unsigned int tessalationShaderID = 0;
        std::map<std::string, IntDefaultedToMinusOne> uniformVars;
        unsigned int loadShader(const std::string &file, int type);
        unsigned int getUniformLocation(const std::string &name);
    public:
        shader(std::string vertex, std::string fragment);
        void bindAttribute(int attribute, std::string name);
        void setUniformBlockLocation(std::string name, int location);
        void setBool(const std::string &name, bool value);
        void setInt(const std::string &name, int value);
        void setFloat(const std::string &name, float value);
        void setMatrix(const std::string &name, glm::mat4x4 matrix);
        void use();
        ~shader();
    };

} // TD

#endif //ENGINE_SHADER_H
