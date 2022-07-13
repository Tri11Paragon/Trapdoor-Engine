package com.trapdoor.totalcrafter.data.nbt;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class FloatTag extends Tag<Float>{

	public FloatTag(String name) {
		super(Tags.TAG_Float, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(type);
			writer.writeUTF(name);
			writer.writeFloat(get());
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public Float get() {
		return super.get();
	}

}
