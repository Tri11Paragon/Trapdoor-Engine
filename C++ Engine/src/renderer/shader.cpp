//
// Created by brett on 22/07/22.
//

#include "shader.h"
#include "gl.h"
#include "../logging.h"
#include <fstream>
#include <utility>

using namespace std;

namespace TD {

    shader::shader(const std::string& vertex, const std::string& fragment, bool loadString) {
        if (loadString){
            vertexShaderID = loadShaderString(vertex, GL_VERTEX_SHADER);
            checkCompileErrors(vertexShaderID, "VERTEX", vertex);
            fragmentShaderID = loadShaderString(fragment, GL_FRAGMENT_SHADER);
            checkCompileErrors(fragmentShaderID, "FRAGMENT", fragment);
        } else {
            vertexShaderID = loadShader(vertex, GL_VERTEX_SHADER);
            checkCompileErrors(vertexShaderID, "VERTEX", vertex);
            fragmentShaderID = loadShader(fragment, GL_FRAGMENT_SHADER);
            checkCompileErrors(fragmentShaderID, "FRAGMENT", fragment);
        }
        programID = glCreateProgram();
        // attach the loaded shaders to the shader program
        glAttachShader(programID, vertexShaderID);
        checkCompileErrors(vertexShaderID, "VERTEX", vertex);
        glAttachShader(programID, fragmentShaderID);
        checkCompileErrors(fragmentShaderID, "FRAGMENT", fragment);
        // link and make sure that our program is valid.
        glLinkProgram(programID);
        checkCompileErrors(programID, "PROGRAM", vertex);
        glValidateProgram(programID);
        use();
        setUniformBlockLocation("Matrices", 1);
        setUniformBlockLocation("LightSpaceMatrices", 3);
        glUseProgram(0);
    }

