


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/1.getting_started/5.2.transformations_exercise1/transformations_exercise1.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">int main()
{
    [...]
    while(<function id='14'>!glfwWindowShouldClose(</function>window))
    {
        [...]        
        // create transformations
        glm::mat4 transform = glm::mat4(1.0f);
        transform =<function id='57'> glm::rotate(</function>transform, (float<function id='47'>)glfwGetTime(</function>), glm::vec3(0.0f, 0.0f, 1.0f)); // switched the order
        transform =<function id='55'> glm::translate(</function>transform, glm::vec3(0.5f, -0.5f, 0.0f)); // switched the order               
        [...]
    }
}

/* Why does our container now spin around our screen?:
== ===================================================
Remember that matrix multiplication is applied in reverse. This time a translation is thus
applied first to the container positioning it in the bottom-right corner of the screen.
After the translation the rotation is applied to the translated container.

A rotation transformation is also known as a change-of-basis transformation
for when we dig a bit deeper into linear algebra. Since we're changing the
basis of the container, the next resulting translations will translate the container
based on the new basis vectors. Once the vector is slightly rotated, the vertical
translations would also be slightly translated for example.

If we would first apply rotations then they'd resolve around the rotation origin (0,0,0), but 
since the container is first translated, its rotation origin is no longer (0,0,0) making it
looks as if its circling around the origin of the scene.

If you had trouble visualizing this or figuring it out, don't worry. If you
experiment with transformations you'll soon get the grasp of it; all it takes
is practice and experience.
*/</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>