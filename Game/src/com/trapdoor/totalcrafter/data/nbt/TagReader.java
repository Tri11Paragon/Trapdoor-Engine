package com.trapdoor.totalcrafter.data.nbt;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 11, 2022
 * 
 */
public class TagReader extends DataInputStream {

	private static final int BUFFER_SIZE = 8192;
	
	public TagReader(String file) throws Exception {
		super(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)));
	}
	
	/**
	 * loads the entire tag structure of the file.
	 */
	public CompoundTag readTags() {
		try {
			return (CompoundTag) reads[this.readByte()].read(this);
		} catch (IOException e) {Logging.logger.error(e.getMessage(), e);}
		return null;
	}
	
	private interface Readable<T extends Tag<?>>{
		public T read(TagReader reader);
	}
	
	private static final Readable<?>[] reads = {
	/* TAG_end */					reader -> null,
	/* TAG_byte */					reader -> {
												try {
													return new ByteTag(reader.readUTF()).put(reader.readByte());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_short */					reader -> {
												try {
													return new ShortTag(reader.readUTF()).put(reader.readShort());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_int */					reader -> {
												try {
													return new IntTag(reader.readUTF()).put(reader.readInt());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_long */					reader -> {
												try {
													return new LongTag(reader.readUTF()).put(reader.readLong());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_float */					reader -> {
												try {
													return new FloatTag(reader.readUTF()).put(reader.readFloat());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_double */				reader -> {
												try {
													return new DoubleTag(reader.readUTF()).put(reader.readDouble());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_byte_array */			reader -> {
												try {
													String name = reader.readUTF();
													int length = reader.readInt();
													byte[] theBytes = new byte[length];
													for (int i = 0; i < length; i++)
														theBytes[i] = reader.readByte();
													return new ByteArrayTag(name).put(theBytes);
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_string */				reader -> {
												try {
													return new StringTag(reader.readUTF()).put(reader.readUTF());
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_list */					reader -> {
												return decodeListTag(reader);
											  },
	/* TAG_Compound */				reader -> {
												try {
													return decodeCompoundTag(reader);
												} catch (Exception e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_int_array */				reader -> {
												try {
													String name = reader.readUTF();
													int length = reader.readInt();
													int[] theBytes = new int[length];
													for (int i = 0; i < length; i++)
														theBytes[i] = reader.readInt();
													return new IntArrayTag(name).put(theBytes);
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	/* TAG_long_array */			reader -> {
												try {
													String name = reader.readUTF();
													int length = reader.readInt();
													long[] theBytes = new long[length];
													for (int i = 0; i < length; i++)
														theBytes[i] = reader.readLong();
													return new LongArrayTag(name).put(theBytes);
												} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
											  },
	};
	
	private static final Readable<?>[] readsNoName = {
			/* TAG_end */					reader -> null,
			/* TAG_byte */					reader -> {
														try {
															return new ByteTag("").put(reader.readByte());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_short */					reader -> {
														try {
															return new ShortTag("").put(reader.readShort());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_int */					reader -> {
														try {
															return new IntTag("").put(reader.readInt());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_long */					reader -> {
														try {
															return new LongTag("").put(reader.readLong());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_float */					reader -> {
														try {
															return new FloatTag("").put(reader.readFloat());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_double */				reader -> {
														try {
															return new DoubleTag("").put(reader.readDouble());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_byte_array */			reader -> {
														try {
															int length = reader.readInt();
															byte[] theBytes = new byte[length];
															for (int i = 0; i < length; i++)
																theBytes[i] = reader.readByte();
															return new ByteArrayTag("").put(theBytes);
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_string */				reader -> {
														try {
															return new StringTag("").put(reader.readUTF());
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_list */					reader -> {
														return decodeListTag(reader);
													  },
			/* TAG_Compound */				reader -> {
														try {
															return decodeCompoundTag(reader);
														} catch (Exception e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_int_array */				reader -> {
														try {
															int length = reader.readInt();
															int[] theBytes = new int[length];
															for (int i = 0; i < length; i++)
																theBytes[i] = reader.readInt();
															return new IntArrayTag("").put(theBytes);
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			/* TAG_long_array */			reader -> {
														try {
															int length = reader.readInt();
															long[] theBytes = new long[length];
															for (int i = 0; i < length; i++)
																theBytes[i] = reader.readLong();
															return new LongArrayTag("").put(theBytes);
														} catch (IOException e) {Logging.logger.error(e.getMessage(), e); return null;}
													  },
			};
	
	private static ListTag decodeListTag(TagReader reader) {
		try {
			String name = reader.readUTF();
			byte id = reader.readByte();
			int length = reader.readInt();
			ListTag ret = new ListTag(name);
			List<Tag<?>> longList = new ArrayList<Tag<?>>();
			if (id == Tags.TAG_End)
				return ret;
			
			for (int i = 0; i < length; i++)
				longList.add(readsNoName[id].read(reader));
			
			ret.put(longList);
			
			return ret;
		} catch (Exception e) {Logging.logger.error(e.getMessage(), e); return null;}
	}
	
	private static CompoundTag decodeCompoundTag(TagReader reader) {
		try {
			CompoundTag tagger = new CompoundTag(reader.readUTF());
			
			byte id;
			while ((id = reader.readByte()) != Tags.TAG_End) {
				tagger.put(reads[id].read(reader));
			}
			return tagger;
		} catch (IOException e) {
			Logging.logger.error(e.getMessage(), e);
		}
		return null;
	}

}
