package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class ByteArrayTag extends Tag<byte[]> {

	public ByteArrayTag(String name) {
		super(Tags.TAG_Byte_Array, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			byte[] data = get();
			writer.writeInt(data.length);
			for (int i = 0; i < data.length; i++)
				writer.writeByte(data[i]);
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
