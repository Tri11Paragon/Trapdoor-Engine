package com.trapdoor.totalcrafter.data.nbt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class CompoundTag extends Tag<List<Tag<?>>> {

	private Map<String, Tag<?>> tagAsso = new HashMap<String, Tag<?>>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CompoundTag(String name) {
		super(Tags.TAG_Compound, name);
		super.put(new ArrayList());
	}

	@Override
	public Tag<List<Tag<?>>> put(List<Tag<?>> payload) {
		// do not allow for the internal list to be overwritten.
		return null;
	}
	
	public void put(Tag<?> tag) {
		if (tag == null)
			return;
		tagAsso.put(tag.getName(), tag);
		this.payload.add(tag);
	}
	
	public boolean hasTag(String name) {
		return tagAsso.containsKey(name);
	}
	
	public <T extends Tag<?>> T getTag(String name) {
		return get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Tag<?>> T get(String name) {
		return (T) tagAsso.get(name);
	}
	
	@Override
	public void writeTag(TagWriter writer) {
		try {
			List<? extends Tag<?>> tags = get();
			
			writer.writeByte(type);
			writer.writeUTF(name);
			
			for (int i = 0; i < tags.size(); i++)
				tags.get(i).writeTag(writer);
			
			writer.writeByte(Tags.TAG_End);
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
