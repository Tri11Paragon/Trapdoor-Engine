//
// Created by brett on 22/07/22.
//
#include "gl.h"
#define STB_IMAGE_IMPLEMENTATION
#define STBI_FAILURE_USERMSG
#include <stb_image.h>
#include "../world/World.h"

namespace TD {

    extern glm::mat4 projectionMatrix;
    extern glm::mat4 projectionViewMatrix;
    extern glm::mat4 viewMatrix;

    float CalcPointLightBSphere(const TD::Light& Light) {
        float MaxChannel = fmax(fmax(Light.Color.x, Light.Color.y), Light.Color.z);

        float ret = (-Light.Linear + sqrtf(Light.Linear * Light.Linear - 4 * Light.Quadratic * (Light.Quadratic - 256 * MaxChannel * 1)))
                    /
                    (2 * Light.Quadratic);
        return ret;
    }

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
        this->drawCount = indicies.size();
        vaoID = createVAO();
        for (int i = 0; i < attributeCount; i++) {
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        storeData(indicies.size(), indicies.data());

        storeData(0, 3, 3 * sizeof(float), 0, verts.size(), verts.data());

        unbind();
    }

    vao::vao(const std::vector<float> &verts, const std::vector<float> &uvs, const std::vector<unsigned int> &indicies, int attributeCount) {
        this->drawCount = indicies.size();
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

    void vao::bind() {
        glBindVertexArray(vaoID);
    }

    void vao::unbind() {
        glBindVertexArray(0);
    }

    vao::~vao() {
        tlog << "Deleting VAO {" << vaoID << "}";
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
        if (drawCount < 0)
            return;
        else
            glDrawElements(GL_TRIANGLES, drawCount, GL_UNSIGNED_INT, 0);
    }

    /***---------------{Model VAO}---------------***/

    modelVAO::modelVAO(const unloadedVAO& vaoInfo, const std::vector<Texture> &textures): localModelTransform(vaoInfo.transform) {
        this->textures = textures;
        this->drawCount = (int)vaoInfo.indicesVec.size();
        this->name = vaoInfo.name;
        vaoID = createVAO();
        for (int i = 0; i < 3; i++) {
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        storeData((int)vaoInfo.indicesVec.size(), vaoInfo.indicesVec.data());

        storeData(vaoInfo.vertexVec);

        unbind();
    }

    /***---------------{Instanced VAO}---------------***/

    unsigned int instancedVAO::createVAO() {
        unsigned int vaoID;
        glGenVertexArrays(1, &vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    unsigned int instancedVAO::storeData(int length, const unsigned int *data) {
        unsigned int eboID;
        glGenBuffers(1, &eboID);

        vbos.push_back(eboID);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        // 4 bytes / int
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, length << 2, data, GL_STATIC_DRAW);

        return eboID;
    }

    unsigned int instancedVAO::storeData(const std::vector<Vertex> &vertices) {
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

    instancedVAO::instancedVAO(int max_transforms, const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices) {
        this->max_transforms = max_transforms;
        this->indexCount = indices.size();
        vaoID = createVAO();
        for (int i = 0; i < 3; i++) {
            glEnableVertexAttribArray(i);
            glEnableVertexArrayAttrib(vaoID, i);
        }
        storeData(indices.size(), indices.data());

        storeData(vertices);
    }

    unsigned int instancedVAO::createInstanceVBO(int bytePerInstance) {
        unsigned int vboID;
        glGenBuffers(1, &vboID);

        instanceVarsVBO = vboID;

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, max_transforms * bytePerInstance, NULL, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return vboID;
    }

    void instancedVAO::bind() {
        glBindVertexArray(vaoID);
    }

    void instancedVAO::draw(int count) {
        //glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, count);
        glDrawElementsInstancedBaseVertexBaseInstance(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, count, 0, 0);
    }

    void instancedVAO::unbind() {
        glBindVertexArray(0);
    }

    instancedVAO::~instancedVAO() {
        const unsigned int vao = vaoID;
        glDeleteVertexArrays(1, &vao);
        for (const unsigned int vbo : vbos){
            glDeleteBuffers(1, &vbo);
        }
        glDeleteBuffers(1, &instanceVarsVBO);
    }

    void instancedVAO::addInstancedAttribute(int attribute, int dataSize, int dataLengthBytes, int offset) {
        glBindBuffer(GL_ARRAY_BUFFER, instanceVarsVBO);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(attribute);
        glVertexAttribPointer(attribute, dataSize, GL_FLOAT, GL_FALSE, dataLengthBytes, (void *) (long)offset);
        glVertexAttribDivisor(attribute, 1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /***---------------{Instanced Light VAO}---------------***/

    instancedLightVAO::instancedLightVAO(const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices): instancedVAO(2048, vertices, indices) {
        createInstanceVBO(sizeof(LightData));
        addInstancedAttribute(3, 4, sizeof(LightData), 0);
        addInstancedAttribute(4, 4, sizeof(LightData), 16);
        addInstancedAttribute(5, 4, sizeof(LightData), 32);
        addInstancedAttribute(6, 4, sizeof(LightData), 48);
        addInstancedAttribute(7, 3, sizeof(LightData), offsetof(LightData, position));
        addInstancedAttribute(8, 3, sizeof(LightData), offsetof(LightData, color));
        addInstancedAttribute(9, 1, sizeof(LightData), offsetof(LightData, Linear));
        addInstancedAttribute(10, 1, sizeof(LightData), offsetof(LightData, Quadratic));
        addInstancedAttribute(11, 1, sizeof(LightData), offsetof(LightData, Radius));
    }

    void instancedLightVAO::updateLightData(TD::camera &camera, const std::vector<Light> &lights) {
        std::vector<LightData> datas;
        for (int i = 0; i < lights.size(); i++){
            Light l = lights[i];
            float sphereScale = CalcPointLightBSphere(l);
            if (!camera.sphereInFrustum(l.Position, sphereScale))
                continue;
            glm::mat4 trans(1.0);
            trans = glm::translate(trans, l.Position);
            trans = glm::scale(trans, glm::vec3(sphereScale));
            LightData data;
            data.transform = trans;
            data.position = l.Position;
            data.color = l.Color;
            data.Linear = l.Linear;
            data.Quadratic = l.Quadratic;
            data.Radius = sphereScale;
            datas.push_back(data);
        }
        glBindBuffer(GL_ARRAY_BUFFER, instanceVarsVBO);
        //glBufferData(GL_ARRAY_BUFFER, datas.size() * sizeof(LightData), NULL, GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, datas.size() * sizeof(LightData), &datas[0]);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    instancedLightVAO::~instancedLightVAO() {

    }

    /***---------------{Texture}---------------***/

    texture::texture(){

    }

    texture::texture(bool loadGL, std::string path) {
        data = loadTexture(path);
        if (data == nullptr){
            flog << "There was an error loading the image file " << path;
            throw std::runtime_error("Error loading image from file!");
        }
        if (loadGL)
            loadGLTexture();
    }

    unsigned char *texture::loadTexture(std::string path) {
        // TODO: add more image processing options
        stbi_set_flip_vertically_on_load(true);
        unsigned char *dta = stbi_load(path.c_str(), &width, &height, &channels, 0);
        //if (stbi__g_failure_reason) {
        //    flog << "STB Error Reason: ";
        //    flog << stbi__g_failure_reason;
        //}
        return dta;
    }

    void texture::loadGLTexture() {
        if (loadedToGpu)
            return;
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
        data = nullptr;
        loadedToGpu = true;
    }

    texture::~texture() {
        tlog << "Deleting Texture {" << textureID << "}";
        glDeleteTextures(1, &textureID);
        if (data != nullptr)
            stbi_image_free(data);
        data = nullptr;
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

    /***---------------{GIF based Texture}---------------***/

    gifTexture::gifTexture(const std::string& path) {
        FILE* file = stbi__fopen(path.c_str(), "rb");
        if (!file)
            throw std::runtime_error("Unable to load GIF! " + path);
        stbi__context s;
        stbi__gif g;
        stbi__start_file(&s, file);
        int* delay;
        data = (unsigned char*) stbi__load_gif_main(&s, &delay, &width, &height, &frames, &channels, 0);
        for (int i = 0; i < frames; i++)
            delays.push_back(delay[i]);
        fclose(file);
    }

    void gifTexture::bind(int frame) {
        if (frame > frames)
            throw std::runtime_error("Frame exceeds number of frames!");
        if (loadedAsArray)
            glBindTexture(GL_TEXTURE_2D_ARRAY, textures[frame]);
        else
            glBindTexture(GL_TEXTURE_2D, textures[frame]);
    }

    void gifTexture::unbind() const {
        if (loadedAsArray)
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        else
            glBindTexture(GL_TEXTURE_2D, 0);
    }

    void gifTexture::loadGL(bool asArray) {
        if (loadedToGL)
            return;
        if (asArray){

        } else {
            unsigned int* textureIDs = new unsigned int[frames];
            glGenTextures(frames, textureIDs);
            for (int i = 0; i < frames; i++) {
                unsigned char* localMem = new unsigned char[channels * width * height];
                // use memcopy?
                //for (int j = 0; j < channels * width * height; j++){
                //    localMem[j] = data[(i * channels * width * height) + j];
                //}
                memcpy(localMem, (void*) data[i * channels * width * height], channels * width * height);
                glBindTexture(GL_TEXTURE_2D, textureIDs[i]);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

                int GL_RGB_SETTING = channels == 3 ? GL_RGB : GL_RGBA;
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB_SETTING, width, height, 0, GL_RGB_SETTING, GL_UNSIGNED_BYTE, localMem);
                glGenerateMipmap(GL_TEXTURE_2D);

                textures.push_back(textureIDs[i]);
                delete[](localMem);
            }
            delete[](textureIDs);
        }
        stbi_image_free(data);
        data = nullptr;
        loadedToGL = true;
    }

    gifTexture::~gifTexture() {
        if (data != nullptr)
            stbi_image_free(data);
        data = nullptr;
        for (int i = 0; i < textures.size(); i++)
            glDeleteTextures(1, &textures[i]);
    }

    /***---------------{Model}---------------***/

    void model::draw(shader &shader, glm::mat4 trans) {
        shader.use();
        for (modelVAO* mesh : meshes){
            shader.setMatrix("transform", trans);
            mesh->bind();
            mesh->bindTextures();
            mesh->draw();
        }
    }

    void model::draw(shader &shader, std::vector<glm::mat4> trans) {
        shader.use();
        for (modelVAO* mesh : meshes){
            mesh->bind();
            mesh->bindTextures();
            for (glm::mat4 tran : trans) {
                shader.setMatrix("transform", tran);
                mesh->draw();
            }
        }
    }

    void model::loadModel(std::string pth) {
        Assimp::Importer import;

        const aiScene *scene = import.ReadFile(pth, aiProcess_Triangulate | aiProcess_FlipUVs
                                                            | aiProcess_ImproveCacheLocality | aiProcess_OptimizeMeshes | aiProcess_PreTransformVertices);

        if(!scene || scene->mFlags & AI_SCENE_FLAGS_INCOMPLETE || !scene->mRootNode) {
            flog << "Error loading model with Assimp.";
            flog << import.GetErrorString();
            return;
        }

        processNode(scene->mRootNode, scene);
    }

    void model::processNode(aiNode *node, const aiScene *scene) {
        // process all the node's meshes (if any)
        for(unsigned int i = 0; i < node->mNumMeshes; i++) {
            aiMesh *mesh = scene->mMeshes[node->mMeshes[i]];
            processMesh(mesh, scene, node->mTransformation);
        }
        // then do the same for each of its children
        for(unsigned int i = 0; i < node->mNumChildren; i++) {
            processNode(node->mChildren[i], scene);
        }
    }

    void model::processMesh(aiMesh *mesh, const aiScene *scene, aiMatrix4x4 transform) {
        std::vector<Vertex> vertices;
        std::vector<unsigned int> indices;

        for(unsigned int i = 0; i < mesh->mNumVertices; i++) {
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
            loadMaterialTextures(material, aiTextureType_DIFFUSE, DIFFUSE);
            //loadMaterialTextures(material, aiTextureType_SPECULAR, SPECULAR);
            //loadMaterialTextures(material, aiTextureType_NORMALS, NORMAL);
        }

        this->vertices = vertices;
        this->indices = indices;
        unloadedVAO unlVAO;
        unlVAO.vertexVec = vertices;
        unlVAO.indicesVec = indices;
        // copy it here where it will be loaded inside a thread.
        glm::mat4 glmTransform;
        glmTransform[0] = glm::vec4(transform.a1, transform.b1, transform.c1, transform.d1);
        glmTransform[1] = glm::vec4(transform.a2, transform.b2, transform.c2, transform.d2);
        glmTransform[2] = glm::vec4(transform.a3, transform.b3, transform.c3, transform.d3);
        glmTransform[3] = glm::vec4(transform.a4, transform.b4, transform.c4, transform.d4);
        unlVAO.transform = glmTransform;
        unlVAO.name = std::string(mesh->mName.C_Str());
        unloadedMeshes.emplace_back(unlVAO);
    }

    void model::loadMaterialTextures(aiMaterial *mat, aiTextureType type, TEXTURE_TYPE textureType) {
        for(unsigned int i = 0; i < mat->GetTextureCount(type); i++) {
            aiString str;
            mat->GetTexture(type, i, &str);
            std::string imgPath = std::string(str.C_Str());

            std::string delim = "/textures/";
            int pos = (int) imgPath.find(delim, 0);
            std::string filterPath = str.C_Str();
            if (pos < imgPath.size())
                filterPath = imgPath.substr(pos + delim.length(), imgPath.size());
            std::string path = std::string("../assets/textures/") + filterPath;

            dlog << "Queuing texture { " << path << " } @ " << path;
            // of course let the loadgl function know that this is the textures we are using
            unloadedTextures.emplace_back(path, textureType);
            // then tell the gameregistry to load this texture.
            TD::GameRegistry::registerTexture(path, path);
        }
    }

    model::~model(){
        for (modelVAO* m : meshes)
            delete(m);
    }

    void model::loadToGL() {
        for (const auto& ul : unloadedTextures)
            uvs.push_back(TD::GameRegistry::getTexture(ul.first));
        for (const auto& ul : unloadedMeshes)
            meshes.push_back(new modelVAO(ul, uvs));
        unloadedMeshes = std::vector<unloadedVAO>();
        unloadedTextures = std::vector<std::pair<std::string, TEXTURE_TYPE>>();
    }

    /***---------------{FBOs}---------------***/
    extern int _display_w, _display_h;
    extern std::vector<WindowResize*> windowResizeCallbacks;

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

    fbo::fbo() : fbo(_display_w, _display_h, DEPTH_BUFFER) { screenSized = true; }
    fbo::fbo(DEPTH_ATTACHMENT_TYPE type): fbo(_display_w, _display_h, type) { screenSized = true; }
    fbo::fbo(int width, int height): fbo(width, height, DEPTH_BUFFER) {}

    fbo::fbo(int width, int height, DEPTH_ATTACHMENT_TYPE type) {
        windowResizeCallbacks.push_back(this);
        this->_width = width;
        this->_height = height;
        this->_fboType = type;
        //createFrameBuffer();
        //unbindFBO();
        quad = new vao(vertices, texCoords, indices, 2);
    }

    void fbo::validateFramebuffer() {
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            flog << "Unable to create framebuffer! " << glCheckFramebufferStatus(GL_FRAMEBUFFER);
            throw std::runtime_error("Unable to create framebuffer!");
        }
    }

    void fbo::bindColorTexture(int activeTexture, int colorAttachment) {
        glActiveTexture(activeTexture);
        glBindTexture(GL_TEXTURE_2D, _colorTextures.at(colorAttachment));
    }

    inline void fbo::assignUsingColorTextures(const std::vector<unsigned int>& colorAttachments) {
        glDrawBuffers(colorAttachments.size(), colorAttachments.data());
    }

    void fbo::bindDepthTexture(int depthAttachment) {
        if (_fboType != DEPTH_TEXTURE)
            throw std::runtime_error("Unable to bind texture. FBO uses renderbuffer!");

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
        renderToQuad(shader, this->_x,this->_y, width, height);
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
        windowResizeCallbacks.erase(std::remove(windowResizeCallbacks.begin(), windowResizeCallbacks.end(), this));
        deleteFrameBuffer();
        dlog << "Deleting FBO " << _fboID;
    }

    void fbo::createFrameBuffer() {
        glGenFramebuffers(1, &_fboID);
        bindFBO();

        createAttachments();

        if (_fboType == DEPTH_TEXTURE){
            glGenTextures(1, &_depthAttachment);
            glBindTexture(GL_TEXTURE_2D, _depthAttachment);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, _width, _height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, NULL);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, _depthAttachment, 0);
        } else if (_fboType == DEPTH_BUFFER) {
            glGenRenderbuffers(1, &_depthAttachment);
            glBindRenderbuffer(GL_RENDERBUFFER, _depthAttachment);

            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, _width, _height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, _depthAttachment);
        }
        if (_fboType != DEPTH_NONE)
            validateFramebuffer();
    }

    void fbo::deleteFrameBuffer() {
        glDeleteFramebuffers(1, &_fboID);
        if (_fboType == DEPTH_BUFFER){
            glDeleteRenderbuffers(1, &_depthAttachment);
        } else if (_fboType == DEPTH_TEXTURE){
            glDeleteTextures(1, &_depthAttachment);
        }
        for (std::pair<int, unsigned int> p : _colorTextures){
            glDeleteTextures(1, &p.second);
        }
        _colorTextures = std::unordered_map<int, unsigned int>();
    }

    void fbo::windowResized(int x, int y, int width, int height) {
        if (!screenSized)
            return;
        this->_width = width;
        this->_height = height;
        this->_x = x;
        this->_y = y;
        deleteFrameBuffer();
        createFrameBuffer();
    }

    /***---------------{GBuffer FBO}---------------***/

    extern std::string assetsPath;

    gBufferFBO::gBufferFBO(){
        this->_width = _display_w;
        this->_height = _display_h;
        this->_fboType = DEPTH_BUFFER;

        firstPassShader = new shader("../assets/shaders/gbuffers/firstpass.vert", "../assets/shaders/gbuffers/firstpass.frag");
        firstPassShader->use();
        firstPassShader->setInt("texture_diffuse1", 0);
        firstPassShader->setInt("texture_specular1", 1);
        secondPassShader = new shader("../assets/shaders/gbuffers/secondpass.vert", "../assets/shaders/gbuffers/secondpass.frag");
        secondPassShader->use();
        secondPassShader->setInt("gPosition", 0);
        secondPassShader->setInt("gNormal", 1);
        secondPassShader->setInt("gAlbedoSpec", 2);
        secondPassShader->setInt("shadowMap", 3);
        secondPassShader->setFloat("farPlane", camera_far_plane);

        //glDeleteFramebuffers(1, &_fboID);
        //glDeleteRenderbuffers(1, &_depthAttachment);
        createFrameBuffer();

        unbindFBO();
    }

    void gBufferFBO::bindFirstPass() {
        bindFBO();
        glClearColor(0.0, 0.0, 0.0, 1.0); // keep it black so it doesn't leak into g-buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        firstPassShader->use();
    }

    void gBufferFBO::updateLights(std::vector<Light> &lights){
        secondPassShader->use();
        for (int i = 0; i < lights.size(); i++)
            secondPassShader->setLightArray("lights", i, lights[i]);
    }

    void gBufferFBO::bindSecondPass(TD::camera &camera) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, _fboID);
        glBlitFramebuffer(0, 0, _width, _height, 0, 0, _width, _height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);

        bindColorTexture(GL_TEXTURE0, GL_COLOR_ATTACHMENT0);
        bindColorTexture(GL_TEXTURE1, GL_COLOR_ATTACHMENT1);
        bindColorTexture(GL_TEXTURE2, GL_COLOR_ATTACHMENT2);
        secondPassShader->use();
        secondPassShader->setVec3("viewPos", camera.getPosition());
        renderToQuad(*secondPassShader);
    }

