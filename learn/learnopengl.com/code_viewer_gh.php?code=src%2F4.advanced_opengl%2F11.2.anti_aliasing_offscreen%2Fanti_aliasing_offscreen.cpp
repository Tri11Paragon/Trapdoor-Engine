


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/4.advanced_opengl/11.2.anti_aliasing_offscreen/anti_aliasing_offscreen.cpp</title>
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
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/camera.h' target='_blank'>learnopengl/camera.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/model.h' target='_blank'>learnopengl/model.h</a>&gt;

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void mouse_callback(GLFWwindow* window, double xpos, double ypos);
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset);
void processInput(GLFWwindow *window);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

// camera
Camera camera(glm::vec3(0.0f, 0.0f, 3.0f));
float lastX = (float)SCR_WIDTH / 2.0;
float lastY = (float)SCR_HEIGHT / 2.0;
bool firstMouse = true;

// timing
float deltaTime = 0.0f;
float lastFrame = 0.0f;

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
    glfwSetCursorPosCallback(window, mouse_callback);
   <function id='64'> glfwSetScrollCallback(</function>window, scroll_callback);

    // tell GLFW to capture our mouse
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

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
    Shader shader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/11.2.anti_aliasing_offscreen/11.2.anti_aliasing.vs' target='_blank'>11.2.anti_aliasing.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/11.2.anti_aliasing_offscreen/11.2.anti_aliasing.fs' target='_blank'>11.2.anti_aliasing.fs</a>");
    Shader screenShader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/11.2.anti_aliasing_offscreen/11.2.aa_post.vs' target='_blank'>11.2.aa_post.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/11.2.anti_aliasing_offscreen/11.2.aa_post.fs' target='_blank'>11.2.aa_post.fs</a>");

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float cubeVertices[] = {
        // positions       
        -0.5f, -0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f,  0.5f, -0.5f,
         0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f,  0.5f,
         0.5f, -0.5f,  0.5f,
         0.5f,  0.5f,  0.5f,
         0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,

        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,

         0.5f,  0.5f,  0.5f,
         0.5f,  0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f, -0.5f,  0.5f,
         0.5f,  0.5f,  0.5f,

        -0.5f, -0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f, -0.5f,  0.5f,
         0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f,  0.5f, -0.5f,
         0.5f,  0.5f, -0.5f,
         0.5f,  0.5f,  0.5f,
         0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f
    };
    float quadVertices[] = {   // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
        // positions   // texCoords
        -1.0f,  1.0f,  0.0f, 1.0f,
        -1.0f, -1.0f,  0.0f, 0.0f,
         1.0f, -1.0f,  1.0f, 0.0f,

        -1.0f,  1.0f,  0.0f, 1.0f,
         1.0f, -1.0f,  1.0f, 0.0f,
         1.0f,  1.0f,  1.0f, 1.0f
    };
    // setup cube VAO
    unsigned int cubeVAO, cubeVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;cubeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;cubeVBO);
   <function id='27'> glBindVertexArray(</function>cubeVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, cubeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(cubeVertices), &amp;cubeVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
    // setup screen VAO
    unsigned int quadVAO, quadVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;quadVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;quadVBO);
   <function id='27'> glBindVertexArray(</function>quadVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, quadVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(quadVertices), &amp;quadVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)(2 * sizeof(float)));


    // configure MSAA framebuffer
    // --------------------------
    unsigned int framebuffer;
   <function id='76'> glGenFramebuffers(</function>1, &amp;framebuffer);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, framebuffer);
    // create a multisampled color attachment texture
    unsigned int textureColorBufferMultiSampled;
   <function id='50'> glGenTextures(</function>1, &amp;textureColorBufferMultiSampled);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled);
   <function id='101'><function id='52'> glTexImage2DM</function>ultisample(</function>GL_TEXTURE_2D_MULTISAMPLE, 4, GL_RGB, SCR_WIDTH, SCR_HEIGHT, GL_TRUE);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D_MULTISAMPLE, 0);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled, 0);
    // create a (also multisampled) renderbuffer object for depth and stencil attachments
    unsigned int rbo;
   <function id='82'> glGenRenderbuffers(</function>1, &amp;rbo);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, rbo);
   <function id='102'><function id='88'> glRenderbufferStorageM</function>ultisample(</function>GL_RENDERBUFFER, 4, GL_DEPTH24_STENCIL8, SCR_WIDTH, SCR_HEIGHT);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, 0);
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        cout &lt;&lt; &quot;ERROR::FRAMEBUFFER:: Framebuffer is not complete!&quot; &lt;&lt; endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // configure second post-processing framebuffer
    unsigned int intermediateFBO;
   <function id='76'> glGenFramebuffers(</function>1, &amp;intermediateFBO);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, intermediateFBO);
    // create a color attachment texture
    unsigned int screenTexture;
   <function id='50'> glGenTextures(</function>1, &amp;screenTexture);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, screenTexture);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGB, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, screenTexture, 0);	// we only need a color buffer

    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        cout &lt;&lt; &quot;ERROR::FRAMEBUFFER:: Intermediate framebuffer is not complete!&quot; &lt;&lt; endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // shader configuration
    // --------------------
    shader.use();
    screenShader.setInt(&quot;screenTexture&quot;, 0);

    // render loop
    // -----------
    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // per-frame time logic
        // --------------------
        float currentFrame =<function id='47'> glfwGetTime(</function>);
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        // input
        // -----
        processInput(window);

        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 1. draw scene as normal in multisampled buffers
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, framebuffer);
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
       <function id='60'> glEnable(</function>GL_DEPTH_TEST);

        // set transformation matrices		
        shader.use();
        glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 1000.0f);
        shader.setMat4(&quot;projection&quot;, projection);
        shader.setMat4(&quot;view&quot;, camera.GetViewMatrix());
        shader.setMat4(&quot;model&quot;, glm::mat4(1.0f));

       <function id='27'> glBindVertexArray(</function>cubeVAO);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);

        // 2. now blit multisampled buffer(s) to normal colorbuffer of intermediate FBO. Image is stored in screenTexture
       <function id='77'> glBindFramebuffer(</function>GL_READ_FRAMEBUFFER, framebuffer);
       <function id='77'> glBindFramebuffer(</function>GL_DRAW_FRAMEBUFFER, intermediateFBO);
       <function id='103'> glBlitFramebuffer(</function>0, 0, SCR_WIDTH, SCR_HEIGHT, 0, 0, SCR_WIDTH, SCR_HEIGHT, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        // 3. now render quad with scene's visuals as its texture image
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);
       <function id='13'><function id='10'> glClearC</function>olor(</function>1.0f, 1.0f, 1.0f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        // draw Screen quad
        screenShader.use();
       <function id='27'> glBindVertexArray(</function>quadVAO);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, screenTexture); // use the now resolved color attachment as the quad's texture
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
// ---------------------------------------------------------------------------------------------------------
void processInput(GLFWwindow *window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);

    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
        camera.ProcessKeyboard(FORWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
        camera.ProcessKeyboard(BACKWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
        camera.ProcessKeyboard(LEFT, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
        camera.ProcessKeyboard(RIGHT, deltaTime);
}

// glfw: whenever the window size changed (by OS or user resize) this callback function executes
// ---------------------------------------------------------------------------------------------
void framebuffer_size_callback(GLFWwindow* window, int width, int height)
{
    // make sure the viewport matches the new window dimensions; note that width and 
    // height will be significantly larger than specified on retina displays.
   <function id='22'> glViewport(</function>0, 0, width, height);
}

// glfw: whenever the mouse moves, this callback is called
// -------------------------------------------------------
void mouse_callback(GLFWwindow* window, double xpos, double ypos)
{
    if (firstMouse)
    {
        lastX = xpos;
        lastY = ypos;
        firstMouse = false;
    }

    float xoffset = xpos - lastX;
    float yoffset = lastY - ypos; // reversed since y-coordinates go from bottom to top

    lastX = xpos;
    lastY = ypos;

    camera.ProcessMouseMovement(xoffset, yoffset);
}

// glfw: whenever the mouse scroll wheel scrolls, this callback is called
// ----------------------------------------------------------------------
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset)
{
    camera.ProcessMouseScroll(yoffset);
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>