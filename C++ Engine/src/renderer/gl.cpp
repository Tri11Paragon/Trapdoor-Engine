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

    unsigned int vao::storeData(int attrNumber, int coordSize, int stride, long offset, int length, const float* data) {
        unsigned int vboID;
        glGenBuffers(1, &vboID);

        vbos.push_back(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, length << 2, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attrNumber,coordSize,GL_FLOAT,false,stride, (void *) offset);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return vboID;
    }

    unsigned int vao::storeData(const std::vector<Vertex> &vertices) {
        unsigned int vboID;
        glGenBuffers(1, &vboID);

        vbos.push_back(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.size() * sizeof(Vertex), &vertices[0], GL_STATIC_DRAW);

        // vertex positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)0);
        // vertex texture coords
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, UV));
        // vertex normals
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, Normal));

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return vboID;
    }

    unsigned int vao::storeData(int length, const unsigned int *data) {
        unsigned int eboID;
        glGenBuffers(1, &eboID);

        vbos.push_back(eboID);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        // 4 bytes / int
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, length << 2, data, GL_STATIC_DRAW);

        return eboID;
    }

    vao::vao(const std::vector<float> &verts, const std::vector<unsigned int> &indicies, int attributeCount) {
        this->indexCount = indicies.size();
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        indicies.size();
        storeData(indicies.size(), indicies.data());

        storeData(0, 3, 3 * sizeof(float), 0, verts.size(), verts.data());

        unbind();
    }

    vao::vao(const std::vector<float> &verts, const std::vector<float> &uvs, const std::vector<unsigned int> &indicies, int attributeCount) {
        this->indexCount = indicies.size();
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(i);
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
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }

        storeData(0, dimensions, dimensions * sizeof(float), 0, verts.size(), verts.data());

        unbind();
    }

    vao::vao(const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices, const std::vector<Texture> &textures) {
        this->textures = textures;
        this->indexCount = indices.size();
        vaoID = createVAO();
        for (int i = 0; i < 3; i++) {
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        storeData(indices.size(), indices.data());

        storeData(vertices);

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

    void vao::bindTextures() {
        for (Texture t : textures){
            glActiveTexture(GL_TEXTURE0 + t.location);
            t.texture->bind();
        }
    }

    void vao::draw() {
        if (indexCount < 0)
            return;
        else
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    /***---------------{Texture}---------------***/

    texture::texture(){

    }

    texture::texture(std::string path) {
        unsigned char* data = loadTexture(path);
        if (data == nullptr){
            flog << "There was an error loading the image file " << path;
            throw "Error loading image from file!";
        }
        loadGLTexture(data);
    }

    unsigned char *texture::loadTexture(std::string path) {
        // TODO: add more image processing options
        stbi_set_flip_vertically_on_load(true);
        unsigned char *data = stbi_load(path.c_str(), &width, &height, &channels, 0);
        //if (stbi__g_failure_reason) {
        //    flog << "STB Error Reason: ";
        //    flog << stbi__g_failure_reason;
        //}
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

    /***---------------{Cubemap Texture}---------------***/

    cubemapTexture::cubemapTexture(std::vector<std::string> paths) {
        if (paths.size() < 6)
            throw "Cubemap must have 6 paths provided!";
        glGenTextures(1, &textureID);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int i = 0; i < paths.size(); i++){
            stbi_set_flip_vertically_on_load(false);
            unsigned char* data = stbi_load(paths[i].c_str(), &width, &height, &channels, 4);

            int GL_RGB_SETTING = GL_RGBA;
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB_SETTING, width, height, 0, GL_RGB_SETTING, GL_UNSIGNED_BYTE, data);

            stbi_image_free(data);
        }
        //glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
    }

    void cubemapTexture::bind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
    }

    void cubemapTexture::unbind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

    /***---------------{Model}---------------***/

    void model::draw(shader &shader, glm::vec3 *positions, int numberOfPositions) {
        shader.use();
        for (vao* mesh : meshes){
            mesh->bind();
            mesh->bindTextures();
            for (int i = 0; i < numberOfPositions; i++){
                shader.setMatrix("transform", glm::translate(glm::mat4(1.0f), positions[i]));
                mesh->draw();
            }
        }
    }

    void model::draw(shader &shader, glm::vec3 position) {
        shader.use();
        shader.setMatrix("transform", glm::translate(glm::mat4(1.0f), position));
        for (vao* mesh : meshes){
            mesh->bindTextures();
            mesh->bind();
            mesh->draw();
        }
    }

    void model::draw(shader &shader, std::vector<glm::vec3> positions) {
        draw(shader, positions.data(), positions.size());
    }

    void model::loadModel(std::string path) {
        Assimp::Importer import;

        const aiScene *scene = import.ReadFile(path, aiProcess_Triangulate | aiProcess_FlipUVs);

        if(!scene || scene->mFlags & AI_SCENE_FLAGS_INCOMPLETE || !scene->mRootNode) {
            flog << "Error loading model with Assimp." << std::endl;
            flog << import.GetErrorString() << std::endl;
            return;
        }
        directory = path.substr(0, path.find_last_of('/'));

        processNode(scene->mRootNode, scene);
    }

    void model::processNode(aiNode *node, const aiScene *scene) {
        // process all the node's meshes (if any)
        for(unsigned int i = 0; i < node->mNumMeshes; i++) {
            aiMesh *mesh = scene->mMeshes[node->mMeshes[i]];
            meshes.push_back(processMesh(mesh, scene));
        }
        // then do the same for each of its children
        for(unsigned int i = 0; i < node->mNumChildren; i++) {
            processNode(node->mChildren[i], scene);
        }
    }

    vao* model::processMesh(aiMesh *mesh, const aiScene *scene) {
        std::vector<Vertex> vertices;
        std::vector<unsigned int> indices;
        std::vector<Texture> textures;

        for(unsigned int i = 0; i < mesh->mNumVertices; i++)
        {
            Vertex vertex;
            // process vertex positions, normals and texture coordinates
            glm::vec3 vector;
            vector.x = mesh->mVertices[i].x;
            vector.y = mesh->mVertices[i].y;
            vector.z = mesh->mVertices[i].z;
            vertex.Position = vector;

            vector.x = mesh->mNormals[i].x;
            vector.y = mesh->mNormals[i].y;
            vector.z = mesh->mNormals[i].z;
            vertex.Normal = vector;

            // does the mesh contain texture coordinates?
            if(mesh->mTextureCoords[0]) {
                glm::vec2 vec;
                vec.x = mesh->mTextureCoords[0][i].x;
                vec.y = mesh->mTextureCoords[0][i].y;
                vertex.UV = vec;
            } else
                vertex.UV = glm::vec2(0.0f, 0.0f);

            vertices.push_back(vertex);
        }
        // process indices
        for(unsigned int i = 0; i < mesh->mNumFaces; i++) {
            aiFace face = mesh->mFaces[i];
            for(unsigned int j = 0; j < face.mNumIndices; j++)
                indices.push_back(face.mIndices[j]);
        }

        // process material
        if(mesh->mMaterialIndex >= 0) {
            aiMaterial *material = scene->mMaterials[mesh->mMaterialIndex];
            std::vector<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, DIFFUSE);
            textures.insert(textures.end(), diffuseMaps.begin(), diffuseMaps.end());

            std::vector<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, SPECULAR);
            textures.insert(textures.end(), specularMaps.begin(), specularMaps.end());

            std::vector<Texture> normalMaps = loadMaterialTextures(material, aiTextureType_NORMALS, NORMAL);
            textures.insert(textures.end(), normalMaps.begin(), normalMaps.end());
        }

        return new vao(vertices, indices, textures);
    }

    extern std::map<std::string, Texture> loadedTextures;

    std::vector<Texture> model::loadMaterialTextures(aiMaterial *mat, aiTextureType type, TEXTURE_TYPE textureType) {
        std::vector<Texture> textures;
        for(unsigned int i = 0; i < mat->GetTextureCount(type); i++) {
            aiString str;
            mat->GetTexture(type, i, &str);
            std::string path = std::string("../assets/textures/") + str.C_Str();

            std::map<std::string, Texture>& textureMap = useTextureCache ? TD::loadedTextures : this->loadedTextures;

            if (textureMap.contains(path)){
                textures.push_back(textureMap.at(path));
                continue;
            }

            ilog << "Loading texture{ " << str.C_Str() << " } @ " << path;
            Texture tex(new TD::texture(path), textureType, path);
            textureMap.insert(std::pair(path, tex));
            textures.push_back(tex);
        }
        return textures;
    }

    model::~model(){
        for (vao* m : meshes)
            delete(m);
        for (std::pair<std::string, Texture> tpair : loadedTextures)
            delete(tpair.second.texture);
    }

    /***---------------{FBOs}---------------***/
    extern int _display_w, _display_h;

    const std::vector<float> vertices = {
            1.0f,  1.0f, 0.0f,  // top right
            1.0f,  0.0f, 0.0f,  // bottom right
            0.0f,  0.0f, 0.0f,  // bottom left
            0.0f,  1.0f, 0.0f   // top left
    };

    const std::vector<unsigned int> indices = {
            3, 1, 0,   // first triangle
            3, 2, 1    // second triangle
    };

    const std::vector<float> texCoords = {
            1.0f, 1.0f,   // top right
            1.0f, 0.0f,   // bottom right
            0.0f, 0.0f,   // bottom left
            0.0f, 1.0f    // top left
    };

    fbo::fbo() : fbo(_display_w, _display_h, DEPTH_BUFFER) {}
    fbo::fbo(DEPTH_ATTACHMENT_TYPE type): fbo(_display_w, _display_h, type) {}
    fbo::fbo(int width, int height): fbo(width, height, DEPTH_BUFFER) {}

    fbo::fbo(int width, int height, DEPTH_ATTACHMENT_TYPE type) {
        this->_width = width;
        this->_height = height;
        this->_fboType = type;
        glGenFramebuffers(1, &_fboID);
        bindFBO();

        if (type == DEPTH_TEXTURE){
            glGenTextures(1, &_depthAttachment);
            glBindTexture(GL_TEXTURE_2D, _depthAttachment);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, _width, _height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, NULL);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, _depthAttachment, 0);
        } else if (type == DEPTH_BUFFER) {
            glGenRenderbuffers(1, &_depthAttachment);
            glBindRenderbuffer(GL_RENDERBUFFER, _depthAttachment);

            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, _width, _height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, _depthAttachment);
        }

        validateFramebuffer();
        unbindFBO();
        quad = new vao(vertices, texCoords, indices, 2);
    }

    void fbo::validateFramebuffer() {
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            flog << "Unable to create framebuffer! " << glCheckFramebufferStatus(GL_FRAMEBUFFER);
            throw "Unable to create framebuffer!";
        }
    }

    void fbo::createColorTexture(int colorAttachment) {
        bindFBO();
        unsigned int colorTextureID;
        glGenTextures(1, &colorTextureID);
        glBindTexture(GL_TEXTURE_2D, colorTextureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, _width, _height, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, colorAttachment, GL_TEXTURE_2D, colorTextureID, 0);
        validateFramebuffer();
        _colorTextures.insert(std::pair<int, unsigned int>(colorAttachment, colorTextureID));
        unbindFBO();
    }

    void fbo::bindColorTexture(int activeTexture, int colorAttachment) {
        glActiveTexture(activeTexture);
        glBindTexture(GL_TEXTURE_2D, _colorTextures.at(colorAttachment));
    }

    void fbo::bindDepthTexture(int depthAttachment) {
        if (_fboType != DEPTH_TEXTURE)
            throw "Unable to bind texture. FBO uses renderbuffer!";

    }

    void fbo::bindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, _fboID);
    }

    void fbo::unbindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    void fbo::bindFBODraw(){
        bindFBO();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // we're not using the stencil buffer now
        glEnable(GL_DEPTH_TEST);
    }

    void fbo::blitToScreen() {
        bindFBO();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, _fboID);
        glDrawBuffer(GL_BACK);
        glBlitFramebuffer(0, 0, _width, _height, 0, 0, _display_w, _display_h, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        unbindFBO();
    }

    void fbo::blitToFBO(int readBuffer, fbo &outputFBO) {
        bindFBO();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFBO._fboID);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, _fboID);
        glReadBuffer(readBuffer);
        glBlitFramebuffer(0, 0, _width, _height, 0, 0, outputFBO._width, outputFBO._height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        unbindFBO();
    }

    void fbo::renderToQuad(TD::shader& shader) {
        renderToQuad(shader, _width, _height);
    }

    void fbo::renderToQuad(TD::shader& shader, glm::vec2 size) {
        renderToQuad(shader, size.x, size.y);
    }

    void fbo::renderToQuad(TD::shader& shader, glm::vec2 pos, glm::vec2 size) {
        renderToQuad(shader, pos.x, pos.y, size.x, size.y);
    }

    void fbo::renderToQuad(TD::shader& shader, int width, int height) {
        renderToQuad(shader, 0,0, width, height);
    }

    void fbo::renderToQuad(TD::shader& shader, int x, int y, int width, int height) {
        shader.use();
        shader.setMatrix("transform", glm::scale(glm::translate(glm::mat4(1), glm::vec3(x, y, -0.5f)), glm::vec3(width, height, 1.0f)));
        glDisable(GL_DEPTH_TEST);
        quad->bind();
        quad->draw();
        quad->unbind();
        glEnable(GL_DEPTH_TEST);
    }

    fbo::~fbo() {
        delete(quad);
        glDeleteFramebuffers(1, &_fboID);
        if (_fboType == DEPTH_BUFFER){
            glDeleteRenderbuffers(1, &_depthAttachment);
        } else if (_fboType == DEPTH_TEXTURE){
            glDeleteTextures(1, &_depthAttachment);
        }
        for (std::pair<int, unsigned int> p : _colorTextures){
            glDeleteTextures(1, &p.second);
        }
    }

    /***---------------{GBuffer FBO}---------------***/



    /***---------------{Static Stuff}---------------***/

    unsigned int matrixUBO;
    const unsigned int MATRIX_COUNT = 4;
    float matrixData[MATRIX_COUNT * 16];

    extern glm::mat4 projectionMatrix;
    extern glm::mat4 projectionViewMatrix;
    extern glm::mat4 viewMatrix;

    void createMatrixUBO() {
        glGenBuffers(1, &matrixUBO);
        glBindBuffer(GL_UNIFORM_BUFFER, matrixUBO);
        glBufferData(GL_UNIFORM_BUFFER, MATRIX_COUNT * 16 * sizeof(float), matrixData, GL_STATIC_DRAW);
        glBindBufferBase(GL_UNIFORM_BUFFER, 1, matrixUBO);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    void updateProjectionMatrixUBO(glm::mat4 matrix) {
        glBindBuffer(GL_UNIFORM_BUFFER, matrixUBO);
        glBufferSubData(GL_UNIFORM_BUFFER, (long) 0, 16 * sizeof(float), glm::value_ptr(matrix));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    void updateViewMatrixUBO(glm::mat4 matrix) {
        viewMatrix = matrix;
        projectionViewMatrix = projectionMatrix * matrix;
        updateProjectViewMatrixUBO(projectionViewMatrix);
        glBindBuffer(GL_UNIFORM_BUFFER, matrixUBO);
        glBufferSubData(GL_UNIFORM_BUFFER, (long) (16 * sizeof(float)), 16 * sizeof(float), glm::value_ptr(matrix));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    void updateOrthoMatrixUBO(glm::mat4 matrix) {
        glBindBuffer(GL_UNIFORM_BUFFER, matrixUBO);
        glBufferSubData(GL_UNIFORM_BUFFER, (long) (32 * sizeof(float)), 16 * sizeof(float), glm::value_ptr(matrix));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    void updateProjectViewMatrixUBO(glm::mat4 matrix) {
        glBindBuffer(GL_UNIFORM_BUFFER, matrixUBO);
        glBufferSubData(GL_UNIFORM_BUFFER, (long) (48 * sizeof(float)), 16 * sizeof(float), glm::value_ptr(matrix));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    void deleteGlobalTextureCache() {
        for (std::pair<std::string, Texture> tpair : loadedTextures)
            delete(tpair.second.texture);
    }

    static const std::vector<unsigned int> INDICES { 0, 1, 3, 1, 2, 3, 1, 5, 2, 2, 5, 6, 4, 7, 5, 5, 7, 6, 0,
                                         3, 4, 4, 3, 7, 7, 3, 6, 6, 3, 2, 4, 5, 0, 0, 5, 1 };

    const std::vector<float> getCubeVertexPositions(float size) {
        return std::vector<float>{ -size, size, size, size, size, size, size, -size, size, -size, -size,
                 size, -size, size, -size, size, size, -size, size, -size, -size, -size, -size,
                 -size };;
    }
    const std::vector<unsigned int> getCubeIndices() {
        return INDICES;
    }

    Texture::Texture(TD::texture* texture, TEXTURE_TYPE type, std::string path) : texture(texture) {this->type = type; this->path = path; this->location = 0;}

}
