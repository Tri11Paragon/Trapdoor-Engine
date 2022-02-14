package com.trapdoor.engine.datatypes.commands;

import org.apache.commons.cli.CommandLine;

/**
 * @author brett
 * @date Feb. 14, 2022
 * 
 */
public class HelloWorldCommand extends Command {

	public HelloWorldCommand() {
		super();
		this.options.addOption("w", "world", false, "adds a 'world' after hello");
	}
	
	@Override
	public String run(CommandLine line, Object last) {
		String ret = "Hello";
		if (line.hasOption("w")) {
			ret += " World!";
		}
		return ret;
	}

}
