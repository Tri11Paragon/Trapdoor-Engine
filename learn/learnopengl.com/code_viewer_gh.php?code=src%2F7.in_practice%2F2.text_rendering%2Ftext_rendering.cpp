


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/7.in_practice/2.text_rendering/text_rendering.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#include &lt;iostream&gt;
#include &lt;map&gt;
#include &lt;string&gt;

#include &lt;glad/glad.h&gt;
#include &lt;GLFW/glfw3.h&gt;

#include &lt;glm/glm.hpp&gt;
#include &lt;glm/gtc/matrix_transform.hpp&gt;
#include &lt;glm/gtc/type_ptr.hpp&gt;

#include &lt;ft2build.h&gt;
#include FT_FREETYPE_H

#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/shader.h' target='_blank'>learnopengl/shader.h</a>&gt;

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void processInput(GLFWwindow *window);
void RenderText(Shader &amp;shader, std::string text, float x, float y, float scale, glm::vec3 color);

// settings
const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

/// Holds all state information relevant to a character as loaded using FreeType
struct Character {
    unsigned int TextureID; // ID handle of the glyph texture
    glm::ivec2   Size;      // Size of glyph
    glm::ivec2   Bearing;   // Offset from baseline to left/top of glyph
    unsigned int Advance;   // Horizontal offset to advance to next glyph
};

std::map&lt;GLchar, Character&gt; Characters;
unsigned int VAO, VBO;

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

    // glad: load all OpenGL function pointers
    // ---------------------------------------
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout &lt;&lt; &quot;Failed to initialize GLAD&quot; &lt;&lt; std::endl;
        return -1;
    }
    
    // OpenGL state
    // ------------
   <function id='60'> glEnable(</function>GL_CULL_FACE);
   <function id='60'> glEnable(</function>GL_BLEND);
   <function id='70'> glBlendFunc(</function>GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    // compile and setup the shader
    // ----------------------------
    Shader shader("<a href='code_viewer_gh.php?code=src/7.in_practice/2.text_rendering/text.vs' target='_blank'>text.vs</a>", "<a href='code_viewer_gh.php?code=src/7.in_practice/2.text_rendering/text.fs' target='_blank'>text.fs</a>");
    glm::mat4 projection =<function id='59'> glm::ortho(</function>0.0f, static_cast&lt;float&gt;(SCR_WIDTH), 0.0f, static_cast&lt;float&gt;(SCR_HEIGHT));
    shader.use();
   <function id='44'> glUniformM</function>atrix4fv<function id='45'>(glGetUniformLocation(</function>shader.ID, &quot;projection&quot;), 1, GL_FALSE, glm::value_ptr(projection));

    // FreeType
    // --------
    FT_Library ft;
    // All functions return a value different than 0 whenever an error occurred
    if (FT_Init_FreeType(&amp;ft))
    {
        std::cout &lt;&lt; &quot;ERROR::FREETYPE: Could not init FreeType Library&quot; &lt;&lt; std::endl;
        return -1;
    }

	// find path to font
    std::string font_name = FileSystem::getPath(&quot;resources/fonts/Antonio-Bold.ttf&quot;);
    if (font_name.empty())
    {
        std::cout &lt;&lt; &quot;ERROR::FREETYPE: Failed to load font_name&quot; &lt;&lt; std::endl;
        return -1;
    }
	
	// load font as face
    FT_Face face;
    if (FT_New_Face(ft, font_name.c_str(), 0, &amp;face)) {
        std::cout &lt;&lt; &quot;ERROR::FREETYPE: Failed to load font&quot; &lt;&lt; std::endl;
        return -1;
    }
    else {
        // set size to load glyphs as
        FT_Set_Pixel_Sizes(face, 0, 48);

        // disable byte-alignment restriction
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // load first 128 characters of ASCII set
        for (unsigned char c = 0; c &lt; 128; c++)
        {
            // Load character glyph 
            if (FT_Load_Char(face, c, FT_LOAD_RENDER))
            {
                std::cout &lt;&lt; &quot;ERROR::FREETYTPE: Failed to load Glyph&quot; &lt;&lt; std::endl;
                continue;
            }
            // generate texture
            unsigned int texture;
           <function id='50'> glGenTextures(</function>1, &amp;texture);
           <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, texture);
           <function id='52'> glTexImage2D(</function>
                GL_TEXTURE_2D,
                0,
                GL_RED,
                face-&gt;glyph-&gt;bitmap.width,
                face-&gt;glyph-&gt;bitmap.rows,
                0,
                GL_RED,
                GL_UNSIGNED_BYTE,
                face-&gt;glyph-&gt;bitmap.buffer
            );
            // set texture options
           <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
           <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
           <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
           <function id='15'> glTexParameteri</function>(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            // now store character for later use
            Character character = {
                texture,
                glm::ivec2(face-&gt;glyph-&gt;bitmap.width, face-&gt;glyph-&gt;bitmap.rows),
                glm::ivec2(face-&gt;glyph-&gt;bitmap_left, face-&gt;glyph-&gt;bitmap_top),
                static_cast&lt;unsigned int&gt;(face-&gt;glyph-&gt;advance.x)
            };
            Characters.insert(std::pair&lt;char, Character&gt;(c, character));
        }
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, 0);
    }
    // destroy FreeType once we're finished
    FT_Done_Face(face);
    FT_Done_FreeType(ft);

    
    // configure VAO/VBO for texture quads
    // -----------------------------------
   <function id='33'> glGenVertexArrays(</function>1, &amp;VAO);
   <function id='12'> glGenBuffers(</function>1, &amp;VBO);
   <function id='27'> glBindVertexArray(</function>VAO);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
   <function id='31'> glBufferData(</function>GL_ARRAY_BUFFER, sizeof(float) * 6 * 4, NULL, GL_DYNAMIC_DRAW);
   <function id='29'><function id='60'> glEnableV</function>ertexAttribArray(</function>0);
   <function id='30'> glVertexAttribPointer(</function>0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(float), 0);
   <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
   <function id='27'> glBindVertexArray(</function>0);

    // render loop
    // -----------
    while (<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        // input
        // -----
        processInput(window);

        // render
        // ------
       <function id='13'><function id='10'> glClearC</function>olor(</function>0.2f, 0.3f, 0.3f, 1.0f);
       <function id='10'> glClear(</function>GL_COLOR_BUFFER_BIT);

        RenderText(shader, &quot;This is sample text&quot;, 25.0f, 25.0f, 1.0f, glm::vec3(0.5, 0.8f, 0.2f));
        RenderText(shader, &quot;(C) LearnOpenGL.com&quot;, 540.0f, 570.0f, 0.5f, glm::vec3(0.3, 0.7f, 0.9f));
       
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
}

