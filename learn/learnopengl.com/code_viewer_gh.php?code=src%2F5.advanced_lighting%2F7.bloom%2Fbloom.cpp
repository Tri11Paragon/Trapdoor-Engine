


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/5.advanced_lighting/7.bloom/bloom.cpp</title>
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
unsigned int loadTexture(const char *path, bool gammaCorrection);
void renderQuad();
void renderCube();

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;
bool bloom = true;
bool bloomKeyPressed = false;
float exposure = 1.0f;

// camera
Camera camera(glm::vec3(0.0f, 0.0f, 5.0f));
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
    Shader shader("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.bloom.vs' target='_blank'>7.bloom.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.bloom.fs' target='_blank'>7.bloom.fs</a>");
    Shader shaderLight("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.bloom.vs' target='_blank'>7.bloom.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.light_box.fs' target='_blank'>7.light_box.fs</a>");
    Shader shaderBlur("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.blur.vs' target='_blank'>7.blur.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.blur.fs' target='_blank'>7.blur.fs</a>");
    Shader shaderBloomFinal("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.bloom_final.vs' target='_blank'>7.bloom_final.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/7.bloom/7.bloom_final.fs' target='_blank'>7.bloom_final.fs</a>");

    // load textures
    // -------------
    unsigned int woodTexture      = loadTexture(FileSystem::getPath(&quot;resources/textures/wood.png&quot;).c_str(), true); // note that we're loading the texture as an SRGB texture
    unsigned int containerTexture = loadTexture(FileSystem::getPath(&quot;resources/textures/container2.png&quot;).c_str(), true); // note that we're loading the texture as an SRGB texture

    // configure (floating point) framebuffers
    // ---------------------------------------
    unsigned int hdrFBO;
   <function id='76'> glGenFramebuffers(</function>1, &amp;hdrFBO);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, hdrFBO);
    // create 2 floating point color buffers (1 for normal rendering, other for brightness threshold values)
    unsigned int colorBuffers[2];
   <function id='50'> glGenTextures(</function>2, colorBuffers);
    for (unsigned int i = 0; i &lt; 2; i++)
    {
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, colorBuffers[i]);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_FLOAT, NULL);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);  // we clamp to the edge as the blur filter would otherwise sample repeated texture values!
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // attach texture to framebuffer
       <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, colorBuffers[i], 0);
    }
    // create and attach depth buffer (renderbuffer)
    unsigned int rboDepth;
   <function id='82'> glGenRenderbuffers(</function>1, &amp;rboDepth);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, rboDepth);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT, SCR_WIDTH, SCR_HEIGHT);
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);
    // tell OpenGL which color attachments we'll use (of this framebuffer) for rendering 
    unsigned int attachments[2] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
    glDrawBuffers(2, attachments);
    // finally check if framebuffer is complete
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;Framebuffer not complete!&quot; &lt;&lt; std::endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // ping-pong-framebuffer for blurring
    unsigned int pingpongFBO[2];
    unsigned int pingpongColorbuffers[2];
   <function id='76'> glGenFramebuffers(</function>2, pingpongFBO);
   <function id='50'> glGenTextures(</function>2, pingpongColorbuffers);
    for (unsigned int i = 0; i &lt; 2; i++)
    {
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, pingpongFBO[i]);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, pingpongColorbuffers[i]);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_FLOAT, NULL);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // we clamp to the edge as the blur filter would otherwise sample repeated texture values!
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
       <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pingpongColorbuffers[i], 0);
        // also check if framebuffers are complete (no need for depth buffer)
        if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            std::cout &lt;&lt; &quot;Framebuffer not complete!&quot; &lt;&lt; std::endl;
    }

    // lighting info
    // -------------
    // positions
    std::vector&lt;glm::vec3&gt; lightPositions;
    lightPositions.push_back(glm::vec3( 0.0f, 0.5f,  1.5f));
    lightPositions.push_back(glm::vec3(-4.0f, 0.5f, -3.0f));
    lightPositions.push_back(glm::vec3( 3.0f, 0.5f,  1.0f));
    lightPositions.push_back(glm::vec3(-.8f,  2.4f, -1.0f));
    // colors
    std::vector&lt;glm::vec3&gt; lightColors;
    lightColors.push_back(glm::vec3(5.0f,   5.0f,  5.0f));
    lightColors.push_back(glm::vec3(10.0f,  0.0f,  0.0f));
    lightColors.push_back(glm::vec3(0.0f,   0.0f,  15.0f));
    lightColors.push_back(glm::vec3(0.0f,   5.0f,  0.0f));


    // shader configuration
    // --------------------
    shader.use();
    shader.setInt(&quot;diffuseTexture&quot;, 0);
    shaderBlur.use();
    shaderBlur.setInt(&quot;image&quot;, 0);
    shaderBloomFinal.use();
    shaderBloomFinal.setInt(&quot;scene&quot;, 0);
    shaderBloomFinal.setInt(&quot;bloomBlur&quot;, 1);

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
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 1. render scene into floating point framebuffer
        // -----------------------------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, hdrFBO);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
        glm::mat4 view = camera.GetViewMatrix();
        glm::mat4 model = glm::mat4(1.0f);
        shader.use();
        shader.setMat4(&quot;projection&quot;, projection);
        shader.setMat4(&quot;view&quot;, view);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, woodTexture);
        // set lighting uniforms
        for (unsigned int i = 0; i &lt; lightPositions.size(); i++)
        {
            shader.setVec3(&quot;lights[&quot; + std::to_string(i) + &quot;].Position&quot;, lightPositions[i]);
            shader.setVec3(&quot;lights[&quot; + std::to_string(i) + &quot;].Color&quot;, lightColors[i]);
        }
        shader.setVec3(&quot;viewPos&quot;, camera.Position);
        // create one large cube that acts as the floor
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, -1.0f, 0.0));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(12.5f, 0.5f, 12.5f));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();
        // then create multiple cubes as the scenery
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, containerTexture);
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, 1.5f, 0.0));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(0.5f));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(2.0f, 0.0f, 1.0));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(0.5f));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-1.0f, -1.0f, 2.0));
        model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>60.0f), glm::normalize(glm::vec3(1.0, 0.0, 1.0)));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, 2.7f, 4.0));
        model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>23.0f), glm::normalize(glm::vec3(1.0, 0.0, 1.0)));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(1.25));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-2.0f, 1.0f, -3.0));
        model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>124.0f), glm::normalize(glm::vec3(1.0, 0.0, 1.0)));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-3.0f, 0.0f, 0.0));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(0.5f));
        shader.setMat4(&quot;model&quot;, model);
        renderCube();

        // finally show all the light sources as bright cubes
        shaderLight.use();
        shaderLight.setMat4(&quot;projection&quot;, projection);
        shaderLight.setMat4(&quot;view&quot;, view);

        for (unsigned int i = 0; i &lt; lightPositions.size(); i++)
        {
            model = glm::mat4(1.0f);
            model =<function id='55'> glm::translate(</function>model, glm::vec3(lightPositions[i]));
            model =<function id='56'> glm::scale(</function>model, glm::vec3(0.25f));
            shaderLight.setMat4(&quot;model&quot;, model);
            shaderLight.setVec3(&quot;lightColor&quot;, lightColors[i]);
            renderCube();
        }
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

        // 2. blur bright fragments with two-pass Gaussian Blur 
        // --------------------------------------------------
        bool horizontal = true, first_iteration = true;
        unsigned int amount = 10;
        shaderBlur.use();
        for (unsigned int i = 0; i &lt; amount; i++)
        {
           <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, pingpongFBO[horizontal]);
            shaderBlur.setInt(&quot;horizontal&quot;, horizontal);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, first_iteration ? colorBuffers[1] : pingpongColorbuffers[!horizontal]);  // bind texture of other framebuffer (or scene if first iteration)
            renderQuad();
            horizontal = !horizontal;
            if (first_iteration)
                first_iteration = false;
        }
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

        // 3. now render floating point color buffer to 2D quad and tonemap HDR colors to default framebuffer's (clamped) color range
        // --------------------------------------------------------------------------------------------------------------------------
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shaderBloomFinal.use();
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, colorBuffers[0]);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE1);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, pingpongColorbuffers[!horizontal]);
        shaderBloomFinal.setInt(&quot;bloom&quot;, bloom);
        shaderBloomFinal.setFloat(&quot;exposure&quot;, exposure);
        renderQuad();

        std::cout &lt;&lt; &quot;bloom: &quot; &lt;&lt; (bloom ? &quot;on&quot; : &quot;off&quot;) &lt;&lt; &quot;| exposure: &quot; &lt;&lt; exposure &lt;&lt; std::endl;

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// renderCube() renders a 1x1 3D cube in NDC.
// -------------------------------------------------
unsigned int cubeVAO = 0;
unsigned int cubeVBO = 0;
void renderCube()
{
    // initialize (if necessary)
    if (cubeVAO == 0)
    {
        float vertices[] = {
            // back face
            -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 0.0f, 0.0f, // bottom-left
             1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 1.0f, 1.0f, // top-right
             1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 1.0f, 0.0f, // bottom-right         
             1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 1.0f, 1.0f, // top-right
            -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 0.0f, 0.0f, // bottom-left
            -1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f, 0.0f, 1.0f, // top-left
            // front face
            -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f, 0.0f, // bottom-left
             1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f, 0.0f, // bottom-right
             1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f, 1.0f, // top-right
             1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f, 1.0f, // top-right
            -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f, 1.0f, // top-left
            -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f, 0.0f, // bottom-left
            // left face
            -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f, 1.0f, 0.0f, // top-right
            -1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  0.0f, 1.0f, 1.0f, // top-left
            -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, 0.0f, 1.0f, // bottom-left
            -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, 0.0f, 1.0f, // bottom-left
            -1.0f, -1.0f,  1.0f, -1.0f,  0.0f,  0.0f, 0.0f, 0.0f, // bottom-right
            -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f, 1.0f, 0.0f, // top-right
            // right face
             1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f, 1.0f, 0.0f, // top-left
             1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f, 0.0f, 1.0f, // bottom-right
             1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  0.0f, 1.0f, 1.0f, // top-right         
             1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f, 0.0f, 1.0f, // bottom-right
             1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f, 1.0f, 0.0f, // top-left
             1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  0.0f, 0.0f, 0.0f, // bottom-left     
            // bottom face
            -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f, 0.0f, 1.0f, // top-right
             1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f, 1.0f, 1.0f, // top-left
             1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f, 1.0f, 0.0f, // bottom-left
             1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f, 1.0f, 0.0f, // bottom-left
            -1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f, 0.0f, 0.0f, // bottom-right
            -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f, 0.0f, 1.0f, // top-right
            // top face
            -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f, 0.0f, 1.0f, // top-left
             1.0f,  1.0f , 1.0f,  0.0f,  1.0f,  0.0f, 1.0f, 0.0f, // bottom-right
             1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f, 1.0f, 1.0f, // top-right     
             1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f, 1.0f, 0.0f, // bottom-right
            -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f, 0.0f, 1.0f, // top-left
            -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f, 0.0f, 0.0f  // bottom-left        
        };
       <function id='33'> glGenVertexArrays(</function>1, &amp;cubeVAO);
       <function id='12'> glGenBuffers(</function>1, &amp;cubeVBO);
        // fill buffer
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, cubeVBO);
       <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
        // link vertex attributes
       <function id='27'> glBindVertexArray(</function>cubeVAO);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
       <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)0);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
       <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(3 * sizeof(float)));
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);
       <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(6 * sizeof(float)));
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
       <function id='27'> glBindVertexArray(</function>0);
    }
    // render Cube
   <function id='27'> glBindVertexArray(</function>cubeVAO);
   <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
   <function id='27'> glBindVertexArray(</function>0);
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

    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
        camera.ProcessKeyboard(FORWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
        camera.ProcessKeyboard(BACKWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
        camera.ProcessKeyboard(LEFT, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
        camera.ProcessKeyboard(RIGHT, deltaTime);

    if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS &amp;&amp; !bloomKeyPressed)
    {
        bloom = !bloom;
        bloomKeyPressed = true;
    }
    if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_RELEASE)
    {
        bloomKeyPressed = false;
    }

    if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
    {
        if (exposure &gt; 0.0f)
            exposure -= 0.001f;
        else
            exposure = 0.0f;
    }
    else if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
    {
        exposure += 0.001f;
    }
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
unsigned int loadTexture(char const * path, bool gammaCorrection)
{
    unsigned int textureID;
   <function id='50'> glGenTextures(</function>1, &amp;textureID);

    int width, height, nrComponents;
    unsigned char *data = stbi_load(path, &amp;width, &amp;height, &amp;nrComponents, 0);
    if (data)
    {
        GLenum internalFormat;
        GLenum dataFormat;
        if (nrComponents == 1)
        {
            internalFormat = dataFormat = GL_RED;
        }
        else if (nrComponents == 3)
        {
            internalFormat = gammaCorrection ? GL_SRGB : GL_RGB;
            dataFormat = GL_RGB;
        }
        else if (nrComponents == 4)
        {
            internalFormat = gammaCorrection ? GL_SRGB_ALPHA : GL_RGBA;
            dataFormat = GL_RGBA;
        }

       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, textureID);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, internalFormat, width, height, 0, dataFormat, GL_UNSIGNED_BYTE, data);
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