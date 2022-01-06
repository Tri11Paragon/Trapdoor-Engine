package com.game.engine.tools.models;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
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
	
	// TODO: Maybe use the transform and have individual transforms per submesh?
	public static final int DEFAULT_FLAGS = Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_PreTransformVertices;
	
	public static Model load(String path, String texturesDir) {
		return load(path, texturesDir, DEFAULT_FLAGS);
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
		
		Material[] materials = MaterialFSFormater.loadMaterialsFromFile(path);
		if (materials == null) {
			int numMaterials = aiScene.mNumMaterials();
			PointerBuffer aiMaterials = aiScene.mMaterials();
			materials = new Material[numMaterials];
			for (int i = 0; i < numMaterials; i++)
				materials[i] = processMaterial(AIMaterial.create(aiMaterials.get(i)), texturesDir);
		}
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for (int i = 0; i < numMeshes; i++)
			meshes[i] = processMesh(AIMesh.create(aiMeshes.get(i)), materials);
		
		return new Model(meshes, materials, aiScene, path);
	}
	
	private static Material processMaterial(AIMaterial material, String texturesDir) {
		if (texturesDir.endsWith("/"))
			texturesDir = texturesDir.substring(0, texturesDir.length()-1);
		
		AIColor4D colour = AIColor4D.create();
		
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String materialTexturePath = path.dataString();
		
		if (materialTexturePath != null && materialTexturePath.length() > 0) {
			if (materialTexturePath.contains("resources/textures")) {
				String[] tmp = materialTexturePath.split("resources/textures");
				materialTexturePath = tmp[1];
			}
			
			String texturePath = (texturesDir + "/" + materialTexturePath).replace("//", "/");
			GameRegistry.registerTexture(texturePath);
			
			Vector4f ambient = Material.DEFAULT_COLOUR;
		    int result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
		    }

		    Vector4f diffuse = Material.DEFAULT_COLOUR;
		    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
		    }

		    Vector4f specular = Material.DEFAULT_COLOUR;
		    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
		    }

			
			AIString normalPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_NORMALS, 0, normalPath, (IntBuffer) null, null, null, null, null, null);
			String normalTexture = normalPath.dataString();
			String normalTexturePath = (texturesDir + "/" + normalTexture).replace("//", "/");
			
			if (normalTexture != null && normalTexture.length() > 0) {
				GameRegistry.registerTexture(normalTexturePath);
				System.out.println(normalTexture);
			} else
				normalTexturePath = GameRegistry.DEFAULT_EMPTY_NORMAL_MAP;
			
			return GameRegistry.registerMaterial2(texturePath, normalTexturePath, new Vector3f(average(diffuse), average(specular), average(ambient)));
		}
		// can't load texture, meaning we need to load error material
		return GameRegistry.getErrorMaterial();
	}
	
	private static float average(Vector4f vec) {
		return (vec.x + vec.y + vec.z + vec.w) / 4f;
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
