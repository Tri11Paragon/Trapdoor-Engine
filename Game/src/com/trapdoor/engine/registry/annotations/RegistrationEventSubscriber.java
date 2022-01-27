package com.trapdoor.engine.registry.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Inherited
/**
 * @author brett
 * @date Jan. 26, 2022
 * This annotation registers the method to be called by the game registry during registration.
 * Use this to register your textures / models
 */
public @interface RegistrationEventSubscriber {
	
}
