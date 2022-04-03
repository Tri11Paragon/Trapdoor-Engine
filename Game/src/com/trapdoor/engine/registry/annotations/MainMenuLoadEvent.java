package com.trapdoor.engine.registry.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
/**
 * @author brett
 * @date Apr. 3, 2022
 * called by the LoadingScreenDisplay after loading has complete
 * feel free to use this to register your main menu / scene that will be loaded
 * after the completion of the load cycle.
 */
public @interface MainMenuLoadEvent {

}
