package com.trapdoor.engine.renderer.shadows;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.camera.ICamera;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.functions.RenderFunction;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.WorldEntityStorage;

public class ShadowRenderer {
	
	public static final float FAR_PLANE = ProjectionMatrix.FAR_PLANE;
	
	public static final float[] shadowCascadeLevels = {
															FAR_PLANE / 50.0f, 
															FAR_PLANE / 25.0f, 
															FAR_PLANE / 10.0f, 
															FAR_PLANE / 2.0f
														};
	
	private Matrix4f shadowView = new Matrix4f();
	
	private ShadowShader shader;
	private ShadowMap map;
	
	private ICamera camera;
	
	public ShadowRenderer(ICamera camera) {
		shader = new ShadowShader();
		map = new ShadowMap(shadowCascadeLevels.length);
		this.camera = camera;
		UBOLoader.createShadowUBO();
	}
	
	public void renderDepthMap(ICamera camera, WorldEntityStorage storage, RenderFunction render) {
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, map.getDepthMapFBO());
		GL33.glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
		
		GL33.glClear(GL33.GL_DEPTH_BUFFER_BIT);
		
		shader.start();
		
		UBOLoader.updateShadowUBO(getLightSpaceMatricies());
		
		GL33.glDisable(GL33.GL_CULL_FACE);
		//GL33.glCullFace(GL33.GL_FRONT);
		// render out the shadows
		storage.render(render);
		//GL33.glCullFace(GL33.GL_BACK);
		GL33.glEnable(GL33.GL_CULL_FACE);
		
		shader.stop();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	public ArrayList<Matrix4f> getLightSpaceMatricies() {
		ArrayList<Matrix4f> matricies = new ArrayList<Matrix4f>();
		
		for (int i = 0; i < shadowCascadeLevels.length + 1; i++) {
			if (i == 0) {
				matricies.add(calculateLightView(ProjectionMatrix.NEAR_PLANE, shadowCascadeLevels[i]));
			} else if (i < shadowCascadeLevels.length) {
				matricies.add(calculateLightView(shadowCascadeLevels[i-1], shadowCascadeLevels[i]));
			} else {
				matricies.add(calculateLightView(shadowCascadeLevels[i-1], FAR_PLANE));
			}
		}
		
		return matricies;
	}
	
	public Matrix4f calculateLightView(float nearPlane, float farPlane) {
		Matrix4f perspective = new Matrix4f().perspective(ProjectionMatrix.FOV, (float)DisplayManager.WIDTH/(float)DisplayManager.HEIGHT, nearPlane, farPlane);
		ArrayList<Vector4f> corners = getFrustumCornersWorldSpace(perspective, camera.getViewMatrix());
		Vector3f center = new Vector3f();
		for (int i = 0; i < corners.size(); i++) {
			center.add(new Vector3f(corners.get(i).x, corners.get(i).y, corners.get(i).z));
		}
		center.div(corners.size());
		
		shadowView = new Matrix4f().lookAt(new Vector3f(center).add(DisplayManager.lightDirection), center, Maths.ry);
		
		float minX = Float.MAX_VALUE;
	    float maxX = Float.MIN_VALUE;
	    float minY = Float.MAX_VALUE;
	    float maxY = Float.MIN_VALUE;
	    float minZ = Float.MAX_VALUE;
	    float maxZ = Float.MIN_VALUE;
	    
	    for (Vector4f v : corners) {
	        Vector4f trf = shadowView.transform(v);
	        minX = Math.min(minX, trf.x);
	        maxX = Math.max(maxX, trf.x);
	        minY = Math.min(minY, trf.y);
	        maxY = Math.max(maxY, trf.y);
	        minZ = Math.min(minZ, trf.z);
	        maxZ = Math.max(maxZ, trf.z);
	    }
	    
	    final float zMult = 10.0f;
	    final float bias = 10.0f;
	    
	    if (minZ < 0) {
	        minZ *= zMult;
	    } else {
	        minZ /= zMult;
	    }
	    
	    if (maxZ < 0) {
	        maxZ /= zMult;
	    } else {
	        maxZ *= zMult;
	    }

	    Matrix4f lightProjection = new Matrix4f().ortho(minX - bias, maxX + bias, minY - bias, maxY + bias, minZ, maxZ);
	    
	    return lightProjection.mul(shadowView);
	}
	
	private ArrayList<Vector4f> getFrustumCornersWorldSpace(Matrix4f proj, Matrix4f view){
		ArrayList<Vector4f> frustumCorners = new ArrayList<Vector4f>();
		
		Matrix4f inverse = new Matrix4f().set(proj).mul(view).invert();
		
		for (int x = 0; x < 2; ++x) {
			for (int y = 0; y < 2; ++y) {
				for (int z = 0; z < 2; ++z) {
					Vector4f pt = inverse.transform(new Vector4f(
														2.0f * x - 1.0f,
														2.0f * y - 1.0f,
														2.0f * z - 1.0f,
														1.0f
													));
					
					frustumCorners.add(pt.div(pt.w));
				}
			}
		}
		
		return frustumCorners;
	}
	
	public ShadowShader getShader() {
		return shader;
	}
	
	public ShadowMap getShadowMap() {
		return map;
	}
	
	public void cleanup() {
		this.shader.cleanUp();
		this.map.cleanup();
	}
	
}
