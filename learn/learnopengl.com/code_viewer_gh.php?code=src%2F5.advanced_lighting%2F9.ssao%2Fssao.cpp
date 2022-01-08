


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/5.advanced_lighting/9.ssao/ssao.cpp</title>
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

#include &lt;glm/glm.hpp&gt;
#include &lt;glm/gtc/matrix_transform.hpp&gt;
#include &lt;glm/gtc/type_ptr.hpp&gt;

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/camera.h' target='_blank'>learnopengl/camera.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/model.h' target='_blank'>learnopengl/model.h</a>&gt;

#include &lt;iostream&gt;
#include &lt;random&gt;

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

// camera
Camera camera(glm::vec3(0.0f, 0.0f, 5.0f));
float lastX = (float)SCR_WIDTH / 2.0;
float lastY = (float)SCR_HEIGHT / 2.0;
bool firstMouse = true;

// timing
float deltaTime = 0.0f;
float lastFrame = 0.0f;

float lerp(float a, float b, float f)
{
    return a + f * (b - a);
}

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
    Shader shaderGeometryPass("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao_geometry.vs' target='_blank'>9.ssao_geometry.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao_geometry.fs' target='_blank'>9.ssao_geometry.fs</a>");
    Shader shaderLightingPass("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao.vs' target='_blank'>9.ssao.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao_lighting.fs' target='_blank'>9.ssao_lighting.fs</a>");
    Shader shaderSSAO("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao.vs' target='_blank'>9.ssao.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao.fs' target='_blank'>9.ssao.fs</a>");
    Shader shaderSSAOBlur("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao.vs' target='_blank'>9.ssao.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/9.ssao/9.ssao_blur.fs' target='_blank'>9.ssao_blur.fs</a>");

    // load models
    // -----------
    Model backpack(FileSystem::getPath(&quot;resources/objects/backpack/backpack.obj&quot;));

    // configure g-buffer framebuffer
    // ------------------------------
    unsigned int gBuffer;
   <function id='76'> glGenFramebuffers(</function>1, &amp;gBuffer);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, gBuffer);
    unsigned int gPosition, gNormal, gAlbedo;
    // position color buffer
   <function id='50'> glGenTextures(</function>1, &amp;gPosition);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gPosition);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_FLOAT, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0);
    // normal color buffer
   <function id='50'> glGenTextures(</function>1, &amp;gNormal);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gNormal);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_FLOAT, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0);
    // color + specular color buffer
   <function id='50'> glGenTextures(</function>1, &amp;gAlbedo);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gAlbedo);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedo, 0);
    // tell OpenGL which color attachments we'll use (of this framebuffer) for rendering 
    unsigned int attachments[3] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
    glDrawBuffers(3, attachments);
    // create and attach depth buffer (renderbuffer)
    unsigned int rboDepth;
   <function id='82'> glGenRenderbuffers(</function>1, &amp;rboDepth);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, rboDepth);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT, SCR_WIDTH, SCR_HEIGHT);
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);
    // finally check if framebuffer is complete
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;Framebuffer not complete!&quot; &lt;&lt; std::endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // also create framebuffer to hold SSAO processing stage 
    // -----------------------------------------------------
    unsigned int ssaoFBO, ssaoBlurFBO;
   <function id='76'> glGenFramebuffers(</function>1, &amp;ssaoFBO); <function id='76'> glGenFramebuffers(</function>1, &amp;ssaoBlurFBO);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, ssaoFBO);
    unsigned int ssaoColorBuffer, ssaoColorBufferBlur;
    // SSAO color buffer
   <function id='50'> glGenTextures(</function>1, &amp;ssaoColorBuffer);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ssaoColorBuffer);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RED, SCR_WIDTH, SCR_HEIGHT, 0, GL_RED, GL_FLOAT, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColorBuffer, 0);
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;SSAO Framebuffer not complete!&quot; &lt;&lt; std::endl;
    // and blur stage
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, ssaoBlurFBO);
   <function id='50'> glGenTextures(</function>1, &amp;ssaoColorBufferBlur);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ssaoColorBufferBlur);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RED, SCR_WIDTH, SCR_HEIGHT, 0, GL_RED, GL_FLOAT, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColorBufferBlur, 0);
    if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        std::cout &lt;&lt; &quot;SSAO Blur Framebuffer not complete!&quot; &lt;&lt; std::endl;
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // generate sample kernel
    // ----------------------
    std::uniform_real_distribution&lt;GLfloat&gt; randomFloats(0.0, 1.0); // generates random floats between 0.0 and 1.0
    std::default_random_engine generator;
    std::vector&lt;glm::vec3&gt; ssaoKernel;
    for (unsigned int i = 0; i &lt; 64; ++i)
    {
        glm::vec3 sample(randomFloats(generator) * 2.0 - 1.0, randomFloats(generator) * 2.0 - 1.0, randomFloats(generator));
        sample = glm::normalize(sample);
        sample *= randomFloats(generator);
        float scale = float(i) / 64.0;

        // scale samples s.t. they're more aligned to center of kernel
        scale = lerp(0.1f, 1.0f, scale * scale);
        sample *= scale;
        ssaoKernel.push_back(sample);
    }

    // generate noise texture
    // ----------------------
    std::vector&lt;glm::vec3&gt; ssaoNoise;
    for (unsigned int i = 0; i &lt; 16; i++)
    {
        glm::vec3 noise(randomFloats(generator) * 2.0 - 1.0, randomFloats(generator) * 2.0 - 1.0, 0.0f); // rotate around z-axis (in tangent space)
        ssaoNoise.push_back(noise);
    }
    unsigned int noiseTexture;<function id='50'> glGenTextures(</function>1, &amp;noiseTexture);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, noiseTexture);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA32F, 4, 4, 0, GL_RGB, GL_FLOAT, &amp;ssaoNoise[0]);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    // lighting info
    // -------------
    glm::vec3 lightPos = glm::vec3(2.0, 4.0, -2.0);
    glm::vec3 lightColor = glm::vec3(0.2, 0.2, 0.7);

    // shader configuration
    // --------------------
    shaderLightingPass.use();
    shaderLightingPass.setInt(&quot;gPosition&quot;, 0);
    shaderLightingPass.setInt(&quot;gNormal&quot;, 1);
    shaderLightingPass.setInt(&quot;gAlbedo&quot;, 2);
    shaderLightingPass.setInt(&quot;ssao&quot;, 3);
    shaderSSAO.use();
    shaderSSAO.setInt(&quot;gPosition&quot;, 0);
    shaderSSAO.setInt(&quot;gNormal&quot;, 1);
    shaderSSAO.setInt(&quot;texNoise&quot;, 2);
    shaderSSAOBlur.use();
    shaderSSAOBlur.setInt(&quot;ssaoInput&quot;, 0);

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

        // 1. geometry pass: render scene's geometry/color data into gbuffer
        // -----------------------------------------------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, gBuffer);
           <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 50.0f);
            glm::mat4 view = camera.GetViewMatrix();
            glm::mat4 model = glm::mat4(1.0f);
            shaderGeometryPass.use();
            shaderGeometryPass.setMat4(&quot;projection&quot;, projection);
            shaderGeometryPass.setMat4(&quot;view&quot;, view);
            // room cube
            model = glm::mat4(1.0f);
            model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0, 7.0f, 0.0f));
            model =<function id='56'> glm::scale(</function>model, glm::vec3(7.5f, 7.5f, 7.5f));
            shaderGeometryPass.setMat4(&quot;model&quot;, model);
            shaderGeometryPass.setInt(&quot;invertedNormals&quot;, 1); // invert normals as we're inside the cube
            renderCube();
            shaderGeometryPass.setInt(&quot;invertedNormals&quot;, 0); 
            // backpack model on the floor
            model = glm::mat4(1.0f);
            model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, 0.5f, 0.0));
            model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>-90.0f), glm::vec3(1.0, 0.0, 0.0));
            model =<function id='56'> glm::scale(</function>model, glm::vec3(1.0f));
            shaderGeometryPass.setMat4(&quot;model&quot;, model);
            backpack.Draw(shaderGeometryPass);
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);


        // 2. generate SSAO texture
        // ------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, ssaoFBO);
           <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);
            shaderSSAO.use();
            // Send kernel + rotation 
            for (unsigned int i = 0; i &lt; 64; ++i)
                shaderSSAO.setVec3(&quot;samples[&quot; + std::to_string(i) + &quot;]&quot;, ssaoKernel[i]);
            shaderSSAO.setMat4(&quot;projection&quot;, projection);
           <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gPosition);
           <function id='49'> glActiveTexture(</function>GL_TEXTURE1);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gNormal);
           <function id='49'> glActiveTexture(</function>GL_TEXTURE2);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, noiseTexture);
            renderQuad();
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);


        // 3. blur SSAO texture to remove noise
        // ------------------------------------
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, ssaoBlurFBO);
           <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);
            shaderSSAOBlur.use();
           <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ssaoColorBuffer);
            renderQuad();
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);


        // 4. lighting pass: traditional deferred Blinn-Phong lighting with added screen-space ambient occlusion
        // -----------------------------------------------------------------------------------------------------
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shaderLightingPass.use();
        // send light relevant uniforms
        glm::vec3 lightPosView = glm::vec3(camera.GetViewMatrix() * glm::vec4(lightPos, 1.0));
        shaderLightingPass.setVec3(&quot;light.Position&quot;, lightPosView);
        shaderLightingPass.setVec3(&quot;light.Color&quot;, lightColor);
        // Update attenuation parameters
        const float linear    = 0.09;
        const float quadratic = 0.032;
        shaderLightingPass.setFloat(&quot;light.Linear&quot;, linear);
        shaderLightingPass.setFloat(&quot;light.Quadratic&quot;, quadratic);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gPosition);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE1);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gNormal);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE2);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, gAlbedo);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3); // add extra SSAO texture to lighting pass
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ssaoColorBufferBlur);
        renderQuad();


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