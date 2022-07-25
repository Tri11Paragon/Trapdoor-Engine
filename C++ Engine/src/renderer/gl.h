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
#include <assimp/Importer.hpp>
#include <assimp/scene.h>
#include <assimp/postprocess.h>

namespace TD {

    class texture {
    protected:
        unsigned int textureID;
        int width, height, channels;
        unsigned char* loadTexture(std::string path);
        void loadGLTexture(unsigned char* data);
    public:
        texture();
        texture(std::string path);
        ~texture();
        virtual void bind();
        virtual void unbind();
        void enableGlTextures(int textureCount);
    };

    class cubemapTexture : public texture {
    public:
        cubemapTexture(std::vector<std::string> paths);
        void bind();
        void unbind();
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

    class vao {
    private:
        unsigned int vaoID;
        std::vector<unsigned int> vbos;
        std::vector<Texture> textures;
        int indexCount = -1;
        unsigned int createVAO();
        unsigned int storeData(int attrNumber, int coordSize, int stride, long offset, int length, const float* data);
        unsigned int storeData(int length, const unsigned int* data);
        unsigned int storeData(const std::vector<Vertex> &vertices);
    public:
        vao(const std::vector<float> &verts, const std::vector<unsigned int> &indicies, int attributeCount);
        vao(std::vector<float> &verts, std::vector<float> &uvs, std::vector<unsigned int> &indicies, int attributeCount);
        vao(std::vector<float> &verts, int dimensions, int attributeCount);
        vao(const std::vector<Vertex> &vertices, const std::vector<unsigned int> &indices, const std::vector<Texture> &textures);
        ~vao();
        void bind();
        void bindTextures();
        void draw();
        void unbind();
    };

    class model {
    public:
        model(std::string path) {
            loadModel(path);
        }
        // useTextureCache refers to using the global texture cache (true) or use the class level cache (false)
        model(std::string path, bool useTextureCache) {
            loadModel(path);
            this->useTextureCache = useTextureCache;
        }
        void draw(shader &shader, glm::vec3 *positions, int numberOfPositions);
        void draw(shader &shader, glm::vec3 position);
        void draw(shader &shader, std::vector<glm::vec3> positions);
        ~model();
    private:
        // model data
        std::vector<vao*> meshes;
        std::string directory;
        bool useTextureCache = false;
        std::map<std::string, Texture> loadedTextures;

        void loadModel(std::string path);
        void processNode(aiNode *node, const aiScene *scene);
        vao* processMesh(aiMesh *mesh, const aiScene *scene);
        std::vector<Texture> loadMaterialTextures(aiMaterial *mat, aiTextureType type, TEXTURE_TYPE textureType);
    };

    enum DEPTH_ATTACHMENT_TYPE {
        DEPTH_BUFFER,
        DEPTH_TEXTURE
    };

    class fbo {
    private:
        unsigned int _fboID;
        DEPTH_ATTACHMENT_TYPE _fboType;
        int _width = 0, _height = 0;

        std::unordered_map<int, unsigned int> _colorTextures;
        unsigned int _depthAttachment;

        void validateFramebuffer();
    public:
        fbo();
        fbo(DEPTH_ATTACHMENT_TYPE type);
        fbo(int width, int height);
        fbo(int width, int height, DEPTH_ATTACHMENT_TYPE type);

        void createColorTexture(int colorAttachment);

        void bindColorTexture(int colorAttachment);
        void bindDepthTexture(int depthAttachment);
        void bindFBO();
        void unbindFBO();

        void blitToScreen();
        void blitToFBO(int readBuffer, fbo& outputFBO);

        void renderToQuad();
        void renderToQuad(glm::vec2 size);
        void renderToQuad(glm::vec2 pos, glm::vec2 size);
        void renderToQuad(int width, int height);
        void renderToQuad(int x, int y, int width, int height);

        ~fbo();
    };

    void createMatrixUBO();
    void updateProjectionMatrixUBO(glm::mat4 matrix);
    void updateViewMatrixUBO(glm::mat4 matrix);
    void deleteGlobalTextureCache();
    const std::vector<float> getCubeVertexPositions(float size);
    const std::vector<unsigned int> getCubeIndices();

}

#endif //ENGINE_GL_H
