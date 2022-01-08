


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/6.pbr/2.1.1.ibl_irradiance_conversion/ibl_irradiance_conversion.cpp</title>
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
void renderSphere();
void renderCube();

// settings
const unsigned int SCR_WIDTH = 1280;
const unsigned int SCR_HEIGHT = 720;

// camera
Camera camera(glm::vec3(0.0f, 0.0f, 3.0f));
float lastX = 800.0f / 2.0;
float lastY = 600.0 / 2.0;
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
   <function id='18'> glfwWindowHint(</function>GLFW_SAMPLES, 4);
   <function id='18'> glfwWindowHint(</function>GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

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
   <function id='66'> glDepthFunc(</function>GL_LEQUAL); // set depth function to less than AND equal for skybox depth trick.

    // build and compile shaders
    // -------------------------
    Shader pbrShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.pbr.vs' target='_blank'>2.1.1.pbr.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.pbr.fs' target='_blank'>2.1.1.pbr.fs</a>");
    Shader equirectangularToCubemapShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.cubemap.vs' target='_blank'>2.1.1.cubemap.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.equirectangular_to_cubemap.fs' target='_blank'>2.1.1.equirectangular_to_cubemap.fs</a>");
    Shader backgroundShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.background.vs' target='_blank'>2.1.1.background.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.1.1.ibl_irradiance_conversion/2.1.1.background.fs' target='_blank'>2.1.1.background.fs</a>");


    pbrShader.use();
    pbrShader.setVec3(&quot;albedo&quot;, 0.5f, 0.0f, 0.0f);
    pbrShader.setFloat(&quot;ao&quot;, 1.0f);

    backgroundShader.use();
    backgroundShader.setInt(&quot;environmentMap&quot;, 0);


    // lights
    // ------
    glm::vec3 lightPositions[] = {
        glm::vec3(-10.0f,  10.0f, 10.0f),
        glm::vec3( 10.0f,  10.0f, 10.0f),
        glm::vec3(-10.0f, -10.0f, 10.0f),
        glm::vec3( 10.0f, -10.0f, 10.0f),
    };
    glm::vec3 lightColors[] = {
        glm::vec3(300.0f, 300.0f, 300.0f),
        glm::vec3(300.0f, 300.0f, 300.0f),
        glm::vec3(300.0f, 300.0f, 300.0f),
        glm::vec3(300.0f, 300.0f, 300.0f)
    };
    int nrRows = 7;
    int nrColumns = 7;
    float spacing = 2.5;

    // pbr: setup framebuffer
    // ----------------------
    unsigned int captureFBO;
    unsigned int captureRBO;
   <function id='76'> glGenFramebuffers(</function>1, &amp;captureFBO);
   <function id='82'> glGenRenderbuffers(</function>1, &amp;captureRBO);

   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, captureRBO);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 512, 512);
   <function id='89'> glFramebufferRenderbuffer(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, captureRBO);

    // pbr: load the HDR environment map
    // ---------------------------------
    stbi_set_flip_vertically_on_load(true);
    int width, height, nrComponents;
    float *data = stbi_loadf(FileSystem::getPath(&quot;resources/textures/hdr/newport_loft.hdr&quot;).c_str(), &amp;width, &amp;height, &amp;nrComponents, 0);
    unsigned int hdrTexture;
    if (data)
    {
       <function id='50'> glGenTextures(</function>1, &amp;hdrTexture);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, hdrTexture);
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, data); // note how we specify the texture's data value to be float

       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
       <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(data);
    }
    else
    {
        std::cout &lt;&lt; &quot;Failed to load HDR image.&quot; &lt;&lt; std::endl;
    }

    // pbr: setup cubemap to render to and attach to framebuffer
    // ---------------------------------------------------------
    unsigned int envCubemap;
   <function id='50'> glGenTextures(</function>1, &amp;envCubemap);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, envCubemap);
    for (unsigned int i = 0; i &lt; 6; ++i)
    {
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 512, 512, 0, GL_RGB, GL_FLOAT, nullptr);
    }
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR); 
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    // pbr: set up projection and view matrices for capturing data onto the 6 cubemap face directions
    // ----------------------------------------------------------------------------------------------
    glm::mat4 captureProjection = glm::perspective<function id='63'>(glm::radians(</function>90.0f), 1.0f, 0.1f, 10.0f);
    glm::mat4 captureViews[] =
    {
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3( 1.0f,  0.0f,  0.0f), glm::vec3(0.0f, -1.0f,  0.0f)),
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3(-1.0f,  0.0f,  0.0f), glm::vec3(0.0f, -1.0f,  0.0f)),
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3( 0.0f,  1.0f,  0.0f), glm::vec3(0.0f,  0.0f,  1.0f)),
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3( 0.0f, -1.0f,  0.0f), glm::vec3(0.0f,  0.0f, -1.0f)),
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3( 0.0f,  0.0f,  1.0f), glm::vec3(0.0f, -1.0f,  0.0f)),
       <function id='62'> glm::lookAt(</function>glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3( 0.0f,  0.0f, -1.0f), glm::vec3(0.0f, -1.0f,  0.0f))
    };

    // pbr: convert HDR equirectangular environment map to cubemap equivalent
    // ----------------------------------------------------------------------
    equirectangularToCubemapShader.use();
    equirectangularToCubemapShader.setInt(&quot;equirectangularMap&quot;, 0);
    equirectangularToCubemapShader.setMat4(&quot;projection&quot;, captureProjection);
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, hdrTexture);

   <function id='22'> glViewport(</function>0, 0, 512, 512); // don't forget to configure the viewport to the capture dimensions.
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
    for (unsigned int i = 0; i &lt; 6; ++i)
    {
        equirectangularToCubemapShader.setMat4(&quot;view&quot;, captureViews[i]);
       <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, envCubemap, 0);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderCube();
    }
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // initialize static shader uniforms before rendering
    // --------------------------------------------------
    glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
    pbrShader.use();
    pbrShader.setMat4(&quot;projection&quot;, projection);
    backgroundShader.use();
    backgroundShader.setMat4(&quot;projection&quot;, projection);

    // then before rendering, configure the viewport to the original framebuffer's screen dimensions
    int scrWidth, scrHeight;
    glfwGetFramebufferSize(window, &amp;scrWidth, &amp;scrHeight);
   <function id='22'> glViewport(</function>0, 0, scrWidth, scrHeight);

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
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.2f, 0.3f, 0.3f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // render scene, supplying the convoluted irradiance map to the final shader.
        // ------------------------------------------------------------------------------------------
        pbrShader.use();
        glm::mat4 view = camera.GetViewMatrix();
        pbrShader.setMat4(&quot;view&quot;, view);
        pbrShader.setVec3(&quot;camPos&quot;, camera.Position);

        // render rows*column number of spheres with varying metallic/roughness values scaled by rows and columns respectively
        glm::mat4 model = glm::mat4(1.0f);
        for (int row = 0; row &lt; nrRows; ++row)
        {
            pbrShader.setFloat(&quot;metallic&quot;, (float)row / (float)nrRows);
            for (int col = 0; col &lt; nrColumns; ++col)
            {
                // we clamp the roughness to 0.025 - 1.0 as perfectly smooth surfaces (roughness of 0.0) tend to look a bit off
                // on direct lighting.
                pbrShader.setFloat(&quot;roughness&quot;, glm::clamp((float)col / (float)nrColumns, 0.05f, 1.0f));

                model = glm::mat4(1.0f);
                model =<function id='55'> glm::translate(</function>model, glm::vec3(
                    (float)(col - (nrColumns / 2)) * spacing,
                    (float)(row - (nrRows / 2)) * spacing,
                    -2.0f
                ));
                pbrShader.setMat4(&quot;model&quot;, model);
                renderSphere();
            }
        }


        // render light source (simply re-render sphere at light positions)
        // this looks a bit off as we use the same shader, but it'll make their positions obvious and 
        // keeps the codeprint small.
        for (unsigned int i = 0; i &lt; sizeof(lightPositions) / sizeof(lightPositions[0]); ++i)
        {
            glm::vec3 newPos = lightPositions[i] + glm::vec3(sin<function id='47'>(glfwGetTime(</function>) * 5.0) * 5.0, 0.0, 0.0);
            newPos = lightPositions[i];
            pbrShader.setVec3(&quot;lightPositions[&quot; + std::to_string(i) + &quot;]&quot;, newPos);
            pbrShader.setVec3(&quot;lightColors[&quot; + std::to_string(i) + &quot;]&quot;, lightColors[i]);

            model = glm::mat4(1.0f);
            model =<function id='55'> glm::translate(</function>model, newPos);
            model =<function id='56'> glm::scale(</function>model, glm::vec3(0.5f));
            pbrShader.setMat4(&quot;model&quot;, model);
            renderSphere();
        }

        // render skybox (render as last to prevent overdraw)
        backgroundShader.use();
        backgroundShader.setMat4(&quot;view&quot;, view);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, envCubemap);
        renderCube();

        /*    equirectangularToCubemapShader.Use();
        equirectangularToCubemapShader.setMat4(&quot;view&quot;, view&quot;);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, hdrTexture);
        renderCube();*/


        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

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

