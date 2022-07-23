//
// Created by brett on 22/07/22.
//
#include "gl.h"

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
        storeData(1, 2, 3 * sizeof(float), 0, uvs.size(), uvs.data());

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


}