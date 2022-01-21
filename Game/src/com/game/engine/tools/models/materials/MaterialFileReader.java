package com.game.engine.tools.models.materials;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.game.engine.tools.Logging;

/**
 * @author brett
 * @date Jan. 20, 2022
 * 
 */
public class MaterialFileReader {

	private String file;
	private DataInputStream data;
	
	public MaterialFileReader(String file) {
		this.file = file;
		try {
			this.data = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))));
		} catch (FileNotFoundException e) {
			Logging.logger.error("Material file not found! [" + file + "]", e);
		} catch (IOException e) {
			Logging.logger.error("Unable to open material file! [" + file + "]", e);
		}
	}
	
	public String readString() {
		StringBuilder builder = new StringBuilder();
		try {
			if (data.readChar() != 's')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading String! (" + file + ")");
			
			char c = ' ';
			while ((c = data.readChar()) != '\0')
				builder.append(c);
			
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (String)! [" + file + "]", e);
		}
		String str = builder.toString();
		
		if (str.contains("NULL") || str.contentEquals("NULL"))
			str = null;
		
		return str;
	}
	
	public int readInt() {
		try {
			if (data.readChar() != 'i')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading int! (" + file + ")");
			return data.readInt();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Integer)! [" + file + "]", e);
		}
		return 0;
	}
	
	public float readFloat() {
		try {
			if (data.readChar() != 'f')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading float! (" + file + ")");
			return data.readFloat();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Float)! [" + file + "]" , e);
		}
		return 0;
	}
	
	public double readDouble() {
		try {
			if (data.readChar() != 'd')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading double! (" + file + ")");
			return data.readDouble();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Double)! [" + file + "]", e);
		}
		return 0;
	}
	
	public long readLong() {
		try {
			if (data.readChar() != 'l')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading long! (" + file + ")");
			return data.readLong();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Long)! [" + file + "]", e);
		}
		return 0;
	}
	
	public byte readByte() {
		try {
			if (data.readChar() != 'u')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading byte! (" + file + ")");
			return data.readByte();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Byte)! [" + file + "]", e);
		}
		return 0;
	}
	
	public short readShort() {
		try {
			if (data.readChar() != 's')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading short! (" + file + ")");
			return data.readShort();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Short)! [" + file + "]", e);
		}
		return 0;
	}
	
	public boolean readBoolean() {
		try {
			if (data.readChar() != 'b')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading boolean! (" + file + ")");
			return data.readBoolean();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Boolean)! [" + file + "]", e);
		}
		return false;
	}
	
	public char readChar() {
		try {
			if (data.readChar() != 'c')
				throw new MaterialFSFormater.InvalidMaterialFileException("Something broke the material file! Missing data char while reading char! (" + file + ")");
			return data.readChar();
		} catch (IOException e) {
			Logging.logger.error("Unable to read material file (Character)! [" + file + "]", e);
		}
		return 0;
	}
	
	public void close() {
		try {
			data.close();
		} catch (IOException e) {
			Logging.logger.error("Unable to close material file reader! [" + file + "]", e);
		}
	}
	
}
