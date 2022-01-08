


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/8.guest/2020/oit/weighted_blended.cpp</title>
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

#include &lt;iostream&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void mouse_callback(GLFWwindow* window, double xpos, double ypos);
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset);
void process_input(GLFWwindow *window);
glm::mat4 calculate_model_matrix(const glm::vec3&amp; position, const glm::vec3&amp; rotation = glm::vec3(0.0f), const glm::vec3&amp; scale = glm::vec3(1.0f));

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

int main(int argc, char* argv[])
{
	// glfw: initialize and configure
	// ------------------------------
<function id='17'>	glfwInit(</function>);
<function id='18'>	glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MAJOR, 4);
<function id='18'>	glfwWindowHint(</function>GLFW_CONTEXT_VERSION_MINOR, 0);
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

	// build and compile shaders
	// -------------------------
	Shader solidShader("<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/solid.vs' target='_blank'>solid.vs</a>", "<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/solid.fs' target='_blank'>solid.fs</a>");
	Shader transparentShader("<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/transparent.vs' target='_blank'>transparent.vs</a>", "<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/transparent.fs' target='_blank'>transparent.fs</a>");
	Shader compositeShader("<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/composite.vs' target='_blank'>composite.vs</a>", "<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/composite.fs' target='_blank'>composite.fs</a>");
	Shader screenShader("<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/screen.vs' target='_blank'>screen.vs</a>", "<a href='code_viewer_gh.php?code=src/8.guest/2020/oit/screen.fs' target='_blank'>screen.fs</a>");

	// set up vertex data (and buffer(s)) and configure vertex attributes
	// ------------------------------------------------------------------
	float quadVertices[] = {
		// positions		// uv
		-1.0f, -1.0f, 0.0f,	0.0f, 0.0f,
		 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
		 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,

		 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
		-1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
		-1.0f, -1.0f, 0.0f, 0.0f, 0.0f
	};

	// quad VAO
	unsigned int quadVAO, quadVBO;
<function id='33'>	glGenVertexArrays(</function>1, &amp;quadVAO);
<function id='12'>	glGenBuffers(</function>1, &amp;quadVBO);
<function id='27'>	glBindVertexArray(</function>quadVAO);
<function id='32'>	glBindBuffer(</function>GL_ARRAY_BUFFER, quadVBO);
<function id='31'>	glBufferData(</function>GL_ARRAY_BUFFER, sizeof(quadVertices), quadVertices, GL_STATIC_DRAW);
<function id='29'><function id='60'>	glEnableV</function>ertexAttribArray(</function>0);
<function id='30'>	glVertexAttribPointer(</function>0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
<function id='29'><function id='60'>	glEnableV</function>ertexAttribArray(</function>1);
<function id='30'>	glVertexAttribPointer(</function>1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
<function id='27'>	glBindVertexArray(</function>0);

	// set up framebuffers and their texture attachments
	// ------------------------------------------------------------------
	unsigned int opaqueFBO, transparentFBO;
<function id='76'>	glGenFramebuffers(</function>1, &amp;opaqueFBO);
<function id='76'>	glGenFramebuffers(</function>1, &amp;transparentFBO);

	// set up attachments for opaque framebuffer
	unsigned int opaqueTexture;
<function id='50'>	glGenTextures(</function>1, &amp;opaqueTexture);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, opaqueTexture);
<function id='52'>	glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_HALF_FLOAT, NULL);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, 0);

	unsigned int depthTexture;
<function id='50'>	glGenTextures(</function>1, &amp;depthTexture);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, depthTexture);
<function id='52'>	glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SCR_WIDTH, SCR_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, 0);

<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, opaqueFBO);
<function id='81'>	glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, opaqueTexture, 0);
<function id='81'>	glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	
	if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		std::cout &lt;&lt; &quot;ERROR::FRAMEBUFFER:: Opaque framebuffer is not complete!&quot; &lt;&lt; std::endl;

<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

	// set up attachments for transparent framebuffer
	unsigned int accumTexture;
<function id='50'>	glGenTextures(</function>1, &amp;accumTexture);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, accumTexture);
<function id='52'>	glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_RGBA16F, SCR_WIDTH, SCR_HEIGHT, 0, GL_RGBA, GL_HALF_FLOAT, NULL);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, 0);

	unsigned int revealTexture;
