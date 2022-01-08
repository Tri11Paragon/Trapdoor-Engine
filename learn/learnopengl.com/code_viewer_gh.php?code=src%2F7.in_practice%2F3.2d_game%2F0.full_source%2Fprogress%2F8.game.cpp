


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/progress/8.game.cpp</title>
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
#include &lt;algorithm&gt;

#include &quot;game.h&quot;
#include &quot;resource_manager.h&quot;
#include &quot;sprite_renderer.h&quot;
#include &quot;game_object.h&quot;
#include &quot;ball_object.h&quot;
#include &quot;particle_generator.h&quot;
#include &quot;post_processor.h&quot;

// Game-related State data
SpriteRenderer    *Renderer;
GameObject        *Player;
BallObject        *Ball;
ParticleGenerator *Particles;
PostProcessor     *Effects;

float ShakeTime = 0.0f;

Game::Game(unsigned int width, unsigned int height) 
    : State(GAME_ACTIVE), Keys(), Width(width), Height(height)
{ 

}

Game::~Game()
{
    delete Renderer;
    delete Player;
    delete Ball;
    delete Particles;
    delete Effects;
}

void Game::Init()
{
    // load shaders
    ResourceManager::LoadShader(&quot;shaders/sprite.vs&quot;, &quot;shaders/sprite.frag&quot;, nullptr, &quot;sprite&quot;);
    ResourceManager::LoadShader(&quot;shaders/particle.vs&quot;, &quot;shaders/particle.frag&quot;, nullptr, &quot;particle&quot;);
    ResourceManager::LoadShader(&quot;shaders/post_processing.vs&quot;, &quot;shaders/post_processing.frag&quot;, nullptr, &quot;postprocessing&quot;);
    // configure shaders
    glm::mat4 projection =<function id='59'> glm::ortho(</function>0.0f, static_cast&lt;float&gt;(this-&gt;Width), 
        static_cast&lt;float&gt;(this-&gt;Height), 0.0f, -1.0f, 1.0f);
    ResourceManager::GetShader(&quot;sprite&quot;).Use().SetInteger(&quot;image&quot;, 0);
    ResourceManager::GetShader(&quot;sprite&quot;).SetMatrix4(&quot;projection&quot;, projection);
    ResourceManager::GetShader(&quot;particle&quot;).Use().SetInteger(&quot;sprite&quot;, 0);
    ResourceManager::GetShader(&quot;particle&quot;).SetMatrix4(&quot;projection&quot;, projection);    
    // load textures
    ResourceManager::LoadTexture(&quot;textures/background.jpg&quot;, false, &quot;background&quot;);
    ResourceManager::LoadTexture(&quot;textures/awesomeface.png&quot;, true, &quot;face&quot;);
    ResourceManager::LoadTexture(&quot;textures/block.png&quot;, false, &quot;block&quot;);
    ResourceManager::LoadTexture(&quot;textures/block_solid.png&quot;, false, &quot;block_solid&quot;);
    ResourceManager::LoadTexture(&quot;textures/paddle.png&quot;, true, &quot;paddle&quot;);
    ResourceManager::LoadTexture(&quot;textures/particle.png&quot;, true, &quot;particle&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_speed.png&quot;, true, &quot;powerup_speed&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_sticky.png&quot;, true, &quot;powerup_sticky&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_increase.png&quot;, true, &quot;powerup_increase&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_confuse.png&quot;, true, &quot;powerup_confuse&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_chaos.png&quot;, true, &quot;powerup_chaos&quot;);
    ResourceManager::LoadTexture(&quot;textures/powerup_passthrough.png&quot;, true, &quot;powerup_passthrough&quot;);
    // set render-specific controls
    Renderer = new SpriteRenderer(ResourceManager::GetShader(&quot;sprite&quot;));
    Particles = new ParticleGenerator(ResourceManager::GetShader(&quot;particle&quot;), ResourceManager::GetTexture(&quot;particle&quot;), 500);
    Effects = new PostProcessor(ResourceManager::GetShader(&quot;postprocessing&quot;), this-&gt;Width, this-&gt;Height);
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
    glm::vec2 ballPos = playerPos + glm::vec2(PLAYER_SIZE.x / 2.0f - BALL_RADIUS, -BALL_RADIUS * 2.0f);
    Ball = new BallObject(ballPos, BALL_RADIUS, INITIAL_BALL_VELOCITY, ResourceManager::GetTexture(&quot;face&quot;));
}

