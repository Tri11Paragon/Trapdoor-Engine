//
// Created by brett on 22/07/22.
//
#include "gl.h"
#define STB_IMAGE_IMPLEMENTATION
#define STBI_FAILURE_USERMSG
#include <stb_image.h>

namespace TD {

    /***---------------{VAO}---------------***/

    unsigned int vao::createVAO() {
        unsigned int vaoID;
        glGenVertexArrays(1, &vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    unsigned int vao::storeData(int attrNumber, int coordSize, int stride, long offset, int length, float* data) {
        unsigned int vboID;
        glGenBuffers(1, &vboID);

        vbos.push_back(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, length << 2, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attrNumber,coordSize,GL_FLOAT,false,stride, (void *) offset);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return vboID;
    }

    unsigned int vao::storeData(int length, unsigned int *data) {
        unsigned int eboID;
        glGenBuffers(1, &eboID);

        vbos.push_back(eboID);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        // 4 bytes / int
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, length << 2, data, GL_STATIC_DRAW);

        return eboID;
    }

    vao::vao(std::vector<float> &verts, std::vector<unsigned int> &indicies, int attributeCount) {
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(1);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        indicies.size();
        storeData(indicies.size(), indicies.data());

        storeData(0, 3, 3 * sizeof(float), 0, verts.size(), verts.data());

        unbind();
    }

    vao::vao(std::vector<float> &verts, std::vector<float> &uvs, std::vector<unsigned int> &indicies, int attributeCount) {
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(1);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        storeData(indicies.size(), indicies.data());

        storeData(0, 3, 3 * sizeof(float), 0, verts.size(), verts.data());
        storeData(1, 2, 2 * sizeof(float), 0, uvs.size(), uvs.data());

        unbind();
    }

    vao::vao(std::vector<float> &verts, int dimensions, int attributeCount) {
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(1);
            glEnableVertexArrayAttrib(vaoID, i);
        }

        storeData(0, dimensions, dimensions * sizeof(float), 0, verts.size(), verts.data());

        unbind();
    }

    void vao::bind() {
        glBindVertexArray(vaoID);
    }

    void vao::unbind() {
        glBindVertexArray(0);
    }

    vao::~vao() {
        const unsigned int vao = vaoID;
        glDeleteVertexArrays(1, &vao);
        for (const unsigned int vbo : vbos){
            glDeleteBuffers(1, &vbo);
        }
    }

    /***---------------{Texture}---------------***/

    texture::texture(std::string path) {
        unsigned char* data = loadTexture(path);
        if (data == NULL){
            flog << "There was an error loading the image file " << path;
            throw "Error loading image from file!";
        }
        loadGLTexture(data);
    }

    unsigned char *texture::loadTexture(std::string path) {
        // TODO: add more image processing options
        stbi_set_flip_vertically_on_load(true);
        unsigned char *data = stbi_load(path.c_str(), &width, &height, &channels, 0);
        if (stbi__g_failure_reason) {
            flog << "STB Error Reason: ";
            flog << stbi__g_failure_reason;
        }
        return data;
    }

    void texture::loadGLTexture(unsigned char *data) {
        glGenTextures(1, &textureID);
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        int GL_RGB_SETTING = channels == 3 ? GL_RGB : GL_RGBA;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB_SETTING, width, height, 0, GL_RGB_SETTING, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(data);
    }

    texture::~texture() {
        glDeleteTextures(1, &textureID);
    }

    void texture::bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    void texture::unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    void texture::enableGlTextures(int textureCount) {
        for (int i = 0; i < textureCount; i++)
            glActiveTexture(GL_TEXTURE0 + i);
    }
}