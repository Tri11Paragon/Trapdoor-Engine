


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/animator.h</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#pragma once

#include &lt;glm/glm.hpp&gt;
#include &lt;map&gt;
#include &lt;vector&gt;
#include &lt;assimp/scene.h&gt;
#include &lt;assimp/Importer.hpp&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/animation.h' target='_blank'>learnopengl/animation.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/bone.h' target='_blank'>learnopengl/bone.h</a>&gt;

class Animator
{
public:
	Animator::Animator(Animation* animation)
	{
		m_CurrentTime = 0.0;
		m_CurrentAnimation = animation;

		m_FinalBoneMatrices.reserve(100);

		for (int i = 0; i &lt; 100; i++)
			m_FinalBoneMatrices.push_back(glm::mat4(1.0f));
	}

	void Animator::UpdateAnimation(float dt)
	{
		m_DeltaTime = dt;
		if (m_CurrentAnimation)
		{
			m_CurrentTime += m_CurrentAnimation-&gt;GetTicksPerSecond() * dt;
			m_CurrentTime = fmod(m_CurrentTime, m_CurrentAnimation-&gt;GetDuration());
			CalculateBoneTransform(&amp;m_CurrentAnimation-&gt;GetRootNode(), glm::mat4(1.0f));
		}
	}

	void Animator::PlayAnimation(Animation* pAnimation)
	{
		m_CurrentAnimation = pAnimation;
		m_CurrentTime = 0.0f;
	}

	void Animator::CalculateBoneTransform(const AssimpNodeData* node, glm::mat4 parentTransform)
	{
		std::string nodeName = node-&gt;name;
		glm::mat4 nodeTransform = node-&gt;transformation;

		Bone* Bone = m_CurrentAnimation-&gt;FindBone(nodeName);

		if (Bone)
		{
			Bone-&gt;Update(m_CurrentTime);
			nodeTransform = Bone-&gt;GetLocalTransform();
		}

		glm::mat4 globalTransformation = parentTransform * nodeTransform;

		auto boneInfoMap = m_CurrentAnimation-&gt;GetBoneIDMap();
		if (boneInfoMap.find(nodeName) != boneInfoMap.end())
		{
			int index = boneInfoMap[nodeName].id;
			glm::mat4 offset = boneInfoMap[nodeName].offset;
			m_FinalBoneMatrices[index] = globalTransformation * offset;
		}

		for (int i = 0; i &lt; node-&gt;childrenCount; i++)
			CalculateBoneTransform(&amp;node-&gt;children[i], globalTransformation);
	}

	std::vector&lt;glm::mat4&gt; GetFinalBoneMatrices()
	{
		return m_FinalBoneMatrices;
	}

private:
	std::vector&lt;glm::mat4&gt; m_FinalBoneMatrices;
	Animation* m_CurrentAnimation;
	float m_CurrentTime;
	float m_DeltaTime;

};</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>