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
#include "../glm.h"
#include "shader.h"
#include "camera.h"
#include <assimp/Importer.hpp>
#include <utility>
#include <assimp/scene.h>
#include <assimp/postprocess.h>

namespace TD {

    class texture {
    private:
        texture(const texture& that); // Disable Copy Constructor
        texture& operator=(const texture& that); // Disable Copy Assignment
    protected:
        unsigned int textureID;
        int width, height, channels;
        unsigned char* loadTexture(std::string path);

        bool loadedToGpu = false;
        unsigned char* data;

    public:
        texture(texture &&) noexcept = delete; // Disable move constructor.
        texture& operator=(texture &&) noexcept = delete; // Disable Move Assignment
        texture();
        explicit texture(std::string path) : texture(true, std::move(path)) {}
        texture(bool loadGL, std::string path);
        ~texture();
        virtual void bind();
        virtual void unbind();
        void loadGLTexture();
        void enableGlTextures(int textureCount);
    };

    class cubemapTexture : public texture {
    private:
        cubemapTexture(const cubemapTexture& that); // Disable Copy Constructor
        cubemapTexture& operator=(const cubemapTexture& that); // Disable Copy Assignment
    public:
        cubemapTexture(cubemapTexture &&) noexcept = delete; // Disable move constructor.
        cubemapTexture& operator=(cubemapTexture &&) noexcept = delete; // Disable Move Assignment
        explicit cubemapTexture(std::vector<std::string> paths);
        void bind() override;
        void unbind() override;
        ~cubemapTexture() = default;
    };

    // this needs very little from the texture base class and isn't necessarily compatible.
    // as a result this is an independent class.
    class gifTexture {
    private:
        std::vector<unsigned int> textures;
        //std::vector<unsigned char*> textureDatas;
        unsigned char* data;
        std::vector<int> delays;
        bool loadedAsArray = false, loadedToGL = false;
        int width = 0, height = 0, channels = 0, frames = 0;

        gifTexture(const gifTexture& that); // Disable Copy Constructor
        gifTexture& operator=(const gifTexture& that); // Disable Copy Assignment
    public:
        gifTexture(gifTexture &&) noexcept = delete; // Disable move constructor.
        gifTexture& operator=(gifTexture &&) noexcept = delete; // Disable Move Assignment
        explicit gifTexture(const std::string& path);
        void bind(int frame = 0);
        void unbind() const;
        void loadGL(bool asArray = false);

        inline std::vector<unsigned int> getTextures() {return textures;}
        inline std::vector<int> getDelays() {return delays;}
        [[nodiscard]] inline int getNumberOfFrames() const {return frames;}
        [[nodiscard]] inline int getWidth() const {return width;}
        [[nodiscard]] inline int getHeight() const {return height;}

        ~gifTexture();
    };

    enum TEXTURE_TYPE {
        DIFFUSE,
        NORMAL,
        SPECULAR
    };
    struct Vertex {
        glm::vec3 Position;
        glm::vec3 Normal;
        glm::vec2 UV;
    };
    struct Texture {
        Texture(TD::texture* texture, TEXTURE_TYPE type, std::string path);
        TD::texture* texture;
        int location;
        TEXTURE_TYPE type;
        std::string path;
    };
    struct unloadedVAO {
        std::vector<Vertex> vertexVec;
        std::vector<unsigned int> indicesVec;
        glm::mat4 transform;
        std::string name;
    };

    class vao {
    private:
        vao(const vao& that); // Disable Copy Constructor
        vao& operator=(const vao& that); // Disable Copy Assignment
    protected:
        unsigned int vaoID{0};
        std::vector<unsigned int> vbos;
        std::vector<Texture> textures;
        int drawCount = -1;
        static unsigned int createVAO();
        unsigned int storeData(int attrNumber, int coordSize, int stride, long offset, int length, const float* data);
        unsigned int storeData(int length, const unsigned int* data);
        unsigned int storeData(const std::vector<Vertex> &vertices);
        vao() = default;
    public:
        vao(vao &&) noexcept = delete; // Disable move constructor.
        vao& operator=(vao &&) noexcept = delete; // Disable Move Assignment
        vao(const std::vector<float> &verts, const std::vector<unsigned int> &indicies, int attributeCount);
        vao(const std::vector<float> &verts, const std::vector<float> &uvs, const std::vector<unsigned int> &indicies, int attributeCount);
        virtual ~vao();
        void bind();
        virtual void bindTextures();
        virtual void draw();
        void unbind();
    };

    class vertexVAO : public vao {
    private:
    public:
        vertexVAO(std::vector<float> &verts, int dimensions, int attributeCount) {
            vaoID = createVAO();
            for (int i = 0; i < attributeCount; i++) {
                glEnableVertexAttribArray(i);
                glEnableVertexArrayAttrib(vaoID, i);
            }

            storeData(0, dimensions, dimensions * (int)sizeof(float), 0, (int)verts.size(), verts.data());

            unbind();
        }
        void draw() override {
            glDrawArrays(GL_TRIANGLES, 0, drawCount);
        }
    };

