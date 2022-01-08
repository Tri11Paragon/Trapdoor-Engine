


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/sprite_renderer.cpp</title>
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
#include &quot;sprite_renderer.h&quot;


SpriteRenderer::SpriteRenderer(Shader &amp;shader)
{
    this-&gt;shader = shader;
    this-&gt;initRenderData();
}

SpriteRenderer::~SpriteRenderer()
{
    glDeleteVertexArrays(1, &amp;this-&gt;quadVAO);
}

void SpriteRenderer::DrawSprite(Texture2D &amp;texture, glm::vec2 position, glm::vec2 size, float rotate, glm::vec3 color)
{
    // prepare transformations
    this-&gt;shader.Use();
    glm::mat4 model = glm::mat4(1.0f);
    model =<function id='55'> glm::translate(</function>model, glm::vec3(position, 0.0f));  // first translate (transformations are: scale happens first, then rotation, and then final translation happens; reversed order)

    model =<function id='55'> glm::translate(</function>model, glm::vec3(0.5f * size.x, 0.5f * size.y, 0.0f)); // move origin of rotation to center of quad
    model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>rotate), glm::vec3(0.0f, 0.0f, 1.0f)); // then rotate
    model =<function id='55'> glm::translate(</function>model, glm::vec3(-0.5f * size.x, -0.5f * size.y, 0.0f)); // move origin back

    model =<function id='56'> glm::scale(</function>model, glm::vec3(size, 1.0f)); // last scale

    this-&gt;shader.SetMatrix4(&quot;model&quot;, model);

    // render textured quad
    this-&gt;shader.SetVector3f(&quot;spriteColor&quot;, color);

   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
    texture.Bind();

   <function id='27'> glBindVertexArray(</function>this-&gt;quadVAO);
   <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
   <function id='27'> glBindVertexArray(</function>0);
}

void SpriteRenderer::initRenderData()
{
    // configure VAO/VBO
    unsigned int VBO;
    float vertices[] = { 
        // pos      // tex
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f, 

        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 0.0f
    };

   <function id='33'> glGenVertexArrays(</function>1, &amp;this-&gt;quadVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);

   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

   <function id='27'> glBindVertexArray(</function>this-&gt;quadVAO);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
   <function id='27'> glBindVertexArray(</function>0);
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>