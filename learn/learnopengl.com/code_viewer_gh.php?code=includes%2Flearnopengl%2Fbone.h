


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/bone.h</title>
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

/* Container for bone data */

#include &lt;vector&gt;
#include &lt;assimp/scene.h&gt;
#include &lt;list&gt;
#include &lt;glm/glm.hpp&gt;
#define GLM_ENABLE_EXPERIMENTAL
#include &lt;glm/gtx/quaternion.hpp&gt;
#include &lt;<a href='code_viewer_gh.php?code=includes/learnopengl/assimp_glm_helpers.h' target='_blank'>learnopengl/assimp_glm_helpers.h</a>&gt;

struct KeyPosition
{
	glm::vec3 position;
	float timeStamp;
};

struct KeyRotation
{
	glm::quat orientation;
	float timeStamp;
};

struct KeyScale
{
	glm::vec3 scale;
	float timeStamp;
};

class Bone
{
public:
	Bone(const std::string&amp; name, int ID, const aiNodeAnim* channel)
		:
		m_Name(name),
		m_ID(ID),
		m_LocalTransform(1.0f)
	{
		m_NumPositions = channel-&gt;mNumPositionKeys;

		for (int positionIndex = 0; positionIndex &lt; m_NumPositions; ++positionIndex)
		{
			aiVector3D aiPosition = channel-&gt;mPositionKeys[positionIndex].mValue;
			float timeStamp = channel-&gt;mPositionKeys[positionIndex].mTime;
			KeyPosition data;
			data.position = AssimpGLMHelpers::GetGLMVec(aiPosition);
			data.timeStamp = timeStamp;
			m_Positions.push_back(data);
		}

		m_NumRotations = channel-&gt;mNumRotationKeys;
		for (int rotationIndex = 0; rotationIndex &lt; m_NumRotations; ++rotationIndex)
		{
			aiQuaternion aiOrientation = channel-&gt;mRotationKeys[rotationIndex].mValue;
			float timeStamp = channel-&gt;mRotationKeys[rotationIndex].mTime;
			KeyRotation data;
			data.orientation = AssimpGLMHelpers::GetGLMQuat(aiOrientation);
			data.timeStamp = timeStamp;
			m_Rotations.push_back(data);
		}

		m_NumScalings = channel-&gt;mNumScalingKeys;
		for (int keyIndex = 0; keyIndex &lt; m_NumScalings; ++keyIndex)
		{
			aiVector3D scale = channel-&gt;mScalingKeys[keyIndex].mValue;
			float timeStamp = channel-&gt;mScalingKeys[keyIndex].mTime;
			KeyScale data;
			data.scale = AssimpGLMHelpers::GetGLMVec(scale);
			data.timeStamp = timeStamp;
			m_Scales.push_back(data);
		}
	}
	
	void Update(float animationTime)
	{
		glm::mat4 translation = InterpolatePosition(animationTime);
		glm::mat4 rotation = InterpolateRotation(animationTime);
		glm::mat4 scale = InterpolateScaling(animationTime);
		m_LocalTransform = translation * rotation * scale;
	}
	glm::mat4 GetLocalTransform() { return m_LocalTransform; }
	std::string GetBoneName() const { return m_Name; }
	int GetBoneID() { return m_ID; }
	


	int GetPositionIndex(float animationTime)
	{
		for (int index = 0; index &lt; m_NumPositions - 1; ++index)
		{
			if (animationTime &lt; m_Positions[index + 1].timeStamp)
				return index;
		}
		assert(0);
	}

	int GetRotationIndex(float animationTime)
	{
		for (int index = 0; index &lt; m_NumRotations - 1; ++index)
		{
			if (animationTime &lt; m_Rotations[index + 1].timeStamp)
				return index;
		}
		assert(0);
	}

	int GetScaleIndex(float animationTime)
	{
		for (int index = 0; index &lt; m_NumScalings - 1; ++index)
		{
			if (animationTime &lt; m_Scales[index + 1].timeStamp)
				return index;
		}
		assert(0);
	}


private:

	float GetScaleFactor(float lastTimeStamp, float nextTimeStamp, float animationTime)
	{
		float scaleFactor = 0.0f;
		float midWayLength = animationTime - lastTimeStamp;
		float framesDiff = nextTimeStamp - lastTimeStamp;
		scaleFactor = midWayLength / framesDiff;
		return scaleFactor;
	}

	glm::mat4 InterpolatePosition(float animationTime)
	{
		if (1 == m_NumPositions)
			return<function id='55'> glm::translate(</function>glm::mat4(1.0f), m_Positions[0].position);

		int p0Index = GetPositionIndex(animationTime);
		int p1Index = p0Index + 1;
		float scaleFactor = GetScaleFactor(m_Positions[p0Index].timeStamp,
			m_Positions[p1Index].timeStamp, animationTime);
		glm::vec3 finalPosition = glm::mix(m_Positions[p0Index].position, m_Positions[p1Index].position
			, scaleFactor);
		return<function id='55'> glm::translate(</function>glm::mat4(1.0f), finalPosition);
	}

	glm::mat4 InterpolateRotation(float animationTime)
	{
		if (1 == m_NumRotations)
		{
			auto rotation = glm::normalize(m_Rotations[0].orientation);
			return glm::toMat4(rotation);
		}

		int p0Index = GetRotationIndex(animationTime);
		int p1Index = p0Index + 1;
		float scaleFactor = GetScaleFactor(m_Rotations[p0Index].timeStamp,
			m_Rotations[p1Index].timeStamp, animationTime);
		glm::quat finalRotation = glm::slerp(m_Rotations[p0Index].orientation, m_Rotations[p1Index].orientation
			, scaleFactor);
		finalRotation = glm::normalize(finalRotation);
		return glm::toMat4(finalRotation);

	}

	glm::mat4 Bone::InterpolateScaling(float animationTime)
	{
		if (1 == m_NumScalings)
			return<function id='56'> glm::scale(</function>glm::mat4(1.0f), m_Scales[0].scale);

		int p0Index = GetScaleIndex(animationTime);
		int p1Index = p0Index + 1;
		float scaleFactor = GetScaleFactor(m_Scales[p0Index].timeStamp,
			m_Scales[p1Index].timeStamp, animationTime);
		glm::vec3 finalScale = glm::mix(m_Scales[p0Index].scale, m_Scales[p1Index].scale
			, scaleFactor);
		return<function id='56'> glm::scale(</function>glm::mat4(1.0f), finalScale);
	}

	std::vector&lt;KeyPosition&gt; m_Positions;
	std::vector&lt;KeyRotation&gt; m_Rotations;
	std::vector&lt;KeyScale&gt; m_Scales;
	int m_NumPositions;
	int m_NumRotations;
	int m_NumScalings;

	glm::mat4 m_LocalTransform;
	std::string m_Name;
	int m_ID;
};

</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>