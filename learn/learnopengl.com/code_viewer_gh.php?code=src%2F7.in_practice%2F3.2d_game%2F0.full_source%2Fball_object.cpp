


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/ball_object.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">/******************************************************************
** This code is part of Breakout.
**
** Breakout is free software: you can redistribute it and/or modify
** it under the terms of the CC BY 4.0 license as published by
** Creative Commons, either version 4 of the License, or (at your
** option) any later version.
******************************************************************/
#include &quot;ball_object.h&quot;


BallObject::BallObject() 
    : GameObject(), Radius(12.5f), Stuck(true), Sticky(false), PassThrough(false)  { }

BallObject::BallObject(glm::vec2 pos, float radius, glm::vec2 velocity, Texture2D sprite)
    : GameObject(pos, glm::vec2(radius * 2.0f, radius * 2.0f), sprite, glm::vec3(1.0f), velocity), Radius(radius), Stuck(true), Sticky(false), PassThrough(false) { }

glm::vec2 BallObject::Move(float dt, unsigned int window_width)
{
    // if not stuck to player board
    if (!this-&gt;Stuck)
    {
        // move the ball
        this-&gt;Position += this-&gt;Velocity * dt;
        // then check if outside window bounds and if so, reverse velocity and restore at correct position
        if (this-&gt;Position.x &lt;= 0.0f)
        {
            this-&gt;Velocity.x = -this-&gt;Velocity.x;
            this-&gt;Position.x = 0.0f;
        }
        else if (this-&gt;Position.x + this-&gt;Size.x &gt;= window_width)
        {
            this-&gt;Velocity.x = -this-&gt;Velocity.x;
            this-&gt;Position.x = window_width - this-&gt;Size.x;
        }
        if (this-&gt;Position.y &lt;= 0.0f)
        {
            this-&gt;Velocity.y = -this-&gt;Velocity.y;
            this-&gt;Position.y = 0.0f;
        }
    }
    return this-&gt;Position;
}

// resets the ball to initial Stuck Position (if ball is outside window bounds)
void BallObject::Reset(glm::vec2 position, glm::vec2 velocity)
{
    this-&gt;Position = position;
    this-&gt;Velocity = velocity;
    this-&gt;Stuck = true;
    this-&gt;Sticky = false;
    this-&gt;PassThrough = false;

}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>