package com.trapdoor.engine.datatypes.lighting;

import java.util.ArrayList;

import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.datatypes.Tuple;
import com.trapdoor.engine.datatypes.util.ExtensibleArray;
import com.trapdoor.engine.renderer.DeferredSecondPassShader;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Jan. 17, 2022
 * 
 * A problem came up when designing the lighting engine.
 * 
 * How do we allow for any entity (in current form) contain multiple lights?
 * 
 * Components? No that's slow, and likely to be removed soon
 * Why?
 * 	Components need to be grabbed each frame and added to this class.
 *	getting all of them takes too much time. It would be great to use components, sure.
 *
 * How about we say an entity is allowed up to say 10 lights. That's reasonable! 
 * 
 * 
 */
public class ExtensibleLightingArray {

	public static final int MAX_LIGHTINGS_PER_ENTITY = 8;
	
	private float[] lightArray = new float[DeferredSecondPassShader.MAX_LIGHTS * DeferredSecondPassShader.STRIDE_SIZE];
	
	// TODO: create own better memory reusing array list
	//private ArrayList<Tuple<ArrayList<Light>, Entity>> lights;
	private ExtensibleArray<Tuple<ArrayList<Light>, Entity>> lights;
	
	public ExtensibleLightingArray() {
		this.lights = new ExtensibleArray<Tuple<ArrayList<Light>, Entity>>();
	}
	
	public void add(ArrayList<Light> lightList, Entity e) {
		this.lights.add(new Tuple<ArrayList<Light>, Entity>(lightList, e));
	}
	
	public void updateUBO() {
		setLightingDataArray();
		UBOLoader.updateLightingUBO(lightArray);
	}
	
	public void clear() {
		//lights = new ArrayList<>();
		this.lights.clear();
	}
	
	private void setLightingDataArray() {
		int cur = 0;
		l1: for (int i = 0; i < lights.size(); i++) {
			ArrayList<Light> lightsArr = lights.get(i).getX();
			Entity e = lights.get(i).getY();
			
			int size = lightsArr.size();
			if (size > MAX_LIGHTINGS_PER_ENTITY)
				size = MAX_LIGHTINGS_PER_ENTITY;
			
			for (int j = 0; j < size; j++) {
				Light l = lightsArr.get(j);
				
				if (l.isDirectional())
					continue;
				
				setLightData(l, cur, e);
				
				cur++;
				if (cur >= DeferredSecondPassShader.MAX_LIGHTS)
					break l1;
			}
		}
	}
	
	private void setLightData(Light l, int index, Entity e) {
		int base = index * 4;
		Transform t = (Transform)e.getComponent(Transform.class);
		// TODO: apply entity rotation
		lightArray[base] = l.getX() + t.getX();
		lightArray[base+1] = l.getY() + t.getY();
		lightArray[base+2] = l.getZ() + t.getZ();
		lightArray[base+3] = l.getLinear();
		int baseOffset = base + 512 / 4;
		lightArray[baseOffset] = l.getQuadratic();
		lightArray[baseOffset+1] = l.getR();
		lightArray[baseOffset+2] = l.getG();
		lightArray[baseOffset+3] = l.getB();
	}
	
}
