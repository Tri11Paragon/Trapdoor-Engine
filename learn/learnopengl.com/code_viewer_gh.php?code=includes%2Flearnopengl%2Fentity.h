


<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8"/>
	<title>Code Viewer. Source code: includes/learnopengl/entity.h</title>
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

<pre style="width: 100%; height: 100%;"><code id="codez" style="margin:0; padding:25; border:0; border-radius:0;">#ifndef ENTITY_H
#define ENTITY_H

#include &lt;glm/glm.hpp&gt; //glm::mat4
#include &lt;list&gt; //std::list
#include &lt;array&gt; //std::array
#include &lt;memory&gt; //std::unique_ptr

class Transform
{
protected:
	//Local space information
	glm::vec3 m_pos = { 0.0f, 0.0f, 0.0f };
	glm::vec3 m_eulerRot = { 0.0f, 0.0f, 0.0f }; //In degrees
	glm::vec3 m_scale = { 1.0f, 1.0f, 1.0f };

	//Global space informaiton concatenate in matrix
	glm::mat4 m_modelMatrix = glm::mat4(1.0f);

	//Dirty flag
	bool m_isDirty = true;

protected:
	glm::mat4 getLocalModelMatrix()
	{
		const glm::mat4 transformX =<function id='57'> glm::rotate(</function>glm::mat4(1.0f),<function id='63'> glm::radians(</function>m_eulerRot.x), glm::vec3(1.0f, 0.0f, 0.0f));
		const glm::mat4 transformY =<function id='57'> glm::rotate(</function>glm::mat4(1.0f),<function id='63'> glm::radians(</function>m_eulerRot.y), glm::vec3(0.0f, 1.0f, 0.0f));
		const glm::mat4 transformZ =<function id='57'> glm::rotate(</function>glm::mat4(1.0f),<function id='63'> glm::radians(</function>m_eulerRot.z), glm::vec3(0.0f, 0.0f, 1.0f));

		// Y * X * Z
		const glm::mat4 roationMatrix = transformY * transformX * transformZ;

		// translation * rotation * scale (also know as TRS matrix)
		return<function id='55'> glm::translate(</function>glm::mat4(1.0f), m_pos) * roationMatrix *<function id='56'> glm::scale(</function>glm::mat4(1.0f), m_scale);
	}
public:

	void computeModelMatrix()
	{
		m_modelMatrix = getLocalModelMatrix();
	}

	void computeModelMatrix(const glm::mat4&amp; parentGlobalModelMatrix)
	{
		m_modelMatrix = parentGlobalModelMatrix * getLocalModelMatrix();
	}

	void setLocalPosition(const glm::vec3&amp; newPosition)
	{
		m_pos = newPosition;
		m_isDirty = true;
	}

	void setLocalRotation(const glm::vec3&amp; newRotation)
	{
		m_eulerRot = newRotation;
		m_isDirty = true;
	}

	void setLocalScale(const glm::vec3&amp; newScale)
	{
		m_scale = newScale;
		m_isDirty = true;
	}

	const glm::vec3&amp; getGlobalPosition() const
	{
		return m_modelMatrix[3];
	}

	const glm::vec3&amp; getLocalPosition() const
	{
		return m_pos;
	}

	const glm::vec3&amp; getLocalRotation() const
	{
		return m_eulerRot;
	}

	const glm::vec3&amp; getLocalScale() const
	{
		return m_scale;
	}

	const glm::mat4&amp; getModelMatrix() const
	{
		return m_modelMatrix;
	}

	glm::vec3 getRight() const
	{
		return m_modelMatrix[0];
	}


	glm::vec3 getUp() const
	{
		return m_modelMatrix[1];
	}

	glm::vec3 getBackward() const
	{
		return m_modelMatrix[2];
	}

	glm::vec3 getForward() const
	{
		return -m_modelMatrix[2];
	}

	glm::vec3 getGlobalScale() const
	{
		return { glm::length(getRight()), glm::length(getUp()), glm::length(getBackward()) };
	}

	bool isDirty() const
	{
		return m_isDirty;
	}
};

struct Plan
{
	glm::vec3 normal = { 0.f, 1.f, 0.f }; // unit vector
	float     distance = 0.f;        // Distance with origin

	Plan() = default;

	Plan(const glm::vec3&amp; p1, const glm::vec3&amp; norm)
		: normal(glm::normalize(norm)),
		distance(glm::dot(normal, p1))
	{}

