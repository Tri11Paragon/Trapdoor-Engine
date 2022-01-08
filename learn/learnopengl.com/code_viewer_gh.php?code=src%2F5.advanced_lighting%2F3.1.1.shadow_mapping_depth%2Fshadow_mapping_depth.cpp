


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/5.advanced_lighting/3.1.1.shadow_mapping_depth/shadow_mapping_depth.cpp</title>
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
unsigned int loadTexture(const char *path);
void renderScene(const Shader &amp;shader);
void renderCube();
void renderQuad();

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

// meshes
unsigned int planeVAO;

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
    Shader simpleDepthShader("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/3.1.1.shadow_mapping_depth/3.1.1.shadow_mapping_depth.vs' target='_blank'>3.1.1.shadow_mapping_depth.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/3.1.1.shadow_mapping_depth/3.1.1.shadow_mapping_depth.fs' target='_blank'>3.1.1.shadow_mapping_depth.fs</a>");
    Shader debugDepthQuad("<a href='code_viewer_gh.php?code=src/5.advanced_lighting/3.1.1.shadow_mapping_depth/3.1.1.debug_quad.vs' target='_blank'>3.1.1.debug_quad.vs</a>", "<a href='code_viewer_gh.php?code=src/5.advanced_lighting/3.1.1.shadow_mapping_depth/3.1.1.debug_quad_depth.fs' target='_blank'>3.1.1.debug_quad_depth.fs</a>");

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    float planeVertices[] = {
        // positions            // normals         // texcoords
         25.0f, -0.5f,  25.0f,  0.0f, 1.0f, 0.0f,  25.0f,  0.0f,
        -25.0f, -0.5f,  25.0f,  0.0f, 1.0f, 0.0f,   0.0f,  0.0f,
        -25.0f, -0.5f, -25.0f,  0.0f, 1.0f, 0.0f,   0.0f, 25.0f,

         25.0f, -0.5f,  25.0f,  0.0f, 1.0f, 0.0f,  25.0f,  0.0f,
        -25.0f, -0.5f, -25.0f,  0.0f, 1.0f, 0.0f,   0.0f, 25.0f,
         25.0f, -0.5f, -25.0f,  0.0f, 1.0f, 0.0f,  25.0f, 10.0f
    };
    // plane VAO
    unsigned int planeVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;planeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;planeVBO);
   <function id='27'> glBindVertexArray(</function>planeVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, planeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(planeVertices), planeVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)0);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
   <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(3 * sizeof(float)));
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);
   <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(6 * sizeof(float)));
   <function id='27'> glBindVertexArray(</function>0);

    // load textures
    // -------------
    unsigned int woodTexture = loadTexture(FileSystem::getPath(&quot;resources/textures/wood.png&quot;).c_str());

    // configure depth map FBO
    // -----------------------
    const unsigned int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;
    unsigned int depthMapFBO;
   <function id='76'> glGenFramebuffers(</function>1, &amp;depthMapFBO);
    // create depth texture
    unsigned int depthMap;
   <function id='50'> glGenTextures(</function>1, &amp;depthMap);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, depthMap);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // attach depth texture as FBO's depth buffer
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, depthMapFBO);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
    glDrawBuffer(GL_NONE);
    glReadBuffer(GL_NONE);
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);


    // shader configuration
    // --------------------
    debugDepthQuad.use();
    debugDepthQuad.setInt(&quot;depthMap&quot;, 0);

    // lighting info
    // -------------
    glm::vec3 lightPos(-2.0f, 4.0f, -1.0f);

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

        // 1. render depth of scene to texture (from light's perspective)
        // --------------------------------------------------------------
        glm::mat4 lightProjection, lightView;
        glm::mat4 lightSpaceMatrix;
        float near_plane = 1.0f, far_plane = 7.5f;
        lightProjection =<function id='59'> glm::ortho(</function>-10.0f, 10.0f, -10.0f, 10.0f, near_plane, far_plane);
        lightView =<function id='62'> glm::lookAt(</function>lightPos, glm::vec3(0.0f), glm::vec3(0.0, 1.0, 0.0));
        lightSpaceMatrix = lightProjection * lightView;
        // render scene from light's point of view
        simpleDepthShader.use();
        simpleDepthShader.setMat4(&quot;lightSpaceMatrix&quot;, lightSpaceMatrix);

       <function id='22'> glViewport(</function>0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, depthMapFBO);
           <function id='10'> glClear(</function>GL_DEPTH_BUFFER_BIT);
           <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, woodTexture);
            renderScene(simpleDepthShader);
       <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

        // reset viewport
       <function id='22'> glViewport(</function>0, 0, SCR_WIDTH, SCR_HEIGHT);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // render Depth map to quad for visual debugging
        // ---------------------------------------------
        debugDepthQuad.use();
        debugDepthQuad.setFloat(&quot;near_plane&quot;, near_plane);
        debugDepthQuad.setFloat(&quot;far_plane&quot;, far_plane);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, depthMap);
        renderQuad();

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;planeVAO);
    glDeleteBuffers(1, &amp;planeVBO);

   <function id='25'> glfwTerminate(</function>);
    return 0;
}

// renders the 3D scene
// --------------------
void renderScene(const Shader &amp;shader)
{
    // floor
    glm::mat4 model = glm::mat4(1.0f);
    shader.setMat4(&quot;model&quot;, model);
   <function id='27'> glBindVertexArray(</function>planeVAO);
   <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
    // cubes
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
    model =<function id='55'> glm::translate(</function>model, glm::vec3(-1.0f, 0.0f, 2.0));
    model =<function id='57'> glm::rotate(</function>model,<function id='63'> glm::radians(</function>60.0f), glm::normalize(glm::vec3(1.0, 0.0, 1.0)));
    model =<function id='56'> glm::scale(</function>model, glm::vec3(0.25));
    shader.setMat4(&quot;model&quot;, model);
    renderCube();
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

       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, format == GL_RGBA ? GL_CLAMP_TO_EDGE : GL_REPEAT); // for this tutorial: use GL_CLAMP_TO_EDGE to prevent semi-transparent borders. Due to interpolation it takes texels from next repeat 
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, format == GL_RGBA ? GL_CLAMP_TO_EDGE : GL_REPEAT);
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