


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/shader.h</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#ifndef SHADER_H
#define SHADER_H

#include &lt;glad/glad.h&gt;
#include &lt;glm/glm.hpp&gt;

#include &lt;string&gt;
#include &lt;fstream&gt;
#include &lt;sstream&gt;
#include &lt;iostream&gt;

class Shader
{
public:
    unsigned int ID;
    // constructor generates the shader on the fly
    // ------------------------------------------------------------------------
    Shader(const char* vertexPath, const char* fragmentPath, const char* geometryPath = nullptr)
    {
        // 1. retrieve the vertex/fragment source code from filePath
        std::string vertexCode;
        std::string fragmentCode;
        std::string geometryCode;
        std::ifstream vShaderFile;
        std::ifstream fShaderFile;
        std::ifstream gShaderFile;
        // ensure ifstream objects can throw exceptions:
        vShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
        fShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
        gShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
        try 
        {
            // open files
            vShaderFile.open(vertexPath);
            fShaderFile.open(fragmentPath);
            std::stringstream vShaderStream, fShaderStream;
            // read file's buffer contents into streams
            vShaderStream &lt;&lt; vShaderFile.rdbuf();
            fShaderStream &lt;&lt; fShaderFile.rdbuf();		
            // close file handlers
            vShaderFile.close();
            fShaderFile.close();
            // convert stream into string
            vertexCode = vShaderStream.str();
            fragmentCode = fShaderStream.str();			
            // if geometry shader path is present, also load a geometry shader
            if(geometryPath != nullptr)
            {
                gShaderFile.open(geometryPath);
                std::stringstream gShaderStream;
                gShaderStream &lt;&lt; gShaderFile.rdbuf();
                gShaderFile.close();
                geometryCode = gShaderStream.str();
            }
        }
        catch (std::ifstream::failure&amp; e)
        {
            std::cout &lt;&lt; &quot;ERROR::SHADER::FILE_NOT_SUCCESFULLY_READ&quot; &lt;&lt; std::endl;
        }
        const char* vShaderCode = vertexCode.c_str();
        const char * fShaderCode = fragmentCode.c_str();
        // 2. compile shaders
        unsigned int vertex, fragment;
        // vertex shader
        vertex =<function id='37'> glCreateShader(</function>GL_VERTEX_SHADER);
       <function id='42'> glShaderSource(</function>vertex, 1, &amp;vShaderCode, NULL);
       <function id='38'> glCompileShader(</function>vertex);
        checkCompileErrors(vertex, &quot;VERTEX&quot;);
        // fragment Shader
        fragment =<function id='37'> glCreateShader(</function>GL_FRAGMENT_SHADER);
       <function id='42'> glShaderSource(</function>fragment, 1, &amp;fShaderCode, NULL);
       <function id='38'> glCompileShader(</function>fragment);
        checkCompileErrors(fragment, &quot;FRAGMENT&quot;);
        // if geometry shader is given, compile geometry shader
        unsigned int geometry;
        if(geometryPath != nullptr)
        {
            const char * gShaderCode = geometryCode.c_str();
            geometry =<function id='37'> glCreateShader(</function>GL_GEOMETRY_SHADER);
           <function id='42'> glShaderSource(</function>geometry, 1, &amp;gShaderCode, NULL);
           <function id='38'> glCompileShader(</function>geometry);
            checkCompileErrors(geometry, &quot;GEOMETRY&quot;);
        }
        // shader Program
        ID =<function id='36'> glCreateProgram(</function>);
       <function id='34'> glAttachShader(</function>ID, vertex);
       <function id='34'> glAttachShader(</function>ID, fragment);
        if(geometryPath != nullptr)
           <function id='34'> glAttachShader(</function>ID, geometry);
       <function id='35'> glLinkProgram(</function>ID);
        checkCompileErrors(ID, &quot;PROGRAM&quot;);
        // delete the shaders as they're linked into our program now and no longer necessery
       <function id='46'> glDeleteShader(</function>vertex);
       <function id='46'> glDeleteShader(</function>fragment);
        if(geometryPath != nullptr)
           <function id='46'> glDeleteShader(</function>geometry);

    }
    // activate the shader
    // ------------------------------------------------------------------------
    void use() 
    { 
       <function id='28'> glUseProgram(</function>ID); 
    }
    // utility uniform functions
    // ------------------------------------------------------------------------
    void setBool(const std::string &amp;name, bool value) const
    {         
       <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), (int)value); 
    }
    // ------------------------------------------------------------------------
    void setInt(const std::string &amp;name, int value) const
    { 
       <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), value); 
    }
    // ------------------------------------------------------------------------
    void setFloat(const std::string &amp;name, float value) const
    { 
       <function id='44'> glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), value); 
    }
    // ------------------------------------------------------------------------
    void setVec2(const std::string &amp;name, const glm::vec2 &amp;value) const
    { 
       <function id='44'> glUniform2</function>fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, &amp;value[0]); 
    }
    void setVec2(const std::string &amp;name, float x, float y) const
    { 
       <function id='44'> glUniform2</function>f<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), x, y); 
    }
    // ------------------------------------------------------------------------
    void setVec3(const std::string &amp;name, const glm::vec3 &amp;value) const
    { 
       <function id='44'> glUniform3</function>fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, &amp;value[0]); 
    }
    void setVec3(const std::string &amp;name, float x, float y, float z) const
    { 
       <function id='44'> glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), x, y, z); 
    }
    // ------------------------------------------------------------------------
    void setVec4(const std::string &amp;name, const glm::vec4 &amp;value) const
    { 
       <function id='44'> glUniform4</function>fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, &amp;value[0]); 
    }
    void setVec4(const std::string &amp;name, float x, float y, float z, float w) 
    { 
       <function id='44'> glUniform4</function>f<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), x, y, z, w); 
    }
    // ------------------------------------------------------------------------
    void setMat2(const std::string &amp;name, const glm::mat2 &amp;mat) const
    {
       <function id='44'> glUniformM</function>atrix2fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, GL_FALSE, &amp;mat[0][0]);
    }
    // ------------------------------------------------------------------------
    void setMat3(const std::string &amp;name, const glm::mat3 &amp;mat) const
    {
       <function id='44'> glUniformM</function>atrix3fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, GL_FALSE, &amp;mat[0][0]);
    }
    // ------------------------------------------------------------------------
    void setMat4(const std::string &amp;name, const glm::mat4 &amp;mat) const
    {
       <function id='44'> glUniformM</function>atrix4fv<function id='45'>(glGetUniformLocation(</function>ID, name.c_str()), 1, GL_FALSE, &amp;mat[0][0]);
    }

