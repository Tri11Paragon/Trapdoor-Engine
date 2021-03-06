package com.trapdoor.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Matrix4f;

import com.karl.Engine.skybox.SkyboxRenderer;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.WorldShader;
import com.trapdoor.engine.renderer.shadows.ShadowBox;
import com.trapdoor.engine.tools.math.Maths;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ProjectionMatrix {
	
	public static float FOV = 70;
	public static final float NEAR_PLANE = 0.20f;
	public static final float FAR_PLANE = SkyboxRenderer.SIZE + 25;
	
	public static final Matrix4f projectionMatrix = new Matrix4f();
	public static final Matrix4f projectionMatrixOrtho = new Matrix4f();
	
	public static HashMap<Integer, WorldShader> shaders = new HashMap<Integer, WorldShader>();
	private static ArrayList<Runnable> projectionChanges = new ArrayList<Runnable>();
	private static int lastIndex = 0;
	
	public static void updateProjectionMatrix(){
    	projectionMatrix.identity();
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		
		Iterator<Entry<Integer, WorldShader>> shaderIt = shaders.entrySet().iterator();
		
		projectionMatrixOrtho.set(Maths.ortho());
		
		while (shaderIt.hasNext()) {
			WorldShader localShader = shaderIt.next().getValue(); 
			localShader.start();
			localShader.loadProjectionMatrix(projectionMatrix);
			localShader.stop();
		}
		
		for (int i = 0; i < projectionChanges.size(); i++)
			projectionChanges.get(i).run();
		
		UBOLoader.updateProjectionMatrix(projectionMatrix);
		UBOLoader.updateOrthoMatrix(projectionMatrixOrtho);
		ShadowBox.calculateWidthsAndHeights();
		
		//GUIShader guishader = VoxelScreenManager.ui.getRenderer().getShader();
		//guishader.start();1
		//guishader.loadProjectionMatrix(projectionMatrixOrtho);
		//guishader.loadScreenHeight(DisplayManager.HEIGHT);
		//guishader.stop();
		
		//FontShader fontshader = ScreenManager.fontrenderer.shader;
		//fontshader.start();
		//fontshader.loadProjectionMatrix(projectionMatrixOrtho);
		//fontshader.loadScreenHeight(DisplayManager.HEIGHT);
		//fontshader.stop();
		
		//SinglePlayer.genBuffers();
    }
	
	public static void cleanShaders () {
		for (Entry<Integer, WorldShader> s : shaders.entrySet()) {
			s.getValue().cleanUp();
		}
	}
	
	public static int addShader(WorldShader shader) {
		shaders.put(lastIndex, shader);
		lastIndex++;
		return lastIndex-1;
	}
	
	public static void addProjectionUpdateListener(Runnable run) {
		projectionChanges.add(run);
	}
	
}
