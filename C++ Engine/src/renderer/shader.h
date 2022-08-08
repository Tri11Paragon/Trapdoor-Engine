//
// Created by brett on 22/07/22.
//

#ifndef ENGINE_SHADER_H
#define ENGINE_SHADER_H

#include "../std.h"
#include "../glm.h"

#define SHADER_LOAD_FAILURE "SHADER_LOAD_FAILURE";

namespace TD {

    /*
     *
     * Distance, Constant, Linear, Quadratic
     * 7 	        1.0 	0.7 	1.8
     * 13 	        1.0 	0.35 	0.44
     * 20 	        1.0 	0.22 	0.20
     * 32 	        1.0 	0.14 	0.07
     * 50 	        1.0 	0.09 	0.032
     * 65 	        1.0 	0.07 	0.017
     * 100 	        1.0 	0.045 	0.0075
     * 160 	        1.0 	0.027 	0.0028
     * 200 	        1.0 	0.022 	0.0019
     * 325 	        1.0 	0.014 	0.0007
     * 600 	        1.0 	0.007 	0.0002
     * 3250 	    1.0 	0.0014 	0.000007
     *
     */

    // put in another file and include it like a c++ file.
#ifdef CPP_GLSL_INCLUDE
    std::string my_shader_glsl_src = R"("
#endif
    // SHADER CODE HERE.
#ifdef CPP_GLSL_INCLUDE
    ")";
#endif

    struct Light {
        glm::vec3 Position;
        glm::vec3 Color;

        float Linear;
        float Quadratic;
        float Radius;
        Light(glm::vec3 position, glm::vec3 color, float linear, float quad, float radius):
            Position(position), Color(color), Linear(linear), Quadratic(quad), Radius(radius) {}
    };

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
        void checkCompileErrors(unsigned int shader, std::string type, std::string shaderPath);
    public:
        shader(std::string vertex, std::string fragment);
        shader(std::string vertex, std::string geometry, std::string fragment);
        void bindAttribute(int attribute, std::string name);
        void setUniformBlockLocation(std::string name, int location);
        void setBool(const std::string &name, bool value);
        void setInt(const std::string &name, int value);
        void setFloat(const std::string &name, float value);
        void setMatrix(const std::string &name, glm::mat4x4 matrix);
        void setVec2(const std::string &name, glm::vec2 vec);
        void setVec3(const std::string &name, glm::vec3 vec);
        void setVec4(const std::string &name, glm::vec4 vec);
        void setVec2(const std::string &name, float x, float y);
        void setVec3(const std::string &name, float x, float y, float z);
        void setVec4(const std::string &name, float x, float y, float z, float w);
        void setColor(const std::string &name, glm::vec3 color);
        void setColor(const std::string &name, glm::vec4 color);
        void setColor(const std::string &name, float r, float g, float b);
        void setColor(const std::string &name, float r, float g, float b, float a);
        void setLightArray(const std::string &name, int pos, Light light);
        void setLight(const std::string &name, Light light);
        void use();
        ~shader();
    };

    class WindowResize {
    public:
        virtual void windowResized(int x, int y, int width, int height) = 0;
    };

} // TD

#endif //ENGINE_SHADER_H
