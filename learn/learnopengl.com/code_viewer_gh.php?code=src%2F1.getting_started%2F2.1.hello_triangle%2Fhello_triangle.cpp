


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/1.getting_started/2.1.hello_triangle/hello_triangle.cpp</title>
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
const char *fragmentShaderSource = &quot;#version 330 core\n&quot;
    &quot;out vec4 FragColor;\n&quot;
    &quot;void main()\n&quot;
    &quot;{\n&quot;
    &quot;   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n&quot;
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
    // vertex shader
    unsigned int vertexShader =<function id='37'> glCreateShader(</function>GL_VERTEX_SHADER);
   <function id='42'> glShaderSource(</function>vertexShader, 1, &amp;vertexShaderSource, NULL);
   <function id='38'> glCompileShader(</function>vertexShader);
    // check for shader compile errors
    int success;
    char infoLog[512];
   <function id='39'> glGetShaderiv(</function>vertexShader, GL_COMPILE_STATUS, &amp;success);
    if (!success)
    {
       <function id='40'> glGetShaderInfoLog(</function>vertexShader, 512, NULL, infoLog);
        std::cout &lt;&lt; &quot;ERROR::SHADER::VERTEX::COMPILATION_FAILED\n&quot; &lt;&lt; infoLog &lt;&lt; std::endl;
    }
    // fragment shader
    unsigned int fragmentShader =<function id='37'> glCreateShader(</function>GL_FRAGMENT_SHADER);
   <function id='42'> glShaderSource(</function>fragmentShader, 1, &amp;fragmentShaderSource, NULL);
   <function id='38'> glCompileShader(</function>fragmentShader);
    // check for shader compile errors
   <function id='39'> glGetShaderiv(</function>fragmentShader, GL_COMPILE_STATUS, &amp;success);
    if (!success)
    {
       <function id='40'> glGetShaderInfoLog(</function>fragmentShader, 512, NULL, infoLog);
        std::cout &lt;&lt; &quot;ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n&quot; &lt;&lt; infoLog &lt;&lt; std::endl;
    }
    // link shaders
    unsigned int shaderProgram =<function id='36'> glCreateProgram(</function>);
   <function id='34'> glAttachShader(</function>shaderProgram, vertexShader);
   <function id='34'> glAttachShader(</function>shaderProgram, fragmentShader);
   <function id='35'> glLinkProgram(</function>shaderProgram);
    // check for linking errors
   <function id='41'> glGetProgramiv(</function>shaderProgram, GL_LINK_STATUS, &amp;success);
    if (!success) {
        glGetProgramInfoLog(shaderProgram, 512, NULL, infoLog);
        std::cout &lt;&lt; &quot;ERROR::SHADER::PROGRAM::LINKING_FAILED\n&quot; &lt;&lt; infoLog &lt;&lt; std::endl;
    }
   <function id='46'> glDeleteShader(</function>vertexShader);
   <function id='46'> glDeleteShader(</function>fragmentShader);

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float vertices[] = {
        -0.5f, -0.5f, 0.0f, // left  
         0.5f, -0.5f, 0.0f, // right 
         0.0f,  0.5f, 0.0f  // top   
    }; 

    unsigned int VBO, VAO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);
    // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
   <function id='27'> glBindVertexArray(</function>VAO);

   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);

    // note that this is allowed, the call to<function id='30'> glVertexAttribPointer </function>registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0); 

    // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
    // VAOs requires a call to<function id='27'> glBindVertexArray </function>anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
   <function id='27'> glBindVertexArray(</function>0); 


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

        // draw our first triangle
       <function id='28'> glUseProgram(</function>shaderProgram);
       <function id='27'> glBindVertexArray(</function>VAO); // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 3);
        //<function id='27'> glBindVertexArray(</function>0); // no need to unbind it every time 
 
        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;VAO);
    glDeleteBuffers(1, &amp;VBO);
    glDeleteProgram(shaderProgram);

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