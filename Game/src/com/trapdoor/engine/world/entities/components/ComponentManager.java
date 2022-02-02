package com.trapdoor.engine.world.entities.components;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public class ComponentManager {
	
	private static final HashMap<Class<? extends IComponent>, Integer> componentIds = new HashMap<Class<? extends IComponent>, Integer>();
	private static final HashMap<Integer, Class<? extends IComponent>> idsComponent = new HashMap<Integer, Class<? extends IComponent>>();
	
	private static final AtomicInteger counter = new AtomicInteger();
	
	public static void registerComponents() {
		registerComponent(new Transform());
	}
	
	public static int leaseID() {
		return counter.incrementAndGet();
	}
	
	public static IComponent getComponent(int id) {
		return idsComponent.get(id).cast(IComponent.class);
	}
	
	public static int getID(Class<? extends IComponent> c) {
		return componentIds.get(c);
	}
	
	private static void registerComponent(IComponent c) {
		componentIds.put(c.getClass(), c.getID());
		idsComponent.put(c.getID(), c.getClass());
	}
	
}
