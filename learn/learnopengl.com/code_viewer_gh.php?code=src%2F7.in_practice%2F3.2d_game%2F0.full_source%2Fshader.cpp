


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/shader.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">/*******************************************************************
** This code is part of Breakout.
**
** Breakout is free software: you can redistribute it and/or modify
** it under the terms of the CC BY 4.0 license as published by
** Creative Commons, either version 4 of the License, or (at your
** option) any later version.
******************************************************************/
#include &quot;shader.h&quot;

#include &lt;iostream&gt;

Shader &amp;Shader::Use()
{
   <function id='28'> glUseProgram(</function>this-&gt;ID);
    return *this;
}

void Shader::Compile(const char* vertexSource, const char* fragmentSource, const char* geometrySource)
{
    unsigned int sVertex, sFragment, gShader;
    // vertex Shader
    sVertex =<function id='37'> glCreateShader(</function>GL_VERTEX_SHADER);
   <function id='42'> glShaderSource(</function>sVertex, 1, &amp;vertexSource, NULL);
   <function id='38'> glCompileShader(</function>sVertex);
    checkCompileErrors(sVertex, &quot;VERTEX&quot;);
    // fragment Shader
    sFragment =<function id='37'> glCreateShader(</function>GL_FRAGMENT_SHADER);
   <function id='42'> glShaderSource(</function>sFragment, 1, &amp;fragmentSource, NULL);
   <function id='38'> glCompileShader(</function>sFragment);
    checkCompileErrors(sFragment, &quot;FRAGMENT&quot;);
    // if geometry shader source code is given, also compile geometry shader
    if (geometrySource != nullptr)
    {
        gShader =<function id='37'> glCreateShader(</function>GL_GEOMETRY_SHADER);
       <function id='42'> glShaderSource(</function>gShader, 1, &amp;geometrySource, NULL);
       <function id='38'> glCompileShader(</function>gShader);
        checkCompileErrors(gShader, &quot;GEOMETRY&quot;);
    }
    // shader program
    this-&gt;ID =<function id='36'> glCreateProgram(</function>);
   <function id='34'> glAttachShader(</function>this-&gt;ID, sVertex);
   <function id='34'> glAttachShader(</function>this-&gt;ID, sFragment);
    if (geometrySource != nullptr)
       <function id='34'> glAttachShader(</function>this-&gt;ID, gShader);
   <function id='35'> glLinkProgram(</function>this-&gt;ID);
    checkCompileErrors(this-&gt;ID, &quot;PROGRAM&quot;);
    // delete the shaders as they're linked into our program now and no longer necessary
   <function id='46'> glDeleteShader(</function>sVertex);
   <function id='46'> glDeleteShader(</function>sFragment);
    if (geometrySource != nullptr)
       <function id='46'> glDeleteShader(</function>gShader);
}

void Shader::SetFloat(const char *name, float value, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), value);
}
void Shader::SetInteger(const char *name, int value, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), value);
}
void Shader::SetVector2f(const char *name, float x, float y, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform2</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), x, y);
}
void Shader::SetVector2f(const char *name, const glm::vec2 &amp;value, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform2</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), value.x, value.y);
}
void Shader::SetVector3f(const char *name, float x, float y, float z, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), x, y, z);
}
void Shader::SetVector3f(const char *name, const glm::vec3 &amp;value, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), value.x, value.y, value.z);
}
void Shader::SetVector4f(const char *name, float x, float y, float z, float w, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform4</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), x, y, z, w);
}
void Shader::SetVector4f(const char *name, const glm::vec4 &amp;value, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniform4</function>f<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), value.x, value.y, value.z, value.w);
}
void Shader::SetMatrix4(const char *name, const glm::mat4 &amp;matrix, bool useShader)
{
    if (useShader)
        this-&gt;Use();
   <function id='44'> glUniformM</function>atrix4fv<function id='45'>(glGetUniformLocation(</function>this-&gt;ID, name), 1, false, glm::value_ptr(matrix));
}


void Shader::checkCompileErrors(unsigned int object, std::string type)
{
    int success;
    char infoLog[1024];
    if (type != &quot;PROGRAM&quot;)
    {
       <function id='39'> glGetShaderiv(</function>object, GL_COMPILE_STATUS, &amp;success);
        if (!success)
        {
           <function id='40'> glGetShaderInfoLog(</function>object, 1024, NULL, infoLog);
            std::cout &lt;&lt; &quot;| ERROR::SHADER: Compile-time error: Type: &quot; &lt;&lt; type &lt;&lt; &quot;\n&quot;
                &lt;&lt; infoLog &lt;&lt; &quot;\n -- --------------------------------------------------- -- &quot;
                &lt;&lt; std::endl;
        }
    }
    else
    {
       <function id='41'> glGetProgramiv(</function>object, GL_LINK_STATUS, &amp;success);
        if (!success)
        {
            glGetProgramInfoLog(object, 1024, NULL, infoLog);
            std::cout &lt;&lt; &quot;| ERROR::Shader: Link-time error: Type: &quot; &lt;&lt; type &lt;&lt; &quot;\n&quot;
                &lt;&lt; infoLog &lt;&lt; &quot;\n -- --------------------------------------------------- -- &quot;
                &lt;&lt; std::endl;
        }
    }
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>