package com.game.engine.registry.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.reflections.Reflections;

import com.game.engine.tools.Logging;

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
	
	public static void init() {
		Logging.logger.debug("Loading event subscribers");
		
		ourClasses = new Reflections("com.game");    

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
				}
			}
		});
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
	
	private static void invokeMethod(Method m) {
		try {
			m.invoke(null, (Object[]) null);
		} catch (IllegalAccessException e) {
			Logging.logger.fatal(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Logging.logger.fatal(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Logging.logger.fatal(e.getMessage(), e);
		}
	}
	
}
