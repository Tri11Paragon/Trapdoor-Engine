


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/4.advanced_opengl/5.2.framebuffers_exercise1/framebuffers_exercise1.cpp</title>
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

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader_m.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/camera.h' target='_blank'>learnopengl/camera.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/model.h' target='_blank'>learnopengl/model.h</a>&gt;

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void mouse_callback(GLFWwindow* window, double xpos, double ypos);
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset);
void processInput(GLFWwindow *window);
unsigned int loadTexture(const char *path);

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
    Shader shader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/5.2.framebuffers_exercise1/5.2.framebuffers.vs' target='_blank'>5.2.framebuffers.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/5.2.framebuffers_exercise1/5.2.framebuffers.fs' target='_blank'>5.2.framebuffers.fs</a>");
    Shader screenShader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/5.2.framebuffers_exercise1/5.2.framebuffers_screen.vs' target='_blank'>5.2.framebuffers_screen.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/5.2.framebuffers_exercise1/5.2.framebuffers_screen.fs' target='_blank'>5.2.framebuffers_screen.fs</a>");

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float cubeVertices[] = {
        // positions          // texture Coords
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
         0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };
    float planeVertices[] = {
        // positions          // texture Coords 
         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
        -5.0f, -0.5f,  5.0f,  0.0f, 0.0f,
        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,

         5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
        -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,
         5.0f, -0.5f, -5.0f,  2.0f, 2.0f
    };
    float quadVertices[] = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates. NOTE that this plane is now much smaller and at the top of the screen
        // positions   // texCoords
        -0.3f,  1.0f,  0.0f, 1.0f,
        -0.3f,  0.7f,  0.0f, 0.0f,
         0.3f,  0.7f,  1.0f, 0.0f,

        -0.3f,  1.0f,  0.0f, 1.0f,
         0.3f,  0.7f,  1.0f, 0.0f,
         0.3f,  1.0f,  1.0f, 1.0f
    };
    // cube VAO
    unsigned int cubeVAO, cubeVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;cubeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;cubeVBO);
   <function id='27'> glBindVertexArray(</function>cubeVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, cubeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(cubeVertices), &amp;cubeVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    // plane VAO
    unsigned int planeVAO, planeVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;planeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;planeVBO);
   <function id='27'> glBindVertexArray(</function>planeVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, planeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(planeVertices), &amp;planeVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    // screen quad VAO
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

    // load textures
    // -------------
    unsigned int cubeTexture = loadTexture(FileSystem::getPath(&quot;resources/textures/container.jpg&quot;).c_str());
    unsigned int floorTexture = loadTexture(FileSystem::getPath(&quot;resources/textures/metal.png&quot;).c_str());

    // shader configuration
    // --------------------
    shader.use();
    shader.setInt(&quot;texture1&quot;, 0);

    screenShader.use();
    screenShader.setInt(&quot;screenTexture&quot;, 0);

    // framebuffer configuration
    // -------------------------
    unsigned int framebuffer;
   <function id='76'> glGenFramebuffers(</function>1, &amp;framebuffer);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, framebuffer);
    // create a color attachment texture
    unsigned int textureColorbuffer;
   <function id='50'> glGenTextures(</function>1, &amp;textureColorbuffer);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textureColorbuffer);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGB, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorbuffer, 0);
    // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
    unsigned int rbo;
   <function id='82'> glGenRenderbuffers(</function>1, &amp;rbo);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, rbo);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, SCR_WIDTH, SCR_HEIGHT); // use a single renderbuffer object for both a depth AND stencil buffer.
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo); // now actually attach it
                                                                                                  // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        cout &lt;&lt; &quot;ERROR::FRAMEBUFFER:: Framebuffer is not complete!&quot; &lt;&lt; endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // draw as wireframe
    /<function id='43'>/glPolygonMode(</function>GL_FRONT_AND_BACK, GL_LINE);

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


        // first render pass: mirror texture.
        // bind to framebuffer and draw to color texture as we normally 
        // would, but with the view camera reversed.
        // bind to framebuffer and draw scene as we normally would to color texture 
        // ------------------------------------------------------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, framebuffer);
       <function id='60'> glEnable(</function>GL_DEPTH_TEST); // enable depth testing (is disabled for rendering screen-space quad)

        // make sure we clear the framebuffer's content
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();
        glm::mat4 model = glm::mat4(1.0f);
        camera.Yaw   += 180.0f; // rotate the camera's yaw 180 degrees around
        camera.ProcessMouseMovement(0, 0, false); // call this to make sure it updates its camera vectors, note that we disable pitch constrains for this specific case (otherwise we can't reverse camera's pitch values)
        glm::mat4 view = camera.GetViewMatrix();
        camera.Yaw   -= 180.0f; // reset it back to its original orientation
        camera.ProcessMouseMovement(0, 0, true); 
        glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
        shader.setMat4(&quot;view&quot;, view);
        shader.setMat4(&quot;projection&quot;, projection);
        // cubes
       <function id='27'> glBindVertexArray(</function>cubeVAO);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, cubeTexture);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-1.0f, 0.0f, -1.0f));
        shader.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(2.0f, 0.0f, 0.0f));
        shader.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        // floor
       <function id='27'> glBindVertexArray(</function>planeVAO);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, floorTexture);
        shader.setMat4(&quot;model&quot;, glm::mat4(1.0f));
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
       <function id='27'> glBindVertexArray(</function>0);

        // second render pass: draw as normal
        // ----------------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

       <function id='13'><function id='10'> glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        model = glm::mat4(1.0f);
        view = camera.GetViewMatrix();
        shader.setMat4(&quot;view&quot;, view);

        // cubes
       <function id='27'> glBindVertexArray(</function>cubeVAO);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, cubeTexture);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-1.0f, 0.0f, -1.0f));
        shader.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(2.0f, 0.0f, 0.0f));
        shader.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        // floor
       <function id='27'> glBindVertexArray(</function>planeVAO);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, floorTexture);
        shader.setMat4(&quot;model&quot;, glm::mat4(1.0f));
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
       <function id='27'> glBindVertexArray(</function>0);

        // now draw the mirror quad with screen texture
        // --------------------------------------------
        glDisable(GL_DEPTH_TEST); // disable depth test so screen-space quad isn't discarded due to depth test.

        screenShader.use();
       <function id='27'> glBindVertexArray(</function>quadVAO);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textureColorbuffer);	// use the color attachment texture as the texture of the quad plane
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);


        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;cubeVAO);
    glDeleteVertexArrays(1, &amp;planeVAO);
    glDeleteVertexArrays(1, &amp;quadVAO);
    glDeleteBuffers(1, &amp;cubeVBO);
    glDeleteBuffers(1, &amp;planeVBO);
    glDeleteBuffers(1, &amp;quadVBO);

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

// utility function for loading a 2D texture from file
// ---------------------------------------------------
unsigned int loadTexture(char const * path)
{
    unsigned int textureID;
   <function id='50'> glGenTextures(</function>1, &amp;textureID);

    int width, height, nrComponents;
    unsigned char *data = stbi_load(path, &amp;width, &amp;height, &amp;nrComponents, 0);
    if (data)
    {
        GLenum format;
        if (nrComponents == 1)
            format = GL_RED;
        else if (nrComponents == 3)
            format = GL_RGB;
        else if (nrComponents == 4)
            format = GL_RGBA;

       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textureID);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
       <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_2D);

       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(data);
    }
    else
    {
        std::cout &lt;&lt; &quot;Texture failed to load at path: &quot; &lt;&lt; path &lt;&lt; std::endl;
        stbi_image_free(data);
    }

    return textureID;
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>