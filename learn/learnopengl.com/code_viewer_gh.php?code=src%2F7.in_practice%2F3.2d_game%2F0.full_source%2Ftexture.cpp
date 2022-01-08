


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/texture.cpp</title>
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

#include &quot;texture.h&quot;


Texture2D::Texture2D()
    : Width(0), Height(0), Internal_Format(GL_RGB), Image_Format(GL_RGB), Wrap_S(GL_REPEAT), Wrap_T(GL_REPEAT), Filter_Min(GL_LINEAR), Filter_Max(GL_LINEAR)
{
   <function id='50'> glGenTextures(</function>1, &amp;this-&gt;ID);
}

void Texture2D::Generate(unsigned int width, unsigned int height, unsigned char* data)
{
    this-&gt;Width = width;
    this-&gt;Height = height;
    // create Texture
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, this-&gt;ID);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, this-&gt;Internal_Format, width, height, 0, this-&gt;Image_Format, GL_UNSIGNED_BYTE, data);
    // set Texture wrap and filter modes
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, this-&gt;Wrap_S);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, this-&gt;Wrap_T);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this-&gt;Filter_Min);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this-&gt;Filter_Max);
    // unbind texture
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, 0);
}

void Texture2D::Bind() const
{
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, this-&gt;ID);
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>