void Game::Update(float dt)
{
    // update objects
    Ball-&gt;Move(dt, this-&gt;Width);
     // check for collisions
    this-&gt;DoCollisions();
    // update particles
    Particles-&gt;Update(dt, *Ball, 2, glm::vec2(Ball-&gt;Radius / 2.0f));
    // update PowerUps
    this-&gt;UpdatePowerUps(dt);
    // reduce shake time
    if (ShakeTime &gt; 0.0f)
    {
        ShakeTime -= dt;
        if (ShakeTime &lt;= 0.0f)
            Effects-&gt;Shake = false;
    }
    // check loss condition
    if (Ball-&gt;Position.y &gt;= this-&gt;Height) // did ball reach bottom edge?
    {
        this-&gt;ResetLevel();
        this-&gt;ResetPlayer();
    }
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
            {
                Player-&gt;Position.x -= velocity;
                if (Ball-&gt;Stuck)
                    Ball-&gt;Position.x -= velocity;
            }
        }
        if (this-&gt;Keys[GLFW_KEY_D])
        {
            if (Player-&gt;Position.x &lt;= this-&gt;Width - Player-&gt;Size.x)
            {
                Player-&gt;Position.x += velocity;
                if (Ball-&gt;Stuck)
                    Ball-&gt;Position.x += velocity;
            }
        }
        if (this-&gt;Keys[GLFW_KEY_SPACE])
            Ball-&gt;Stuck = false;
    }
}

void Game::Render()
{
    if(this-&gt;State == GAME_ACTIVE)
    {
        // begin rendering to postprocessing framebuffer
        Effects-&gt;BeginRender();
            // draw background
            Renderer-&gt;DrawSprite(ResourceManager::GetTexture(&quot;background&quot;), glm::vec2(0.0f, 0.0f), glm::vec2(this-&gt;Width, this-&gt;Height), 0.0f);
            // draw level
            this-&gt;Levels[this-&gt;Level].Draw(*Renderer);
            // draw player
            Player-&gt;Draw(*Renderer);
            // draw PowerUps
            for (PowerUp &amp;powerUp : this-&gt;PowerUps)
                if (!powerUp.Destroyed)
                    powerUp.Draw(*Renderer);            
            // draw particles	
            Particles-&gt;Draw();
            // draw ball
            Ball-&gt;Draw(*Renderer);            
        // end rendering to postprocessing framebuffer
        Effects-&gt;EndRender();
        // render postprocessing quad
        Effects-&gt;Render<function id='47'>(glfwGetTime(</function>));
    }
}


