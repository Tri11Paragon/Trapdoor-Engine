package com.trapdoor.engine.renderer;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.lighting.ExtensibleLightingArray;

/**
 * @author laptop
 * @date Mar. 16, 2022
 * 
 */
public class MaterialPassRenderer {
	
	private MaterialPassShader shader;
	private Camera camera;
	
	private ExtensibleLightingArray lights = new ExtensibleLightingArray();
	
	public MaterialPassRenderer(Camera camera) {
		this.shader = new MaterialPassShader();
		this.camera = camera;
	}
	
	public void start() {
		this.shader.start();
		this.shader.loadVector("viewPos", camera.getPosition());
		lights.updateUBO(this.camera.getViewMatrix());
		lights.clear();
	}
	
	public void end() {
		this.shader.stop();
	}
	
	public MaterialPassShader getShader() {
		return shader;
	}
	
	public ExtensibleLightingArray getLightingArray() {
		return this.lights;
	}
	
}
