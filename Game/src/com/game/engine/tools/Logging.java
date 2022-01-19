package com.game.engine.tools;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

public class Logging {

	static {
		ConfigurationFactory.setConfigurationFactory(new CustomConfigFactory());
	}
	public static final Logger logger = LogManager.getRootLogger();;
	
	public static void init() {
		logger.debug("Logger Init Successful");
	}
	
	// TODO: more functions?
	
	@Plugin(name = "CustomConfigFactory", category = ConfigurationFactory.CATEGORY)
	@Order(50)
	public static class CustomConfigFactory extends ConfigurationFactory {
		
		static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
	        builder.setConfigurationName(name);
	        builder.setStatusLevel(Level.ALL);
	        
	        // TODO: file logger
	        
	        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
	            addAttribute("level", Level.ALL));
	        
	        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
	            addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
	        // %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %5p %F:%L - %msg%n
	        appenderBuilder.add(builder.newLayout("PatternLayout").
	            addAttribute("pattern", "[%d{HH:mm:ss.SSS}] [%t/%p]: (%F:%L): %msg%n"));
	        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY,
	            Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
	        builder.add(appenderBuilder);
	        
	        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG).
	            add(builder.newAppenderRef("Stdout")).
	            addAttribute("additivity", false));
	        
	        builder.add(builder.newRootLogger(Level.ALL).add(builder.newAppenderRef("Stdout")));
	        
	        return builder.build();
	    }
		
		@Override
		protected String[] getSupportedTypes() {
			return new String[] {"*"};
		}

		@Override
	    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
	        return getConfiguration(loggerContext, source.toString(), null);
	    }

	    @Override
	    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
	        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
	        return createConfiguration(name, builder);
	    }
		
	}
	
}
