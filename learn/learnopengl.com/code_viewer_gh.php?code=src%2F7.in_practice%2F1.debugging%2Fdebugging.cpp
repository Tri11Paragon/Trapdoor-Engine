


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/1.debugging/debugging.cpp</title>
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

#include &lt;glm/glm.hpp&gt;
#include &lt;glm/gtc/matrix_transform.hpp&gt;
#include &lt;glm/gtc/type_ptr.hpp&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void processInput(GLFWwindow *window);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;


GLenum glCheckError_(const char *file, int line)
{
    GLenum errorCode;
    while ((errorCode = glGetError()) != GL_NO_ERROR)
    {
        std::string error;
        switch (errorCode)
        {
            case GL_INVALID_ENUM:                  error = &quot;INVALID_ENUM&quot;; break;
            case GL_INVALID_VALUE:                 error = &quot;INVALID_VALUE&quot;; break;
            case GL_INVALID_OPERATION:             error = &quot;INVALID_OPERATION&quot;; break;
            case GL_STACK_OVERFLOW:                error = &quot;STACK_OVERFLOW&quot;; break;
            case GL_STACK_UNDERFLOW:               error = &quot;STACK_UNDERFLOW&quot;; break;
            case GL_OUT_OF_MEMORY:                 error = &quot;OUT_OF_MEMORY&quot;; break;
            case GL_INVALID_FRAMEBUFFER_OPERATION: error = &quot;INVALID_FRAMEBUFFER_OPERATION&quot;; break;
        }
        std::cout &lt;&lt; error &lt;&lt; &quot; | &quot; &lt;&lt; file &lt;&lt; &quot; (&quot; &lt;&lt; line &lt;&lt; &quot;)&quot; &lt;&lt; std::endl;
    }
    return errorCode;
}
#define glCheckError() glCheckError_(__FILE__, __LINE__)

void APIENTRY glDebugOutput(GLenum source, 
                            GLenum type, 
                            unsigned int id, 
                            GLenum severity, 
                            GLsizei length, 
                            const char *message, 
                            const void *userParam)
{
    if(id == 131169 || id == 131185 || id == 131218 || id == 131204) return; // ignore these non-significant error codes

    std::cout &lt;&lt; &quot;---------------&quot; &lt;&lt; std::endl;
    std::cout &lt;&lt; &quot;Debug message (&quot; &lt;&lt; id &lt;&lt; &quot;): &quot; &lt;&lt;  message &lt;&lt; std::endl;

    switch (source)
    {
        case GL_DEBUG_SOURCE_API:             std::cout &lt;&lt; &quot;Source: API&quot;; break;
        case GL_DEBUG_SOURCE_WINDOW_SYSTEM:   std::cout &lt;&lt; &quot;Source: Window System&quot;; break;
        case GL_DEBUG_SOURCE_SHADER_COMPILER: std::cout &lt;&lt; &quot;Source: Shader Compiler&quot;; break;
        case GL_DEBUG_SOURCE_THIRD_PARTY:     std::cout &lt;&lt; &quot;Source: Third Party&quot;; break;
        case GL_DEBUG_SOURCE_APPLICATION:     std::cout &lt;&lt; &quot;Source: Application&quot;; break;
        case GL_DEBUG_SOURCE_OTHER:           std::cout &lt;&lt; &quot;Source: Other&quot;; break;
    } std::cout &lt;&lt; std::endl;

    switch (type)
    {
        case GL_DEBUG_TYPE_ERROR:               std::cout &lt;&lt; &quot;Type: Error&quot;; break;
        case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR: std::cout &lt;&lt; &quot;Type: Deprecated Behaviour&quot;; break;
        case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:  std::cout &lt;&lt; &quot;Type: Undefined Behaviour&quot;; break; 
        case GL_DEBUG_TYPE_PORTABILITY:         std::cout &lt;&lt; &quot;Type: Portability&quot;; break;
        case GL_DEBUG_TYPE_PERFORMANCE:         std::cout &lt;&lt; &quot;Type: Performance&quot;; break;
        case GL_DEBUG_TYPE_MARKER:              std::cout &lt;&lt; &quot;Type: Marker&quot;; break;
        case GL_DEBUG_TYPE_PUSH_GROUP:          std::cout &lt;&lt; &quot;Type: Push Group&quot;; break;
        case GL_DEBUG_TYPE_POP_GROUP:           std::cout &lt;&lt; &quot;Type: Pop Group&quot;; break;
        case GL_DEBUG_TYPE_OTHER:               std::cout &lt;&lt; &quot;Type: Other&quot;; break;
    } std::cout &lt;&lt; std::endl;
    
    switch (severity)
    {
        case GL_DEBUG_SEVERITY_HIGH:         std::cout &lt;&lt; &quot;Severity: high&quot;; break;
        case GL_DEBUG_SEVERITY_MEDIUM:       std::cout &lt;&lt; &quot;Severity: medium&quot;; break;
        case GL_DEBUG_SEVERITY_LOW:          std::cout &lt;&lt; &quot;Severity: low&quot;; break;
        case GL_DEBUG_SEVERITY_NOTIFICATION: std::cout &lt;&lt; &quot;Severity: notification&quot;; break;
    } std::cout &lt;&lt; std::endl;
    std::cout &lt;&lt; std::endl;
}