    shader::shader(const std::string& vertex, const std::string& geometry, const std::string& fragment, bool loadString) {
        if (loadString){
            vertexShaderID = loadShaderString(vertex, GL_VERTEX_SHADER);
            checkCompileErrors(vertexShaderID, "VERTEX", vertex);
            geometryShaderID = loadShaderString(geometry, GL_GEOMETRY_SHADER);
            checkCompileErrors(geometryShaderID, "GEOMETRY", geometry);
            fragmentShaderID = loadShaderString(fragment, GL_FRAGMENT_SHADER);
            checkCompileErrors(fragmentShaderID, "FRAGMENT", fragment);
        } else {
            vertexShaderID = loadShader(vertex, GL_VERTEX_SHADER);
            checkCompileErrors(vertexShaderID, "VERTEX", vertex);
            geometryShaderID = loadShader(geometry, GL_GEOMETRY_SHADER);
            checkCompileErrors(geometryShaderID, "GEOMETRY", geometry);
            fragmentShaderID = loadShader(fragment, GL_FRAGMENT_SHADER);
            checkCompileErrors(fragmentShaderID, "FRAGMENT", fragment);
        }
        programID = glCreateProgram();
        // attach the loaded shaders to the shader program
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, geometryShaderID);
        glAttachShader(programID, fragmentShaderID);
        // link and make sure that our program is valid.
        glLinkProgram(programID);
        checkCompileErrors(programID, "PROGRAM", vertex);
        glValidateProgram(programID);
        use();
        setUniformBlockLocation("Matrices", 1);
        setUniformBlockLocation("LightSpaceMatrices", 3);
        glUseProgram(0);
    }

    unsigned int shader::loadShaderString(const std::string &str, int type) {
        const char* shaderCode = str.c_str();
        // creates a shader
        unsigned int shaderID = glCreateShader(type);
        // puts the loaded shader code into the graphics card
        glShaderSource(shaderID, 1, &shaderCode, NULL);
        // Compile it
        glCompileShader(shaderID);
        return shaderID;
    }

    unsigned int shader::loadShader(const string &file, int type) {
        // 1. retrieve the vertex/fragment source code from filePath
        string shaderSource;
        ifstream vShaderFile;
        if (!vShaderFile.good()){
            flog << "Shader file not found.\n";
            throw std::runtime_error("Shader file not found!");
        }
        // ensure ifstream objects can throw exceptions:
        vShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
        try {
            // open files
            vShaderFile.open(file);
            stringstream shaderStream;
            // read file's buffer contents into streams
            shaderStream << vShaderFile.rdbuf();
            // close file handlers
            vShaderFile.close();
            // convert stream into string
            shaderSource = shaderStream.str();
        } catch(std::ifstream::failure& e) {
            fout << "Unable to read shader file! " << file << endl;
            return -1;
        }

        const char* shaderCode = shaderSource.c_str();
        // creates a shader
        unsigned int shaderID = glCreateShader(type);
        // puts the loaded shader code into the graphics card
        glShaderSource(shaderID, 1, &shaderCode, NULL);
        // Compile it
        glCompileShader(shaderID);
        // make sure there is no errors
        /*int status = 0;
        glGetShaderiv(shaderID, GL_COMPILE_STATUS, &status);
        if (!status) {
            char* log;
            int length = 0;
            glGetShaderInfoLog(shaderID, 512, &length, log);
            flog << "Error long length: " << length << "\n";
            flog << (log) << "\n";
            flog << "Could not compile shader! (Shader type: "
                                 << (type == GL_VERTEX_SHADER ? "vertex" : type == GL_GEOMETRY_SHADER ? "geometry" : "fragment") << ")\n";
            flog << "Shader File: " << file << "\n";
            return -1;
        }*/
        return shaderID;
    }

    void shader::use() {
        glUseProgram(programID);
    }

    void shader::bindAttribute(int attribute, std::string name) {
        use();
        glBindAttribLocation(programID, attribute, name.c_str());
    }

    void shader::setUniformBlockLocation(std::string name, int location) {
        use();
        glUniformBlockBinding(programID, glGetUniformBlockIndex(programID, name.c_str()), location);
    }

    unsigned int shader::getUniformLocation(const std::string &name) {
        if (uniformVars[name].i != -1)
            return uniformVars[name].i;
        unsigned int loc = glGetUniformLocation(programID, name.c_str());
        uniformVars[name].i = loc;
        return loc;
    }

    shader::~shader() {
        glUseProgram(0);
        // remove all the shaders from the program
        glDetachShader(programID, vertexShaderID);
        if (geometryShaderID)
            glDetachShader(programID, geometryShaderID);
        if (tessalationShaderID)
            glDetachShader(programID, tessalationShaderID);
        glDetachShader(programID, fragmentShaderID);

        // delete the shaders
        glDeleteShader(vertexShaderID);
        if (geometryShaderID)
            glDeleteShader(geometryShaderID);
        if (tessalationShaderID)
            glDeleteShader(tessalationShaderID);
        glDeleteShader(fragmentShaderID);

        // delete the shader program
        glDeleteProgram(programID);
    }

    void shader::setBool(const string &name, bool value) {
        glUniform1i(getUniformLocation(name), (int)value);
    }

    void shader::setInt(const string &name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }

    void shader::setFloat(const string &name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }

    void shader::setMatrix(const string &name, glm::mat4x4 matrix) {
        glUniformMatrix4fv(getUniformLocation(name), 1, GL_FALSE, glm::value_ptr(matrix) );
    }

    void shader::setVec2(const string &name, glm::vec2 vec) {
        glUniform2f(getUniformLocation(name), vec.x, vec.y);
    }

    void shader::setVec3(const string &name, glm::vec3 vec) {
        glUniform3f(getUniformLocation(name), vec.x, vec.y, vec.z);
    }

    void shader::setVec4(const string &name, glm::vec4 vec) {
        glUniform4f(getUniformLocation(name), vec.x, vec.y, vec.z, vec.w);
    }

    void shader::setVec2(const string &name, float x, float y) {
        glUniform2f(getUniformLocation(name), x, y);
    }

    void shader::setVec3(const string &name, float x, float y, float z) {
        glUniform3f(getUniformLocation(name), x, y, z);
    }

    void shader::setVec4(const string &name, float x, float y, float z, float w) {
        glUniform4f(getUniformLocation(name), x, y, z, w);
    }

    void shader::setLightArray(const std::string &name, int pos, Light light) {
        setVec3(name + "[" + std::to_string(pos) + "].Position", light.Position);
        setVec3(name + "[" + std::to_string(pos) + "].Color", light.Color);
        setFloat(name + "[" + std::to_string(pos) + "].Linear", light.Linear);
        setFloat(name + "[" + std::to_string(pos) + "].Quadratic", light.Quadratic);
        setFloat(name + "[" + std::to_string(pos) + "].Radius", light.Radius);
    }

    void shader::setLight(const std::string &name, Light light) {
        setVec3(name + ".Position", light.Position);
        setVec3(name + ".Color", light.Color);
        setFloat(name + ".Linear", light.Linear);
        setFloat(name + ".Quadratic", light.Quadratic);
        setFloat(name + ".Radius", light.Radius);
    }

    void shader::setColor(const string &name, glm::vec3 color) {
        setColor(name, color.r, color.g, color.b, 255);
    }

    void shader::setColor(const string &name, glm::vec4 color) {
        setColor(name, color.r, color.g, color.b, color.a);
    }

    void shader::setColor(const string &name, float r, float g, float b) {
        setColor(name, r, g, b, 255);
    }

    void shader::setColor(const string &name, float r, float g, float b, float a) {
        setVec4(name, r / 255.0, g / 255.0, b / 255.0, a / 255.0);
    }

    void shader::checkCompileErrors(GLuint shader, std::string type, std::string shaderPath) {
        GLint success;
        GLchar infoLog[1024];
        if(type != "PROGRAM") {
            glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
            if(!success) {
                glGetShaderInfoLog(shader, 1024, NULL, infoLog);
                std::cout << shaderPath << "\n";
                std::cout << "ERROR::SHADER_COMPILATION_ERROR of type: " << type << "\n" << infoLog << "\n -- --------------------------------------------------- -- " << std::endl;
            }
        } else {
            glGetProgramiv(shader, GL_LINK_STATUS, &success);
            if(!success) {
                glGetProgramInfoLog(shader, 1024, NULL, infoLog);
                std::cout << shaderPath << "\n";
                std::cout << "ERROR::PROGRAM_LINKING_ERROR of type: " << type << "\n" << infoLog << "\n -- --------------------------------------------------- -- " << std::endl;
            }
        }
    }

} // TD