void Game::ResetLevel()
{
    if (this-&gt;Level == 0)
        this-&gt;Levels[0].Load(&quot;levels/one.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    else if (this-&gt;Level == 1)
        this-&gt;Levels[1].Load(&quot;levels/two.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    else if (this-&gt;Level == 2)
        this-&gt;Levels[2].Load(&quot;levels/three.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
    else if (this-&gt;Level == 3)
        this-&gt;Levels[3].Load(&quot;levels/four.lvl&quot;, this-&gt;Width, this-&gt;Height / 2);
}

void Game::ResetPlayer()
{
    // reset player/ball stats
    Player-&gt;Size = PLAYER_SIZE;
    Player-&gt;Position = glm::vec2(this-&gt;Width / 2.0f - PLAYER_SIZE.x / 2.0f, this-&gt;Height - PLAYER_SIZE.y);
    Ball-&gt;Reset(Player-&gt;Position + glm::vec2(PLAYER_SIZE.x / 2.0f - BALL_RADIUS, -(BALL_RADIUS * 2.0f)), INITIAL_BALL_VELOCITY);
    // also disable all active powerups
    Effects-&gt;Chaos = Effects-&gt;Confuse = false;
    Ball-&gt;PassThrough = Ball-&gt;Sticky = false;
    Player-&gt;Color = glm::vec3(1.0f);
    Ball-&gt;Color = glm::vec3(1.0f);
}

// powerups
bool IsOtherPowerUpActive(std::vector&lt;PowerUp&gt; &amp;powerUps, std::string type);

void Game::UpdatePowerUps(float dt)
{
    for (PowerUp &amp;powerUp : this-&gt;PowerUps)
    {
        powerUp.Position += powerUp.Velocity * dt;
        if (powerUp.Activated)
        {
            powerUp.Duration -= dt;

            if (powerUp.Duration &lt;= 0.0f)
            {
                // remove powerup from list (will later be removed)
                powerUp.Activated = false;
                // deactivate effects
                if (powerUp.Type == &quot;sticky&quot;)
                {
                    if (!IsOtherPowerUpActive(this-&gt;PowerUps, &quot;sticky&quot;))
                    {	// only reset if no other PowerUp of type sticky is active
                        Ball-&gt;Sticky = false;
                        Player-&gt;Color = glm::vec3(1.0f);
                    }
                }
                else if (powerUp.Type == &quot;pass-through&quot;)
                {
                    if (!IsOtherPowerUpActive(this-&gt;PowerUps, &quot;pass-through&quot;))
                    {	// only reset if no other PowerUp of type pass-through is active
                        Ball-&gt;PassThrough = false;
                        Ball-&gt;Color = glm::vec3(1.0f);
                    }
                }
                else if (powerUp.Type == &quot;confuse&quot;)
                {
                    if (!IsOtherPowerUpActive(this-&gt;PowerUps, &quot;confuse&quot;))
                    {	// only reset if no other PowerUp of type confuse is active
                        Effects-&gt;Confuse = false;
                    }
                }
                else if (powerUp.Type == &quot;chaos&quot;)
                {
                    if (!IsOtherPowerUpActive(this-&gt;PowerUps, &quot;chaos&quot;))
                    {	// only reset if no other PowerUp of type chaos is active
                        Effects-&gt;Chaos = false;
                    }
                }
            }
        }
    }
    // Remove all PowerUps from vector that are destroyed AND !activated (thus either off the map or finished)
    // Note we use a lambda expression to remove each PowerUp which is destroyed and not activated
    this-&gt;PowerUps.erase(std::remove_if(this-&gt;PowerUps.begin(), this-&gt;PowerUps.end(),
        [](const PowerUp &amp;powerUp) { return powerUp.Destroyed &amp;&amp; !powerUp.Activated; }
    ), this-&gt;PowerUps.end());
}

bool ShouldSpawn(unsigned int chance)
{
    unsigned int random = rand() % chance;
    return random == 0;
}
void Game::SpawnPowerUps(GameObject &amp;block)
{
    if (ShouldSpawn(75)) // 1 in 75 chance
        this-&gt;PowerUps.push_back(PowerUp(&quot;speed&quot;, glm::vec3(0.5f, 0.5f, 1.0f), 0.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_speed&quot;)));
    if (ShouldSpawn(75))
        this-&gt;PowerUps.push_back(PowerUp(&quot;sticky&quot;, glm::vec3(1.0f, 0.5f, 1.0f), 20.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_sticky&quot;)));
    if (ShouldSpawn(75))
        this-&gt;PowerUps.push_back(PowerUp(&quot;pass-through&quot;, glm::vec3(0.5f, 1.0f, 0.5f), 10.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_passthrough&quot;)));
    if (ShouldSpawn(75))
        this-&gt;PowerUps.push_back(PowerUp(&quot;pad-size-increase&quot;, glm::vec3(1.0f, 0.6f, 0.4), 0.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_increase&quot;)));
    if (ShouldSpawn(15)) // Negative powerups should spawn more often
        this-&gt;PowerUps.push_back(PowerUp(&quot;confuse&quot;, glm::vec3(1.0f, 0.3f, 0.3f), 15.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_confuse&quot;)));
    if (ShouldSpawn(15))
        this-&gt;PowerUps.push_back(PowerUp(&quot;chaos&quot;, glm::vec3(0.9f, 0.25f, 0.25f), 15.0f, block.Position, ResourceManager::GetTexture(&quot;powerup_chaos&quot;)));
}

