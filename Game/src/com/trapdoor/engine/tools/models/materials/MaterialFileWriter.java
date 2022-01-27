package com.trapdoor.engine.tools.models.materials;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jan. 20, 2022
 * 
 */
public class MaterialFileWriter {
	
	private String file;
	private DataOutputStream data;
	
	public MaterialFileWriter(String file) {
		this.file = file;
		try {
			this.data = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file))));
		} catch (FileNotFoundException e) {
			Logging.logger.error("Material file not found! (No clue how this is possible! We are writing here!) [" + file + "]", e);
		} catch (IOException e) {
			Logging.logger.error("Unable to open material file! (Might be a permission issue) [" + file + "]", e);
		}
	}
	
	public void writeInt(int i) {
		try {
			data.writeChar('i');
			data.writeInt(i);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Integer)! [" + file + "]", e);
		}
	}
	
	public void writeFloat(float f) {
		try {
			data.writeChar('f');
			data.writeFloat(f);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Float)! [" + file + "]", e);
		}
	}
	
	public void writeDouble(double d) {
		try {
			data.writeChar('d');
			data.writeDouble(d);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Double)! [" + file + "]", e);
		}
	}
	
	public void writeLong(long l) {
		try {
			data.writeChar('l');
			data.writeLong(l);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Long)! [" + file + "]", e);
		}
	}
	
	public void writeByte(byte b) {
		try {
			data.writeChar('u');
			data.writeByte(b);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Byte)! [" + file + "]", e);
		}
	}
	
	public void writeShort(short s) {
		try {
			data.writeChar('s');
			data.writeShort(s);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Short)! [" + file + "]", e);
		}
	}
	
	public void writeBoolean(boolean b) {
		try {
			data.writeChar('b');
			data.writeBoolean(b);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Boolean)! [" + file + "]", e);
		}
	}
	
	public void writeChar(char c) {
		try {
			data.writeChar('c');
			data.writeChar(c);
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (Character)! [" + file + "]", e);
		}
	}
	
	public void writeString(String str) {
		if (str == null)
			str = "NULL";
		char[] chars = str.toCharArray();
		try {
			data.writeChar('s');
			for (int i = 0; i < chars.length; i++)
				data.writeChar(chars[i]);
			data.writeChar('\0');
		} catch (Exception e) {
			Logging.logger.error("Unable to write to material file (String)! [" + file + "]", e);
		}
	}
	
	public void close() {
		try {
			this.data.close();
		} catch (IOException e) {
			Logging.logger.error("Unable to close material file writer! [" + file + "]", e);
		}
	}
	
}
