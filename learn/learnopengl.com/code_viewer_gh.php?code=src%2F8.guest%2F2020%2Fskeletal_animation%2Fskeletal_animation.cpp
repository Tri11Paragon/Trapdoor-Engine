


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/8.guest/2020/skeletal_animation/skeletal_animation.cpp</title>
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

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader_m.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/camera.h' target='_blank'>learnopengl/camera.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/animator.h' target='_blank'>learnopengl/animator.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/model_animation.h' target='_blank'>learnopengl/model_animation.h</a>&gt;



#include &lt;iostream&gt;


void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void mouse_callback(GLFWwindow* window, double xpos, double ypos);
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset);
void processInput(GLFWwindow* window);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

// camera
Camera camera(glm::vec3(0.0f, 0.0f, 3.0f));
float lastX = SCR_WIDTH / 2.0f;
float lastY = SCR_HEIGHT / 2.0f;
bool firstMouse = true;

// timing
float deltaTime = 0.0f;
float lastFrame = 0.0f;

int main()
{
	// glfw: initialize and configure
	// ------------------------------
<function id='17'>	glfwInit(</function>);
<function id='18'>	glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MAJOR, 3);
<function id='18'>	glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MINOR, 3);
<function id='18'>	glfwWindowHint(</function>GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

#ifdef __APPLE__
<function id='18'>	glfwWindowHint(</function>GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif

	// glfw window creation
	// --------------------
	GLFWwindow* window =<function id='20'> glfwCreateWindow(</function>SCR_WIDTH, SCR_HEIGHT, &quot;LearnOpenGL&quot;, NULL, NULL);
	if (window == NULL)
	{
		std::cout &lt;&lt; &quot;Failed to create GLFW window&quot; &lt;&lt; std::endl;
	<function id='25'>	glfwTerminate(</function>);
		return -1;
	}
<function id='19'>	glfwMakeContextCurrent(</function>window);
	glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);
	glfwSetCursorPosCallback(window, mouse_callback);
<function id='64'>	glfwSetScrollCallback(</function>window, scroll_callback);

	// tell GLFW to capture our mouse
	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

	// glad: load all OpenGL function pointers
	// ---------------------------------------
	if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
	{
		std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
		return -1;
	}

	// tell stb_image.h to flip loaded texture's on the y-axis (before loading model).
	stbi_set_flip_vertically_on_load(true);

	// configure global opengl state
	// -----------------------------
<function id='60'>	glEnable(</function>GL_DEPTH_TEST);

	// build and compile shaders
	// -------------------------
	Shader ourShader("<a href='code_viewer_gh.php?code=src/8.guest/2020/skeletal_animation/anim_model.vs' target='_blank'>anim_model.vs</a>", "<a href='code_viewer_gh.php?code=src/8.guest/2020/skeletal_animation/anim_model.fs' target='_blank'>anim_model.fs</a>");

	
	// load models
	// -----------
	Model ourModel(FileSystem::getPath(&quot;resources/objects/vampire/dancing_vampire.dae&quot;));
	Animation danceAnimation(FileSystem::getPath(&quot;resources/objects/vampire/dancing_vampire.dae&quot;),&amp;ourModel);
	Animator animator(&amp;danceAnimation);


	// draw in wireframe
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
		animator.UpdateAnimation(deltaTime);
		
		// render
		// ------
	<function id='13'><function id='10'>	glClearC</function>olor(</function>0.05f, 0.05f, 0.05f, 1.0f);
	<function id='10'>	glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// don't forget to enable shader before setting uniforms
		ourShader.use();

		// view/projection transformations
		glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
		glm::mat4 view = camera.GetViewMatrix();
		ourShader.setMat4(&quot;projection&quot;, projection);
		ourShader.setMat4(&quot;view&quot;, view);

		auto transforms = animator.GetPoseTransforms();
		for (int i = 0; i &lt; transforms.size(); ++i)
			ourShader.setMat4(&quot;finalBonesTransformations[&quot; + std::to_string(i) + &quot;]&quot;, transforms[i]);


		// render the loaded model
		glm::mat4 model = glm::mat4(1.0f);
		model =<function id='55'> glm::translate(</function>model, glm::vec3(0.0f, -0.4f, 0.0f)); // translate it down so it's at the center of the scene
		model =<function id='56'> glm::scale(</function>model, glm::vec3(.5f, .5f, .5f));	// it's a bit too big for our scene, so scale it down
		ourShader.setMat4(&quot;model&quot;, model);
		ourModel.Draw(ourShader);


		// glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
		// -------------------------------------------------------------------------------
	<function id='24'>	glfwSwapBuffers(</function>window);
	<function id='23'>	glfwPollEvents(</function>);
	}

	// glfw: terminate, clearing all previously allocated GLFW resources.
	// ------------------------------------------------------------------
<function id='25'>	glfwTerminate(</function>);
	return 0;
}

// process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
// ---------------------------------------------------------------------------------------------------------
void processInput(GLFWwindow* window)
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
<function id='22'>	glViewport(</function>0, 0, width, height);
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