<function id='50'>	glGenTextures(</function>1, &amp;revealTexture);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, revealTexture);
<function id='52'>	glTexImage2D(</function>GL_TEXTURE_2D, 0, GL_R8, SCR_WIDTH, SCR_HEIGHT, 0, GL_RED, GL_FLOAT, NULL);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
<function id='15'>	glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, 0);

<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, transparentFBO);
<function id='81'>	glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, accumTexture, 0);
<function id='81'>	glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, revealTexture, 0);
<function id='81'>	glFramebufferTexture2D(</function>GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0); // opaque framebuffer's depth texture

	const GLenum transparentDrawBuffers[] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
	glDrawBuffers(2, transparentDrawBuffers);

	if <function id='79'>(glCheckFramebufferStatus(</function>GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		std::cout &lt;&lt; &quot;ERROR::FRAMEBUFFER:: Transparent framebuffer is not complete!&quot; &lt;&lt; std::endl;

<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);

	// set up transformation matrices
	// ------------------------------------------------------------------
	glm::mat4 redModelMat = calculate_model_matrix(glm::vec3(0.0f, 0.0f, 1.0f));
	glm::mat4 greenModelMat = calculate_model_matrix(glm::vec3(0.0f, 0.0f, 0.0f));
	glm::mat4 blueModelMat = calculate_model_matrix(glm::vec3(0.0f, 0.0f, 2.0f));

	// set up intermediate variables
	// ------------------------------------------------------------------
	glm::vec4 zeroFillerVec(0.0f);
	glm::vec4 oneFillerVec(1.0f);
	
	// render loop
	// -----------
	while (<function id='14'>!glfwWindowShouldClose(</function>window))
	{
		// per-frame time logic
		// --------------------
		float currentFrame =<function id='47'> glfwGetTime(</function>);
		deltaTime = currentFrame - lastFrame;
		lastFrame = currentFrame;

		// camera matrices
		glm::mat4 projection = glm::perspective<function id='63'>(glm::radians(</function>camera.Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 100.0f);
		glm::mat4 view = camera.GetViewMatrix();
		glm::mat4 vp = projection * view;

		// input
		// -----
		process_input(window);

		// render
		// ------

		// draw solid objects (solid pass)
		// ------

		// configure render states
	<function id='60'>	glEnable(</function>GL_DEPTH_TEST);
	<function id='66'>	glDepthFunc(</function>GL_LESS);
	<function id='65'>	glDepthMask(</function>GL_TRUE);
		glDisable(GL_BLEND);
	<function id='13'><function id='10'>	glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 0.0f);

		// bind opaque framebuffer to render solid objects
	<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, opaqueFBO);
	<function id='10'>	glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// use solid shader
		solidShader.use();

		// draw red quad
		solidShader.setMat4(&quot;mvp&quot;, vp * redModelMat);
		solidShader.setVec3(&quot;color&quot;, glm::vec3(1.0f, 0.0f, 0.0f));
	<function id='27'>	glBindVertexArray(</function>quadVAO);
	<function id='1'>	glDrawArrays(</function>GL_TRIANGLES, 0, 6);

		// draw transparent objects (transparent pass)
		// -----

		// configure render states
	<function id='65'>	glDepthMask(</function>GL_FALSE);
	<function id='60'>	glEnable(</function>GL_BLEND);
	<function id='70'>	glBlendFunci</function>(0, GL_ONE, GL_ONE);
	<function id='70'>	glBlendFunci</function>(1, GL_ZERO, GL_ONE_MINUS_SRC_COLOR);
	<function id='72'>	glBlendEquation(</function>GL_FUNC_ADD);

		// bind transparent framebuffer to render transparent objects
	<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, transparentFBO);
	<function id='10'>	glClearB</function>ufferfv(GL_COLOR, 0, &amp;zeroFillerVec[0]);
	<function id='10'>	glClearB</function>ufferfv(GL_COLOR, 1, &amp;oneFillerVec[0]);

		// use transparent shader
		transparentShader.use();

		// draw green quad
		transparentShader.setMat4(&quot;mvp&quot;, vp * greenModelMat);
		transparentShader.setVec4(&quot;color&quot;, glm::vec4(0.0f, 1.0f, 0.0f, 0.5f));
	<function id='27'>	glBindVertexArray(</function>quadVAO);
	<function id='1'>	glDrawArrays(</function>GL_TRIANGLES, 0, 6);

		// draw blue quad
		transparentShader.setMat4(&quot;mvp&quot;, vp * blueModelMat);
		transparentShader.setVec4(&quot;color&quot;, glm::vec4(0.0f, 0.0f, 1.0f, 0.5f));
	<function id='27'>	glBindVertexArray(</function>quadVAO);
	<function id='1'>	glDrawArrays(</function>GL_TRIANGLES, 0, 6);

		// draw composite image (composite pass)
		// -----

		// set render states
	<function id='66'>	glDepthFunc(</function>GL_ALWAYS);
	<function id='60'>	glEnable(</function>GL_BLEND);
	<function id='70'>	glBlendFunc(</function>GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// bind opaque framebuffer
	<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, opaqueFBO);

		// use composite shader
		compositeShader.use();

		// draw screen quad
	<function id='49'>	glActiveTexture(</function>GL_TEXTURE0);
	<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, accumTexture);
	<function id='49'>	glActiveTexture(</function>GL_TEXTURE1);
	<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, revealTexture);
	<function id='27'>	glBindVertexArray(</function>quadVAO);
	<function id='1'>	glDrawArrays(</function>GL_TRIANGLES, 0, 6);

		// draw to backbuffer (final pass)
		// -----

		// set render states
		glDisable(GL_DEPTH_TEST);
	<function id='65'>	glDepthMask(</function>GL_TRUE); // enable depth writes so<function id='10'> glClear </function>won't ignore clearing the depth buffer
		glDisable(GL_BLEND);

		// bind backbuffer
	<function id='77'>	glBindFramebuffer(</function>GL_FRAMEBUFFER, 0);
	<function id='13'><function id='10'>	glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 0.0f);
	<function id='10'>	glClear(</function>GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		// use screen shader
		screenShader.use();

		// draw final screen quad
	<function id='49'>	glActiveTexture(</function>GL_TEXTURE0);
	<function id='48'>	glBindTexture(</function>GL_TEXTURE_2D, opaqueTexture);
	<function id='27'>	glBindVertexArray(</function>quadVAO);
	<function id='1'>	glDrawArrays(</function>GL_TRIANGLES, 0, 6);

		// glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
		// -------------------------------------------------------------------------------
	<function id='24'>	glfwSwapBuffers(</function>window);
	<function id='23'>	glfwPollEvents(</function>);
	}

	// optional: de-allocate all resources once they've outlived their purpose:
	// ------------------------------------------------------------------------
	glDeleteVertexArrays(1, &amp;quadVAO);
	glDeleteBuffers(1, &amp;quadVBO);
	glDeleteTextures(1, &amp;opaqueTexture);
	glDeleteTextures(1, &amp;depthTexture);
	glDeleteTextures(1, &amp;accumTexture);
	glDeleteTextures(1, &amp;revealTexture);
