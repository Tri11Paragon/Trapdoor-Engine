package com.trapdoor.totalcrafter.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.trapdoor.totalcrafter.data.nbt.ByteArrayTag;
import com.trapdoor.totalcrafter.data.nbt.ByteTag;
import com.trapdoor.totalcrafter.data.nbt.CompoundTag;
import com.trapdoor.totalcrafter.data.nbt.DoubleTag;
import com.trapdoor.totalcrafter.data.nbt.FloatTag;
import com.trapdoor.totalcrafter.data.nbt.IntTag;
import com.trapdoor.totalcrafter.data.nbt.ListTag;
import com.trapdoor.totalcrafter.data.nbt.LongTag;
import com.trapdoor.totalcrafter.data.nbt.ShortTag;
import com.trapdoor.totalcrafter.data.nbt.StringTag;
import com.trapdoor.totalcrafter.data.nbt.Tag;
import com.trapdoor.totalcrafter.data.nbt.TagReader;
import com.trapdoor.totalcrafter.data.nbt.TagWriter;

import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.io.CompressionType;

/**
 * @author brett
 * @date Jul. 9, 2022
 * 
 */
public class NBTImplTest {
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void main(String[] args) throws Exception {
		
		int timer = 5000;
		int wtimer = 5000;
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < wtimer; i++) {
			TagWriter writer = new TagWriter("based.nbt");
			CompoundTag tag = new CompoundTag("Based");
			
			tag.put(new IntTag("Magic").put(5));
			tag.put(new StringTag("Namers").put("GaybitchUytsef"));
			tag.put(new ByteTag("SexByes").put((byte) 12));
			tag.put(new FloatTag("FloatyNess").put(5.125234f));
			tag.put(new DoubleTag("HappyDoubles").put(52.5213406));
			tag.put(new ByteArrayTag("heyitbyres").put(new byte[] {0b1001, 0b0001, 0b1011}));
			CompoundTag taggy = new CompoundTag("Background");
			taggy.put(new StringTag("SoundlyFex").put("TheNames"));
			taggy.put(new LongTag("LongLegs").put(5120l));
			tag.put(taggy);
			
			tag.put(new ShortTag("shortTest").put((short) 32767));
			
			List<Tag<?>> longList = new ArrayList<Tag<?>>();
			longList.add(new LongTag("Irrelavant").put(5l));
			longList.add(new LongTag("Irrelavant").put(6l));
			longList.add(new LongTag("Irrelavant").put(8l));
			longList.add(new LongTag("Irrelavant").put(0l));
			longList.add(new LongTag("Irrelavant").put(55l));
			
			ListTag listTag = new ListTag("LongestList");
			listTag.put(longList);
			tag.put(listTag);
			
			tag.writeTag(writer);
			writer.close();
		}
		long end = System.currentTimeMillis();
		System.out.println("My implementation took " + (end-start) + "ms to run.");
		
