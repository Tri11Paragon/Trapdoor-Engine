


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/3.2d_game/0.full_source/progress/2.program.cpp</title>
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
#include &lt;glad/glad.h&gt;
#include &lt;GLFW/glfw3.h&gt;

#include &quot;game.h&quot;
#include &quot;resource_manager.h&quot;

#include &lt;iostream&gt;

// GLFW function declarations
void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode);

// The Width of the screen
const unsigned int SCREEN_WIDTH = 800;
// The height of the screen
const unsigned int SCREEN_HEIGHT = 600;

Game Breakout(SCREEN_WIDTH, SCREEN_HEIGHT);

int main(int argc, char *argv[])
{
   <function id='17'> glfwInit(</function>);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MAJOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MINOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
#ifdef __APPLE__
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif
   <function id='18'> glfwWindowHint(</function>GLFW_RESIZABLE, false);

    GLFWwindow* window =<function id='20'> glfwCreateWindow(</function>SCREEN_WIDTH, SCREEN_HEIGHT, &quot;Breakout&quot;, nullptr, nullptr);
   <function id='19'> glfwMakeContextCurrent(</function>window);

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }

   <function id='26'> glfwSetKeyCallback(</function>window, key_callback);
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);

    // OpenGL configuration
    // --------------------
   <function id='22'> glViewport(</function>0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
   <function id='60'> glEnable(</function>GL_BLEND);
   <function id='70'> glBlendFunc(</function>GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // initialize game
    // ---------------
    Breakout.Init();

    // deltaTime variables
    // -------------------
    float deltaTime = 0.0f;
    float lastFrame = 0.0f;

    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // calculate delta time
        // --------------------
        float currentFrame =<function id='47'> glfwGetTime(</function>);
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;
       <function id='23'> glfwPollEvents(</function>);

        // manage user input
        // -----------------
        Breakout.ProcessInput(deltaTime);

        // update game state
        // -----------------
        Breakout.Update(deltaTime);

        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);
        Breakout.Render();

       <function id='24'> glfwSwapBuffers(</function>window);
    }

    // delete all resources as loaded using the resource manager
    // ---------------------------------------------------------
    ResourceManager::Clear();

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode)
{
    // when a user presses the escape key, we set the WindowShouldClose property to true, closing the application
    if (key == GLFW_KEY_ESCAPE &amp;&amp; action == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);
    if (key &gt;= 0 &amp;&amp; key &lt; 1024)
    {
        if (action == GLFW_PRESS)
            Breakout.Keys[key] = true;
        else if (action == GLFW_RELEASE)
            Breakout.Keys[key] = false;
    }
}

void framebuffer_size_callback(GLFWwindow* window, int width, int height)
{
    // make sure the viewport matches the new window dimensions; note that width and 
    // height will be significantly larger than specified on retina displays.
   <function id='22'> glViewport(</function>0, 0, width, height);
}</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>