package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class LongTag extends Tag<Long>{

	public LongTag(String name) {
		super(Tags.TAG_Long, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeLong(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