int main()
{
    // glfw: initialize and configure
    // ------------------------------
   <function id='17'> glfwInit(</function>);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MAJOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MINOR, 3);
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_DEBUG_CONTEXT, true); // comment this line in a release build! 

#ifdef __APPLE__
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif

    // glfw window creation
    // --------------------
    GLFWwindow* window =<function id='20'> glfwCreateWindow(</function>SCR_WIDTH, SCR_HEIGHT, &quot;LearnOpenGL&quot;, NULL, NULL);
   <function id='19'> glfwMakeContextCurrent(</function>window);
    if (window == NULL)
    {
        std::cout &lt;&lt; &quot;Failed to create GLFW window&quot; &lt;&lt; std::endl;
       <function id='25'> glfwTerminate(</function>);
        return -1;
    }
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);

    // tell GLFW to capture our mouse
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }

    // enable OpenGL debug context if context allows for debug context
    int flags; glGetIntegerv(GL_CONTEXT_FLAGS, &amp;flags);
    if (flags &amp; GL_CONTEXT_FLAG_DEBUG_BIT)
    {
       <function id='60'> glEnable(</function>GL_DEBUG_OUTPUT);
       <function id='60'> glEnable(</function>GL_DEBUG_OUTPUT_SYNCHRONOUS); // makes sure errors are displayed synchronously
        glDebugMessageCallback(glDebugOutput, nullptr);
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, nullptr, GL_TRUE);
    }

    // configure global opengl state
    // -----------------------------
   <function id='60'> glEnable(</function>GL_DEPTH_TEST);
   <function id='60'> glEnable(</function>GL_CULL_FACE);
 
    // OpenGL initial state
    Shader shader("<a href='code_viewer_gh.php?code=src/7.in_practice/1.debugging/debugging.vs' target='_blank'>debugging.vs</a>", "<a href='code_viewer_gh.php?code=src/7.in_practice/1.debugging/debugging.fs' target='_blank'>debugging.fs</a>");

    // configure 3D cube
    unsigned int cubeVAO, cubeVBO;
    float vertices[] = {
         // back face
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, // bottom-left
         0.5f,  0.5f, -0.5f,  1.0f,  1.0f, // top-right
         0.5f, -0.5f, -0.5f,  1.0f,  0.0f, // bottom-right         
         0.5f,  0.5f, -0.5f,  1.0f,  1.0f, // top-right
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, // bottom-left
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f, // top-left
         // front face
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, // bottom-left
         0.5f, -0.5f,  0.5f,  1.0f,  0.0f, // bottom-right
         0.5f,  0.5f,  0.5f,  1.0f,  1.0f, // top-right
         0.5f,  0.5f,  0.5f,  1.0f,  1.0f, // top-right
        -0.5f,  0.5f,  0.5f,  0.0f,  1.0f, // top-left
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, // bottom-left
         // left face
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f, // top-right
        -0.5f,  0.5f, -0.5f, -1.0f,  1.0f, // top-left
        -0.5f, -0.5f, -0.5f, -0.0f,  1.0f, // bottom-left
        -0.5f, -0.5f, -0.5f, -0.0f,  1.0f, // bottom-left
        -0.5f, -0.5f,  0.5f, -0.0f,  0.0f, // bottom-right
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f, // top-right
         // right face
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f, // top-left
         0.5f, -0.5f, -0.5f,  0.0f,  1.0f, // bottom-right
         0.5f,  0.5f, -0.5f,  1.0f,  1.0f, // top-right         
         0.5f, -0.5f, -0.5f,  0.0f,  1.0f, // bottom-right
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f, // top-left
         0.5f, -0.5f,  0.5f,  0.0f,  0.0f, // bottom-left     
         // bottom face
        -0.5f, -0.5f, -0.5f,  0.0f,  1.0f, // top-right
         0.5f, -0.5f, -0.5f,  1.0f,  1.0f, // top-left
         0.5f, -0.5f,  0.5f,  1.0f,  0.0f, // bottom-left
         0.5f, -0.5f,  0.5f,  1.0f,  0.0f, // bottom-left
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, // bottom-right
        -0.5f, -0.5f, -0.5f,  0.0f,  1.0f, // top-right
         // top face
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f, // top-left
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f, // bottom-right
         0.5f,  0.5f, -0.5f,  1.0f,  1.0f, // top-right     
         0.5f,  0.5f,  0.5f,  1.0f,  0.0f, // bottom-right
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f, // top-left
        -0.5f,  0.5f,  0.5f,  0.0f,  0.0f  // bottom-left        
    };
   <function id='33'> glGenVertexArrays(</function>1, &amp;cubeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;cubeVBO);
    // fill buffer
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, cubeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    // link vertex attributes
   <function id='27'> glBindVertexArray(</function>cubeVAO);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
   <function id='27'> glBindVertexArray(</function>0);

    // load cube texture
    unsigned int texture;
   <function id='50'> glGenTextures(</function>1, &amp;texture);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture);
    int width, height, nrComponents;
    unsigned char *data = stbi_load(FileSystem::getPath(&quot;resources/textures/wood.png&quot;).c_str(), &amp;width, &amp;height, &amp;nrComponents, 0);
    if (data)
    {
       <function id='52'> glTexImage2D(</function>GL_FRAMEBUFFER, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
       <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_2D);

       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
    else
    {
        std::cout &lt;&lt; &quot;Failed to load texture&quot; &lt;&lt; std::endl;
    }
    stbi_image_free(data);

    // set up projection matrix
    glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>45.0f), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 10.0f);
   <function id='44'> glUniformM</function>atrix4fv<function id='45'>(glGetUniformLocation(</function>shader.ID, &quot;projection&quot;), 1, GL_FALSE, glm::value_ptr(projection));
   <function id='44'> glUniform1</function>i<function id='45'>(glGetUniformLocation(</function>shader.ID, &quot;tex&quot;), 0);

    // render loop
    // -----------
    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // input
        // -----
        processInput(window);

        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();
        float rotationSpeed = 10.0f;
        float angle = (float<function id='47'>)glfwGetTime(</function>) * rotationSpeed;
        glm::mat4 model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0, 0.0f, -2.5));
        model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>angle), glm::vec3(1.0f, 1.0f, 1.0f));
       <function id='44'> glUniformM</function>atrix4fv<function id='45'>(glGetUniformLocation(</function>shader.ID, &quot;model&quot;), 1, GL_FALSE, glm::value_ptr(model));

       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture);
       <function id='27'> glBindVertexArray(</function>cubeVAO);
           <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
       <function id='27'> glBindVertexArray(</function>0);

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// renderQuad() renders a 1x1 XY quad in NDC
// -----------------------------------------
unsigned int quadVAO = 0;
unsigned int quadVBO;
void renderQuad()
{
    if (quadVAO == 0)
    {
        float quadVertices[] = {
            // positions        // texture Coords
            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
             1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
             1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        };
        // setup plane VAO
       <function id='33'> glGenVertexArrays(</function>1, &amp;quadVAO);
       <function id='12'> glGenBuffers(</function>1, &amp;quadVBO);
       <function id='27'> glBindVertexArray(</function>quadVAO);
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, quadVBO);
       <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(quadVertices), &amp;quadVertices, GL_STATIC_DRAW);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
       <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
       <function id='30'> glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    }
   <function id='27'> glBindVertexArray(</function>quadVAO);
   <function id='1'> glDrawArrays(</function>GL_TRIANGLE_STRIP, 0, 4);
   <function id='27'> glBindVertexArray(</function>0);
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
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>