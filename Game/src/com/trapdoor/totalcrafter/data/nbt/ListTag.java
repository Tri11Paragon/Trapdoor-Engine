package com.trapdoor.totalcrafter.data.nbt;

import java.io.IOException;
import java.util.List;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class ListTag extends Tag<List<Tag<?>>> {

	public ListTag(String name) {
		super(Tags.TAG_List, name);
	}

	@Override
	public void writeTag(TagWriter writer) {
		try {
			writer.writeByte(this.type);
			writer.writeUTF(name);
			if (get().size() <= 0)
				writer.writeByte(Tags.TAG_End);
			else {
				Tag<?> type = get().get(0);
				writer.writeByte(type.type);
			}
			writer.writeInt(get().size());
			for (Tag<?> tag : this.get())
				writes[tag.getType()].write(tag, writer);
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * I don't intend to use lists often, even if i do i don't expect this to cause many issues
	 * however using the function version as i have above eg writeForList() function would've been better
	 * but not necessary 
	 */
	private interface Writable {
		public void write(Tag<?> data, TagWriter writer);
	}
	@SuppressWarnings("unchecked")
	private static final Writable[] writes = {
	/* TAG_end */					(data, writer) -> {},
	/* TAG_byte */					(data, writer) -> {try {writer.writeByte((byte) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_short */					(data, writer) -> {try {writer.writeShort((short) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_int */					(data, writer) -> {try {writer.writeInt((int) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_long */					(data, writer) -> {try {writer.writeLong((long) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_float */					(data, writer) -> {try {writer.writeFloat((float) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_double */				(data, writer) -> {try {writer.writeDouble((double) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_byte_array */			(data, writer) -> {try {
																byte[] bdata = (byte[]) data.get();
																writer.writeInt(bdata.length);
																for (int i = 0; i < bdata.length; i++)
																	writer.writeByte(bdata[i]);
															} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_string */				(data, writer) -> {try {writer.writeChar(((String) data.get()).length()); writer.writeUTF((String) data.get());} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_list */					(data, writer) -> {writeRecursiveList(((Tag<List<Tag<?>>>) data), writer);},
	/* TAG_compound */				(data, writer) -> {try {
																CompoundTag tager = (CompoundTag) data;
																for (int i = 0; i < tager.get().size(); i++)
																	tager.get().get(i).writeTag(writer);
																
																writer.writeByte(Tags.TAG_End);
															} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_int_array */				(data, writer) -> {try {
																int[] bdata = (int[]) data.get();
																writer.writeInt(bdata.length);
																for (int i = 0; i < bdata.length; i++)
																	writer.writeInt(bdata[i]);
															} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	/* TAG_long_array */			(data, writer) -> {try {
																long[] bdata = (long[]) data.get();
																writer.writeInt(bdata.length);
																for (int i = 0; i < bdata.length; i++)
																	writer.writeLong(bdata[i]);
															} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}},
	};
	
	private static void writeRecursiveList(Tag<List<Tag<?>>> data, TagWriter writer) {
		try {
			if (data.get().size() <= 0)
				writer.writeByte(Tags.TAG_End);
			else {
				Tag<?> type = data.get().get(0);
				writer.writeByte(type.type);
			}
			writer.writeInt(data.get().size());
			for (Tag<?> tag : data.get())
				writes[tag.getType()].write(tag, writer);
		} catch (IOException e) {
			Logging.logger.error(e.getMessage(), e);
		}
	}

}