    class modelVAO : public vao {
    private:
        std::string name;
        glm::mat4 localModelTransform;
    public:
        modelVAO(const unloadedVAO& vaoInfo, const std::vector<Texture> &textures);
        inline std::string& getName(){
            return name;
        }
        inline glm::mat4 getTransform(){
            return localModelTransform;
        }
    };

    class instancedVAO {
    private:
        instancedVAO(const instancedVAO& that); // Disable Copy Constructor
        instancedVAO& operator=(const instancedVAO& that); // Disable Copy Assignment
    protected:
        unsigned int vaoID;
        unsigned int instanceVarsVBO;
        std::vector<unsigned int> vbos;
        int indexCount = -1;
        int max_transforms = 10000;
        unsigned int storeData(int length, const unsigned int* data);
        unsigned int storeData(const std::vector<Vertex> &vertices);
        static unsigned int createVAO();
        unsigned int createInstanceVBO(int bytePerInstance);
        void addInstancedAttribute(int attribute, int size, int dataLength, int offset);
    public:
        instancedVAO(instancedVAO &&) noexcept = delete; // Disable move constructor.
        instancedVAO& operator=(instancedVAO &&) noexcept = delete; // Disable Move Assignment
        instancedVAO(int max_transforms, const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices);
        void bind();
        void draw(int count);
        void unbind();
        ~instancedVAO();
    };

    class instancedLightVAO : public instancedVAO {
    private:
        instancedLightVAO(const instancedLightVAO& that); // Disable Copy Constructor
        instancedLightVAO& operator=(const instancedLightVAO& that); // Disable Copy Assignment
        struct LightData {
            glm::mat4 transform;
            glm::vec3 position;
            glm::vec3 color;
            float Linear;
            float Quadratic;
            float Radius;
        };
    public:
        instancedLightVAO(instancedLightVAO &&) noexcept = delete; // Disable move constructor.
        instancedLightVAO& operator=(instancedLightVAO &&) noexcept = delete; // Disable Move Assignment
        instancedLightVAO(const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices);
        void updateLightData(TD::camera &camera, const std::vector<Light> &lights);
        ~instancedLightVAO();
    };

    class model {
    private:
        model(const model& that); // Disable Copy Constructor
        model& operator=(const model& that); // Disable Copy Assignment
    public:
        model(model &&) noexcept = delete; // Disable move constructor.
        model& operator=(model &&) noexcept = delete; // Disable Move Assignment
        explicit model(const std::string& path) {
            this->path = path;
            loadModel(path);
        }
        void loadToGL();
        inline std::vector<modelVAO*> getMeshes() {return meshes;}
        void draw(shader &shader, glm::mat4 trans);
        void draw(shader &shader, std::vector<glm::mat4> transforms);

        inline std::vector<Vertex> getVertices(){return vertices;}
        inline std::vector<unsigned int> getIndices(){return indices;}
        inline std::vector<Texture> getUVs(){return uvs;}

        ~model();
    private:
        // model data
        std::vector<modelVAO*> meshes;
        // unloaded data, gets loaded by loadToGL
        std::vector<std::pair<std::string, TEXTURE_TYPE>> unloadedTextures;
        std::vector<unloadedVAO> unloadedMeshes;

        std::string path;
        std::vector<Vertex> vertices;
        std::vector<unsigned int> indices;
        std::vector<Texture> uvs;

        void loadModel(std::string path);
        void processNode(aiNode *node, const aiScene *scene);
        void processMesh(aiMesh *mesh, const aiScene *scene, aiMatrix4x4 transform);
        void loadMaterialTextures(aiMaterial *mat, aiTextureType type, TEXTURE_TYPE textureType);
    };

    enum DEPTH_ATTACHMENT_TYPE {
        DEPTH_BUFFER,
        DEPTH_TEXTURE,
        DEPTH_NONE,
    };

    class fbo : public WindowResize {
    private:
        fbo(const fbo& that); // Disable Copy Constructor
        fbo& operator=(const fbo& that); // Disable Copy Assignment
    protected:
        unsigned int _fboID;
        DEPTH_ATTACHMENT_TYPE _fboType;
        int _width = 0, _height = 0, _x = 0, _y = 0;
        bool screenSized = false;
        vao* quad;

        std::unordered_map<int, unsigned int> _colorTextures;
        unsigned int _depthAttachment;

        void validateFramebuffer();
        virtual void createFrameBuffer();
        virtual void createAttachments() {}
        void deleteFrameBuffer();
    public:
        fbo(fbo &&) noexcept = delete; // Disable move constructor.
        fbo& operator=(fbo &&) noexcept = delete; // Disable Move Assignment
        fbo();
        explicit fbo(DEPTH_ATTACHMENT_TYPE type);
        fbo(int width, int height);
        fbo(int width, int height, DEPTH_ATTACHMENT_TYPE type);

