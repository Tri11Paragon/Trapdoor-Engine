


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/model.h</title>
	<link rel="stylesheet" type="text/css" href="layout.css">
    <link rel="stylesheet" type="text/css" href="js/styles/obsidian.css">
    <script src="js/jquery-1.11.0.min.js"></script>
    <script src="js/hoverintent.js"></script>
    <script src="js/highlight.pack.js"></script>
    <script src="js/functions.js"></script>
    <script type="text/javascript" src="js/mathjax/MathJax.js?config=TeX-AMS_HTML"> // Has to be loaded last due to content bug </script>
    <script>hljs.initHighlightingOnLoad();</script>
    <script>
        window.onload = function() {
            $("#codez").mousedown(function() { switchNumbering(true); });
            $("#codez").mouseup(function() { switchNumbering(false); });
            
            function switchNumbering(hide)
            {     
                if(hide)
                    $('span.number').hide();
                else
                    $('span.number').show();
            }
            
            // Create all function callbacks
            SetFunctionTagCallbacks();
        };
        
    </script>
</head>
<body style="margin:0; padding:0; background-image: none; background-color: #282B2E;">

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#ifndef MODEL_H
#define MODEL_H

#include &lt;glad/glad.h&gt; 

#include &lt;glm/glm.hpp&gt;
#include &lt;glm/gtc/matrix_transform.hpp&gt;
#include &lt;stb_image.h&gt;
#include &lt;assimp/Importer.hpp&gt;
#include &lt;assimp/scene.h&gt;
#include &lt;assimp/postprocess.h&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/mesh.h' target='_blank'>learnopengl/mesh.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;

#include &lt;string&gt;
#include &lt;fstream&gt;
#include &lt;sstream&gt;
#include &lt;iostream&gt;
#include &lt;map&gt;
#include &lt;vector&gt;
using namespace std;

unsigned int TextureFromFile(const char *path, const string &amp;directory, bool gamma = false);

class Model 
{
public:
    // model data 
    vector&lt;Texture&gt; textures_loaded;	// stores all the textures loaded so far, optimization to make sure textures aren't loaded more than once.
    vector&lt;Mesh&gt;    meshes;
    string directory;
    bool gammaCorrection;

    // constructor, expects a filepath to a 3D model.
    Model(string const &amp;path, bool gamma = false) : gammaCorrection(gamma)
    {
        loadModel(path);
    }

    // draws the model, and thus all its meshes
    void Draw(Shader &amp;shader)
    {
        for(unsigned int i = 0; i &lt; meshes.size(); i++)
            meshes[i].Draw(shader);
    }
    
private:
    // loads a model with supported ASSIMP extensions from file and stores the resulting meshes in the meshes vector.
    void loadModel(string const &amp;path)
    {
        // read file via ASSIMP
        Assimp::Importer importer;
        const aiScene* scene = importer.ReadFile(path, aiProcess_Triangulate | aiProcess_GenSmoothNormals | aiProcess_FlipUVs | aiProcess_CalcTangentSpace);
        // check for errors
        if(!scene || scene-&gt;mFlags &amp; AI_SCENE_FLAGS_INCOMPLETE || !scene-&gt;mRootNode) // if is Not Zero
        {
            cout &lt;&lt; &quot;ERROR::ASSIMP:: &quot; &lt;&lt; importer.GetErrorString() &lt;&lt; endl;
            return;
        }
        // retrieve the directory path of the filepath
        directory = path.substr(0, path.find_last_of('/'));

        // process ASSIMP's root node recursively
        processNode(scene-&gt;mRootNode, scene);
    }

    // processes a node in a recursive fashion. Processes each individual mesh located at the node and repeats this process on its children nodes (if any).
    void processNode(aiNode *node, const aiScene *scene)
    {
        // process each mesh located at the current node
        for(unsigned int i = 0; i &lt; node-&gt;mNumMeshes; i++)
        {
            // the node object only contains indices to index the actual objects in the scene. 
            // the scene contains all the data, node is just to keep stuff organized (like relations between nodes).
            aiMesh* mesh = scene-&gt;mMeshes[node-&gt;mMeshes[i]];
            meshes.push_back(processMesh(mesh, scene));
        }
        // after we've processed all of the meshes (if any) we then recursively process each of the children nodes
        for(unsigned int i = 0; i &lt; node-&gt;mNumChildren; i++)
        {
            processNode(node-&gt;mChildren[i], scene);
        }

    }

