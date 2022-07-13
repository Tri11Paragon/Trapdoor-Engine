package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class DoubleTag extends Tag<Double> {

	public DoubleTag(String name) {
		super(Tags.TAG_Double, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeDouble(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