    gBufferFBO::~gBufferFBO() {
        delete(firstPassShader);
        delete(secondPassShader);
        dlog << "Deleting GBuffer " << _fboID;
    }

    void gBufferFBO::createAttachments(){
        createColorTexture(GL_COLOR_ATTACHMENT0, GL_RGBA32F, GL_RGBA, GL_FLOAT, GL_NEAREST);
        createColorTexture(GL_COLOR_ATTACHMENT1, GL_RGBA16F, GL_RGBA, GL_FLOAT, GL_NEAREST);
        createColorTexture(GL_COLOR_ATTACHMENT2, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, GL_NEAREST);

        unsigned int attachments[3] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
        glDrawBuffers(3, attachments);
    }

    void gBufferFBO::updateDirLight(glm::vec3 direction, glm::vec3 color, bool enabled) {
        secondPassShader->use();
        secondPassShader->setVec3("direction", direction);
        secondPassShader->setColor("color", color);
        secondPassShader->setBool("enabled", enabled);
    }

    /***---------------{Shadow FBO}---------------***/

    shadowFBO::shadowFBO() {
        // TODO: settings loader
        this->_width = 2048;
        this->_height = 2048;
        this->_fboType = DEPTH_NONE;

        depthShader = new TD::shader("../assets/shaders/shadows/shadow.vert", "../assets/shaders/shadows/shadow.geom", "../assets/shaders/shadows/shadow.frag", false);

        glGenFramebuffers(1, &_fboID);

        glGenTextures(1, &_depthAttachment);
        glBindTexture(GL_TEXTURE_2D_ARRAY, _depthAttachment);
        glTexImage3D(
                GL_TEXTURE_2D_ARRAY,
                0,
                GL_DEPTH_COMPONENT32F,
                this->_width,
                this->_height,
                int(shadowCascadeLevels.size()) + 1,
                0,
                GL_DEPTH_COMPONENT,
                GL_FLOAT,
                nullptr);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        constexpr float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindFramebuffer(GL_FRAMEBUFFER, _fboID);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, _depthAttachment, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        _colorTextures.insert(std::pair<int, unsigned int>(GL_TEXTURE3, _depthAttachment));
        validateFramebuffer();
        unbindFBO();
        glGenBuffers(1, &matricesUBO);
        glBindBuffer(GL_UNIFORM_BUFFER, matricesUBO);
        glBufferData(GL_UNIFORM_BUFFER, sizeof(glm::mat4x4) * 5, nullptr, GL_STATIC_DRAW);
        glBindBufferBase(GL_UNIFORM_BUFFER, 3, matricesUBO);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    void shadowFBO::createAttachments() {

    }

    void shadowFBO::bind() {
        depthShader->use();
        const auto lightMatrices = generateLightMatrix();
        glBindBuffer(GL_UNIFORM_BUFFER, matricesUBO);
        for (size_t i = 0; i < lightMatrices.size(); ++i) {
            //tlog << lightMatrices[i][0].x;
            glBufferSubData(GL_UNIFORM_BUFFER, i * sizeof(glm::mat4x4), sizeof(glm::mat4x4), &lightMatrices[i]);
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, _fboID);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_TEXTURE_2D_ARRAY, _depthAttachment, 0);
        glViewport(0, 0, _width, _height);
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_CULL_FACE);
        //glCullFace(GL_FRONT);  // peter panning
    }

    void shadowFBO::finish() {
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, _display_w, _display_h);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    shadowFBO::~shadowFBO() {
        glDeleteBuffers(1, &matricesUBO);
        delete(depthShader);
    }

    std::vector<glm::vec4> shadowFBO::getFrustumCornersWorldSpace(glm::mat4 projectView) {
        const auto inv = glm::inverse(projectView);

        std::vector<glm::vec4> frustumCorners;
        for (unsigned int x = 0; x < 2; ++x) {
            for (unsigned int y = 0; y < 2; ++y) {
                for (unsigned int z = 0; z < 2; ++z) {
                    const glm::vec4 pt = inv * glm::vec4(
                            2.0f * x - 1.0f,
                            2.0f * y - 1.0f,
                            2.0f * z - 1.0f,
                            1.0f);
                    frustumCorners.push_back(pt / pt.w);
                }
            }
        }

        return frustumCorners;
    }

    std::vector<glm::vec4> shadowFBO::getFrustumCornersWorldSpace(glm::mat4 project, glm::mat4 view) {
        return getFrustumCornersWorldSpace(project * view);
    }

    extern float fov;

    glm::mat4 shadowFBO::getLightSpaceMatrix(const float nearPlane, const float farPlane) {
        const auto proj = glm::perspective(
                glm::radians(fov), (float)_display_w / (float)_display_h, nearPlane,
                farPlane);
        const auto corners = getFrustumCornersWorldSpace(proj, viewMatrix);

        glm::vec3 center = glm::vec3(0, 0, 0);
        for (const auto& v : corners)
        {
            center += glm::vec3(v);
        }
        center /= corners.size();

        const auto lightView = glm::lookAt(center + lightDireciton, center, glm::vec3(0.0f, 1.0f, 0.0f));

        float minX = std::numeric_limits<float>::max();
        float maxX = std::numeric_limits<float>::min();
        float minY = std::numeric_limits<float>::max();
        float maxY = std::numeric_limits<float>::min();
        float minZ = std::numeric_limits<float>::max();
        float maxZ = std::numeric_limits<float>::min();
        for (const auto& v : corners) {
            const auto trf = lightView * v;
            minX = std::min(minX, trf.x);
            maxX = std::max(maxX, trf.x);
            minY = std::min(minY, trf.y);
            maxY = std::max(maxY, trf.y);
            minZ = std::min(minZ, trf.z);
            maxZ = std::max(maxZ, trf.z);
        }

        // Tune this parameter according to the scene
        constexpr float zMult = 10.0f;
        constexpr float bias = 10.0f;
        if (minZ < 0){
            minZ *= zMult;
        }else{
            minZ /= zMult;
        }
        if (maxZ < 0){
            maxZ /= zMult;
        }else{
            maxZ *= zMult;
        }

        const glm::mat4 lightProjection = glm::ortho(minX - bias, maxX + bias, minY - bias, maxY + bias, minZ, maxZ);

        return lightProjection * lightView;
    }

    std::vector<glm::mat4> shadowFBO::generateLightMatrix() {
        std::vector<glm::mat4> ret;
        for (size_t i = 0; i < shadowCascadeLevels.size() + 1; ++i) {
            if (i == 0) {
                ret.push_back(getLightSpaceMatrix(0.1f, shadowCascadeLevels[i]));
            } else if (i < shadowCascadeLevels.size()) {
                ret.push_back(getLightSpaceMatrix(shadowCascadeLevels[i - 1], shadowCascadeLevels[i]));
            } else {
                ret.push_back(getLightSpaceMatrix(shadowCascadeLevels[i - 1], camera_far_plane));
            }
        }
        return ret;
    }

    void shadowFBO::bindDepthTextureArray() {
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D_ARRAY, _depthAttachment);
    }

    /***---------------{Static Stuff}---------------***/

    unsigned int matrixUBO;
    const unsigned int MATRIX_COUNT = 4;
    float matrixData[MATRIX_COUNT * 16];

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
