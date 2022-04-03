package com.trapdoor.engine.registry.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.reflections.Reflections;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jan. 26, 2022
 * 
 */
public class AnnotationHandler {
	
	private static Reflections ourClasses;
	
	private static final ArrayList<Method> preRegistrationEventSubscribers = new ArrayList<Method>();
	private static final ArrayList<Method> registrationEventSubscribers = new ArrayList<Method>();
	private static final ArrayList<Method> postRegistrationEventSubscribers = new ArrayList<Method>();
	private static final ArrayList<Method> rescaleEventSubscribers = new ArrayList<Method>();
	private static final ArrayList<Method> clearScreenEventSubscribers = new ArrayList<Method>();
	private static Method mainMenuLoadEvent = null;
	
	public static void init() {
		Logging.logger.debug("Loading event subscribers");
		
		// search for game implementations of the engine's annotations
		ourClasses = new Reflections("com.game");
		runAnnotationSearch(ourClasses);
		
		// should never have any in here, but just in case
		ourClasses = new Reflections("com.karl");
		runAnnotationSearch(ourClasses);
		
		// finally get the engine annotations, last since this is most likely the place for all the important annotations
		ourClasses = new Reflections("com.trapdoor");
		runAnnotationSearch(ourClasses);
		
	}
	
	private static void runAnnotationSearch(Reflections ourClasses) {
		Set<Class<? extends AnnotatedClass>> classes = ourClasses.getSubTypesOf(AnnotatedClass.class);
		
		classes.forEach((c) -> {
			Method[] test = c.getMethods();
			
			for (Method m : test) {
				for (Annotation a : m.getAnnotations()) {
					if (a instanceof PreRegistrationEventSubscriber)
						preRegistrationEventSubscribers.add(m);
					else if (a instanceof RegistrationEventSubscriber)
						registrationEventSubscribers.add(m);
					else if (a instanceof PostRegistrationEventSubscriber)
						postRegistrationEventSubscribers.add(m);
					else if (a instanceof RescaleEventSubscriber)
						rescaleEventSubscribers.add(m);
					else if (a instanceof ClearScreenEventSubscriber)
						clearScreenEventSubscribers.add(m);
					else if (a instanceof MainMenuLoadEvent)
						mainMenuLoadEvent = m;
				}
			}
		});
	}
	
	/**
	 * Use this to close all open layers which may be intrusive (ie Console, DebugScreen)
	 */
	public static void cleanScreen() {
		for (Method m : clearScreenEventSubscribers) {
			invokeMethod(m);
		}
	}
	
	private static long timeSinceLast = 0;
	
	public static void runRescaleEvent(int width, int height) {
		if (System.currentTimeMillis() - timeSinceLast > 250) {
			Logging.logger.debug("Running rescale events! (" + width + ", " + height + ")");
			timeSinceLast = System.currentTimeMillis();
		}
		for (Method m : rescaleEventSubscribers) {
			Logging.logger.trace("Running rescale on class: " + m.getDeclaringClass().getSimpleName() + " Method: " + m.getName());
			invokeMethod(m, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		}
	}
	
	public static void runPreRegistration() {
		Logging.logger.debug("Running pre-registration");
		for (Method m : preRegistrationEventSubscribers) {
			Logging.logger.trace("Running pre-registration on class: " + m.getDeclaringClass().getSimpleName() + " Method: " + m.getName());
			invokeMethod(m);
		}
	}
	
	public static void runRegistration() {
		Logging.logger.debug("Running registration");
		for (Method m : registrationEventSubscribers) {
			Logging.logger.trace("Running registration on class: " + m.getDeclaringClass().getSimpleName() + " Method: " + m.getName());
			invokeMethod(m);
		}
	}
	
	public static void runPostRegistration() {
		Logging.logger.debug("Running post-registration");
		for (Method m : postRegistrationEventSubscribers) {
			Logging.logger.trace("Running post-registration on class: " + m.getDeclaringClass().getSimpleName() + " Method: " + m.getName());
			invokeMethod(m);
		}
	}
	
	public static void runMainMenu() {
		invokeMethod(mainMenuLoadEvent);
	}
	
	private static void invokeMethod(Method m) {
		invokeMethod(m, (Object[]) null);
	}
	
	private static void invokeMethod(Method m, Object... args) {
		try {
			m.invoke(null, args);
		} catch (IllegalAccessException e) {
			Logging.logger.fatal(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Logging.logger.fatal(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Logging.logger.fatal(e.getMessage(), e);
		}
	}
	
}
