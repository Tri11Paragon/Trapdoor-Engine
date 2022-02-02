package com.trapdoor.engine.world.entities.components;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public class ComponentStore {
	
	private ArrayList<IComponent> components = new ArrayList<IComponent>();
	private HashMap<Class<? extends IComponent>, IComponent> componentMap = new HashMap<Class<? extends IComponent>, IComponent>();
	
	public ComponentStore() {
		
	}
	
	public void render() {
		for (int i = 0; i < components.size(); i++)
			components.get(i).render();
	}
	
	public void update() {
		for (int i = 0; i < components.size(); i++)
			components.get(i).update();
	}
	
	/**
	 * returns the component (if it exists on this entity) associated with a specifed class
	 * 
	 * eg: getting the transform with c can be supplied as Transform.class
	 */
	public IComponent getCompoent(Class<? extends IComponent> c) {
		return componentMap.get(c);
	}
	
	public void addComponent(IComponent c) {
		this.components.add(c);
		this.componentMap.put(c.getClass(), c);
	}
	
}
