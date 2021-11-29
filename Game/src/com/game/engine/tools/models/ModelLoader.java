package com.game.engine.tools.models;

import java.nio.IntBuffer;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

import com.game.engine.TextureLoader;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Mesh;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.datatypes.ogl.assimp.ModelNotFoundException;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class ModelLoader {
	
	public static Model load(String path, String texturesDir) {
		return load(path, texturesDir, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
	}
	
	/**
	 * Warning: this function must be called after the loader is created.
	 * @param path
	 * @param texturesDir
	 * @param flags
	 * @return
	 */
	public static Model load(String path, String texturesDir, int flags) {
		AIScene aiScene = Assimp.aiImportFile(path, flags);
		if (aiScene == null)
			throw new ModelNotFoundException(path);
		
		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer aiMaterials = aiScene.mMaterials();
		Material[] materials = new Material[numMaterials];
		for (int i = 0; i < numMaterials; i++)
			materials[i] = processMaterial(AIMaterial.create(aiMaterials.get(i)), texturesDir);
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++)
			meshes[i] = processMesh(AIMesh.create(aiMeshes.get(i)), materials);
		
		return new Model();
	}
	
	private static Material processMaterial(AIMaterial material, String texturesDir) {
		AIColor4D color = AIColor4D.create();
		
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String texturePath = path.dataString();
		Texture t = TextureLoader.loadTexture(texturesDir + "/" + texturePath);
		
		
		
		Vector4f ambient = Material.DEFAULT_AMBIENT;
		int result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color);
		if (result == 0)
	    	ambient = new Vector4f(color.r(), color.g(), color.b(), color.a());
	    
	    Vector4f diffuse = Material.DEFAULT_DIFFUSE;
	    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color);
	    if (result == 0)
	        diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
	    
	    Vector4f specular = Material.DEFAULT_SPECULAR;
	    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color);
	    if (result == 0)
	    	specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
		
		return new Material(t, ambient, diffuse, specular);
	}
	
	private static Mesh processMesh(AIMesh mesh, Material[] materials) {
		
		return null;
	}
	
}