        inline void createColorTexture(int colorAttachment) {
            createColorTexture(colorAttachment, GL_RGB);
        }
        inline void createColorTexture(int colorAttachment, int format) {
            createColorTexture(colorAttachment, format, format, GL_UNSIGNED_BYTE, GL_LINEAR);
        }
        inline void createColorTexture(int colorAttachment, int format, int formatInternal, int type, int filter) {
            unsigned int colorTextureID;
            glGenTextures(1, &colorTextureID);
            glBindTexture(GL_TEXTURE_2D, colorTextureID);

            glTexImage2D(GL_TEXTURE_2D, 0, format, _width, _height, 0, formatInternal, type, nullptr);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);

            glFramebufferTexture2D(GL_FRAMEBUFFER, colorAttachment, GL_TEXTURE_2D, colorTextureID, 0);
            _colorTextures.insert(std::pair<int, unsigned int>(colorAttachment, colorTextureID));
        }
        inline void assignUsingColorTextures(const std::vector<unsigned int>& colorAttachments);

        void bindColorTexture(int activeTexture, int colorAttachment);
        void bindDepthTexture(int depthAttachment);
        void bindFBO();
        void unbindFBO();

        void bindFBODraw();

        void blitToScreen();
        void blitToFBO(int readBuffer, fbo& outputFBO);

        void renderToQuad(TD::shader& shader);
        void renderToQuad(TD::shader& shader, glm::vec2 size);
        void renderToQuad(TD::shader& shader, glm::vec2 pos, glm::vec2 size);
        void renderToQuad(TD::shader& shader, int width, int height);
        void renderToQuad(TD::shader& shader, int x, int y, int width, int height);
        virtual void windowResized(int x, int y, int width, int height);

        virtual ~fbo();
    };

    class gBufferFBO : public fbo {
    private:
        gBufferFBO(const gBufferFBO& that); // Disable Copy Constructor
        gBufferFBO& operator=(const gBufferFBO& that); // Disable Copy Assignment

        TD::shader* firstPassShader;
        TD::shader* secondPassShader;


        virtual void createAttachments();
    public:
        gBufferFBO(gBufferFBO &&) noexcept = delete; // Disable move constructor.
        gBufferFBO& operator=(gBufferFBO &&) noexcept = delete; // Disable Move Assignment
        gBufferFBO();

        inline TD::shader* getFirstPassShader() {return firstPassShader;}
        inline TD::shader* getSecondPassShader() {return secondPassShader;}

        void bindFirstPass();
        void updateLights(std::vector<Light> &lights);
        void updateDirLight(glm::vec3 direction, glm::vec3 color, bool enabled);
        void bindSecondPass(TD::camera &camera);

        ~gBufferFBO();
    };

    extern float camera_far_plane;

    class shadowFBO : public fbo {
    private:
        shadowFBO(const shadowFBO& that); // Disable Copy Constructor
        shadowFBO& operator=(const shadowFBO& that); // Disable Copy Assignment

        TD::shader* depthShader;
        unsigned int matricesUBO;

        glm::vec3 lightDireciton;
        glm::mat4 lightViewMatrix;
        glm::mat4 lightProjectionMatrix;

        virtual void createAttachments();
        static std::vector<glm::vec4> getFrustumCornersWorldSpace(glm::mat4 projectView);
        static std::vector<glm::vec4> getFrustumCornersWorldSpace(glm::mat4 project, glm::mat4 view);
        glm::mat4 getLightSpaceMatrix(float nearPlane, float farPlane);
        std::vector<glm::mat4> generateLightMatrix();
    public:
        shadowFBO(shadowFBO &&) noexcept = delete; // Disable move constructor.
        shadowFBO& operator=(shadowFBO &&) noexcept = delete; // Disable Move Assignment

        std::vector<float> shadowCascadeLevels{ camera_far_plane / 50.0f, camera_far_plane / 25.0f, camera_far_plane / 10.0f, camera_far_plane / 2.0f };
        shadowFBO();
        ~shadowFBO();
        void updateLightDirection(glm::vec3 lightDirection) {
            this->lightDireciton = lightDirection;
        }
        inline shader* getShader() {return depthShader;}
        void bind();
        void bindDepthTextureArray();
        void finish();
        virtual void windowResized(int x, int y, int width, int height);
    };

    void createMatrixUBO();
    void updateProjectionMatrixUBO(glm::mat4 matrix);
    void updateViewMatrixUBO(glm::mat4 matrix);
    void updateOrthoMatrixUBO(glm::mat4 matrix);
    void updateProjectViewMatrixUBO(glm::mat4 matrix);
    const std::vector<float> getCubeVertexPositions(float size);
    const std::vector<unsigned int> getCubeIndices();

}

#endif //ENGINE_GL_H
