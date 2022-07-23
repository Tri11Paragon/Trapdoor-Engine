package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class StringTag extends Tag<String> {

	public StringTag(String name) {
		super(Tags.TAG_String, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeUTF(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
