


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/text_renderer.cpp</title>
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
#include &lt;iostream&gt;

#include &lt;glm/gtc/matrix_transform.hpp&gt;
#include &lt;ft2build.h&gt;
#include FT_FREETYPE_H

#include &quot;text_renderer.h&quot;
#include &quot;resource_manager.h&quot;


TextRenderer::TextRenderer(unsigned int width, unsigned int height)
{
    // load and configure shader
    this-&gt;TextShader = ResourceManager::LoadShader("<a href='code_viewer_gh.php?code=src/7.in_practice/3.2d_game/0.full_source/text_2d.vs' target='_blank'>text_2d.vs</a>", "<a href='code_viewer_gh.php?code=src/7.in_practice/3.2d_game/0.full_source/text_2d.fs' target='_blank'>text_2d.fs</a>", nullptr, &quot;text&quot;);
    this-&gt;TextShader.SetMatrix4(&quot;projection&quot;,<function id='59'> glm::ortho(</function>0.0f, static_cast&lt;float&gt;(width), static_cast&lt;float&gt;(height), 0.0f), true);
    this-&gt;TextShader.SetInteger(&quot;text&quot;, 0);
    // configure VAO/VBO for texture quads
   <function id='33'> glGenVertexArrays(</function>1, &amp;this-&gt;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;this-&gt;VBO);
   <function id='27'> glBindVertexArray(</function>this-&gt;VAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, this-&gt;VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(float) * 6 * 4, NULL, GL_DYNAMIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(float), 0);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
   <function id='27'> glBindVertexArray(</function>0);
}

void TextRenderer::Load(std::string font, unsigned int fontSize)
{
    // first clear the previously loaded Characters
    this-&gt;Characters.clear();
    // then initialize and load the FreeType library
    FT_Library ft;    
    if (FT_Init_FreeType(&amp;ft)) // all functions return a value different than 0 whenever an error occurred
        std::cout &lt;&lt; &quot;ERROR::FREETYPE: Could not init FreeType Library&quot; &lt;&lt; std::endl;
    // load font as face
    FT_Face face;
    if (FT_New_Face(ft, font.c_str(), 0, &amp;face))
        std::cout &lt;&lt; &quot;ERROR::FREETYPE: Failed to load font&quot; &lt;&lt; std::endl;
    // set size to load glyphs as
    FT_Set_Pixel_Sizes(face, 0, fontSize);
    // disable byte-alignment restriction
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1); 
    // then for the first 128 ASCII characters, pre-load/compile their characters and store them
    for (GLubyte c = 0; c &lt; 128; c++) // lol see what I did there 
    {
        // load character glyph 
        if (FT_Load_Char(face, c, FT_LOAD_RENDER))
        {
            std::cout &lt;&lt; &quot;ERROR::FREETYTPE: Failed to load Glyph&quot; &lt;&lt; std::endl;
            continue;
        }
        // generate texture
        unsigned int texture;
       <function id='50'> glGenTextures(</function>1, &amp;texture);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture);
       <function id='52'> glTexImage2D(</function>
            GL_TEXTURE_2D,
            0,
            GL_RED,
            face-&gt;glyph-&gt;bitmap.width,
            face-&gt;glyph-&gt;bitmap.rows,
            0,
            GL_RED,
            GL_UNSIGNED_BYTE,
            face-&gt;glyph-&gt;bitmap.buffer
            );
        // set texture options
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
       
        // now store character for later use
        Character character = {
            texture,
            glm::ivec2(face-&gt;glyph-&gt;bitmap.width, face-&gt;glyph-&gt;bitmap.rows),
            glm::ivec2(face-&gt;glyph-&gt;bitmap_left, face-&gt;glyph-&gt;bitmap_top),
            face-&gt;glyph-&gt;advance.x
        };
        Characters.insert(std::pair&lt;char, Character&gt;(c, character));
    }
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, 0);
    // destroy FreeType once we're finished
    FT_Done_Face(face);
    FT_Done_FreeType(ft);
}

void TextRenderer::RenderText(std::string text, float x, float y, float scale, glm::vec3 color)
{
    // activate corresponding render state	
    this-&gt;TextShader.Use();
    this-&gt;TextShader.SetVector3f(&quot;textColor&quot;, color);
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
   <function id='27'> glBindVertexArray(</function>this-&gt;VAO);

    // iterate through all characters
    std::string::const_iterator c;
    for (c = text.begin(); c != text.end(); c++)
    {
        Character ch = Characters[*c];

        float xpos = x + ch.Bearing.x * scale;
        float ypos = y + (this-&gt;Characters['H'].Bearing.y - ch.Bearing.y) * scale;

        float w = ch.Size.x * scale;
        float h = ch.Size.y * scale;
        // update VBO for each character
        float vertices[6][4] = {
            { xpos,     ypos + h,   0.0f, 1.0f },
            { xpos + w, ypos,       1.0f, 0.0f },
            { xpos,     ypos,       0.0f, 0.0f },

            { xpos,     ypos + h,   0.0f, 1.0f },
            { xpos + w, ypos + h,   1.0f, 1.0f },
            { xpos + w, ypos,       1.0f, 0.0f }
        };
        // render glyph texture over quad
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ch.TextureID);
        // update content of VBO memory
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, this-&gt;VBO);
       <function id='90'> glBufferSubData(</function>GL_ARRAY_BUFFER, 0, sizeof(vertices), vertices); // be sure to use<function id='90'> glBufferSubData </function>and not<function id='31'> glBufferData
</function>       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
        // render quad
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
        // now advance cursors for next glyph
        x += (ch.Advance &gt;&gt; 6) * scale; // bitshift by 6 to get value in pixels (1/64th times 2^6 = 64)
    }
   <function id='27'> glBindVertexArray(</function>0);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, 0);
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>