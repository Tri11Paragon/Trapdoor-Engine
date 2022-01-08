


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/progress/4.game.cpp</title>
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
#include &quot;game.h&quot;
#include &quot;resource_manager.h&quot;
#include &quot;sprite_renderer.h&quot;
#include &quot;game_object.h&quot;

// Game-related State data
SpriteRenderer  *Renderer;
GameObject      *Player;

Game::Game(unsigned int width, unsigned int height) 
    : State(GAME_ACTIVE), Keys(), Width(width), Height(height)
{ 

}

Game::~Game()
{
    delete Renderer;
    delete Player;
}

void Game::Init()
{
    // load shaders
    ResourceManager::LoadShader(&quot;shaders/sprite.vs&quot;, &quot;shaders/sprite.frag&quot;, nullptr, &quot;sprite&quot;);
    // configure shaders
    glm::mat4 projection =<function id='59'> glm::ortho(</function>0.0f, static_cast&lt;float&gt;(this-&gt;Width), 
        static_cast&lt;float&gt;(this-&gt;Height), 0.0f, -1.0f, 1.0f);
    ResourceManager::GetShader(&quot;sprite&quot;).Use().SetInteger(&quot;image&quot;, 0);
    ResourceManager::GetShader(&quot;sprite&quot;).SetMatrix4(&quot;projection&quot;, projection);
    // set render-specific controls
    Renderer = new SpriteRenderer(ResourceManager::GetShader(&quot;sprite&quot;));
    // load textures
    ResourceManager::LoadTexture(&quot;textures/background.jpg&quot;, false, &quot;background&quot;);
    ResourceManager::LoadTexture(&quot;textures/awesomeface.png&quot;, true, &quot;face&quot;);
    ResourceManager::LoadTexture(&quot;textures/block.png&quot;, false, &quot;block&quot;);
    ResourceManager::LoadTexture(&quot;textures/block_solid.png&quot;, false, &quot;block_solid&quot;);
    ResourceManager::LoadTexture(&quot;textures/paddle.png&quot;, true, &quot;paddle&quot;);
    // load levels
    GameLevel one; one.Load(&quot;levels/one.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    GameLevel two; two.Load(&quot;levels/two.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    GameLevel three; three.Load(&quot;levels/three.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    GameLevel four; four.Load(&quot;levels/four.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    this-&gt;Levels.push_back(one);
    this-&gt;Levels.push_back(two);
    this-&gt;Levels.push_back(three);
    this-&gt;Levels.push_back(four);
    this-&gt;Level = 0;
    // configure game objects
    glm::vec2 playerPos = glm::vec2(this-&gt;Width / 2.0f - PLAYER_SIZE.x / 2.0f, this-&gt;Height - PLAYER_SIZE.y);
    Player = new GameObject(playerPos, PLAYER_SIZE, ResourceManager::GetTexture(&quot;paddle&quot;));
}

void Game::Update(float dt)
{
    
}

void Game::ProcessInput(float dt)
{
    if (this-&gt;State == GAME_ACTIVE)
    {
        float velocity = PLAYER_VELOCITY * dt;
        // move playerboard
        if (this-&gt;Keys[GLFW_KEY_A])
        {
            if (Player-&gt;Position.x &gt;= 0.0f)
                Player-&gt;Position.x -= velocity;
        }
        if (this-&gt;Keys[GLFW_KEY_D])
        {
            if (Player-&gt;Position.x &lt;= this-&gt;Width - Player-&gt;Size.x)
                Player-&gt;Position.x += velocity;
        }
    }
}

void Game::Render()
{
    if(this-&gt;State == GAME_ACTIVE)
    {
        // draw background
        Renderer-&gt;DrawSprite(ResourceManager::GetTexture(&quot;background&quot;), glm::vec2(0.0f, 0.0f), glm::vec2(this-&gt;Width, this-&gt;Height), 0.0f);
        // draw level
        this-&gt;Levels[this-&gt;Level].Draw(*Renderer);
        // draw player
        Player-&gt;Draw(*Renderer);
    }
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>