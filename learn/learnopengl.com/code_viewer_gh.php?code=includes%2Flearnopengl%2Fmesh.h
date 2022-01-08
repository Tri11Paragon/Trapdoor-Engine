


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/mesh.h</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#ifndef MESH_H
#define MESH_H

#include &lt;glad/glad.h&gt; // holds all OpenGL type declarations

#include &lt;glm/glm.hpp&gt;
#include &lt;glm/gtc/matrix_transform.hpp&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;

#include &lt;string&gt;
#include &lt;vector&gt;
using namespace std;

#define MAX_BONE_INFLUENCE 4

struct Vertex {
    // position
    glm::vec3 Position;
    // normal
    glm::vec3 Normal;
    // texCoords
    glm::vec2 TexCoords;
    // tangent
    glm::vec3 Tangent;
    // bitangent
    glm::vec3 Bitangent;
	//bone indexes which will influence this vertex
	int m_BoneIDs[MAX_BONE_INFLUENCE];
	//weights from each bone
	float m_Weights[MAX_BONE_INFLUENCE];
};

struct Texture {
    unsigned int id;
    string type;
    string path;
};

class Mesh {
public:
    // mesh Data
    vector&lt;Vertex&gt;       vertices;
    vector&lt;unsigned int&gt; indices;
    vector&lt;Texture&gt;      textures;
    unsigned int VAO;

    // constructor
    Mesh(vector&lt;Vertex&gt; vertices, vector&lt;unsigned int&gt; indices, vector&lt;Texture&gt; textures)
    {
        this-&gt;vertices = vertices;
        this-&gt;indices = indices;
        this-&gt;textures = textures;

        // now that we have all the required data, set the vertex buffers and its attribute pointers.
        setupMesh();
    }

    // render the mesh
    void Draw(Shader &amp;shader) 
    {
        // bind appropriate textures
        unsigned int diffuseNr  = 1;
        unsigned int specularNr = 1;
        unsigned int normalNr   = 1;
        unsigned int heightNr   = 1;
        for(unsigned int i = 0; i &lt; textures.size(); i++)
        {
           <function id='49'> glActiveTexture(</function>GL_TEXTURE0 + i); // active proper texture unit before binding
            // retrieve texture number (the N in diffuse_textureN)
            string number;
            string name = textures[i].type;
            if(name == &quot;texture_diffuse&quot;)
                number = std::to_string(diffuseNr++);
            else if(name == &quot;texture_specular&quot;)
                number = std::to_string(specularNr++); // transfer unsigned int to string
            else if(name == &quot;texture_normal&quot;)
                number = std::to_string(normalNr++); // transfer unsigned int to string
             else if(name == &quot;texture_height&quot;)
                number = std::to_string(heightNr++); // transfer unsigned int to string

            // now set the sampler to the correct texture unit
           <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>shader.ID, (name + number).c_str()), i);
            // and finally bind the texture
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textures[i].id);
        }
        
        // draw mesh
       <function id='27'> glBindVertexArray(</function>VAO);
       <function id='2'> glDrawElements(</function>GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
       <function id='27'> glBindVertexArray(</function>0);

        // always good practice to set everything back to defaults once configured.
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
    }

private:
    // render data 
    unsigned int VBO, EBO;

    // initializes all the buffer objects/arrays
    void setupMesh()
    {
        // create buffers/arrays
       <function id='33'> glGenVertexArrays(</function>1, &amp;VAO);
       <function id='12'> glGenBuffers(</function>1, &amp;VBO);
       <function id='12'> glGenBuffers(</function>1, &amp;EBO);

       <function id='27'> glBindVertexArray(</function>VAO);
        // load data into vertex buffers
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
        // A great thing about structs is that their memory layout is sequential for all its items.
        // The effect is that we can simply pass a pointer to the struct and it translates perfectly to a glm::vec3/2 array which
        // again translates to 3/2 floats which translates to a byte array.
       <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, vertices.size() * sizeof(Vertex), &amp;vertices[0], GL_STATIC_DRAW);  

       <function id='32'> glBindBuffer(</function>GL_ELEMENT_ARRAY_BUFFER, EBO);
       <function id='31'> glBufferData(</function>GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(unsigned int), &amp;indices[0], GL_STATIC_DRAW);

        // set the vertex attribute pointers
        // vertex Positions
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);	
       <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)0);
        // vertex normals
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);	
       <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, Normal));
        // vertex texture coords
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);	
       <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, TexCoords));
        // vertex tangent
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>3);
       <function id='30'> glVertexAttribPointer(</function>3, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, Tangent));
        // vertex bitangent
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>4);
       <function id='30'> glVertexAttribPointer(</function>4, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, Bitangent));
		// ids
	<function id='29'><function id='60'>	glEnableV</function>ertexAttribArray(</function>5);
		glVertexAttribIPointer(5, 4, GL_INT, sizeof(Vertex), (void*)offsetof(Vertex, m_BoneIDs));

		// weights
	<function id='29'><function id='60'>	glEnableV</function>ertexAttribArray(</function>6);
	<function id='30'>	glVertexAttribPointer(</function>6, 4, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, m_Weights));
       <function id='27'> glBindVertexArray(</function>0);
    }
};
#endif
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>