	float getSignedDistanceToPlan(const glm::vec3&amp; point) const
	{
		return glm::dot(normal, point) - distance;
	}
};

struct Frustum
{
	Plan topFace;
	Plan bottomFace;

	Plan rightFace;
	Plan leftFace;

	Plan farFace;
	Plan nearFace;
};

struct BoundingVolume
{
	virtual bool isOnFrustum(const Frustum&amp; camFrustum, const Transform&amp; transform) const = 0;

	virtual bool isOnOrForwardPlan(const Plan&amp; plan) const = 0;

	bool isOnFrustum(const Frustum&amp; camFrustum) const
	{
		return (isOnOrForwardPlan(camFrustum.leftFace) &amp;&amp;
			isOnOrForwardPlan(camFrustum.rightFace) &amp;&amp;
			isOnOrForwardPlan(camFrustum.topFace) &amp;&amp;
			isOnOrForwardPlan(camFrustum.bottomFace) &amp;&amp;
			isOnOrForwardPlan(camFrustum.nearFace) &amp;&amp;
			isOnOrForwardPlan(camFrustum.farFace));
	};
};

struct Sphere : public BoundingVolume
{
	glm::vec3 center{ 0.f, 0.f, 0.f };
	float radius{ 0.f };

	Sphere(const glm::vec3&amp; inCenter, float inRadius)
		: BoundingVolume{}, center{ inCenter }, radius{ inRadius }
	{}

	bool isOnOrForwardPlan(const Plan&amp; plan) const final
	{
		return plan.getSignedDistanceToPlan(center) &gt; -radius;
	}

	bool isOnFrustum(const Frustum&amp; camFrustum, const Transform&amp; transform) const final
	{
		//Get global scale thanks to our transform
		const glm::vec3 globalScale = transform.getGlobalScale();

		//Get our global center with process it with the global model matrix of our transform
		const glm::vec3 globalCenter{ transform.getModelMatrix() * glm::vec4(center, 1.f) };

		//To wrap correctly our shape, we need the maximum scale scalar.
		const float maxScale = std::max(std::max(globalScale.x, globalScale.y), globalScale.z);

		//Max scale is assuming for the diameter. So, we need the half to apply it to our radius
		Sphere globalSphere(globalCenter, radius * (maxScale * 0.5f));

		//Check Firstly the result that have the most chance to faillure to avoid to call all functions.
		return (globalSphere.isOnOrForwardPlan(camFrustum.leftFace) &amp;&amp;
			globalSphere.isOnOrForwardPlan(camFrustum.rightFace) &amp;&amp;
			globalSphere.isOnOrForwardPlan(camFrustum.farFace) &amp;&amp;
			globalSphere.isOnOrForwardPlan(camFrustum.nearFace) &amp;&amp;
			globalSphere.isOnOrForwardPlan(camFrustum.topFace) &amp;&amp;
			globalSphere.isOnOrForwardPlan(camFrustum.bottomFace));
	};
};

struct SquareAABB : public BoundingVolume
{
	glm::vec3 center{ 0.f, 0.f, 0.f };
	float extent{ 0.f };

	SquareAABB(const glm::vec3&amp; inCenter, float inExtent)
		: BoundingVolume{}, center{ inCenter }, extent{ inExtent }
	{}

	bool isOnOrForwardPlan(const Plan&amp; plan) const final
	{
		// Compute the projection interval radius of b onto L(t) = b.c + t * p.n
		const float r = extent * (std::abs(plan.normal.x) + std::abs(plan.normal.y) + std::abs(plan.normal.z));
		return -r &lt;= plan.getSignedDistanceToPlan(center);
	}

	bool isOnFrustum(const Frustum&amp; camFrustum, const Transform&amp; transform) const final
	{
		//Get global scale thanks to our transform
		const glm::vec3 globalCenter{ transform.getModelMatrix() * glm::vec4(center, 1.f) };

		// Scaled orientation
		const glm::vec3 right = transform.getRight() * extent;
		const glm::vec3 up = transform.getUp() * extent;
		const glm::vec3 forward = transform.getForward() * extent;

		const float newIi = std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, forward));

		const float newIj = std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, forward));

		const float newIk = std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, forward));

		const SquareAABB globalAABB(globalCenter, std::max(std::max(newIi, newIj), newIk));

		return (globalAABB.isOnOrForwardPlan(camFrustum.leftFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.rightFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.topFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.bottomFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.nearFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.farFace));
	};
};

