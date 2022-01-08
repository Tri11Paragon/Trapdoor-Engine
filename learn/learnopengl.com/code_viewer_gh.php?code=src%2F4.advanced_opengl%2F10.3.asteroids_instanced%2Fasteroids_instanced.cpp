


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/4.advanced_opengl/10.3.asteroids_instanced/asteroids_instanced.cpp</title>
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
Camera camera(glm::vec3(0.0f, 0.0f, 155.0f));
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
    Shader asteroidShader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.3.asteroids_instanced/10.3.asteroids.vs' target='_blank'>10.3.asteroids.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.3.asteroids_instanced/10.3.asteroids.fs' target='_blank'>10.3.asteroids.fs</a>");
    Shader planetShader("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.3.asteroids_instanced/10.3.planet.vs' target='_blank'>10.3.planet.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/10.3.asteroids_instanced/10.3.planet.fs' target='_blank'>10.3.planet.fs</a>");

    // load models
    // -----------
    Model rock(FileSystem::getPath(&quot;resources/objects/rock/rock.obj&quot;));
    Model planet(FileSystem::getPath(&quot;resources/objects/planet/planet.obj&quot;));

    // generate a large list of semi-random model transformation matrices
    // ------------------------------------------------------------------
    unsigned int amount = 100000;
    glm::mat4* modelMatrices;
    modelMatrices = new glm::mat4[amount];
    srand<function id='47'>(glfwGetTime(</function>)); // initialize random seed	
    float radius = 150.0;
    float offset = 25.0f;
    for (unsigned int i = 0; i &lt; amount; i++)
    {
        glm::mat4 model = glm::mat4(1.0f);
        // 1. translation: displace along circle with 'radius' in range [-offset, offset]
        float angle = (float)i / (float)amount * 360.0f;
        float displacement = (rand() % (int)(2 * offset * 100)) / 100.0f - offset;
        float x = sin(angle) * radius + displacement;
        displacement = (rand() % (int)(2 * offset * 100)) / 100.0f - offset;
        float y = displacement * 0.4f; // keep height of asteroid field smaller compared to width of x and z
        displacement = (rand() % (int)(2 * offset * 100)) / 100.0f - offset;
        float z = cos(angle) * radius + displacement;
        model =<function id='55'> glm::translate(</function>model, glm::vec3(x, y, z));

        // 2. scale: Scale between 0.05 and 0.25f
        float scale = (rand() % 20) / 100.0f + 0.05;
        model =<function id='56'> glm::scale(</function>model, glm::vec3(scale));

        // 3. rotation: add random rotation around a (semi)randomly picked rotation axis vector
        float rotAngle = (rand() % 360);
        model =<function id='57'> glm::rotate(</function>model, rotAngle, glm::vec3(0.4f, 0.6f, 0.8f));

        // 4. now add to list of matrices
        modelMatrices[i] = model;
    }

    // configure instanced array
    // -------------------------
    unsigned int buffer;
   <function id='12'> glGenBuffers(</function>1, &amp;buffer);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, buffer);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, amount * sizeof(glm::mat4), &amp;modelMatrices[0], GL_STATIC_DRAW);

    // set transformation matrices as an instance vertex attribute (with divisor 1)
    // note: we're cheating a little by taking the, now publicly declared, VAO of the model's mesh(es) and adding new vertexAttribPointers
    // normally you'd want to do this in a more organized fashion, but for learning purposes this will do.
    // -----------------------------------------------------------------------------------------------------------------------------------
    for (unsigned int i = 0; i &lt; rock.meshes.size(); i++)
    {
        unsigned int VAO = rock.meshes[i].VAO;
       <function id='27'> glBindVertexArray(</function>VAO);
        // set attribute pointers for matrix (4 times vec4)
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>3);
       <function id='30'> glVertexAttribPointer(</function>3, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)0);
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>4);
       <function id='30'> glVertexAttribPointer(</function>4, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(sizeof(glm::vec4)));
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>5);
       <function id='30'> glVertexAttribPointer(</function>5, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(2 * sizeof(glm::vec4)));
       <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>6);
       <function id='30'> glVertexAttribPointer(</function>6, 4, GL_FLOAT, GL_FALSE, sizeof(glm::mat4), (void*)(3 * sizeof(glm::vec4)));

       <function id='100'> glVertexAttribDivisor(</function>3, 1);
       <function id='100'> glVertexAttribDivisor(</function>4, 1);
       <function id='100'> glVertexAttribDivisor(</function>5, 1);
       <function id='100'> glVertexAttribDivisor(</function>6, 1);

       <function id='27'> glBindVertexArray(</function>0);
    }

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

        // configure transformation matrices
        glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>45.0f), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 1000.0f);
        glm::mat4 view = camera.GetViewMatrix();
        asteroidShader.use();
        asteroidShader.setMat4(&quot;projection&quot;, projection);
        asteroidShader.setMat4(&quot;view&quot;, view);
        planetShader.use();
        planetShader.setMat4(&quot;projection&quot;, projection);
        planetShader.setMat4(&quot;view&quot;, view);
        
        // draw planet
        glm::mat4 model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, -3.0f, 0.0f));
        model =<function id='56'> glm::scale(</function>model, glm::vec3(4.0f, 4.0f, 4.0f));
        planetShader.setMat4(&quot;model&quot;, model);
        planet.Draw(planetShader);

        // draw meteorites
        asteroidShader.use();
        asteroidShader.setInt(&quot;texture_diffuse1&quot;, 0);
       <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, rock.textures_loaded[0].id); // note: we also made the textures_loaded vector public (instead of private) from the model class.
        for (unsigned int i = 0; i &lt; rock.meshes.size(); i++)
        {
           <function id='27'> glBindVertexArray(</function>rock.meshes[i].VAO);
           <function id='99'><function id='2'> glDrawElementsI</function>nstanced(</function>GL_TRIANGLES, rock.meshes[i].indices.size(), GL_UNSIGNED_INT, 0, amount);
           <function id='27'> glBindVertexArray(</function>0);
        }

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