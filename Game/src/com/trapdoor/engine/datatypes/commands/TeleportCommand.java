package com.trapdoor.engine.datatypes.commands;

import org.apache.commons.cli.CommandLine;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Feb. 14, 2022
 * 
 */
public class TeleportCommand extends Command {
	
	private Transform cameraEnt; 
	private Camera cam;
	
	public TeleportCommand(EntityCamera cameraEnt, Camera cam) {
		this.cameraEnt = cameraEnt.getComponent(Transform.class);
		this.cam = cam;
	}

	@Override
	public Object run(CommandLine line, Object lastObject) {
		String[] args = line.getArgs();
		if (args.length < 3)
			return "Please enter a X Y Z";
		String argX = args[0];
		String argY = args[1];
		String argZ = args[2];
		double x = 0;
		double y = 0;
		double z = 0;
		if (argX.startsWith("~")) {
			
		}
		
		
		return null;
	}
	
}