struct AABB : public BoundingVolume
{
	glm::vec3 center{ 0.f, 0.f, 0.f };
	glm::vec3 extents{ 0.f, 0.f, 0.f };

	AABB(const glm::vec3&amp; min, const glm::vec3&amp; max)
		: BoundingVolume{}, center{ (max + min) * 0.5f }, extents{ max.x - center.x, max.y - center.y, max.z - center.z }
	{}

	AABB(const glm::vec3&amp; inCenter, float iI, float iJ, float iK)
		: BoundingVolume{}, center{ inCenter }, extents{ iI, iJ, iK }
	{}

	std::array&lt;glm::vec3, 8&gt; getVertice() const
	{
		std::array&lt;glm::vec3, 8&gt; vertice;
		vertice[0] = { center.x - extents.x, center.y - extents.y, center.z - extents.z };
		vertice[1] = { center.x + extents.x, center.y - extents.y, center.z - extents.z };
		vertice[2] = { center.x - extents.x, center.y + extents.y, center.z - extents.z };
		vertice[3] = { center.x + extents.x, center.y + extents.y, center.z - extents.z };
		vertice[4] = { center.x - extents.x, center.y - extents.y, center.z + extents.z };
		vertice[5] = { center.x + extents.x, center.y - extents.y, center.z + extents.z };
		vertice[6] = { center.x - extents.x, center.y + extents.y, center.z + extents.z };
		vertice[7] = { center.x + extents.x, center.y + extents.y, center.z + extents.z };
		return vertice;
	}

	//see https://gdbooks.gitbooks.io/3dcollisions/content/Chapter2/static_aabb_plan.html
	bool isOnOrForwardPlan(const Plan&amp; plan) const final
	{
		// Compute the projection interval radius of b onto L(t) = b.c + t * p.n
		const float r = extents.x * std::abs(plan.normal.x) + extents.y * std::abs(plan.normal.y) +
			extents.z * std::abs(plan.normal.z);

		return -r &lt;= plan.getSignedDistanceToPlan(center);
	}

	bool isOnFrustum(const Frustum&amp; camFrustum, const Transform&amp; transform) const final
	{
		//Get global scale thanks to our transform
		const glm::vec3 globalCenter{ transform.getModelMatrix() * glm::vec4(center, 1.f) };

		// Scaled orientation
		const glm::vec3 right = transform.getRight() * extents.x;
		const glm::vec3 up = transform.getUp() * extents.y;
		const glm::vec3 forward = transform.getForward() * extents.z;

		const float newIi = std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, forward));

		const float newIj = std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, forward));

		const float newIk = std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, forward));

		const AABB globalAABB(globalCenter, newIi, newIj, newIk);

		return (globalAABB.isOnOrForwardPlan(camFrustum.leftFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.rightFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.topFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.bottomFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.nearFace) &amp;&amp;
			globalAABB.isOnOrForwardPlan(camFrustum.farFace));
	};
};

Frustum createFrustumFromCamera(const Camera&amp; cam, float aspect, float fovY, float zNear, float zFar)
{
	Frustum     frustum;
	const float halfVSide = zFar * tanf(fovY * .5f);
	const float halfHSide = halfVSide * aspect;
	const glm::vec3 frontMultFar = zFar * cam.Front;

	frustum.nearFace = { cam.Position + zNear * cam.Front, cam.Front };
	frustum.farFace = { cam.Position + frontMultFar, -cam.Front };
	frustum.rightFace = { cam.Position,<function id='61'> glm::cross(</function>cam.Up, frontMultFar + cam.Right * halfHSide) };
	frustum.leftFace = { cam.Position,<function id='61'> glm::cross(</function>frontMultFar - cam.Right * halfHSide, cam.Up) };
	frustum.topFace = { cam.Position,<function id='61'> glm::cross(</function>cam.Right, frontMultFar - cam.Up * halfVSide) };
	frustum.bottomFace = { cam.Position,<function id='61'> glm::cross(</function>frontMultFar + cam.Up * halfVSide, cam.Right) };

	return frustum;
}

