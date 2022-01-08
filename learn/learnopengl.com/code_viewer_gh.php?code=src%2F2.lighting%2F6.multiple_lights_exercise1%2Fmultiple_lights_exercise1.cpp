


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: src/2.lighting/6.multiple_lights_exercise1/multiple_lights_exercise1.cpp</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">// == ==============================================================================================
//       DESERT
// == ==============================================================================================<function id='13'><function id='10'>
glClearC</function>olor(</function>0.75f, 0.52f, 0.3f, 1.0f);
[...]
glm::vec3 pointLightColors[] = {
    glm::vec3(1.0f, 0.6f, 0.0f),
    glm::vec3(1.0f, 0.0f, 0.0f),
    glm::vec3(1.0f, 1.0, 0.0),
    glm::vec3(0.2f, 0.2f, 1.0f)
};
[...]
// Directional light<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.direction&quot;), -0.2f, -1.0f, -0.3f);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.ambient&quot;), 0.3f, 0.24f, 0.14f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.diffuse&quot;), 0.7f, 0.42f, 0.26f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.specular&quot;), 0.5f, 0.5f, 0.5f);
// Point light 1<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].position&quot;), pointLightPositions[0].x, pointLightPositions[0].y, pointLightPositions[0].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].ambient&quot;), pointLightColors[0].x * 0.1,  pointLightColors[0].y * 0.1,  pointLightColors[0].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].diffuse&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].specular&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].quadratic&quot;), 0.032);		
// Point light 2<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].position&quot;), pointLightPositions[1].x, pointLightPositions[1].y, pointLightPositions[1].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].ambient&quot;), pointLightColors[1].x * 0.1,  pointLightColors[1].y * 0.1,  pointLightColors[1].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].diffuse&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].specular&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].quadratic&quot;), 0.032);		
// Point light 3<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].position&quot;), pointLightPositions[2].x, pointLightPositions[2].y, pointLightPositions[2].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].ambient&quot;), pointLightColors[2].x * 0.1,  pointLightColors[2].y * 0.1,  pointLightColors[2].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].diffuse&quot;), pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].specular&quot;) ,pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].quadratic&quot;), 0.032);		
// Point light 4<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].position&quot;), pointLightPositions[3].x, pointLightPositions[3].y, pointLightPositions[3].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].ambient&quot;), pointLightColors[3].x * 0.1,  pointLightColors[3].y * 0.1,  pointLightColors[3].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].diffuse&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].specular&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].quadratic&quot;), 0.032);		
// SpotLight<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.position&quot;), camera.Position.x, camera.Position.y, camera.Position.z);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.direction&quot;), camera.Front.x, camera.Front.y, camera.Front.z);<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.ambient&quot;), 0.0f, 0.0f, 0.0f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.diffuse&quot;), 0.8f, 0.8f, 0.0f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.specular&quot;), 0.8f, 0.8f, 0.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.quadratic&quot;), 0.032);			<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.cutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>12.5f)));<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.outerCutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>13.0f)));	
// == ==============================================================================================
//       FACTORY
// == ==============================================================================================<function id='13'><function id='10'>
glClearC</function>olor(</function>0.1f, 0.1f, 0.1f, 1.0f);
[...]
glm::vec3 pointLightColors[] = {
    glm::vec3(0.2f, 0.2f, 0.6f),
    glm::vec3(0.3f, 0.3f, 0.7f),
    glm::vec3(0.0f, 0.0f, 0.3f),
    glm::vec3(0.4f, 0.4f, 0.4f)
};
[...]
// Directional light<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.direction&quot;), -0.2f, -1.0f, -0.3f);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.ambient&quot;), 0.05f, 0.05f, 0.1f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.diffuse&quot;), 0.2f, 0.2f, 0.7); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.specular&quot;), 0.7f, 0.7f, 0.7f);
// Point light 1<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].position&quot;), pointLightPositions[0].x, pointLightPositions[0].y, pointLightPositions[0].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].ambient&quot;), pointLightColors[0].x * 0.1,  pointLightColors[0].y * 0.1,  pointLightColors[0].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].diffuse&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].specular&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].quadratic&quot;), 0.032);		
// Point light 2<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].position&quot;), pointLightPositions[1].x, pointLightPositions[1].y, pointLightPositions[1].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].ambient&quot;), pointLightColors[1].x * 0.1,  pointLightColors[1].y * 0.1,  pointLightColors[1].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].diffuse&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].specular&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].quadratic&quot;), 0.032);		
// Point light 3<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].position&quot;), pointLightPositions[2].x, pointLightPositions[2].y, pointLightPositions[2].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].ambient&quot;), pointLightColors[2].x * 0.1,  pointLightColors[2].y * 0.1,  pointLightColors[2].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].diffuse&quot;), pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].specular&quot;) ,pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].quadratic&quot;), 0.032);		
// Point light 4<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].position&quot;), pointLightPositions[3].x, pointLightPositions[3].y, pointLightPositions[3].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].ambient&quot;), pointLightColors[3].x * 0.1,  pointLightColors[3].y * 0.1,  pointLightColors[3].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].diffuse&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].specular&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].quadratic&quot;), 0.032);		
// SpotLight<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.position&quot;), camera.Position.x, camera.Position.y, camera.Position.z);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.direction&quot;), camera.Front.x, camera.Front.y, camera.Front.z);<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.ambient&quot;), 0.0f, 0.0f, 0.0f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.diffuse&quot;), 1.0f, 1.0f, 1.0f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.specular&quot;), 1.0f, 1.0f, 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.linear&quot;), 0.009);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.quadratic&quot;), 0.0032);			<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.cutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>10.0f)));<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.outerCutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>12.5f)));	
// == ==============================================================================================
//       HORROR
// == ==============================================================================================<function id='13'><function id='10'>
glClearC</function>olor(</function>0.0f, 0.0f, 0.0f, 1.0f);
[...]
glm::vec3 pointLightColors[] = {
    glm::vec3(0.1f, 0.1f, 0.1f),
    glm::vec3(0.1f, 0.1f, 0.1f),
    glm::vec3(0.1f, 0.1f, 0.1f),
    glm::vec3(0.3f, 0.1f, 0.1f)
};
[...]
// Directional light<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.direction&quot;), -0.2f, -1.0f, -0.3f);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.ambient&quot;), 0.0f, 0.0f, 0.0f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.diffuse&quot;), 0.05f, 0.05f, 0.05); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.specular&quot;), 0.2f, 0.2f, 0.2f);
// Point light 1<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].position&quot;), pointLightPositions[0].x, pointLightPositions[0].y, pointLightPositions[0].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].ambient&quot;), pointLightColors[0].x * 0.1,  pointLightColors[0].y * 0.1,  pointLightColors[0].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].diffuse&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].specular&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].linear&quot;), 0.14);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].quadratic&quot;), 0.07);		
// Point light 2<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].position&quot;), pointLightPositions[1].x, pointLightPositions[1].y, pointLightPositions[1].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].ambient&quot;), pointLightColors[1].x * 0.1,  pointLightColors[1].y * 0.1,  pointLightColors[1].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].diffuse&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].specular&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].linear&quot;), 0.14);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].quadratic&quot;), 0.07);		
// Point light 3<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].position&quot;), pointLightPositions[2].x, pointLightPositions[2].y, pointLightPositions[2].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].ambient&quot;), pointLightColors[2].x * 0.1,  pointLightColors[2].y * 0.1,  pointLightColors[2].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].diffuse&quot;), pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].specular&quot;) ,pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].linear&quot;), 0.22);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].quadratic&quot;), 0.20);		
// Point light 4<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].position&quot;), pointLightPositions[3].x, pointLightPositions[3].y, pointLightPositions[3].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].ambient&quot;), pointLightColors[3].x * 0.1,  pointLightColors[3].y * 0.1,  pointLightColors[3].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].diffuse&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].specular&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].linear&quot;), 0.14);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].quadratic&quot;), 0.07);		
// SpotLight<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.position&quot;), camera.Position.x, camera.Position.y, camera.Position.z);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.direction&quot;), camera.Front.x, camera.Front.y, camera.Front.z);<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.ambient&quot;), 0.0f, 0.0f, 0.0f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.diffuse&quot;), 1.0f, 1.0f, 1.0f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.specular&quot;), 1.0f, 1.0f, 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.linear&quot;), 0.09);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.quadratic&quot;), 0.032);			<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.cutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>10.0f)));<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.outerCutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>15.0f)));
// == ==============================================================================================
//       BIOCHEMICAL LAB
// == ==============================================================================================<function id='13'><function id='10'>
glClearC</function>olor(</function>0.9f, 0.9f, 0.9f, 1.0f);
[...]
glm::vec3 pointLightColors[] = {
    glm::vec3(0.4f, 0.7f, 0.1f),
    glm::vec3(0.4f, 0.7f, 0.1f),
    glm::vec3(0.4f, 0.7f, 0.1f),
    glm::vec3(0.4f, 0.7f, 0.1f)
};
[...]
// Directional light<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.direction&quot;), -0.2f, -1.0f, -0.3f);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.ambient&quot;), 0.5f, 0.5f, 0.5f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.diffuse&quot;), 1.0f, 1.0f, 1.0f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;dirLight.specular&quot;), 1.0f, 1.0f, 1.0f);
// Point light 1<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].position&quot;), pointLightPositions[0].x, pointLightPositions[0].y, pointLightPositions[0].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].ambient&quot;), pointLightColors[0].x * 0.1,  pointLightColors[0].y * 0.1,  pointLightColors[0].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].diffuse&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].specular&quot;), pointLightColors[0].x,  pointLightColors[0].y,  pointLightColors[0].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].linear&quot;), 0.07);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[0].quadratic&quot;), 0.017);		
// Point light 2<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].position&quot;), pointLightPositions[1].x, pointLightPositions[1].y, pointLightPositions[1].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].ambient&quot;), pointLightColors[1].x * 0.1,  pointLightColors[1].y * 0.1,  pointLightColors[1].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].diffuse&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].specular&quot;), pointLightColors[1].x,  pointLightColors[1].y,  pointLightColors[1].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].linear&quot;), 0.07);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[1].quadratic&quot;), 0.017);		
// Point light 3<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].position&quot;), pointLightPositions[2].x, pointLightPositions[2].y, pointLightPositions[2].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].ambient&quot;), pointLightColors[2].x * 0.1,  pointLightColors[2].y * 0.1,  pointLightColors[2].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].diffuse&quot;), pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].specular&quot;) ,pointLightColors[2].x,  pointLightColors[2].y,  pointLightColors[2].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].linear&quot;), 0.07);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[2].quadratic&quot;), 0.017);		
// Point light 4<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].position&quot;), pointLightPositions[3].x, pointLightPositions[3].y, pointLightPositions[3].z);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].ambient&quot;), pointLightColors[3].x * 0.1,  pointLightColors[3].y * 0.1,  pointLightColors[3].z * 0.1);		<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].diffuse&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].specular&quot;), pointLightColors[3].x,  pointLightColors[3].y,  pointLightColors[3].z);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].linear&quot;), 0.07);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;pointLights[3].quadratic&quot;), 0.017);		
// SpotLight<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.position&quot;), camera.Position.x, camera.Position.y, camera.Position.z);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.direction&quot;), camera.Front.x, camera.Front.y, camera.Front.z);<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.ambient&quot;), 0.0f, 0.0f, 0.0f);	<function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.diffuse&quot;), 0.0f, 1.0f, 0.0f); <function id='44'>
glUniform3</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.specular&quot;), 0.0f, 1.0f, 0.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.constant&quot;), 1.0f);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.linear&quot;), 0.07);<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.quadratic&quot;), 0.017);	<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.cutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>7.0f)));<function id='44'>
glUniform1</function>f<function id='45'>(glGetUniformLocation(</function>lightingShader.Program, &quot;spotLight.outerCutOff&quot;), glm::cos<function id='63'>(glm::radians(</function>10.0f)));	</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>