    Mesh processMesh(aiMesh *mesh, const aiScene *scene)
    {
        // data to fill
        vector&lt;Vertex&gt; vertices;
        vector&lt;unsigned int&gt; indices;
        vector&lt;Texture&gt; textures;

        // walk through each of the mesh's vertices
        for(unsigned int i = 0; i &lt; mesh-&gt;mNumVertices; i++)
        {
            Vertex vertex;
            glm::vec3 vector; // we declare a placeholder vector since assimp uses its own vector class that doesn't directly convert to glm's vec3 class so we transfer the data to this placeholder glm::vec3 first.
            // positions
            vector.x = mesh-&gt;mVertices[i].x;
            vector.y = mesh-&gt;mVertices[i].y;
            vector.z = mesh-&gt;mVertices[i].z;
            vertex.Position = vector;
            // normals
            if (mesh-&gt;HasNormals())
            {
                vector.x = mesh-&gt;mNormals[i].x;
                vector.y = mesh-&gt;mNormals[i].y;
                vector.z = mesh-&gt;mNormals[i].z;
                vertex.Normal = vector;
            }
            // texture coordinates
            if(mesh-&gt;mTextureCoords[0]) // does the mesh contain texture coordinates?
            {
                glm::vec2 vec;
                // a vertex can contain up to 8 different texture coordinates. We thus make the assumption that we won't 
                // use models where a vertex can have multiple texture coordinates so we always take the first set (0).
                vec.x = mesh-&gt;mTextureCoords[0][i].x; 
                vec.y = mesh-&gt;mTextureCoords[0][i].y;
                vertex.TexCoords = vec;
                // tangent
                vector.x = mesh-&gt;mTangents[i].x;
                vector.y = mesh-&gt;mTangents[i].y;
                vector.z = mesh-&gt;mTangents[i].z;
                vertex.Tangent = vector;
                // bitangent
                vector.x = mesh-&gt;mBitangents[i].x;
                vector.y = mesh-&gt;mBitangents[i].y;
                vector.z = mesh-&gt;mBitangents[i].z;
                vertex.Bitangent = vector;
            }
            else
                vertex.TexCoords = glm::vec2(0.0f, 0.0f);

            vertices.push_back(vertex);
        }
        // now wak through each of the mesh's faces (a face is a mesh its triangle) and retrieve the corresponding vertex indices.
        for(unsigned int i = 0; i &lt; mesh-&gt;mNumFaces; i++)
        {
            aiFace face = mesh-&gt;mFaces[i];
            // retrieve all indices of the face and store them in the indices vector
            for(unsigned int j = 0; j &lt; face.mNumIndices; j++)
                indices.push_back(face.mIndices[j]);        
        }
        // process materials
        aiMaterial* material = scene-&gt;mMaterials[mesh-&gt;mMaterialIndex];    
        // we assume a convention for sampler names in the shaders. Each diffuse texture should be named
        // as 'texture_diffuseN' where N is a sequential number ranging from 1 to MAX_SAMPLER_NUMBER. 
        // Same applies to other texture as the following list summarizes:
        // diffuse: texture_diffuseN
        // specular: texture_specularN
        // normal: texture_normalN

        // 1. diffuse maps
        vector&lt;Texture&gt; diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, &quot;texture_diffuse&quot;);
        textures.insert(textures.end(), diffuseMaps.begin(), diffuseMaps.end());
        // 2. specular maps
        vector&lt;Texture&gt; specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, &quot;texture_specular&quot;);
        textures.insert(textures.end(), specularMaps.begin(), specularMaps.end());
        // 3. normal maps
        std::vector&lt;Texture&gt; normalMaps = loadMaterialTextures(material, aiTextureType_HEIGHT, &quot;texture_normal&quot;);
        textures.insert(textures.end(), normalMaps.begin(), normalMaps.end());
        // 4. height maps
        std::vector&lt;Texture&gt; heightMaps = loadMaterialTextures(material, aiTextureType_AMBIENT, &quot;texture_height&quot;);
        textures.insert(textures.end(), heightMaps.begin(), heightMaps.end());
        
        // return a mesh object created from the extracted mesh data
        return Mesh(vertices, indices, textures);
    }

    // checks all material textures of a given type and loads the textures if they're not loaded yet.
    // the required info is returned as a Texture struct.
    vector&lt;Texture&gt; loadMaterialTextures(aiMaterial *mat, aiTextureType type, string typeName)
    {
        vector&lt;Texture&gt; textures;
        for(unsigned int i = 0; i &lt; mat-&gt;GetTextureCount(type); i++)
        {
            aiString str;
            mat-&gt;GetTexture(type, i, &amp;str);
            // check if texture was loaded before and if so, continue to next iteration: skip loading a new texture
            bool skip = false;
            for(unsigned int j = 0; j &lt; textures_loaded.size(); j++)
            {
                if(std::strcmp(textures_loaded[j].path.data(), str.C_Str()) == 0)
                {
                    textures.push_back(textures_loaded[j]);
                    skip = true; // a texture with the same filepath has already been loaded, continue to next one. (optimization)
                    break;
                }
            }
            if(!skip)
            {   // if texture hasn't been loaded already, load it
                Texture texture;
                texture.id = TextureFromFile(str.C_Str(), this-&gt;directory);
                texture.type = typeName;
                texture.path = str.C_Str();
                textures.push_back(texture);
                textures_loaded.push_back(texture);  // store it as texture loaded for entire model, to ensure we won't unnecesery load duplicate textures.
            }
        }
        return textures;
    }
};


unsigned int TextureFromFile(const char *path, const string &amp;directory, bool gamma)
{
    string filename = string(path);
    filename = directory + '/' + filename;

    unsigned int textureID;
   <function id='50'> glGenTextures(</function>1, &amp;textureID);

    int width, height, nrComponents;
    unsigned char *data = stbi_load(filename.c_str(), &amp;width, &amp;height, &amp;nrComponents, 0);
    if (data)
    {
        GLenum format;
        if (nrComponents == 1)
            format = GL_RED;
        else if (nrComponents == 3)
            format = GL_RGB;
        else if (nrComponents == 4)
            format = GL_RGBA;

       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textureID);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
       <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_2D);

       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(data);
    }
    else
    {
        std::cout &lt;&lt; &quot;Texture failed to load at path: &quot; &lt;&lt; path &lt;&lt; std::endl;
        stbi_image_free(data);
    }

    return textureID;
}
#endif
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>