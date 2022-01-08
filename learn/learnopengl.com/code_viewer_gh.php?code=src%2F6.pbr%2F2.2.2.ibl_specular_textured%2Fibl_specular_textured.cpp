


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/6.pbr/2.2.2.ibl_specular_textured/ibl_specular_textured.cpp</title>
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
void renderSphere();
void renderCube();
void renderQuad();

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
    // set depth function to less than AND equal for skybox depth trick.
   <function id='66'> glDepthFunc(</function>GL_LEQUAL);
    // enable seamless cubemap sampling for lower mip levels in the pre-filter map.
   <function id='60'> glEnable(</function>GL_TEXTURE_CUBE_MAP_SEAMLESS);

    // build and compile shaders
    // -------------------------
    Shader pbrShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.pbr.vs' target='_blank'>2.2.2.pbr.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.pbr.fs' target='_blank'>2.2.2.pbr.fs</a>");
    Shader equirectangularToCubemapShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.cubemap.vs' target='_blank'>2.2.2.cubemap.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.equirectangular_to_cubemap.fs' target='_blank'>2.2.2.equirectangular_to_cubemap.fs</a>");
    Shader irradianceShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.cubemap.vs' target='_blank'>2.2.2.cubemap.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.irradiance_convolution.fs' target='_blank'>2.2.2.irradiance_convolution.fs</a>");
    Shader prefilterShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.cubemap.vs' target='_blank'>2.2.2.cubemap.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.prefilter.fs' target='_blank'>2.2.2.prefilter.fs</a>");
    Shader brdfShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.brdf.vs' target='_blank'>2.2.2.brdf.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.brdf.fs' target='_blank'>2.2.2.brdf.fs</a>");
    Shader backgroundShader("<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.background.vs' target='_blank'>2.2.2.background.vs</a>", "<a href='code_viewer_gh.php?code=src/6.pbr/2.2.2.ibl_specular_textured/2.2.2.background.fs' target='_blank'>2.2.2.background.fs</a>");

    pbrShader.use();
    pbrShader.setInt(&quot;irradianceMap&quot;, 0);
    pbrShader.setInt(&quot;prefilterMap&quot;, 1);
    pbrShader.setInt(&quot;brdfLUT&quot;, 2);
    pbrShader.setInt(&quot;albedoMap&quot;, 3);
    pbrShader.setInt(&quot;normalMap&quot;, 4);
    pbrShader.setInt(&quot;metallicMap&quot;, 5);
    pbrShader.setInt(&quot;roughnessMap&quot;, 6);
    pbrShader.setInt(&quot;aoMap&quot;, 7);

    backgroundShader.use();
    backgroundShader.setInt(&quot;environmentMap&quot;, 0);

    // load PBR material textures
    // --------------------------
    // rusted iron
    unsigned int ironAlbedoMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/rusted_iron/albedo.png&quot;).c_str());
    unsigned int ironNormalMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/rusted_iron/normal.png&quot;).c_str());
    unsigned int ironMetallicMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/rusted_iron/metallic.png&quot;).c_str());
    unsigned int ironRoughnessMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/rusted_iron/roughness.png&quot;).c_str());
    unsigned int ironAOMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/rusted_iron/ao.png&quot;).c_str());

    // gold
    unsigned int goldAlbedoMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/gold/albedo.png&quot;).c_str());
    unsigned int goldNormalMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/gold/normal.png&quot;).c_str());
    unsigned int goldMetallicMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/gold/metallic.png&quot;).c_str());
    unsigned int goldRoughnessMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/gold/roughness.png&quot;).c_str());
    unsigned int goldAOMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/gold/ao.png&quot;).c_str());

    // grass
    unsigned int grassAlbedoMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/grass/albedo.png&quot;).c_str());
    unsigned int grassNormalMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/grass/normal.png&quot;).c_str());
    unsigned int grassMetallicMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/grass/metallic.png&quot;).c_str());
    unsigned int grassRoughnessMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/grass/roughness.png&quot;).c_str());
    unsigned int grassAOMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/grass/ao.png&quot;).c_str());

    // plastic
    unsigned int plasticAlbedoMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/plastic/albedo.png&quot;).c_str());
    unsigned int plasticNormalMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/plastic/normal.png&quot;).c_str());
    unsigned int plasticMetallicMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/plastic/metallic.png&quot;).c_str());
    unsigned int plasticRoughnessMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/plastic/roughness.png&quot;).c_str());
    unsigned int plasticAOMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/plastic/ao.png&quot;).c_str());

    // wall
    unsigned int wallAlbedoMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/wall/albedo.png&quot;).c_str());
    unsigned int wallNormalMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/wall/normal.png&quot;).c_str());
    unsigned int wallMetallicMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/wall/metallic.png&quot;).c_str());
    unsigned int wallRoughnessMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/wall/roughness.png&quot;).c_str());
    unsigned int wallAOMap = loadTexture(FileSystem::getPath(&quot;resources/textures/pbr/wall/ao.png&quot;).c_str());

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
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR); // enable pre-filter mipmap sampling (combatting visible dots artifact)
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

    // then let OpenGL generate mipmaps from first mip face (combatting visible dots artifact)
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, envCubemap);
   <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_CUBE_MAP);

    // pbr: create an irradiance cubemap, and re-scale capture FBO to irradiance scale.
    // --------------------------------------------------------------------------------
    unsigned int irradianceMap;
   <function id='50'> glGenTextures(</function>1, &amp;irradianceMap);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, irradianceMap);
    for (unsigned int i = 0; i &lt; 6; ++i)
    {
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 32, 32, 0, GL_RGB, GL_FLOAT, nullptr);
    }
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, captureRBO);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);

    // pbr: solve diffuse integral by convolution to create an irradiance (cube)map.
    // -----------------------------------------------------------------------------
    irradianceShader.use();
    irradianceShader.setInt(&quot;environmentMap&quot;, 0);
    irradianceShader.setMat4(&quot;projection&quot;, captureProjection);
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, envCubemap);

   <function id='22'> glViewport(</function>0, 0, 32, 32); // don't forget to configure the viewport to the capture dimensions.
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
    for (unsigned int i = 0; i &lt; 6; ++i)
    {
        irradianceShader.setMat4(&quot;view&quot;, captureViews[i]);
       <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, irradianceMap, 0);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderCube();
    }
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // pbr: create a pre-filter cubemap, and re-scale capture FBO to pre-filter scale.
    // --------------------------------------------------------------------------------
    unsigned int prefilterMap;
   <function id='50'> glGenTextures(</function>1, &amp;prefilterMap);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, prefilterMap);
    for (unsigned int i = 0; i &lt; 6; ++i)
    {
       <function id='52'> glTexImage2D(</function>GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 128, 128, 0, GL_RGB, GL_FLOAT, nullptr);
    }
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR); // be sure to set minification filter to mip_linear 
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // generate mipmaps for the cubemap so OpenGL automatically allocates the required memory.
   <function id='51'> glGenerateMipmap(</function>GL_TEXTURE_CUBE_MAP);

    // pbr: run a quasi monte-carlo simulation on the environment lighting to create a prefilter (cube)map.
    // ----------------------------------------------------------------------------------------------------
    prefilterShader.use();
    prefilterShader.setInt(&quot;environmentMap&quot;, 0);
    prefilterShader.setMat4(&quot;projection&quot;, captureProjection);
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, envCubemap);

   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
    unsigned int maxMipLevels = 5;
    for (unsigned int mip = 0; mip &lt; maxMipLevels; ++mip)
    {
        // reisze framebuffer according to mip-level size.
        unsigned int mipWidth = 128 * std::pow(0.5, mip);
        unsigned int mipHeight = 128 * std::pow(0.5, mip);
       <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, captureRBO);
       <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipWidth, mipHeight);
       <function id='22'> glViewport(</function>0, 0, mipWidth, mipHeight);

        float roughness = (float)mip / (float)(maxMipLevels - 1);
        prefilterShader.setFloat(&quot;roughness&quot;, roughness);
        for (unsigned int i = 0; i &lt; 6; ++i)
        {
            prefilterShader.setMat4(&quot;view&quot;, captureViews[i]);
           <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, prefilterMap, mip);

           <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderCube();
        }
    }
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

    // pbr: generate a 2D LUT from the BRDF equations used.
    // ----------------------------------------------------
    unsigned int brdfLUTTexture;
   <function id='50'> glGenTextures(</function>1, &amp;brdfLUTTexture);

    // pre-allocate enough memory for the LUT texture.
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, brdfLUTTexture);
   <function id='52'> glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RG16F, 512, 512, 0, GL_RG, GL_FLOAT, 0);
    // be sure to set wrapping mode to GL_CLAMP_TO_EDGE
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    // then re-configure capture framebuffer object and render screen-space quad with BRDF shader.
   <function id='77'> glBindFramebuffer(</function>GL_FRAMEBUFFER, captureFBO);
   <function id='83'> glBindRenderbuffer(</function>GL_RENDERBUFFER, captureRBO);
   <function id='88'> glRenderbufferStorage(</function>GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 512, 512);
   <function id='81'> glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, brdfLUTTexture, 0);

   <function id='22'> glViewport(</function>0, 0, 512, 512);
    brdfShader.use();
   <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    renderQuad();

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
        glm::mat4 model = glm::mat4(1.0f);
        glm::mat4 view = camera.GetViewMatrix();
        pbrShader.setMat4(&quot;view&quot;, view);
        pbrShader.setVec3(&quot;camPos&quot;, camera.Position);

        // bind pre-computed IBL data
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, irradianceMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE1);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_CUBE_MAP, prefilterMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE2);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, brdfLUTTexture);

        // rusted iron
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ironAlbedoMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE4);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ironNormalMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE5);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ironMetallicMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE6);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ironRoughnessMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE7);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ironAOMap);

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-5.0, 0.0, 2.0));
        pbrShader.setMat4(&quot;model&quot;, model);
        renderSphere();

        // gold
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, goldAlbedoMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE4);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, goldNormalMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE5);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, goldMetallicMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE6);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, goldRoughnessMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE7);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, goldAOMap);

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-3.0, 0.0, 2.0));
        pbrShader.setMat4(&quot;model&quot;, model);
        renderSphere();

        // grass
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, grassAlbedoMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE4);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, grassNormalMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE5);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, grassMetallicMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE6);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, grassRoughnessMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE7);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, grassAOMap);

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-1.0, 0.0, 2.0));
        pbrShader.setMat4(&quot;model&quot;, model);
        renderSphere();

        // plastic
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, plasticAlbedoMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE4);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, plasticNormalMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE5);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, plasticMetallicMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE6);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, plasticRoughnessMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE7);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, plasticAOMap);

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(1.0, 0.0, 2.0));
        pbrShader.setMat4(&quot;model&quot;, model);
        renderSphere();

        // wall
       <function id='49'> glActiveTexture(</function>GL_TEXTURE3);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, wallAlbedoMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE4);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, wallNormalMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE5);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, wallMetallicMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE6);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, wallRoughnessMap);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE7);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, wallAOMap);

        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(3.0, 0.0, 2.0));
        pbrShader.setMat4(&quot;model&quot;, model);
        renderSphere();

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
        /<function id='48'>/glBindTexture(</function>GL_TEXTURE_CUBE_MAP, irradianceMap); // display irradiance map
        /<function id='48'>/glBindTexture(</function>GL_TEXTURE_CUBE_MAP, prefilterMap); // display prefilter map
        renderCube();

        // render BRDF map to screen
        //brdfShader.Use();
        //renderQuad();


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