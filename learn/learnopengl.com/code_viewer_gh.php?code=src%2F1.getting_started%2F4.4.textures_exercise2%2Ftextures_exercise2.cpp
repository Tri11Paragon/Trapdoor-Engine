


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/1.getting_started/4.4.textures_exercise2/textures_exercise2.cpp</title>
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
#include &lt;stb_image.h&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader_s.h</a>&gt;

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void processInput(GLFWwindow *window);

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
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }

    // build and compile our shader zprogram
    // ------------------------------------
    Shader ourShader("<a href='code_viewer_gh.php?code=src/1.getting_started/4.4.textures_exercise2/4.3.texture.vs' target='_blank'>4.3.texture.vs</a>", "<a href='code_viewer_gh.php?code=src/1.getting_started/4.4.textures_exercise2/4.3.texture.fs' target='_blank'>4.3.texture.fs</a>");

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float vertices[] = {
        // positions          // colors           // texture coords (note that we changed them to 2.0f!)
         0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   2.0f, 2.0f, // top right
         0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   2.0f, 0.0f, // bottom right
        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f, // bottom left
        -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 2.0f  // top left 
    };
    unsigned int indices[] = {
        0, 1, 3, // first triangle
        1, 2, 3  // second triangle
    };
    unsigned int VBO, VAO, EBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);
   <function id='12'> glGenBuffers(</function>1, &amp;EBO);

   <function id='27'> glBindVertexArray(</function>VAO);

   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

   <function id='32'> glBindBuffer(</function>GL_ELEMENT_ARRAY_BUFFER, EBO);
   <function id='31'> glBufferData(</function>GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    // position attribute
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
    // color attribute
   <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(3 * sizeof(float)));
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
    // texture coord attribute
   <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(6 * sizeof(float)));
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);


    // load and create a texture 
    // -------------------------
    unsigned int texture1, texture2;
    // texture 1
    // ---------
   <function id='50'> glGenTextures(</function>1, &amp;texture1);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture1);
    // set the texture wrapping parameters
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // note that we set the container wrapping method to GL_CLAMP_TO_EDGE
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    // set texture filtering parameters
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // load image, create texture and generate mipmaps
    int width, height, nrChannels;
    stbi_set_flip_vertically_on_load(true); // tell stb_image.h to flip loaded texture's on the y-axis.
    // The FileSystem::getPath(...) is part of the GitHub repository so we can find files on any IDE/platform; replace it with your own image path.
    unsigned char *data = stbi_load(FileSystem::getPath(&quot;resources/textures/container.jpg&quot;).c_str(), &amp;width, &amp;height, &amp;nrChannels, 0);
    if (data)
    {
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
       <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_2D);
    }
    else
    {
        std::cout &lt;&lt; &quot;Failed to load texture&quot; &lt;&lt; std::endl;
    }
    stbi_image_free(data);
    // texture 2
    // ---------
   <function id='50'> glGenTextures(</function>1, &amp;texture2);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture2);
    // set the texture wrapping parameters
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);	// we want to repeat the awesomeface pattern so we kept it at GL_REPEAT
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // set texture filtering parameters
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // load image, create texture and generate mipmaps
    data = stbi_load(FileSystem::getPath(&quot;resources/textures/awesomeface.png&quot;).c_str(), &amp;width, &amp;height, &amp;nrChannels, 0);
    if (data)
    {
        // note that the awesomeface.png has transparency and thus an alpha channel, so make sure to tell OpenGL the data type is of GL_RGBA
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
       <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_2D);
    }
    else
    {
        std::cout &lt;&lt; &quot;Failed to load texture&quot; &lt;&lt; std::endl;
    }
    stbi_image_free(data);

    // tell opengl for each sampler to which texture unit it belongs to (only has to be done once)
    // -------------------------------------------------------------------------------------------
    ourShader.use(); // don't forget to activate/use the shader before setting uniforms!
    // either set it manually like so:
   <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>ourShader.ID, &quot;texture1&quot;), 0);
    // or set it via the texture class
    ourShader.setInt(&quot;texture2&quot;, 1);


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

        // bind textures on corresponding texture units
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture1);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE1);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture2);

        // render container
        ourShader.use();
       <function id='27'> glBindVertexArray(</function>VAO);
       <function id='2'> glDrawElements(</function>GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;VAO);
    glDeleteBuffers(1, &amp;VBO);
    glDeleteBuffers(1, &amp;EBO);

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