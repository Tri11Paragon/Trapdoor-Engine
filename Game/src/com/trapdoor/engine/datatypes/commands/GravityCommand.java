package com.trapdoor.engine.datatypes.commands;

import org.apache.commons.cli.CommandLine;

import com.jme3.math.Vector3f;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;

public class GravityCommand extends Command {
	
	private EntityCamera camera;
	public GravityCommand(EntityCamera camera) {
		this.options.addOption("r", "raycast", false, "Use the raycast (ie last object)");
		this.options.addOption("c", "camera", false, "Use the raycast (ie last object)");
		this.camera = camera;
	}

	@Override
	public Object run(CommandLine line, Object lastObject) {
		try {
			double gravity = Double.parseDouble(line.getArgs()[1]);
			if (line.hasOption("r")) {
				((Entity) lastObject).getRigidbody().setGravity(new Vector3f(0, (float) gravity, 0));
				return "Set " + lastObject + " Gravity!";
			} else if (line.hasOption("c")) {
				camera.getCharacter().setGravity(new Vector3f(0, (float) gravity, 0));
				return "Set Camera Gravity";
			} else
				return "Unable to set gravity of object";
		} catch (Exception e) {
			return "Exception: " + e.getMessage();
		}
	}

}