// renders (and builds at first invocation) a sphere
// -------------------------------------------------
unsigned int sphereVAO = 0;
unsigned int indexCount;
void renderSphere()
{
    if (sphereVAO == 0)
    {
       <function id='33'> glGenVertexArrays(</function>1, &amp;sphereVAO);

        unsigned int vbo, ebo;
       <function id='12'> glGenBuffers(</function>1, &amp;vbo);
       <function id='12'> glGenBuffers(</function>1, &amp;ebo);

        std::vector&lt;glm::vec3&gt; positions;
        std::vector&lt;glm::vec2&gt; uv;
        std::vector&lt;glm::vec3&gt; normals;
        std::vector&lt;unsigned int&gt; indices;

        const unsigned int X_SEGMENTS = 64;
        const unsigned int Y_SEGMENTS = 64;
        const float PI = 3.14159265359;
        for (unsigned int x = 0; x &lt;= X_SEGMENTS; ++x)
        {
            for (unsigned int y = 0; y &lt;= Y_SEGMENTS; ++y)
            {
                float xSegment = (float)x / (float)X_SEGMENTS;
                float ySegment = (float)y / (float)Y_SEGMENTS;
                float xPos = std::cos(xSegment * 2.0f * PI) * std::sin(ySegment * PI);
                float yPos = std::cos(ySegment * PI);
                float zPos = std::sin(xSegment * 2.0f * PI) * std::sin(ySegment * PI);

                positions.push_back(glm::vec3(xPos, yPos, zPos));
                uv.push_back(glm::vec2(xSegment, ySegment));
                normals.push_back(glm::vec3(xPos, yPos, zPos));
            }
        }

        bool oddRow = false;
        for (unsigned int y = 0; y &lt; Y_SEGMENTS; ++y)
        {
            if (!oddRow) // even rows: y == 0, y == 2; and so on
            {
                for (unsigned int x = 0; x &lt;= X_SEGMENTS; ++x)
                {
                    indices.push_back(y * (X_SEGMENTS + 1) + x);
                    indices.push_back((y + 1) * (X_SEGMENTS + 1) + x);
                }
            }
            else
            {
                for (int x = X_SEGMENTS; x &gt;= 0; --x)
                {
                    indices.push_back((y + 1) * (X_SEGMENTS + 1) + x);
                    indices.push_back(y * (X_SEGMENTS + 1) + x);
                }
            }
            oddRow = !oddRow;
        }
        indexCount = indices.size();

        std::vector&lt;float&gt; data;
        for (unsigned int i = 0; i &lt; positions.size(); ++i)
        {
            data.push_back(positions[i].x);
            data.push_back(positions[i].y);
            data.push_back(positions[i].z);
            if (normals.size() &gt; 0)
            {
                data.push_back(normals[i].x);
                data.push_back(normals[i].y);
                data.push_back(normals[i].z);
            }
            if (uv.size() &gt; 0)
            {
                data.push_back(uv[i].x);
                data.push_back(uv[i].y);
            }
        }
       <function id='27'> glBindVertexArray(</function>sphereVAO);
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, vbo);
       <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, data.size() * sizeof(float), &amp;data[0], GL_STATIC_DRAW);
       <function id='32'> glBindBuffer(</function>GL_ELEMENT_ARRAY_BUFFER, ebo);
       <function id='31'> glBufferData(</function>GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(unsigned int), &amp;indices[0], GL_STATIC_DRAW);
        unsigned int stride = (3 + 2 + 3) * sizeof(float);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
       <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, stride, (void*)0);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>1);
       <function id='30'> glVertexAttribPointer(</function>1, 3, GL_FLOAT, GL_FALSE, stride, (void*)(3 * sizeof(float)));
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>2);
       <function id='30'> glVertexAttribPointer(</function>2, 2, GL_FLOAT, GL_FALSE, stride, (void*)(6 * sizeof(float)));
    }

   <function id='27'> glBindVertexArray(</function>sphereVAO);
   <function id='2'> glDrawElements(</function>GL_TRIANGLE_STRIP, indexCount, GL_UNSIGNED_INT, 0);
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
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>