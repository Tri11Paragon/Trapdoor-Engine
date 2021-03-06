package com.trapdoor.engine;

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
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;

import com.trapdoor.engine.datatypes.ogl.Texture;
import com.trapdoor.engine.datatypes.ogl.TextureData;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TextureLoader {
	
	public static float TEXTURE_LOD = -0.2f;
	public static final int MINMAG_FILTER = GL11.GL_NEAREST;
	public static int TEXTURE_SCALE = 1;
	
	public static Texture nullTexture = new Texture(0, 32, 32, 4);
	
	// map of loaded textures.
	public static Map<String, Texture> textureMap = new HashMap<String, Texture>();
	
	private static List<Integer> textures = new ArrayList<Integer>();
	private static List<ByteBuffer> textureBuffers = new ArrayList<ByteBuffer>();
	
	public static int getTextures() {
		return textures.size();
	}

	/**
	 * does all the texture atlas stuff.
	 * @param path path to the texture atlas root folder
	 */
	public static void init(String path) {
		nullTexture = new Texture(0, 32, 32, 4);
	}
	
	/**
	 * loads a texture with default settings.
	 */
	public static Texture loadTexture(String filename) {
		return loadTexture(filename, 0, 0);
	}
	
	/**
	 * loads a texture to a specified width and height
	 */
	public static Texture loadTexture(String filename, int width, int height) {
		return loadTexture(filename, width, height, TEXTURE_LOD, MINMAG_FILTER);
	}
	
	/**
	 * Loads a texture with specified LOD bias, min/mag filtering and mipmap filtering.
	 */
	public static Texture loadTexture(String texture, int width, int height, float bias, int minmag_filter) {
		// return the texture if its already been loaded.
		if (textureMap.containsKey(texture))
			return textureMap.get(texture);
		// don't load if we don't have a window with OpenGL (we are the server)
		if (GL.getCapabilities() == null)
			return nullTexture;
		return loadTextureI(texture, decodeTextureToSize("resources/textures/" + texture, true, false, width, height), bias, minmag_filter);
	}
	
	public static Texture loadTexture(int width, int height, String texture1, String texture2, String texture3, String texture4) {
		return loadTexture(width, height, texture1, texture2, texture3, texture4, TEXTURE_LOD, MINMAG_FILTER);
	}
	
	public static Texture loadTexture(int width, int height, String texture1, String texture2, String texture3, String texture4, float bias, int minmag_filter) {
		// don't load if we don't have a window with OpenGL (we are the server)
		if (GL.getCapabilities() == null)
			return nullTexture;
		return loadTextureI(null, decodeTextureFile(width,height,
				"resources/textures/" + texture1, 
				"resources/textures/" + texture2, 
				"resources/textures/" + texture3, 
				"resources/textures/" + texture4), 
				bias, minmag_filter);
	}
	
	/**
	 * Textures must be in order:
	 * Right
	 * Left
	 * Top
	 * Bottom
	 * Back
	 * Front
	 * @param textures
	 * @return
	 */
	public static Texture loadCubeMap(TextureData[] textures) {
		int id = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
		
		Texture t = new Texture(id, textures[0].getWidth(), textures[0].getHeight(), textures[0].getChannels());
		for (int i = 0; i < textures.length; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, textures[i].getWidth(), textures[i].getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textures[i].getBuffer());
		}
		assignTextureModes(GL11.GL_LINEAR, GL11.GL_REPEAT);
		GL33.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
		TextureLoader.textures.add(id);
		return t;
	}
	
	public static Texture loadTextureI(String texture, TextureData d, float bias, int minmag_filter) {
		try {
			// generate a new texture buffer
			int id = GL11.glGenTextures();
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			assignTextureModes(minmag_filter, GL11.GL_REPEAT);

			// this bias is how fast a texture loses detail (LOD = level of detail)
			// > 0 = less detail
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);
			// applies anisotropic filtering and makes sure that the graphics card supports
			// this level of AF
			float amount = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
	        
	        // put the texture data into the texture buffer.
			// TODO: fix non alpha channel loading
			int GL_RGBI = SettingsLoader.GAMMA != 0.0 ? GL33.GL_SRGB_ALPHA : GL33.GL_RGBA;
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBI, d.getWidth(), d.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, d.getBuffer());
	        
	        // generates the mipmaps
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			
			Texture t = new Texture(id, d.getWidth(), d.getHeight(), d.getChannels());
			if (texture != null) {
				// add this texture to the map
				textureMap.put(texture, t);
			}
			// add to the textures buffer list (for deletion when the game closes)
			textures.add(id);
			// return the buffer.
			return t;
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
			Logging.logger.fatal("Unable load " + texture + " to GPU!");
			System.exit(-1);
			return nullTexture;
		}
	}
	
	public static int loadMaterialTextureArray(List<TextureData> textures, Map<String, Integer> map) {
		try {
			float anisf = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			int id = GL11.glGenTextures();
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, id); 
			// allocate texture memory
			int GL_RGBI = SettingsLoader.GAMMA != 0.0 ? GL33.GL_SRGB8_ALPHA8 : GL33.GL_RGBA8;
	        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 4, GL_RGBI, SettingsLoader.TEXTURE_SIZE, SettingsLoader.TEXTURE_SIZE, textures.size());
	        
	        for (int i = 0; i < textures.size(); i++) {
	        	TextureData data = textures.get(i);
	        	GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY,
	        			// level
	        			0, 
	        			// x,y,z offsets using the texture # as the position in the array
	        			0, 0, i,
	        			// width, height depth
	        			SettingsLoader.TEXTURE_SIZE, SettingsLoader.TEXTURE_SIZE, 1, 
	        			// format, format
	        			GL33.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
	        			// decode the image texture
	        			data.getBuffer());
	        	// AF
	        	GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisf);
	        	map.put(textures.get(i).getName(), i);
	        }
	        
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	        
	        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
			// > 0 = less detail
			GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, 0.2f);
			
			// add texture for later deletion.
			TextureLoader.textures.add(id);
			return id;
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
		}
		return 0;
	}
	
	public static int loadSpecialTextureATLAS(int width, int height, List<TextureData> textures, Map<String, Integer> map) {
		try {
			//for more detail on array textures
			//https://www.khronos.org/opengl/wiki/Array_Texture
			float anisf = Math.min(SettingsLoader.AF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			// generate a texture id like normal
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer, this time to texture array.
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, id); 
			
			
	        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 4, GL11.GL_RGBA8, width, height, textures.size());
	        
	        // loop through all textures.
	        for (int i = 0; i < textures.size(); i++) {
	        	TextureData data = textures.get(i);
	        	// i don't understand why this is in gl12 but to allocate this is in gl42
	        	GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY,
	        			// level
	        			0, 
	        			// x,y,z offsets using the texture # as the position in the array
	        			0, 0, i,
	        			// width, height depth
	        			width, height, 1, 
	        			// format, format
	        			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
	        			// decode the image texture
	        			data.getBuffer());
	        	// AF
	        	GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisf);
	        	map.put(textures.get(i).getName(), i);
	        }
	        
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	        
	        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
			// > 0 = less detail
			GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, 0.2f);
	        
			// add texture for later deletion.
			TextureLoader.textures.add(id);
			return id;
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
		}
		return 0;
	}
	
	private static int removeNegative(byte b) {
		if (b >= 0)
			return b;
		return b + 256;
	}
	
	// can fit 4 files into one texture.
	public static TextureData decodeTextureFile(int width, int height, String file1, String file2, String file3, String file4) {
		TextureData t1 = null;
		if (width <= 0 || height <= 0)
			t1 = decodeTextureToSize(file1, false, true, 0, 0);
		else
			t1 = decodeTextureToSize(file1, true, true, width, height);
		TextureData t2 = decodeTextureToSize(file2, true, true, t1.getWidth(), t1.getHeight());
		TextureData t3 = decodeTextureToSize(file3, true, true, t1.getWidth(), t1.getHeight());
		TextureData t4 = decodeTextureToSize(file4, true, true, t1.getWidth(), t1.getHeight());
		
		int[] t1bytes = new int[t1.getBuffer().capacity()];
		int[] t2bytes = new int[t2.getBuffer().capacity()];
		int[] t3bytes = new int[t3.getBuffer().capacity()];
		int[] t4bytes = new int[t4.getBuffer().capacity()];
		
		for (int i = 0; i < t1bytes.length; i++)
			t1bytes[i] = removeNegative(t1.getBuffer().get());
		for (int i = 0; i < t2bytes.length; i++)
			t2bytes[i] = removeNegative(t2.getBuffer().get());
		for (int i = 0; i < t3bytes.length; i++)
			t3bytes[i] = removeNegative(t3.getBuffer().get());
		for (int i = 0; i < t4bytes.length; i++)
			t4bytes[i] = removeNegative(t4.getBuffer().get());
		
		ByteBuffer newBuff = BufferUtils.createByteBuffer(t1.getBuffer().capacity());
		
		for (int i = 0; i < t1bytes.length; i+=4) {
			int t1r = t1bytes[i];
			int t1g = t1bytes[i+1];
			int t1b = t1bytes[i+2];
			
			int t1avg = (t1r + t1g + t1b)/3;
			
			int t2r = t2bytes[i];
			int t2g = t2bytes[i+1];
			int t2b = t2bytes[i+2];
			
			int t2avg = (t2r + t2g + t2b)/3;
			
			int t3r = t3bytes[i];
			int t3g = t3bytes[i+1];
			int t3b = t3bytes[i+2];
			
			int t3avg = (t3r + t3g + t3b)/3;
			
			int t4r = t4bytes[i];
			int t4g = t4bytes[i+1];
			int t4b = t4bytes[i+2];
			
			int t4avg = (t4r + t4g + t4b)/3;
			
			newBuff.put((byte)t1avg);
			newBuff.put((byte)t2avg);
			newBuff.put((byte)t3avg);
			newBuff.put((byte)t4avg);
		}
		
		newBuff.flip();
		
		return new TextureData(newBuff, t1.getWidth(), t1.getHeight(), 4, file1 + file2 + file3 + file4);
	}
	
	public static TextureData decodeTextureToMaterialArray(String fileName, boolean flip) {
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
			
			buffer = STBImage.stbi_load(fileName, w, h, ch, 4);
			
			// assigns the width and height of the texture data
			wd = w[0];
			hd = h[0];
			channels = ch[0];
			// loaded in to 4
			channels = 4;
			
			int alpha;
			if (channels == 4) 
				alpha = channels-1;
			else 
				alpha = STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
			// not sure why *4 is needed, but without it the JVM crashes.
			ByteBuffer newImage = BufferUtils.createByteBuffer(SettingsLoader.TEXTURE_SIZE * SettingsLoader.TEXTURE_SIZE * channels * 4);
			STBImageResize.stbir_resize(buffer, wd, hd, wd * channels, 
					newImage, SettingsLoader.TEXTURE_SIZE, SettingsLoader.TEXTURE_SIZE, SettingsLoader.TEXTURE_SIZE * channels, 
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
			Logging.logger.fatal(e.getMessage(), e);
			Logging.logger.fatal("Tried to load material texture " + fileName + ", didn't work!");
			System.exit(-1);
		}
		// return the texture data.
		return new TextureData(buffer, SettingsLoader.TEXTURE_SIZE, SettingsLoader.TEXTURE_SIZE, channels, fileName);
	}
	
	public static TextureData decodeTextureToSize(String fileName, boolean flip, boolean scale, int width, int height) {
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
			
			buffer = STBImage.stbi_load(fileName, w, h, ch, 4);
			
			// assigns the width and height of the texture data
			wd = w[0];
			hd = h[0];
			channels = ch[0];
			// loaded in to 4
			channels = 4;
			
			if ((wd == width && hd == height) || (width <= 0 && height <= 0)) {
				if (buffer.position() != 0 && flip)
					buffer.flip();
				if (!scale)
					return new TextureData(buffer, wd, hd, channels, fileName);
				else {
					STBImage.stbi_image_free(buffer);
					return decodeTextureToSize(fileName, flip, false, wd/TEXTURE_SCALE, hd/TEXTURE_SCALE);
				}
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
			Logging.logger.fatal(e.getMessage(), e);
			Logging.logger.fatal("Tried to load texture " + fileName + ", didn't work!");
			System.exit(-1);
		}
		// return the texture data.
		return new TextureData(buffer, width, height, channels, fileName);
	}
	
	public static void loadToArray(String loc) {
		// image data storage.
		ByteBuffer buffer = null;
		try {
			// decoder for the image files
			int[] w = new int[1];
			int[] h = new int[1];
			int[] ch = new int[1];

			buffer = STBImage.stbi_load(loc, w, h, ch, 4);
			
			System.out.println("byte[] errorBytes = {");
			for (int i = 0; i < buffer.capacity(); i++) {
				System.out.print(buffer.get());
				if (i != buffer.capacity()-1)
					System.out.print(",");
				if (i % 32 == 31)
					System.out.println();
			}
			System.out.println("};");
			System.out.println(w[0]);
			System.out.println(h[0]);
			
		} catch (Exception e) {
			// we had issue loading texture. exit the game.
			Logging.logger.fatal(e.getMessage(), e);
			Logging.logger.fatal("Tried to load texture " + loc + ", didn't work!");
			System.exit(-1);
		}

	}
	
	public static void cleanup() {
		for(int texture:textures)
			GL11.glDeleteTextures(texture);
		for (ByteBuffer buff : textureBuffers)
			STBImage.stbi_image_free(buff);
	}
	
	public static void print() {
		Logging.logger.info("Textures Size: " + textures.size());
		Logging.logger.info("Texture Map Size: " + textureMap.size());
	}
	
	private static void assignTextureModes(int mingmag, int textureWrap) {
		// Min and Mag filter is for when a texture is upscaled or downscaled.
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mingmag);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mingmag);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrap);
	}
	
}
