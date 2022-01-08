


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/1.getting_started/2.5.hello_triangle_exercise3/hello_triangle_exercise3.cpp</title>
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

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void processInput(GLFWwindow *window);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

const char *vertexShaderSource = &quot;#version 330 core\n&quot;
    &quot;layout (location = 0) in vec3 aPos;\n&quot;
    &quot;void main()\n&quot;
    &quot;{\n&quot;
    &quot;   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n&quot;
    &quot;}\0&quot;;
const char *fragmentShader1Source = &quot;#version 330 core\n&quot;
    &quot;out vec4 FragColor;\n&quot;
    &quot;void main()\n&quot;
    &quot;{\n&quot;
    &quot;   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n&quot;
    &quot;}\n\0&quot;;
const char *fragmentShader2Source = &quot;#version 330 core\n&quot;
    &quot;out vec4 FragColor;\n&quot;
    &quot;void main()\n&quot;
    &quot;{\n&quot;
    &quot;   FragColor = vec4(1.0f, 1.0f, 0.0f, 1.0f);\n&quot;
    &quot;}\n\0&quot;;


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
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }


    // build and compile our shader program
    // ------------------------------------
    // we skipped compile log checks this time for readability (if you do encounter issues, add the compile-checks! see previous code samples)
    unsigned int vertexShader =<function id='37'> glCreateShader(</function>GL_VERTEX_SHADER);
    unsigned int fragmentShaderOrange =<function id='37'> glCreateShader(</function>GL_FRAGMENT_SHADER); // the first fragment shader that outputs the color orange
    unsigned int fragmentShaderYellow =<function id='37'> glCreateShader(</function>GL_FRAGMENT_SHADER); // the second fragment shader that outputs the color yellow
    unsigned int shaderProgramOrange =<function id='36'> glCreateProgram(</function>);
    unsigned int shaderProgramYellow =<function id='36'> glCreateProgram(</function>); // the second shader program
   <function id='42'> glShaderSource(</function>vertexShader, 1, &amp;vertexShaderSource, NULL);
   <function id='38'> glCompileShader(</function>vertexShader);
   <function id='42'> glShaderSource(</function>fragmentShaderOrange, 1, &amp;fragmentShader1Source, NULL);
   <function id='38'> glCompileShader(</function>fragmentShaderOrange);
   <function id='42'> glShaderSource(</function>fragmentShaderYellow, 1, &amp;fragmentShader2Source, NULL);
   <function id='38'> glCompileShader(</function>fragmentShaderYellow);
    // link the first program object
   <function id='34'> glAttachShader(</function>shaderProgramOrange, vertexShader);
   <function id='34'> glAttachShader(</function>shaderProgramOrange, fragmentShaderOrange);
   <function id='35'> glLinkProgram(</function>shaderProgramOrange);
    // then link the second program object using a different fragment shader (but same vertex shader)
    // this is perfectly allowed since the inputs and outputs of both the vertex and fragment shaders are equally matched.
   <function id='34'> glAttachShader(</function>shaderProgramYellow, vertexShader);
   <function id='34'> glAttachShader(</function>shaderProgramYellow, fragmentShaderYellow);
   <function id='35'> glLinkProgram(</function>shaderProgramYellow);

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float firstTriangle[] = {
        -0.9f, -0.5f, 0.0f,  // left 
        -0.0f, -0.5f, 0.0f,  // right
        -0.45f, 0.5f, 0.0f,  // top 
    };
    float secondTriangle[] = {
        0.0f, -0.5f, 0.0f,  // left
        0.9f, -0.5f, 0.0f,  // right
        0.45f, 0.5f, 0.0f   // top 
    };
    unsigned int VBOs[2], VAOs[2];
   <function id='33'> glGenVertexArrays(</function>2, VAOs); // we can also generate multiple VAOs or buffers at the same time
   <function id='12'> glGenBuffers(</function>2, VBOs);
    // first triangle setup
    // --------------------
   <function id='27'> glBindVertexArray(</function>VAOs[0]);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBOs[0]);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(firstTriangle), firstTriangle, GL_STATIC_DRAW);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);	// Vertex attributes stay the same
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
    //<function id='27'> glBindVertexArray(</function>0); // no need to unbind at all as we directly bind a different VAO the next few lines
    // second triangle setup
    // ---------------------
   <function id='27'> glBindVertexArray(</function>VAOs[1]);	// note that we bind to a different VAO now
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBOs[1]);	// and a different VBO
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(secondTriangle), secondTriangle, GL_STATIC_DRAW);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 0, (void*)0); // because the vertex data is tightly packed we can also specify 0 as the vertex attribute's stride to let OpenGL figure it out
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
    //<function id='27'> glBindVertexArray(</function>0); // not really necessary as well, but beware of calls that could affect VAOs while this one is bound (like binding element buffer objects, or enabling/disabling vertex attributes)


    // uncomment this call to draw in wireframe polygons.
    /<function id='43'>/glPolygonMode(</function>GL_FRONT_AND_BACK, GL_LINE);

    // render loop
    // -----------
    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // input
        // -----
        processInput(window);

        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.2f, 0.3f, 0.3f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);

        // now when we draw the triangle we first use the vertex and orange fragment shader from the first program
       <function id='28'> glUseProgram(</function>shaderProgramOrange);
        // draw the first triangle using the data from our first VAO
       <function id='27'> glBindVertexArray(</function>VAOs[0]);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 3);	// this call should output an orange triangle
        // then we draw the second triangle using the data from the second VAO
        // when we draw the second triangle we want to use a different shader program so we switch to the shader program with our yellow fragment shader.
       <function id='28'> glUseProgram(</function>shaderProgramYellow);
       <function id='27'> glBindVertexArray(</function>VAOs[1]);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 3);	// this call should output a yellow triangle

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(2, VAOs);
    glDeleteBuffers(2, VBOs);
    glDeleteProgram(shaderProgramOrange);
    glDeleteProgram(shaderProgramYellow);

    // glfw: terminate, clearing all previously allocated GLFW resources.
    // ------------------------------------------------------------------
   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
// ---------------------------------------------------------------------------------------------------------
void processInput(GLFWwindow *window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);
}

// glfw: whenever the window size changed (by OS or user resize) this callback function executes
// ---------------------------------------------------------------------------------------------
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