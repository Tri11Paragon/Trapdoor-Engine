//
// Created by brett on 22/07/22.
//

#ifndef ENGINE_SHADER_H
#define ENGINE_SHADER_H

#include "../std.h"

#define SHADER_LOAD_FAILURE "SHADER_LOAD_FAILURE";

namespace TD {

    class shader {
    private:
        unsigned int programID = 0;
        unsigned int vertexShaderID = 0;
        unsigned int fragmentShaderID = 0;
        unsigned int geometryShaderID = 0;
        unsigned int tessalationShaderID = 0;
        std::map<std::string, unsigned int> uniformVars;
        unsigned int loadShader(std::string file, int type);
        unsigned int getUniformLocation(std::string name);
    public:
        shader(std::string vertex, std::string fragment);
        void bindAttribute(int attribute, std::string name);
        void setUniformBlockLocation(std::string name, int location);
        void setBool(const std::string &name, bool value);
        void setInt(const std::string &name, int value);
        void setFloat(const std::string &name, float value);
        void use();
        ~shader();
    };

} // TD

#endif //ENGINE_SHADER_H