		long nstart = System.currentTimeMillis();
		for (int i = 0; i < wtimer; i++) {
			final Nbt NBT = new Nbt();
			dev.dewy.nbt.tags.collection.CompoundTag ntag = new dev.dewy.nbt.tags.collection.CompoundTag("Based");
			
			ntag.put(new dev.dewy.nbt.tags.primitive.IntTag("Magic", 5));
			ntag.put(new dev.dewy.nbt.tags.primitive.StringTag("Namers", "GaybitchUytsef"));
			ntag.put(new dev.dewy.nbt.tags.primitive.ByteTag("SexByes", (byte) 12));
			ntag.put(new dev.dewy.nbt.tags.primitive.FloatTag("FloatyNess", 5.125234f));
			ntag.put(new dev.dewy.nbt.tags.primitive.DoubleTag("HappyDoubles", 52.5213406));
			ntag.put(new dev.dewy.nbt.tags.array.ByteArrayTag("heyitbyres", new byte[] {0b1001, 0b0001, 0b1011}));
			dev.dewy.nbt.tags.collection.CompoundTag ntaggy = new dev.dewy.nbt.tags.collection.CompoundTag("Background");
			ntaggy.put(new dev.dewy.nbt.tags.primitive.StringTag("SoundlyFex", "TheNames"));
			ntaggy.put(new dev.dewy.nbt.tags.primitive.LongTag("LongLegs", 5120l));
			ntag.put(ntaggy);
			
			ntag.put(new dev.dewy.nbt.tags.primitive.ShortTag("shortTest", (short) 32767));
			
			List<dev.dewy.nbt.tags.primitive.LongTag> nlongList = new ArrayList<dev.dewy.nbt.tags.primitive.LongTag>();
			nlongList.add(new dev.dewy.nbt.tags.primitive.LongTag(5));
			nlongList.add(new dev.dewy.nbt.tags.primitive.LongTag(6l));
			nlongList.add(new dev.dewy.nbt.tags.primitive.LongTag(8l));
			nlongList.add(new dev.dewy.nbt.tags.primitive.LongTag(0l));
			nlongList.add(new dev.dewy.nbt.tags.primitive.LongTag(55l));
			
			dev.dewy.nbt.tags.collection.ListTag<dev.dewy.nbt.tags.primitive.LongTag> nlistTag = new dev.dewy.nbt.tags.collection.ListTag<dev.dewy.nbt.tags.primitive.LongTag>("LongestList", nlongList);
			ntag.put(nlistTag);
			
			NBT.toFile(ntag, new File("unbased.nbt"), CompressionType.GZIP);
		}
		long nend = System.currentTimeMillis();
		System.out.println("Their implementation took " + (nend-nstart) + "ms to run.");
		
		long rtstart = System.currentTimeMillis();
		for (int j = 0; j < timer; j++) {
			TagReader reader = new TagReader("based.nbt");
			CompoundTag readTag = reader.readTags();
			
//			System.out.println(readTag.getName());
//			
//			if(readTag.hasTag("Magic"))
//				System.out.println(readTag.getTag("Magic").get());
			
			if (readTag.hasTag("Background")) {
				CompoundTag back = readTag.getTag("Background");
//				if (back.hasTag("LongLegs"))
//					System.out.println(back.getTag("LongLegs").get());
			}
			
			if (readTag.hasTag("LongestList")) {
				List<Tag<?>> lister = (List<Tag<?>>) readTag.getTag("LongestList").get();
//				for (int i = 0; i < lister.size(); i++)
//					System.out.println(lister.get(i).get());
			}
			reader.close();
		}
		long rernd = System.currentTimeMillis();
		System.out.println("My Reader took " + (rernd-rtstart) + "ms to run.");
		
		long rstart = System.currentTimeMillis();
		for (int j = 0; j < timer; j++) {
			final Nbt NBT = new Nbt();
			dev.dewy.nbt.tags.collection.CompoundTag readTag = NBT.fromFile(new File("unbased.nbt"));
			
//			System.out.println(readTag.getName());
			
//			if(readTag.contains("Magic"))
//				System.out.println(readTag.get("Magic").getValue());
			
			if (readTag.contains("Background")) {
				dev.dewy.nbt.tags.collection.CompoundTag back = readTag.getCompound("Background");
//				if (back.contains("LongLegs"))
//					System.out.println(back.get("LongLegs").getValue());
			}
			
			if (readTag.contains("LongestList")) {
				dev.dewy.nbt.tags.collection.ListTag<dev.dewy.nbt.api.Tag> lister = readTag.getList("LongestList");
//				for (int i = 0; i < lister.size(); i++)
//					System.out.println(lister.get(i).getValue());
			}
		}
		long rend = System.currentTimeMillis();
		System.out.println("Their Reader took " + (rend-rstart) + "ms to run.");
		
	}
	
	

}
