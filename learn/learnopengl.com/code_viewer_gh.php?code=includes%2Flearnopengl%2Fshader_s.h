


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/shader_s.h</title>
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
    Shader(const char* vertexPath, const char* fragmentPath)
    {
        // 1. retrieve the vertex/fragment source code from filePath
        std::string vertexCode;
        std::string fragmentCode;
        std::ifstream vShaderFile;
        std::ifstream fShaderFile;
        // ensure ifstream objects can throw exceptions:
        vShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
        fShaderFile.exceptions (std::ifstream::failbit | std::ifstream::badbit);
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
            vertexCode   = vShaderStream.str();
            fragmentCode = fShaderStream.str();
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
        // shader Program
        ID =<function id='36'> glCreateProgram(</function>);
       <function id='34'> glAttachShader(</function>ID, vertex);
       <function id='34'> glAttachShader(</function>ID, fragment);
       <function id='35'> glLinkProgram(</function>ID);
        checkCompileErrors(ID, &quot;PROGRAM&quot;);
        // delete the shaders as they're linked into our program now and no longer necessary
       <function id='46'> glDeleteShader(</function>vertex);
       <function id='46'> glDeleteShader(</function>fragment);
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

private:
    // utility function for checking shader compilation/linking errors.
    // ------------------------------------------------------------------------
    void checkCompileErrors(unsigned int shader, std::string type)
    {
        int success;
        char infoLog[1024];
        if (type != &quot;PROGRAM&quot;)
        {
           <function id='39'> glGetShaderiv(</function>shader, GL_COMPILE_STATUS, &amp;success);
            if (!success)
            {
               <function id='40'> glGetShaderInfoLog(</function>shader, 1024, NULL, infoLog);
                std::cout &lt;&lt; &quot;ERROR::SHADER_COMPILATION_ERROR of type: &quot; &lt;&lt; type &lt;&lt; &quot;\n&quot; &lt;&lt; infoLog &lt;&lt; &quot;\n -- --------------------------------------------------- -- &quot; &lt;&lt; std::endl;
            }
        }
        else
        {
           <function id='41'> glGetProgramiv(</function>shader, GL_LINK_STATUS, &amp;success);
            if (!success)
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