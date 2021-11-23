package com.game.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;

import com.game.Main;
import com.game.engine.datatypes.Atlas;
import com.game.engine.datatypes.ogl.TextureData;
import com.game.engine.tools.Logger;
import com.game.engine.tools.SettingsLoader;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TextureLoader {
	
	// map of loaded textures.
	public static Map<String, Integer> textureMap = new HashMap<String, Integer>();
	
	private static List<Integer> textures = new ArrayList<Integer>();
	private static List<ByteBuffer> textureBuffers = new ArrayList<ByteBuffer>();
	
	// contains TextureName -> array pos
	public static HashMap<String, Integer> atlasMap = new HashMap<String, Integer>();
	// contains TextureName -> atlas id
	public static HashMap<String, Integer> atlasTextureMap = new HashMap<String, Integer>();
	public static HashMap<Integer, ArrayList<String>> textureNames = new HashMap<Integer, ArrayList<String>>();

	/**
	 * does all the texture atlas stuff.
	 * @param path path to the texture atlas root folder
	 */
	public static void init(String path) {
		Logger.writeln("Loading texture atlas(es):");
		
		File root = new File(path);
		ArrayList<File> subdirs = new ArrayList<File>();
		
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f == null)
				continue;
			if (f.isDirectory())
				subdirs.add(f);
		}
		
		ArrayList<Atlas> atlases = new ArrayList<Atlas>();
		
		for (File d : subdirs) {
			Logger.writeln("Loading " + d.getName());
			File[] contents = d.listFiles();
			ArrayList<File> textures = new ArrayList<File>();
			int width = 0;
			int height = 0;
			for (File f : contents) {
				if (f.getName().contains(".properties")) {
					try {
						BufferedReader reader = new BufferedReader(new FileReader(f));
						width = Integer.parseInt(reader.readLine());
						try {
							height = Integer.parseInt(reader.readLine());
						} catch (Exception e) {
							height = width;
						}
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					textures.add(f);
					if (Main.verbose)
						Logger.writeln("Loading texture " + f.getName());
				}
			}
			if (width == 0 || height == 0) {
				if (d.getName().contains("SIZE_")) {
					String[] size = d.getName().split("_");
					width = height = Integer.parseInt(size[1]);
				} else {
					width = height = 128;
					Logger.writeErrorln("UNABLE TO FIND ATLAS SIZE. ASSUMING 128x128. \n "
							+ "If you are seeing this, please include the size in the folder title \n "
							+ "or include a atlas.properties with the width and height on separate lines.");
				}
			}
			Logger.printBoxY(new String[] {"Loaded atlas appears to be " + width + " width and " + height + " height"});
			atlases.add(new Atlas(textures, width, height));
		}
		
		for (Atlas e : atlases) {
			loadSpecialTextureATLAS(e);
		}
		
	}
	
	/**
	 * gets the corresponding texture ID of the atlas to the texture name supplied (must contain the .png/.jpg etc)
	 * @param filename name of the file
	 * @return the id of the texture atlas containing the file
	 */
	public static int getTextureAtlas(String filename) {
		Integer i = atlasTextureMap.get(filename);
		if (i == null) {
			Logger.writeErrorln("Unable to find texture atlas.");
			return -1;
		}
		return i;
	}
	
	/**
	 * gets the internal ID (z) of the texture inside the atlas
	 * @param filename name of the file
	 * @return the index of the texture contained inside its corresponding atlas
	 */
	public static int getTextureAtlasID(String filename) {
		Integer i = atlasMap.get(filename);
		if (i == null) {
			Logger.writeErrorln("Unable to find texture inside atlas.");
			return -1;
		}
		return i;
	}
	
	/**
	 * loads a texture with default settings.
	 */
	public static int loadTexture(String filename) {
		try {
			// load texture with default settings.
			return loadTexture(filename, -0.2f);
		} catch (RuntimeException e) {}
		return 0;
	}
	
	/**
	 * loads textures with a specified LOD bias.
	 */
	public static int loadTexture(String filename, float bias) {
		// this used to be a different function but I have changed it to use the special texture loader
		// as it appears to be able to handle non 2^x sized images and its just more clean.
		return loadTexture(filename, bias, GL11.GL_NEAREST, GL11.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	public static int loadTexture(String filename, int width, int height) {
		// this used to be a different function but I have changed it to use the special texture loader
		// as it appears to be able to handle non 2^x sized images and its just more clean.
		return loadTexture(filename, width, height, -0.2f, GL11.GL_NEAREST, GL11.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	/**
	 * Loads a texture with specified LOD bias, min/mag filtering and mipmap filtering.
	 */
	public static int loadTexture(String texture, float bias, int minmag_filter, int minmag_mipmap) {
		// return the texture if its already been loaded.
		if (textureMap.containsKey(texture))
			return textureMap.get(texture);
		try {
			// don't load if we don't have a window with OpenGL (we are the server)
			if (GL.getCapabilities() == null)
				return 0;
			// decode some texture data.
			TextureData d = decodeTextureFile("resources/textures/" + texture);
			// generate a new texture buffer
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_filter); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_filter); 
			
	        // put the texture data into the texture buffer.
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, d.getWidth(), d.getHeight(), 0, d.getChannels() == 4 ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, d.getBuffer());
	        
	        // generates the mipmaps
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			// im pretty sure i only need to call this ^ but i'd like to make sure that
			// the mipmaps use the same filters.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_mipmap); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_mipmap); 
	        // this bias is how fast a texture loses detail (LOD = level of detail)
			// > 0 = less detail
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);
			// applies anisotropic filtering and makes sure that the graphics card supports this level of AF
			float amount = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			
			// add this texture to the map
			textureMap.put(texture, id);
			// add to the textures buffer list (for deletion when the game closes)
			textures.add(id);
			// return the buffer.
			return id;
		} catch (Exception e) {return 0;}
	}
	
	public static int loadTexture(String texture, int width, int height, float bias, int minmag_filter, int minmag_mipmap) {
		// return the texture if its already been loaded.
		if (textureMap.containsKey(texture))
			return textureMap.get(texture);
		try {
			// don't load if we don't have a window with OpenGL (we are the server)
			if (GL.getCapabilities() == null)
				return 0;
			// decode some texture data.
			TextureData d = decodeTextureToSize("resources/textures/" + texture, width, height);
			// generate a new texture buffer
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_filter); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_filter); 
			
	        // put the texture data into the texture buffer.
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, d.getWidth(), d.getHeight(), 0, d.getChannels() == 4 ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, d.getBuffer());
	        
	        // generates the mipmaps
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			// im pretty sure i only need to call this ^ but i'd like to make sure that
			// the mipmaps use the same filters.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_mipmap); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_mipmap); 
	        // this bias is how fast a texture loses detail (LOD = level of detail)
			// > 0 = less detail
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);
			// applies anisotropic filtering and makes sure that the graphics card supports this level of AF
			float amount = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			
			// add this texture to the map
			textureMap.put(texture, id);
			// add to the textures buffer list (for deletion when the game closes)
			textures.add(id);
			// return the buffer.
			return id;
		} catch (Exception e) {return 0;}
	}
	
	private static int loadSpecialTextureATLAS(Atlas atlas) {
		try {
			ArrayList<String> names = new ArrayList<String>();
			//for more detail on array textures
			//https://www.khronos.org/opengl/wiki/Array_Texture
			float anisf = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			// map of textures that need to be put into the texture array.
			// they should be in order from 0 to #
			ArrayList<File> textures = atlas.textures;
			// generate a texture id like normal
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer, this time to texture array.
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, id); 
			
	        // WHY THE FUCK IS THIS IN GL42
	        // i feel like this should be in gl30
			// but it lets me define a context of GL33 without any issues with this function
			// WHAT THE FUCK
			// (GL30/GL33 doesn't contain glTexStorage3D)
	        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 4, GL11.GL_RGBA8, atlas.width, atlas.height, textures.size());
	        
	        // loop through all textures.
	        for (int i = 0; i < textures.size(); i++) {
	        	TextureData data = decodeTextureToSize(textures.get(i).getPath(), atlas.width, atlas.height);
	        	// i don't understand why this is in gl12 but to allocate this is in gl42
	        	GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY,
	        			// level
	        			0, 
	        			// x,y,z offsets using the texture # as the position in the array
	        			0, 0, i,
	        			// width, height depth
	        			atlas.width, atlas.height, 1, 
	        			// format, format
	        			data.getChannels() == 4 ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 
	        			// decode the image texture
	        			data.getBuffer());
	        	// AF
	        	GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisf);
	        	atlasMap.put(textures.get(i).getName(), i);
	        	// memory? we got lots of that! right?
	        	atlasTextureMap.put(textures.get(i).getName(), id);
	        	names.add(textures.get(i).getName());
	        }
	        
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	        
	        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			// > 0 = less detail
			GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, 0.2f);
	        
			// add texture for later deletion.
			TextureLoader.textures.add(id);
			textureNames.put(id, names);
			return id;
		} catch (Exception e) {}
		return 0;
	}
	
	/**
	 * Decodes texture data from a file
	 */
	private static TextureData decodeTextureFile(String fileName) {
		// image data storage.
		int width = 0;
		int height = 0;
		int channels = 0;
		ByteBuffer buffer = null;
		try {
			// decoder for the PNG file
			int[] w = new int[1];
			int[] h = new int[1];
			int[] ch = new int[1];
			
			buffer = STBImage.stbi_load(fileName, w, h, ch, 0);
			
			// assigns the width and height of the texture data
			width = w[0];
			height = h[0];
			channels = ch[0];
			
			//if (buffer.position() != 0)
			buffer.flip();
		} catch (Exception e) {
			// we had issue loading texture. exit the game.
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		// return the texture data.
		return new TextureData(buffer, width, height, channels);
	}
	
	private static TextureData decodeTextureToSize(String fileName, int width, int height) {
		// image data storage.
		int wd = 0;
		int hd = 0;
		int channels = 0;
		ByteBuffer buffer = null;
		try {
			// decoder for the image files
			int[] w = new int[1];
			int[] h = new int[1];
			int[] ch = new int[1];
			
			buffer = STBImage.stbi_load(fileName, w, h, ch, 0);
			
			// assigns the width and height of the texture data
			wd = w[0];
			hd = h[0];
			channels = ch[0];
			
			if (wd == width && hd == height) {
				if (buffer.position() != 0)
					buffer.flip();
				return new TextureData(buffer, width, height, channels);
			}
			
			int alpha;
			if (channels == 4) 
				alpha = channels-1;
			else 
				alpha = STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
			// not sure why *4 is needed, but without it the JVM crashes.
			ByteBuffer newImage = BufferUtils.createByteBuffer(width * height * channels * 4);
			STBImageResize.stbir_resize(buffer, wd, hd, wd * channels, 
					newImage, width, height, width * channels, 
					STBImageResize.STBIR_TYPE_UINT8,
					channels,
					alpha,
					0,
					STBImageResize.STBIR_EDGE_ZERO,
					STBImageResize.STBIR_EDGE_ZERO,
					STBImageResize.STBIR_FILTER_CUBICBSPLINE,
					STBImageResize.STBIR_FILTER_CUBICBSPLINE,
					STBImageResize.STBIR_COLORSPACE_SRGB
					);
			
			STBImage.stbi_image_free(buffer);
			buffer = newImage;
			if (buffer.position() != 0)
				buffer.flip();
		} catch (Exception e) {
			// we had issue loading texture. exit the game.
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		// return the texture data.
		return new TextureData(buffer, width, height, channels);
	}
	
	public static void cleanup() {
		for(int texture:textures)
			GL11.glDeleteTextures(texture);
		for (ByteBuffer buff : textureBuffers)
			STBImage.stbi_image_free(buff);
	}
	
	public static void print() {
		Logger.writeln("Textures Size: " + textures.size());
		Logger.writeln("Texture Map Size: " + textureMap.size());
	}
	
}
