package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class IntTag extends Tag<Integer> {

	public IntTag(String name) {
		super(Tags.TAG_Int, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeInt(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}
	
}
