package com.game.engine.registry.annotations;

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
 * @date Jan. 26, 2022
 * Called when a class wants all intrusive open layers (ie Console, DebugScreen) to be closed.
 */
public @interface ClearScreenEventSubscriber {

}
