package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.lighting.ExtensibleLightingArray;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Mar. 16, 2022
 * 
 */
public abstract class RenderFunction {
	
	protected ShaderProgram program;
	protected ExtensibleLightingArray frameLights;
	
	public RenderFunction(ShaderProgram program, ExtensibleLightingArray frameLights) {
		this.program = program;
		this.frameLights = frameLights;
	}
	
	/**
	 * check if bounding box is in camera frustum
	 * @param bb must be in world space
	 * @return
	 */
	public boolean checkInFrustum(Camera camera, AxisAlignedBoundingBox bb) {
		return camera.cubeInFrustum(bb.getMinX(), bb.getMinY(), bb.getMinZ(), bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
	}
	
	public abstract void render(Model m, ArrayList<Entity> lis, Camera camera);
	
}
