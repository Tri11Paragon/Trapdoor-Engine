package com.trapdoor.engine.tools;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.trapdoor.engine.renderer.ui.Console;

/**
 * @author brett
 * @date Mar. 18, 2022
 * 
 */
@Plugin(name = "DebugConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class DebugConsoleAppender extends AbstractAppender {

	protected DebugConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
		super(name, filter, layout, ignoreExceptions, properties);
	}

	@Override
	public void append(LogEvent event) {
		
		System.err.println(event.getMessage().getFormattedMessage());
		
		Console.getInstance().append(event.getMessage().getFormattedMessage());
		
	}
	
    @PluginFactory
    public static DebugConsoleAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginElement("Layout") Layout<? extends Serializable> layout,
                                              @PluginElement("Filters") Filter filter) {
 
        if (name == null) {
            LOGGER.error("No name provided for StubAppender");
            return null;
        }
 
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DebugConsoleAppender(name, filter, layout, true, Property.EMPTY_ARRAY);
    }
	
}
