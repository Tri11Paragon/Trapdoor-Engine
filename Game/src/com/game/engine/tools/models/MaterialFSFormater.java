package com.game.engine.tools.models;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Model;

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
			BufferedWriter writer = new BufferedWriter(new FileWriter(model.getPath() + ".mtl.gz"));
			for (int i = 0; i < model.getMaterials().length; i++) {
				Material m = model.getMaterials()[i];
				writer.write("mtl.begin");
				writer.write("mtl.diffuse:" + m.getDiffuseTexturePath());
				writer.write("mtl.normal:" + m.getNormalTexturePath());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
