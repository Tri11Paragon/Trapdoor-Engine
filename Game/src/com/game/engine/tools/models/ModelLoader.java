package com.game.engine.tools.models;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Mesh;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.datatypes.ogl.assimp.ModelNotFoundException;
import com.game.engine.threading.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class ModelLoader {
	
	public static Model load(String path, String texturesDir) {
		return load(path, texturesDir, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
	}
	
	public static Model load(String path) {
		return load(path, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
	}
	
	public static Model load(String path, Material material) {
		return load(path, material, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals);
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
		
		return new Model(meshes, materials);
	}
	
	public static Model load(String path, int flags) {
		AIScene aiScene = Assimp.aiImportFile(path, flags);
		if (aiScene == null)
			throw new ModelNotFoundException(path);
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++)
			meshes[i] = processMesh(AIMesh.create(aiMeshes.get(i)), null);
		
		return new Model(meshes, new Material[] {GameRegistry.getErrorMaterial()});
	}
	
	public static Model load(String path, Material material, int flags) {
		AIScene aiScene = Assimp.aiImportFile(path, flags);
		if (aiScene == null)
			throw new ModelNotFoundException(path);
		
		Material[] materials = new Material[1];
		materials[0] = material;
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++)
			meshes[i] = processMesh(AIMesh.create(aiMeshes.get(i)), materials);
		
		return new Model(meshes, materials);
	}
	
	private static Material processMaterial(AIMaterial material, String texturesDir) {
		// TODO: what happens when this is null?
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String texturePath = texturesDir + "/" + path.dataString();
		GameRegistry.registerTexture(texturePath);
		
		AIString normalPath = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_NORMALS, 0, normalPath, (IntBuffer) null, null, null, null, null, null);
		String normalTexturePath = texturesDir + "/" + normalPath.dataString();
		
		if (normalTexturePath != null && normalTexturePath.length() > 0) {
			GameRegistry.registerTexture(normalTexturePath);
		} else
			normalTexturePath = GameRegistry.DEFAULT_EMPTY_NORMAL_MAP;
		
		return GameRegistry.registerMaterial(texturePath, normalTexturePath);
	}
	
	private static Mesh processMesh(AIMesh mesh, Material[] materials) {
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Float> textures = new ArrayList<Float>();
		ArrayList<Float> normals = new ArrayList<Float>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
	    
		processVertices(mesh, vertices);
	    processNormals(mesh, normals);
	    processTextCoords(mesh, textures);
	    processIndices(mesh, indices);
	    
	    Material m = GameRegistry.getErrorMaterial();
	    if (materials != null) {
		    int materialIndex = mesh.mMaterialIndex();
		    if (materialIndex >= 0 && materialIndex < materials.length)
		    	m = materials[materialIndex];
		    else {
		    	if (materials.length == 1)
		    		m = materials[0];
		    	else
		    		m = GameRegistry.getErrorMaterial();
		    }
	    }
	    
		return new Mesh(m, toFloatArray(vertices), toFloatArray(textures), toFloatArray(normals), toIntArray(indices));
	}
	
	private static void processVertices(AIMesh mesh, ArrayList<Float> vertices) {
	    AIVector3D.Buffer verts = mesh.mVertices();
	    while (verts.remaining() > 0) {
	        AIVector3D aiVertex = verts.get();
	        vertices.add(aiVertex.x());
	        vertices.add(aiVertex.y());
	        vertices.add(aiVertex.z());
	    }
	}
	
	private static void processNormals(AIMesh mesh, ArrayList<Float> normals) {
	    AIVector3D.Buffer norms = mesh.mNormals();
	    while (norms.remaining() > 0) {
	        AIVector3D aiVertex = norms.get();
	        normals.add(aiVertex.x());
	        normals.add(aiVertex.y());
	        normals.add(aiVertex.z());
	    }
	}
	
	private static void processTextCoords(AIMesh mesh, ArrayList<Float> textures) {
        AIVector3D.Buffer textCoords = mesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }
	
	private static void processIndices(AIMesh mesh, ArrayList<Integer> indices) {
        int numFaces = mesh.mNumFaces();
        AIFace.Buffer aiFaces = mesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
	
	// TODO: not having to do this nonsense
	
	public static int[] toIntArray(ArrayList<Integer> list) {
		int[] arr = new int[list.size()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = list.get(i);
		return arr;
	}
	
	public static float[] toFloatArray(ArrayList<Float> list) {
		float[] arr = new float[list.size()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = list.get(i);
		return arr;
	}
	
}
