


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/animation.h</title>
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

#include &lt;vector&gt;
#include &lt;map&gt;
#include &lt;glm/glm.hpp&gt;
#include &lt;assimp/scene.h&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/bone.h' target='_blank'>learnopengl/bone.h</a>&gt;
#include &lt;functional&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/animdata.h' target='_blank'>learnopengl/animdata.h</a>&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/model_animation.h' target='_blank'>learnopengl/model_animation.h</a>&gt;

struct AssimpNodeData
{
	glm::mat4 transformation;
	std::string name;
	int childrenCount;
	std::vector&lt;AssimpNodeData&gt; children;
};

class Animation
{
public:
	Animation() = default;

	Animation(const std::string&amp; animationPath, Model* model)
	{
		Assimp::Importer importer;
		const aiScene* scene = importer.ReadFile(animationPath, aiProcess_Triangulate);
		assert(scene &amp;&amp; scene-&gt;mRootNode);
		auto animation = scene-&gt;mAnimations[0];
		m_Duration = animation-&gt;mDuration;
		m_TicksPerSecond = animation-&gt;mTicksPerSecond;
		aiMatrix4x4 globalTransformation = scene-&gt;mRootNode-&gt;mTransformation;
		globalTransformation = globalTransformation.Inverse();
		ReadHeirarchyData(m_RootNode, scene-&gt;mRootNode);
		ReadMissingBones(animation, *model);
	}

	~Animation()
	{
	}

	Bone* FindBone(const std::string&amp; name)
	{
		auto iter = std::find_if(m_Bones.begin(), m_Bones.end(),
			[&amp;](const Bone&amp; Bone)
			{
				return Bone.GetBoneName() == name;
			}
		);
		if (iter == m_Bones.end()) return nullptr;
		else return &amp;(*iter);
	}

	
	inline float GetTicksPerSecond() { return m_TicksPerSecond; }
	inline float GetDuration() { return m_Duration;}
	inline const AssimpNodeData&amp; GetRootNode() { return m_RootNode; }
	inline const std::map&lt;std::string,BoneInfo&gt;&amp; GetBoneIDMap() 
	{ 
		return m_BoneInfoMap;
	}

private:
	void ReadMissingBones(const aiAnimation* animation, Model&amp; model)
	{
		int size = animation-&gt;mNumChannels;

		auto&amp; boneInfoMap = model.GetBoneInfoMap();//getting m_BoneInfoMap from Model class
		int&amp; boneCount = model.GetBoneCount(); //getting the m_BoneCounter from Model class

		//reading channels(bones engaged in an animation and their keyframes)
		for (int i = 0; i &lt; size; i++)
		{
			auto channel = animation-&gt;mChannels[i];
			std::string boneName = channel-&gt;mNodeName.data;

			if (boneInfoMap.find(boneName) == boneInfoMap.end())
			{
				boneInfoMap[boneName].id = boneCount;
				boneCount++;
			}
			m_Bones.push_back(Bone(channel-&gt;mNodeName.data,
				boneInfoMap[channel-&gt;mNodeName.data].id, channel));
		}

		m_BoneInfoMap = boneInfoMap;
	}

	void ReadHeirarchyData(AssimpNodeData&amp; dest, const aiNode* src)
	{
		assert(src);

		dest.name = src-&gt;mName.data;
		dest.transformation = AssimpGLMHelpers::ConvertMatrixToGLMFormat(src-&gt;mTransformation);
		dest.childrenCount = src-&gt;mNumChildren;

		for (int i = 0; i &lt; src-&gt;mNumChildren; i++)
		{
			AssimpNodeData newData;
			ReadHeirarchyData(newData, src-&gt;mChildren[i]);
			dest.children.push_back(newData);
		}
	}
	float m_Duration;
	int m_TicksPerSecond;
	std::vector&lt;Bone&gt; m_Bones;
	AssimpNodeData m_RootNode;
	std::map&lt;std::string, BoneInfo&gt; m_BoneInfoMap;
};

</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>