<function id='80'>	glDeleteFramebuffers(</function>1, &amp;opaqueFBO);
<function id='80'>	glDeleteFramebuffers(</function>1, &amp;transparentFBO);

<function id='25'>	glfwTerminate(</function>);

	return EXIT_SUCCESS;
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

// process all input: query GLFW whether relevant keys are pressed/released this frame and react accordingly
// ---------------------------------------------------------------------------------------------------------
void process_input(GLFWwindow *window)
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

// generate a model matrix
// ---------------------------------------------------------------------------------------------------------
glm::mat4 calculate_model_matrix(const glm::vec3&amp; position, const glm::vec3&amp; rotation, const glm::vec3&amp; scale)
{
	glm::mat4 trans = glm::mat4(1.0f);

	trans =<function id='55'> glm::translate(</function>trans, position);
	trans =<function id='57'> glm::rotate(</function>trans,<function id='63'> glm::radians(</function>rotation.x), glm::vec3(1.0, 0.0, 0.0));
	trans =<function id='57'> glm::rotate(</function>trans,<function id='63'> glm::radians(</function>rotation.y), glm::vec3(0.0, 1.0, 0.0));
	trans =<function id='57'> glm::rotate(</function>trans,<function id='63'> glm::radians(</function>rotation.z), glm::vec3(0.0, 0.0, 1.0));
	trans =<function id='56'> glm::scale(</function>trans, scale);

	return trans;
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>