void ActivatePowerUp(PowerUp &amp;powerUp)
{
    if (powerUp.Type == &quot;speed&quot;)
    {
        Ball-&gt;Velocity *= 1.2;
    }
    else if (powerUp.Type == &quot;sticky&quot;)
    {
        Ball-&gt;Sticky = true;
        Player-&gt;Color = glm::vec3(1.0f, 0.5f, 1.0f);
    }
    else if (powerUp.Type == &quot;pass-through&quot;)
    {
        Ball-&gt;PassThrough = true;
        Ball-&gt;Color = glm::vec3(1.0f, 0.5f, 0.5f);
    }
    else if (powerUp.Type == &quot;pad-size-increase&quot;)
    {
        Player-&gt;Size.x += 50;
    }
    else if (powerUp.Type == &quot;confuse&quot;)
    {
        if (!Effects-&gt;Chaos)
            Effects-&gt;Confuse = true; // only activate if chaos wasn't already active
    }
    else if (powerUp.Type == &quot;chaos&quot;)
    {
        if (!Effects-&gt;Confuse)
            Effects-&gt;Chaos = true;
    }
}

bool IsOtherPowerUpActive(std::vector&lt;PowerUp&gt; &amp;powerUps, std::string type)
{
    // Check if another PowerUp of the same type is still active
    // in which case we don't disable its effect (yet)
    for (const PowerUp &amp;powerUp : powerUps)
    {
        if (powerUp.Activated)
            if (powerUp.Type == type)
                return true;
    }
    return false;
}

// collision detection
bool CheckCollision(GameObject &amp;one, GameObject &amp;two);
Collision CheckCollision(BallObject &amp;one, GameObject &amp;two);
Direction VectorDirection(glm::vec2 closest);

void Game::DoCollisions()
{
    for (GameObject &amp;box : this-&gt;Levels[this-&gt;Level].Bricks)
    {
        if (!box.Destroyed)
        {
            Collision collision = CheckCollision(*Ball, box);
            if (std::get&lt;0&gt;(collision)) // if collision is true
            {
                // destroy block if not solid
                if (!box.IsSolid)
                {
                    box.Destroyed = true;
                    this-&gt;SpawnPowerUps(box);
                }
                else
                {   // if block is solid, enable shake effect
                    ShakeTime = 0.05f;
                    Effects-&gt;Shake = true;
                }
                // collision resolution
                Direction dir = std::get&lt;1&gt;(collision);
                glm::vec2 diff_vector = std::get&lt;2&gt;(collision);
                if (!(Ball-&gt;PassThrough &amp;&amp; !box.IsSolid)) // don't do collision resolution on non-solid bricks if pass-through is activated
                {
                    if (dir == LEFT || dir == RIGHT) // horizontal collision
                    {
                        Ball-&gt;Velocity.x = -Ball-&gt;Velocity.x; // reverse horizontal velocity
                        // relocate
                        float penetration = Ball-&gt;Radius - std::abs(diff_vector.x);
                        if (dir == LEFT)
                            Ball-&gt;Position.x += penetration; // move ball to right
                        else
                            Ball-&gt;Position.x -= penetration; // move ball to left;
                    }
                    else // vertical collision
                    {
                        Ball-&gt;Velocity.y = -Ball-&gt;Velocity.y; // reverse vertical velocity
                        // relocate
                        float penetration = Ball-&gt;Radius - std::abs(diff_vector.y);
                        if (dir == UP)
                            Ball-&gt;Position.y -= penetration; // move ball bback up
                        else
                            Ball-&gt;Position.y += penetration; // move ball back down
                    }
                }
            }
        }    
    }
    
    // also check collisions on PowerUps and if so, activate them
    for (PowerUp &amp;powerUp : this-&gt;PowerUps)
    {
        if (!powerUp.Destroyed)
        {
            // first check if powerup passed bottom edge, if so: keep as inactive and destroy
            if (powerUp.Position.y &gt;= this-&gt;Height)
                powerUp.Destroyed = true;

            if (CheckCollision(*Player, powerUp))
            {	// collided with player, now activate powerup
                ActivatePowerUp(powerUp);
                powerUp.Destroyed = true;
                powerUp.Activated = true;
            }
        }
    }
    
    // and finally check collisions for player pad (unless stuck)
    Collision result = CheckCollision(*Ball, *Player);
    if (!Ball-&gt;Stuck &amp;&amp; std::get&lt;0&gt;(result))
    {
        // check where it hit the board, and change velocity based on where it hit the board
        float centerBoard = Player-&gt;Position.x + Player-&gt;Size.x / 2.0f;
        float distance = (Ball-&gt;Position.x + Ball-&gt;Radius) - centerBoard;
        float percentage = distance / (Player-&gt;Size.x / 2.0f);
        // then move accordingly
        float strength = 2.0f;
        glm::vec2 oldVelocity = Ball-&gt;Velocity;
        Ball-&gt;Velocity.x = INITIAL_BALL_VELOCITY.x * percentage * strength; 
        //Ball-&gt;Velocity.y = -Ball-&gt;Velocity.y;
        Ball-&gt;Velocity = glm::normalize(Ball-&gt;Velocity) * glm::length(oldVelocity); // keep speed consistent over both axes (multiply by length of old velocity, so total strength is not changed)
        // fix sticky paddle
        Ball-&gt;Velocity.y = -1.0f * abs(Ball-&gt;Velocity.y);
        
        // if Sticky powerup is activated, also stick ball to paddle once new velocity vectors were calculated
        Ball-&gt;Stuck = Ball-&gt;Sticky;
    }
}

