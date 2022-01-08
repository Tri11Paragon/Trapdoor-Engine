


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/particle_generator.cpp</title>
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
#include &quot;particle_generator.h&quot;

ParticleGenerator::ParticleGenerator(Shader shader, Texture2D texture, unsigned int amount)
    : shader(shader), texture(texture), amount(amount)
{
    this-&gt;init();
}

void ParticleGenerator::Update(float dt, GameObject &amp;object, unsigned int newParticles, glm::vec2 offset)
{
    // add new particles 
    for (unsigned int i = 0; i &lt; newParticles; ++i)
    {
        int unusedParticle = this-&gt;firstUnusedParticle();
        this-&gt;respawnParticle(this-&gt;particles[unusedParticle], object, offset);
    }
    // update all particles
    for (unsigned int i = 0; i &lt; this-&gt;amount; ++i)
    {
        Particle &amp;p = this-&gt;particles[i];
        p.Life -= dt; // reduce life
        if (p.Life &gt; 0.0f)
        {	// particle is alive, thus update
            p.Position -= p.Velocity * dt; 
            p.Color.a -= dt * 2.5f;
        }
    }
}

// render all particles
void ParticleGenerator::Draw()
{
    // use additive blending to give it a 'glow' effect
   <function id='70'> glBlendFunc(</function>GL_SRC_ALPHA, GL_ONE);
    this-&gt;shader.Use();
    for (Particle particle : this-&gt;particles)
    {
        if (particle.Life &gt; 0.0f)
        {
            this-&gt;shader.SetVector2f(&quot;offset&quot;, particle.Position);
            this-&gt;shader.SetVector4f(&quot;color&quot;, particle.Color);
            this-&gt;texture.Bind();
           <function id='27'> glBindVertexArray(</function>this-&gt;VAO);
           <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
           <function id='27'> glBindVertexArray(</function>0);
        }
    }
    // don't forget to reset to default blending mode
   <function id='70'> glBlendFunc(</function>GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
}

void ParticleGenerator::init()
{
    // set up mesh and attribute properties
    unsigned int VBO;
    float particle_quad[] = {
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,

        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 1.0f, 0.0f
    }; 
   <function id='33'> glGenVertexArrays(</function>1, &amp;this-&gt;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);
   <function id='27'> glBindVertexArray(</function>this-&gt;VAO);
    // fill mesh buffer
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(particle_quad), particle_quad, GL_STATIC_DRAW);
    // set mesh attributes
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
   <function id='27'> glBindVertexArray(</function>0);

    // create this-&gt;amount default particle instances
    for (unsigned int i = 0; i &lt; this-&gt;amount; ++i)
        this-&gt;particles.push_back(Particle());
}

// stores the index of the last particle used (for quick access to next dead particle)
unsigned int lastUsedParticle = 0;
unsigned int ParticleGenerator::firstUnusedParticle()
{
    // first search from last used particle, this will usually return almost instantly
    for (unsigned int i = lastUsedParticle; i &lt; this-&gt;amount; ++i){
        if (this-&gt;particles[i].Life &lt;= 0.0f){
            lastUsedParticle = i;
            return i;
        }
    }
    // otherwise, do a linear search
    for (unsigned int i = 0; i &lt; lastUsedParticle; ++i){
        if (this-&gt;particles[i].Life &lt;= 0.0f){
            lastUsedParticle = i;
            return i;
        }
    }
    // all particles are taken, override the first one (note that if it repeatedly hits this case, more particles should be reserved)
    lastUsedParticle = 0;
    return 0;
}

void ParticleGenerator::respawnParticle(Particle &amp;particle, GameObject &amp;object, glm::vec2 offset)
{
    float random = ((rand() % 100) - 50) / 10.0f;
    float rColor = 0.5f + ((rand() % 100) / 100.0f);
    particle.Position = object.Position + random + offset;
    particle.Color = glm::vec4(rColor, rColor, rColor, 1.0f);
    particle.Life = 1.0f;
    particle.Velocity = object.Velocity * 0.1f;
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>