package com.game.engine.tools.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;

import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.threading.GameRegistry;

/**
 * @author brett
 * @date Jan. 5, 2022
 * due to the fact that the various model formats supported by the engine (Assimp)
 * don't all contain the same specifications for materials, I have created a material format
 * which is what gets written when you save a model's edited material. The .mtl.gz format is a
 * very simple, compressed plain text format which allows for the saving of the following engine used information: <br>
 * <ul>
 * 	<li>Diffuse Texture</li>
 * 	<li>Normal Texture</li>
 *  <li>Specular Map (planned)</li>
 *  <li>Bump Map (planned)</li>
 *  <li>Ambient Occlusion Map (planned)</li>
 *  <li>Base Specular value</li>
 *  <li>Base Diffuse value</li>
 *  <li>Base Ambient value (lighting)</li>
 * </ul>
 */
public class MaterialFSFormater {
	
	public static void saveMaterialsToFile(Model model) {
		try {
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(model.getPath() + ".mtl.gz")));
			for (int i = 0; i < model.getMaterials().length; i++) {
				Material m = model.getMaterials()[i];
				writeString(dos, m.getDiffuseTexturePath());
				writeString(dos, m.getNormalTexturePath());
				writeString(dos, m.getDisplacementTexturePath());
				writeString(dos, m.getAmbientOcclusionTexturePath());
				writeString(dos, m.getSpecularTexturePath());
				dos.writeChar('f');
				dos.writeFloat(m.getColorInformation().x);
				dos.writeChar('f');
				dos.writeFloat(m.getColorInformation().y);
				dos.writeChar('f');
				dos.writeFloat(m.getColorInformation().z);
				dos.writeChar('e');
			}
			dos.writeChar('q');
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeString(DataOutputStream dos, String str) throws IOException {
		char[] chars = str.toCharArray();
		dos.writeChar('s');
		for (int i = 0; i < chars.length; i++)
			dos.writeChar(chars[i]);
		dos.writeChar('\0');
	}
	
	private static String readString(DataInputStream dis) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		char c = ' ';
		while ((c = dis.readChar()) != '\0')
			builder.append(c);
		
		return builder.toString();
	}
	
	/**
	 * this function will find and load the corresponding materials from the associated .mtl.gz file.
	 * If none is found or a file error occurs null will be returned.
	 * @param modelPath path to the model WITHOUT the .mtl.gz extension
	 * @return array of loaded materials, or null.
	 */
	public static Material[] loadMaterialsFromFile(String modelPath) {
		if (modelPath.contains(".mtl.gz"))
			throw new RuntimeException("Invalid input. The .mtl.gz must not be included!");
		if (!new File(modelPath + ".mtl.gz").exists())
			return null;
		try {
			DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(modelPath + ".mtl.gz")));
			ArrayList<Material> m = new ArrayList<Material>();
			
			while (true) {
				char c = dis.readChar();
				if (c == 'q')
					break;
				if (c != 's')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (1) \n" + modelPath);
				String diffuseTexture = readString(dis);
				if (dis.readChar() != 's')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (2) \n" + modelPath);
				String normalTexture = readString(dis);
				if (dis.readChar() != 's')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (2) \n" + modelPath);
				String displacementTexture = readString(dis);
				if (dis.readChar() != 's')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (2) \n" + modelPath);
				String AOTexture = readString(dis);
				if (dis.readChar() != 's')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (2) \n" + modelPath);
				String SpecTexture = readString(dis);
				if (dis.readChar() != 'f')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (3) \n" + modelPath);
				float diffuse = dis.readFloat();
				if (dis.readChar() != 'f')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (4) \n" + modelPath);
				float specular = dis.readFloat();
				if (dis.readChar() != 'f')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (5) \n" + modelPath);
				float ambient = dis.readFloat();
				if ((c = dis.readChar()) != 'e')
					throw new InvalidMaterialFileException("Something broke the material file! Missing data char! (6) " + c + " \n" + modelPath);
				
				if (!diffuseTexture.contains("NORENDER"))
					GameRegistry.registerTexture(diffuseTexture);
				GameRegistry.registerTexture(normalTexture);
				m.add(GameRegistry.registerMaterial2(diffuseTexture, normalTexture, displacementTexture, AOTexture, SpecTexture, new Vector3f(diffuse, specular, ambient)));
			}
			
			Material[] mats = new Material[m.size()];
			for (int i = 0; i < mats.length; i++)
				mats[i] = m.get(i);
			
			if (mats.length <= 0)
				throw new InvalidMaterialFileException("Something resulting in an error during material loading!");
			
			return mats;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static class InvalidMaterialFileException extends RuntimeException {

		private static final long serialVersionUID = 8237111019455353558L;
		
		public InvalidMaterialFileException(String e) {
			super(e);
		}
		
	}
	
}
