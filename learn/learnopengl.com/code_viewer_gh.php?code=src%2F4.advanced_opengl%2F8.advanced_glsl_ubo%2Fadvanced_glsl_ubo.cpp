


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/4.advanced_opengl/8.advanced_glsl_ubo/advanced_glsl_ubo.cpp</title>
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
    Shader shaderRed("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.advanced_glsl.vs' target='_blank'>8.advanced_glsl.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.red.fs' target='_blank'>8.red.fs</a>");
    Shader shaderGreen("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.advanced_glsl.vs' target='_blank'>8.advanced_glsl.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.green.fs' target='_blank'>8.green.fs</a>");
    Shader shaderBlue("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.advanced_glsl.vs' target='_blank'>8.advanced_glsl.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.blue.fs' target='_blank'>8.blue.fs</a>");
    Shader shaderYellow("<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.advanced_glsl.vs' target='_blank'>8.advanced_glsl.vs</a>", "<a href='code_viewer_gh.php?code=src/4.advanced_opengl/8.advanced_glsl_ubo/8.yellow.fs' target='_blank'>8.yellow.fs</a>");
    
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
        -0.5f,  0.5f, -0.5f, 
    };
    // cube VAO
    unsigned int cubeVAO, cubeVBO;
   <function id='33'> glGenVertexArrays(</function>1, &amp;cubeVAO);
   <function id='12'> glGenBuffers(</function>1, &amp;cubeVBO);
   <function id='27'> glBindVertexArray(</function>cubeVAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, cubeVBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(cubeVertices), &amp;cubeVertices, GL_STATIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);

    // configure a uniform buffer object
    // ---------------------------------
    // first. We get the relevant block indices
    unsigned int uniformBlockIndexRed =<function id='94'> glGetUniformBlockIndex(</function>shaderRed.ID, &quot;Matrices&quot;);
    unsigned int uniformBlockIndexGreen =<function id='94'> glGetUniformBlockIndex(</function>shaderGreen.ID, &quot;Matrices&quot;);
    unsigned int uniformBlockIndexBlue =<function id='94'> glGetUniformBlockIndex(</function>shaderBlue.ID, &quot;Matrices&quot;);
    unsigned int uniformBlockIndexYellow =<function id='94'> glGetUniformBlockIndex(</function>shaderYellow.ID, &quot;Matrices&quot;);
    // then we link each shader's uniform block to this uniform binding point
   <function id='95'><function id='44'> glUniformB</function>lockBinding(</function>shaderRed.ID, uniformBlockIndexRed, 0);
   <function id='95'><function id='44'> glUniformB</function>lockBinding(</function>shaderGreen.ID, uniformBlockIndexGreen, 0);
   <function id='95'><function id='44'> glUniformB</function>lockBinding(</function>shaderBlue.ID, uniformBlockIndexBlue, 0);
   <function id='95'><function id='44'> glUniformB</function>lockBinding(</function>shaderYellow.ID, uniformBlockIndexYellow, 0);
    // Now actually create the buffer
    unsigned int uboMatrices;
   <function id='12'> glGenBuffers(</function>1, &amp;uboMatrices);
   <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, uboMatrices);
   <function id='31'> glBufferData(</function>GL_UNIFORM_BUFFER, 2 * sizeof(glm::mat4), NULL, GL_STATIC_DRAW);
   <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, 0);
    // define the range of the buffer that links to a uniform binding point
   <function id='97'><function id='32'> glBindBufferR</function>ange(</function>GL_UNIFORM_BUFFER, 0, uboMatrices, 0, 2 * sizeof(glm::mat4));

    // store the projection matrix (we only do this once now) (note: we're not using zoom anymore by changing the FoV)
    glm::mat4 projection =<function id='58'> glm::perspective(</function>45.0f, (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
   <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, uboMatrices);
   <function id='90'> glBufferSubData(</function>GL_UNIFORM_BUFFER, 0, sizeof(glm::mat4), glm::value_ptr(projection));
   <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, 0);
  
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

        // set the view and projection matrix in the uniform block - we only have to do this once per loop iteration.
        glm::mat4 view = camera.GetViewMatrix();
       <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, uboMatrices);
       <function id='90'> glBufferSubData(</function>GL_UNIFORM_BUFFER, sizeof(glm::mat4), sizeof(glm::mat4), glm::value_ptr(view));
       <function id='32'> glBindBuffer(</function>GL_UNIFORM_BUFFER, 0);

        // draw 4 cubes 
        // RED
       <function id='27'> glBindVertexArray(</function>cubeVAO);
        shaderRed.use();
        glm::mat4 model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-0.75f, 0.75f, 0.0f)); // move top-left
        shaderRed.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        // GREEN
        shaderGreen.use();
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.75f, 0.75f, 0.0f)); // move top-right
        shaderGreen.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        // YELLOW
        shaderYellow.use();
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(-0.75f, -0.75f, 0.0f)); // move bottom-left
        shaderYellow.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);
        // BLUE
        shaderBlue.use();
        model = glm::mat4(1.0f);
        model =<function id='55'> glm::translate(</function>model, glm::vec3(0.75f, -0.75f, 0.0f)); // move bottom-right
        shaderBlue.setMat4(&quot;model&quot;, model);
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 36);

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
       <function id='24'> glfwSwapBuffers(</function>window);
       <function id='23'> glfwPollEvents(</function>);
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(1, &amp;cubeVAO);
    glDeleteBuffers(1, &amp;cubeVBO);

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
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>