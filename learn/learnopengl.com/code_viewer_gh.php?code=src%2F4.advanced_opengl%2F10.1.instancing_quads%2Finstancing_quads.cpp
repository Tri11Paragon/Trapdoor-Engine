


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/4.advanced_opengl/10.1.instancing_quads/instancing_quads.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#include &lt;glad/glad.h&gt;
#include &lt;GLFW/glfw3.h&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

int main()
{
    // glfw: initialize and configure
    // ------------------------------
   <function id='17'> glfwInit(</function>);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MAJOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MINOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

#ifdef __APPLE__
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif

    // glfw window creation
    // --------------------
    GLFWwindow* window =<function id='20'> glfwCreateWindow(</function>SCR_WIDTH, SCR_HEIGHT, &quot;LearnOpenGL&quot;, NULL, NULL);
    if (window == NULL)
    {
        std::cout &lt;&lt; &quot;Failed to create GLFW window&quot; &lt;&lt; std::endl;
       <function id='25'> glfwTerminate(</function>);
        return -1;
    }
   <function id='19'> glfwMakeContextCurrent(</function>window);

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }

    // configure global opengl state
    // -----------------------------
   <function id='60'> glEnable(</function>GL_DEPTH_TEST);

    // build and compile shaders
    // -------------------------
    Shader shader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.1.instancing_quads/10.1.instancing.vs' target='_blank'>10.1.instancing.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.1.instancing_quads/10.1.instancing.fs' target='_blank'>10.1.instancing.fs</a>");

    // generate a list of 100 quad locations/translation-vectors
    // ---------------------------------------------------------
    glm::vec2 translations[100];
    int index = 0;
    float offset = 0.1f;
    for (int y = -10; y &lt; 10; y += 2)
    {
        for (int x = -10; x &lt; 10; x += 2)
        {
            glm::vec2 translation;
            translation.x = (float)x / 10.0f + offset;
            translation.y = (float)y / 10.0f + offset;
            translations[index++] = translation;
        }
    }

    // store instance data in an array buffer
    // --------------------------------------
    unsigned int instanceVBO;
   <function id='12'> glGenBuffers(</function>1, &amp;instanceVBO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, instanceVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(glm::vec2) * 100, &amp;translations[0], GL_STATIC_DRAW);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float quadVertices[] = {
        // positions     // colors
        -0.05f,  0.05f,  1.0f, 0.0f, 0.0f,
         0.05f, -0.05f,  0.0f, 1.0f, 0.0f,
        -0.05f, -0.05f,  0.0f, 0.0f, 1.0f,

        -0.05f,  0.05f,  1.0f, 0.0f, 0.0f,
         0.05f, -0.05f,  0.0f, 1.0f, 0.0f,
         0.05f,  0.05f,  0.0f, 1.0f, 1.0f
    };
    unsigned int quadVAO, quadVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;quadVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;quadVBO);
   <function id='27'> glBindVertexArray(</function>quadVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, quadVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(quadVertices), quadVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(2 * sizeof(float)));
    // also set instance data
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, instanceVBO); // this attribute comes from a different vertex buffer
   <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(float), (void*)0);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
   <function id='100'> glVertexAttribDivisor(</function>2, 1); // tell OpenGL this is an instanced vertex attribute.


    // render loop
    // -----------
    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // draw 100 instanced quads
        shader.use();
       <function id='27'> glBindVertexArray(</function>quadVAO);
       <function id='98'><function id='1'> glDrawArraysI</function>nstanced(</function>GL_TRIANGLES, 0, 6, 100); // 100 triangles of 6 vertices each
       <function id='27'> glBindVertexArray(</function>0);

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;quadVAO);
    glDeleteBuffers(1, &amp;quadVBO);

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// glfw: whenever the window size changed (by OS or user resize) this callback function executes
// ---------------------------------------------------------------------------------------------
void framebuffer_size_callback(GLFWwindow* window, int width, int height)
{
    // make sure the viewport matches the new window dimensions; note that width and 
    // height will be significantly larger than specified on retina displays.
   <function id='22'> glViewport(</function>0, 0, width, height);
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>