package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class ShortTag extends Tag<Short> {

	public ShortTag(String name) {
		super(Tags.TAG_Short, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeShort(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
