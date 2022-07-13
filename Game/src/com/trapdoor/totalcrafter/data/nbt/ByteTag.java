package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class ByteTag extends Tag<Byte> {

	public ByteTag(String name) {
		super(Tags.TAG_Byte, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeByte(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}
	
}
