package com.trapdoor.totalcrafter.data.nbt;

/**
 * @author brett
 * @date Jul. 8, 2022
 * http://web.archive.org/web/20110723210920/http://www.minecraft.net/docs/NBT.txt
 */
public abstract class Tag<P> {
	
	// The tagType is a single byte defining the contents of the payload of the tag. 
	protected byte type;
	// The name is a descriptive name, and can be anything (eg "cat", "banana", "Hello World!"). It has nothing to do with the tagType.
	protected String name;
	
	protected P payload;
	
	public Tag(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Tag<P> put(P payload) {
		this.payload = payload;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public byte getType() {
		return type;
	}
	
	public P get() {
		return payload;
	}
	
	public abstract void writeTag(TagWriter writer);
	
}
