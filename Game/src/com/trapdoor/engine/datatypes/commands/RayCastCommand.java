package com.trapdoor.engine.datatypes.commands;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.trapdoor.engine.tools.RayCasting;

/**
 * @author brett
 * @date Feb. 14, 2022
 * 
 */
public class RayCastCommand extends Command {

	private RayCasting raycaster;
	
	public RayCastCommand(RayCasting raycaster) {
		super();
		this.raycaster = raycaster;
		this.options.addOption("r", "range", true, "Specify the range of the RayCast");
		this.options.addOption("s", "sort", false, "Use the sorted version of Trapdoor's raycasting");
		this.options.addOption("a", "all", false, "Return all of the objects or just the first one? Useful with sort");
	}
	
	@Override
	public Object run(CommandLine line, Object last) {
		int range = 10;
		try {
			range = (int) Float.parseFloat((String) line.getParsedOptionValue("r"));
		} catch (ParseException e) {
			return "Invalid input, unable to parse range. \n\tThis is what I know:\n\t" + e.getMessage() + "\n\t Assuming range of " + range;
		}
		List<PhysicsRayTestResult> results = null;
		if (line.hasOption("s"))
			results = raycaster.raycastSorted(range);
		else 
			results = raycaster.raycast(range);
		return line.hasOption("a") ? results : results.get(0);
	}

}
