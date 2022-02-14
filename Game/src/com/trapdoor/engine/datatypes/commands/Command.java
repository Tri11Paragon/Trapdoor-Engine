package com.trapdoor.engine.datatypes.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * @author brett
 * @date Feb. 14, 2022
 * 
 */
public abstract class Command {
	
	protected final Options options;
	
	public Command() {
		this.options = new Options();
		
	}
	
	public abstract Object run(CommandLine line, Object lastObject);
	
	public Options getOptions() {
		return options;
	}
	
}
