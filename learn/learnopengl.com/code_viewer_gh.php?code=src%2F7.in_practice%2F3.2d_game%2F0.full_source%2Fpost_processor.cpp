


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/post_processor.cpp</title>
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
#include &quot;post_processor.h&quot;

#include &lt;iostream&gt;

PostProcessor::PostProcessor(Shader shader, unsigned int width, unsigned int height) 
    : PostProcessingShader(shader), Texture(), Width(width), Height(height), Confuse(false), Chaos(false), Shake(false)
{
    // initialize renderbuffer/framebuffer object
   <function id='76'> glGenFramebuffers(</function>1, &amp;this-&gt;MSFBO);
   <function id='76'> glGenFramebuffers(</function>1, &amp;this-&gt;FBO);
   <function id='82'> glGenRenderbuffers(</function>1, &amp;this-&gt;RBO);
    // initialize renderbuffer storage with a multisampled color buffer (don't need a depth/stencil buffer)
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, this-&gt;MSFBO);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, this-&gt;RBO);
   <function id='102'><function id='88'> glRenderbufferStorageM</function>ultisample(</function>GL_RENDERBUFFER, 4, GL_RGB, width, height); // allocate storage for render buffer object
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, this-&gt;RBO); // attach MS render buffer object to framebuffer
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;ERROR::POSTPROCESSOR: Failed to initialize MSFBO&quot; &lt;&lt; std::endl;
    // also initialize the FBO/texture to blit multisampled color-buffer to; used for shader operations (for postprocessing effects)
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, this-&gt;FBO);
    this-&gt;Texture.Generate(width, height, NULL);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this-&gt;Texture.ID, 0); // attach texture to framebuffer as its color attachment
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;ERROR::POSTPROCESSOR: Failed to initialize FBO&quot; &lt;&lt; std::endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);
    // initialize render data and uniforms
    this-&gt;initRenderData();
    this-&gt;PostProcessingShader.SetInteger(&quot;scene&quot;, 0, true);
    float offset = 1.0f / 300.0f;
    float offsets[9][2] = {
        { -offset,  offset  },  // top-left
        {  0.0f,    offset  },  // top-center
        {  offset,  offset  },  // top-right
        { -offset,  0.0f    },  // center-left
        {  0.0f,    0.0f    },  // center-center
        {  offset,  0.0f    },  // center - right
        { -offset, -offset  },  // bottom-left
        {  0.0f,   -offset  },  // bottom-center
        {  offset, -offset  }   // bottom-right    
    };
   <function id='44'> glUniform2</function>fv<function id='45'>(glGetUniformLocation(</function>this-&gt;PostProcessingShader.ID, &quot;offsets&quot;), 9, (float*)offsets);
    int edge_kernel[9] = {
        -1, -1, -1,
        -1,  8, -1,
        -1, -1, -1
    };
   <function id='44'> glUniform1</function>iv<function id='45'>(glGetUniformLocation(</function>this-&gt;PostProcessingShader.ID, &quot;edge_kernel&quot;), 9, edge_kernel);
    float blur_kernel[9] = {
        1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f,
        2.0f / 16.0f, 4.0f / 16.0f, 2.0f / 16.0f,
        1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f
    };
   <function id='44'> glUniform1</function>fv<function id='45'>(glGetUniformLocation(</function>this-&gt;PostProcessingShader.ID, &quot;blur_kernel&quot;), 9, blur_kernel);    
}

void PostProcessor::BeginRender()
{
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, this-&gt;MSFBO);
   <function id='13'><function id='10'> glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 1.0f);
   <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);
}
void PostProcessor::EndRender()
{
    // now resolve multisampled color-buffer into intermediate FBO to store to texture
   <function id='77'> glBindFramebuffer(</function>GL_READ_FRAMEBUFFER, this-&gt;MSFBO);
   <function id='77'> glBindFramebuffer(</function>GL_DRAW_FRAMEBUFFER, this-&gt;FBO);
   <function id='103'> glBlitFramebuffer(</function>0, 0, this-&gt;Width, this-&gt;Height, 0, 0, this-&gt;Width, this-&gt;Height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0); // binds both READ and WRITE framebuffer to default framebuffer
}

void PostProcessor::Render(float time)
{
    // set uniforms/options
    this-&gt;PostProcessingShader.Use();
    this-&gt;PostProcessingShader.SetFloat(&quot;time&quot;, time);
    this-&gt;PostProcessingShader.SetInteger(&quot;confuse&quot;, this-&gt;Confuse);
    this-&gt;PostProcessingShader.SetInteger(&quot;chaos&quot;, this-&gt;Chaos);
    this-&gt;PostProcessingShader.SetInteger(&quot;shake&quot;, this-&gt;Shake);
    // render textured quad
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
    this-&gt;Texture.Bind();	
   <function id='27'> glBindVertexArray(</function>this-&gt;VAO);
   <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
   <function id='27'> glBindVertexArray(</function>0);
}

void PostProcessor::initRenderData()
{
    // configure VAO/VBO
    unsigned int VBO;
    float vertices[] = {
        // pos        // tex
        -1.0f, -1.0f, 0.0f, 0.0f,
         1.0f,  1.0f, 1.0f, 1.0f,
        -1.0f,  1.0f, 0.0f, 1.0f,

        -1.0f, -1.0f, 0.0f, 0.0f,
         1.0f, -1.0f, 1.0f, 0.0f,
         1.0f,  1.0f, 1.0f, 1.0f
    };
   <function id='33'> glGenVertexArrays(</function>1, &amp;this-&gt;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);

   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

   <function id='27'> glBindVertexArray(</function>this-&gt;VAO);
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