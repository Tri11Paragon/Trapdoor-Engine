//
// Created by brett on 21/07/22.
// Used to import common headers when using OpenGL.
// Will contain define utils in the future.
//

#ifndef ENGINE_GL_H
#define ENGINE_GL_H

#include "../std.h"
#include "gladsources/glad_gl_core/include/glad/gl.h"
#include <GLFW/glfw3.h>
#include "../logging.h"

namespace TD {

    class vao {
    private:
        unsigned int vaoID;
        std::vector<unsigned int> vbos;
        unsigned int createVAO();
        unsigned int storeData(int attrNumber, int coordSize, int stride, long offset, int length, float* data);
        unsigned int storeData(int length, unsigned int* data);
    public:
        vao(std::vector<float> &verts, std::vector<unsigned int> &indicies, int attributeCount);
        vao(std::vector<float> &verts, std::vector<float> &uvs, std::vector<unsigned int> &indicies, int attributeCount);
        vao(std::vector<float> &verts, int dimensions, int attributeCount);
        ~vao();
        void bind();
        void unbind();
    };

    class texture {
    private:
        unsigned int textureID;
        int width, height, channels;
        unsigned char* loadTexture(std::string path);
        void loadGLTexture(unsigned char* data);
    public:
        texture(std::string path);
        ~texture();
        void bind();
        void unbind();
        void enableGlTextures(int textureCount);
    };

}

#endif //ENGINE_GL_H
