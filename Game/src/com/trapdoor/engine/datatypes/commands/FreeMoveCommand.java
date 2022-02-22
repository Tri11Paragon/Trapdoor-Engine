package com.trapdoor.engine.datatypes.commands;

import org.apache.commons.cli.CommandLine;

import com.trapdoor.engine.camera.CreativeFirstPerson;

public class FreeMoveCommand extends Command {

	private CreativeFirstPerson c;
	
	public FreeMoveCommand(CreativeFirstPerson c) {
		this.c = c;
	}
	
	@Override
	public Object run(CommandLine line, Object lastObject) {
		c.allowFreeMovement = !c.allowFreeMovement;
		return "Free move: " + c.allowFreeMovement;
	}

}