private:
    // utility function for checking shader compilation/linking errors.
    // ------------------------------------------------------------------------
    void checkCompileErrors(GLuint shader, std::string type)
    {
        GLint success;
        GLchar infoLog[1024];
        if(type != &quot;PROGRAM&quot;)
        {
           <function id='39'> glGetShaderiv(</function>shader, GL_COMPILE_STATUS, &amp;success);
            if(!success)
            {
               <function id='40'> glGetShaderInfoLog(</function>shader, 1024, NULL, infoLog);
                std::cout &lt;&lt; &quot;ERROR::SHADER_COMPILATION_ERROR of type: &quot; &lt;&lt; type &lt;&lt; &quot;\n&quot; &lt;&lt; infoLog &lt;&lt; &quot;\n -- --------------------------------------------------- -- &quot; &lt;&lt; std::endl;
            }
        }
        else
        {
           <function id='41'> glGetProgramiv(</function>shader, GL_LINK_STATUS, &amp;success);
            if(!success)
            {
                glGetProgramInfoLog(shader, 1024, NULL, infoLog);
                std::cout &lt;&lt; &quot;ERROR::PROGRAM_LINKING_ERROR of type: &quot; &lt;&lt; type &lt;&lt; &quot;\n&quot; &lt;&lt; infoLog &lt;&lt; &quot;\n -- --------------------------------------------------- -- &quot; &lt;&lt; std::endl;
            }
        }
    }
};
#endif
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>