AABB generateAABB(const Model&amp; model)
{
	glm::vec3 minAABB = glm::vec3(std::numeric_limits&lt;float&gt;::max());
	glm::vec3 maxAABB = glm::vec3(std::numeric_limits&lt;float&gt;::min());
	for (auto&amp;&amp; mesh : model.meshes)
	{
		for (auto&amp;&amp; vertex : mesh.vertices)
		{
			minAABB.x = std::min(minAABB.x, vertex.Position.x);
			minAABB.y = std::min(minAABB.y, vertex.Position.y);
			minAABB.z = std::min(minAABB.z, vertex.Position.z);

			maxAABB.x = std::max(maxAABB.x, vertex.Position.x);
			maxAABB.y = std::max(maxAABB.y, vertex.Position.y);
			maxAABB.z = std::max(maxAABB.z, vertex.Position.z);
		}
	}
	return AABB(minAABB, maxAABB);
}

Sphere generateSphereBV(const Model&amp; model)
{
	glm::vec3 minAABB = glm::vec3(std::numeric_limits&lt;float&gt;::max());
	glm::vec3 maxAABB = glm::vec3(std::numeric_limits&lt;float&gt;::min());
	for (auto&amp;&amp; mesh : model.meshes)
	{
		for (auto&amp;&amp; vertex : mesh.vertices)
		{
			minAABB.x = std::min(minAABB.x, vertex.Position.x);
			minAABB.y = std::min(minAABB.y, vertex.Position.y);
			minAABB.z = std::min(minAABB.z, vertex.Position.z);

			maxAABB.x = std::max(maxAABB.x, vertex.Position.x);
			maxAABB.y = std::max(maxAABB.y, vertex.Position.y);
			maxAABB.z = std::max(maxAABB.z, vertex.Position.z);
		}
	}

	return Sphere((maxAABB + minAABB) * 0.5f, glm::length(minAABB - maxAABB));
}

class Entity
{
public:
	//Scene graph
	std::list&lt;std::unique_ptr&lt;Entity&gt;&gt; children;
	Entity* parent = nullptr;

	//Space information
	Transform transform;

	Model* pModel = nullptr;
	std::unique_ptr&lt;AABB&gt; boundingVolume;


	// constructor, expects a filepath to a 3D model.
	Entity(Model&amp; model) : pModel{ &amp;model }
	{
		boundingVolume = std::make_unique&lt;AABB&gt;(generateAABB(model));
		//boundingVolume = std::make_unique&lt;Sphere&gt;(generateSphereBV(model));
	}

	AABB getGlobalAABB()
	{
		//Get global scale thanks to our transform
		const glm::vec3 globalCenter{ transform.getModelMatrix() * glm::vec4(boundingVolume-&gt;center, 1.f) };

		// Scaled orientation
		const glm::vec3 right = transform.getRight() * boundingVolume-&gt;extents.x;
		const glm::vec3 up = transform.getUp() * boundingVolume-&gt;extents.y;
		const glm::vec3 forward = transform.getForward() * boundingVolume-&gt;extents.z;

		const float newIi = std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 1.f, 0.f, 0.f }, forward));

		const float newIj = std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 1.f, 0.f }, forward));

		const float newIk = std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, right)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, up)) +
			std::abs(glm::dot(glm::vec3{ 0.f, 0.f, 1.f }, forward));

		return AABB(globalCenter, newIi, newIj, newIk);
	}

	//Add child. Argument input is argument of any constructor that you create. By default you can use the default constructor and don't put argument input.
	template&lt;typename... TArgs&gt;
	void addChild(TArgs&amp;... args)
	{
		children.emplace_back(std::make_unique&lt;Entity&gt;(args...));
		children.back()-&gt;parent = this;
	}

	//Update transform if it was changed
	void updateSelfAndChild()
	{
		if (!transform.isDirty())
			return;

		forceUpdateSelfAndChild();
	}

	//Force update of transform even if local space don't change
	void forceUpdateSelfAndChild()
	{
		if (parent)
			transform.computeModelMatrix(parent-&gt;transform.getModelMatrix());
		else
			transform.computeModelMatrix();

		for (auto&amp;&amp; child : children)
		{
			child-&gt;forceUpdateSelfAndChild();
		}
	}


	void drawSelfAndChild(const Frustum&amp; frustum, Shader&amp; ourShader, unsigned int&amp; display, unsigned int&amp; total)
	{
		if (boundingVolume-&gt;isOnFrustum(frustum, transform))
		{
			ourShader.setMat4(&quot;model&quot;, transform.getModelMatrix());
			pModel-&gt;Draw(ourShader);
			display++;
		}
		total++;

		for (auto&amp;&amp; child : children)
		{
			child-&gt;drawSelfAndChild(frustum, ourShader, display, total);
		}
	}
};
#endif
</pre></code>

<div id="hover">
        HI
</div>

</body>
</html>