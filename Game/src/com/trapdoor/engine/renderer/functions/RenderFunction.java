package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.lighting.ExtensibleLightingArray;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

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
	
	public void doRender(Model m, Entity[] ents, Camera camera) {
		
	}
	
	public Entity[] sortEntities(ArrayList<Entity> lis) {
		// TODO: reuse entity buffer
		Entity[] sorted = new Entity[lis.size()];
		sorted[0] = lis.get(0);
		for (int i = 1; i < lis.size(); i++) {
			Entity e = lis.get(i);
			Transform t = e.getComponent(Transform.class);
			try {
				sorted[i] = e;
				if (t.getDistanceToCamera() < sorted[i-1].getComponent(Transform.class).getDistanceToCamera()) {
					sorted[i] = sorted[i-1];
					sorted[i-1] = e;
				}
			} catch (Exception err) {}
		}
		return insertionSort(sorted);
	}
	
	public Entity[] insertionSort(Entity[] ents) {  
		
		// insertion sort
        int n = ents.length;  
        for (int j = 1; j < n; j++) {  
            Entity key = ents[j];  
            int i = j-1;  
            try {
            while ( (i > -1) && ( ents[i].getComponent(Transform.class).getDistanceToCamera() > key.getComponent(Transform.class).getDistanceToCamera() ) ) {  
                ents[i+1] = ents[i];  
                i--;  
            }  
            } catch (Exception e) {
            	Logging.logger.warn(e.getMessage(), e);
            	Logging.logger.warn("This warn is a TODO which will never be done.");
            }
            ents[i+1] = key;  
        }  
        return ents;
    }  
	
	public abstract void render(Model m, ArrayList<Entity> lis, Camera camera);
	
}