bool CheckCollision(GameObject &amp;one, GameObject &amp;two) // AABB - AABB collision
{
    // collision x-axis?
    bool collisionX = one.Position.x + one.Size.x &gt;= two.Position.x &amp;&amp;
        two.Position.x + two.Size.x &gt;= one.Position.x;
    // collision y-axis?
    bool collisionY = one.Position.y + one.Size.y &gt;= two.Position.y &amp;&amp;
        two.Position.y + two.Size.y &gt;= one.Position.y;
    // collision only if on both axes
    return collisionX &amp;&amp; collisionY;
}

Collision CheckCollision(BallObject &amp;one, GameObject &amp;two) // AABB - Circle collision
{
    // get center point circle first 
    glm::vec2 center(one.Position + one.Radius);
    // calculate AABB info (center, half-extents)
    glm::vec2 aabb_half_extents(two.Size.x / 2.0f, two.Size.y / 2.0f);
    glm::vec2 aabb_center(two.Position.x + aabb_half_extents.x, two.Position.y + aabb_half_extents.y);
    // get difference vector between both centers
    glm::vec2 difference = center - aabb_center;
    glm::vec2 clamped = glm::clamp(difference, -aabb_half_extents, aabb_half_extents);
    // now that we know the the clamped values, add this to AABB_center and we get the value of box closest to circle
    glm::vec2 closest = aabb_center + clamped;
    // now retrieve vector between center circle and closest point AABB and check if length &lt; radius
    difference = closest - center;

    if (glm::length(difference) &lt; one.Radius) // not &lt;= since in that case a collision also occurs when object one exactly touches object two, which they are at the end of each collision resolution stage.
        return std::make_tuple(true, VectorDirection(difference), difference);
    else
        return std::make_tuple(false, UP, glm::vec2(0.0f, 0.0f));
}

// calculates which direction a vector is facing (N,E,S or W)
Direction VectorDirection(glm::vec2 target)
{
    glm::vec2 compass[] = {
        glm::vec2(0.0f, 1.0f),	// up
        glm::vec2(1.0f, 0.0f),	// right
        glm::vec2(0.0f, -1.0f),	// down
        glm::vec2(-1.0f, 0.0f)	// left
    };
    float max = 0.0f;
    unsigned int best_match = -1;
    for (unsigned int i = 0; i &lt; 4; i++)
    {
        float dot_product = glm::dot(glm::normalize(target), compass[i]);
        if (dot_product &gt; max)
        {
            max = dot_product;
            best_match = i;
        }
    }
    return (Direction)best_match;
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>