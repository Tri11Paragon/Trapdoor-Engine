


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/game_level.cpp</title>
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
#include &quot;game_level.h&quot;

#include &lt;fstream&gt;
#include &lt;sstream&gt;


void GameLevel::Load(const char *file, unsigned int levelWidth, unsigned int levelHeight)
{
    // clear old data
    this-&gt;Bricks.clear();
    // load from file
    unsigned int tileCode;
    GameLevel level;
    std::string line;
    std::ifstream fstream(file);
    std::vector&lt;std::vector&lt;unsigned int&gt;&gt; tileData;
    if (fstream)
    {
        while (std::getline(fstream, line)) // read each line from level file
        {
            std::istringstream sstream(line);
            std::vector&lt;unsigned int&gt; row;
            while (sstream &gt;&gt; tileCode) // read each word separated by spaces
                row.push_back(tileCode);
            tileData.push_back(row);
        }
        if (tileData.size() &gt; 0)
            this-&gt;init(tileData, levelWidth, levelHeight);
    }
}

void GameLevel::Draw(SpriteRenderer &amp;renderer)
{
    for (GameObject &amp;tile : this-&gt;Bricks)
        if (!tile.Destroyed)
            tile.Draw(renderer);
}

bool GameLevel::IsCompleted()
{
    for (GameObject &amp;tile : this-&gt;Bricks)
        if (!tile.IsSolid &amp;&amp; !tile.Destroyed)
            return false;
    return true;
}

void GameLevel::init(std::vector&lt;std::vector&lt;unsigned int&gt;&gt; tileData, unsigned int levelWidth, unsigned int levelHeight)
{
    // calculate dimensions
    unsigned int height = tileData.size();
    unsigned int width = tileData[0].size(); // note we can index vector at [0] since this function is only called if height &gt; 0
    float unit_width = levelWidth / static_cast&lt;float&gt;(width), unit_height = levelHeight / height; 
    // initialize level tiles based on tileData		
    for (unsigned int y = 0; y &lt; height; ++y)
    {
        for (unsigned int x = 0; x &lt; width; ++x)
        {
            // check block type from level data (2D level array)
            if (tileData[y][x] == 1) // solid
            {
                glm::vec2 pos(unit_width * x, unit_height * y);
                glm::vec2 size(unit_width, unit_height);
                GameObject obj(pos, size, ResourceManager::GetTexture(&quot;block_solid&quot;), glm::vec3(0.8f, 0.8f, 0.7f));
                obj.IsSolid = true;
                this-&gt;Bricks.push_back(obj);
            }
            else if (tileData[y][x] &gt; 1)	// non-solid; now determine its color based on level data
            {
                glm::vec3 color = glm::vec3(1.0f); // original: white
                if (tileData[y][x] == 2)
                    color = glm::vec3(0.2f, 0.6f, 1.0f);
                else if (tileData[y][x] == 3)
                    color = glm::vec3(0.0f, 0.7f, 0.0f);
                else if (tileData[y][x] == 4)
                    color = glm::vec3(0.8f, 0.8f, 0.4f);
                else if (tileData[y][x] == 5)
                    color = glm::vec3(1.0f, 0.5f, 0.0f);

                glm::vec2 pos(unit_width * x, unit_height * y);
                glm::vec2 size(unit_width, unit_height);
                this-&gt;Bricks.push_back(GameObject(pos, size, ResourceManager::GetTexture(&quot;block&quot;), color));
            }
        }
    }
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>