// glfw: whenever the window size changed (by OS or user resize) this callback function executes
// ---------------------------------------------------------------------------------------------
void framebuffer_size_callback(GLFWwindow* window, int width, int height)
{
    // make sure the viewport matches the new window dimensions; note that width and 
    // height will be significantly larger than specified on retina displays.
   <function id='22'> glViewport(</function>0, 0, width, height);
}


// render line of text
// -------------------
void RenderText(Shader &amp;shader, std::string text, float x, float y, float scale, glm::vec3 color)
{
    // activate corresponding render state	
    shader.use();
   <function id='44'> glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>shader.ID, &quot;textColor&quot;), color.x, color.y, color.z);
   <function id='49'> glActiveTexture(</function>GL_TEXTURE0);
   <function id='27'> glBindVertexArray(</function>VAO);

    // iterate through all characters
    std::string::const_iterator c;
    for (c = text.begin(); c != text.end(); c++) 
    {
        Character ch = Characters[*c];

        float xpos = x + ch.Bearing.x * scale;
        float ypos = y - (ch.Size.y - ch.Bearing.y) * scale;

        float w = ch.Size.x * scale;
        float h = ch.Size.y * scale;
        // update VBO for each character
        float vertices[6][4] = {
            { xpos,     ypos + h,   0.0f, 0.0f },            
            { xpos,     ypos,       0.0f, 1.0f },
            { xpos + w, ypos,       1.0f, 1.0f },

            { xpos,     ypos + h,   0.0f, 0.0f },
            { xpos + w, ypos,       1.0f, 1.0f },
            { xpos + w, ypos + h,   1.0f, 0.0f }           
        };
        // render glyph texture over quad
       <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, ch.TextureID);
        // update content of VBO memory
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, VBO);
       <function id='90'> glBufferSubData(</function>GL_ARRAY_BUFFER, 0, sizeof(vertices), vertices); // be sure to use<function id='90'> glBufferSubData </function>and not<function id='31'> glBufferData
</function>
       <function id='32'> glBindBuffer(</function>GL_ARRAY_BUFFER, 0);
        // render quad
       <function id='1'> glDrawArrays(</function>GL_TRIANGLES, 0, 6);
        // now advance cursors for next glyph (note that advance is number of 1/64 pixels)
        x += (ch.Advance &gt;&gt; 6) * scale; // bitshift by 6 to get value in pixels (2^6 = 64 (divide amount of 1/64th pixels by 64 to get amount of pixels))
    }
   <function id='27'> glBindVertexArray(</function>0);
   <function id='48'> glBindTexture(</function>GL_TEXTURE_2D, 0);
}
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>