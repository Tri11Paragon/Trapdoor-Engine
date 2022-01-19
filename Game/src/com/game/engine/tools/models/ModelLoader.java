package com.game.engine.tools.models;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Mesh;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.datatypes.ogl.assimp.ModelNotFoundException;
import com.game.engine.threading.GameRegistry;
import com.game.engine.tools.Logging;

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
			if (!materialTexturePath.contains("NORENDER"))
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
				Logging.logger.error(normalTexture);
			} else
				normalTexturePath = findNormalMap(texturePath);
			
			AIString displacementPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DISPLACEMENT, 0, displacementPath, (IntBuffer) null, null, null, null, null, null);
			String displacementTexture = displacementPath.dataString();
			String displacementTexturePath = (texturesDir + "/" + displacementTexture).replace("//", "/");
			
			if (displacementTexture != null && displacementTexture.length() > 0) {
				GameRegistry.registerTexture(displacementTexturePath);
				Logging.logger.error(displacementTexturePath);
			} else
				displacementTexturePath = findDisplacementMap(texturePath);
			
			AIString AOPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DISPLACEMENT, 0, AOPath, (IntBuffer) null, null, null, null, null, null);
			String AOTexture = AOPath.dataString();
			String AOTexturePath = (texturesDir + "/" + AOTexture).replace("//", "/");
			
			if (AOTexture != null && AOTexture.length() > 0) {
				GameRegistry.registerTexture(AOTexturePath);
				Logging.logger.error(AOTexturePath);
			} else
				AOTexturePath = findAOMap(texturePath);
			
			AIString SpecPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DISPLACEMENT, 0, SpecPath, (IntBuffer) null, null, null, null, null, null);
			String SpecTexture = SpecPath.dataString();
			String SpecTexturePath = (texturesDir + "/" + SpecTexture).replace("//", "/");
			
			if (SpecTexture != null && SpecTexture.length() > 0) {
				GameRegistry.registerTexture(SpecTexturePath);
				Logging.logger.error(SpecTexturePath);
			} else
				SpecTexturePath = findSpecMap(texturePath);
			
			return GameRegistry.registerMaterial2(texturePath, normalTexturePath, displacementTexturePath, AOTexturePath, SpecTexturePath, new Vector3f(average(diffuse), average(specular), average(ambient)));
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
	    
	    // now construct the mesh collider information
	    int numberOfVertices = vertices.size();
	    int numberofTriangles = numberOfVertices / 3;
	    
	    final int triangleIndexStride = 3 * 4;
	    final int triangleVertexStride = 3 * 4;
	    
	    final ByteBuffer indexBuffer = BufferUtils.createByteBuffer(numberofTriangles * 3 * Integer.SIZE); // yes this is just number of vertices
	    final ByteBuffer vertexBuffer = BufferUtils.createByteBuffer(numberOfVertices * Float.SIZE);
	    
	    for (int i = 0; i < indices.size(); i++) {
	    	Integer ind = indices.get(i);
	    	indexBuffer.putInt(ind);
	    }
	    
	    for (int i = 0; i < vertices.size(); i++) {
	    	Float vert = vertices.get(i);
	    	vertexBuffer.putFloat(vert);
	    }
	    
	    IndexedMesh meshInfo = new IndexedMesh();
	    
	    meshInfo.numTriangles = numberofTriangles;
	    meshInfo.triangleIndexBase = indexBuffer;
	    meshInfo.triangleIndexStride = triangleIndexStride;
	    meshInfo.numVertices = numberOfVertices;
	    meshInfo.vertexBase = vertexBuffer;
	    meshInfo.vertexStride = triangleVertexStride;
	    
		return new Mesh(m, toFloatArray(vertices), toFloatArray(textures), toFloatArray(normals), toIntArray(indices), meshInfo);
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
	
	private static String findNormalMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_NORMAL_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		
		// TODO: add more?
		String newPath = checkPossibleLocations(dir, name, "diff", "nor", "normal", "normalmap", "normalMap", "norMap", "normap");
		if (newPath == null)
			newPath = checkPossibleLocations(dir, name, "diffuse", "nor", "normal", "normalmap", "normalMap", "norMap", "normap");
		if (newPath == null)
			return returnval;
		else {
			GameRegistry.registerTexture(newPath);
			return newPath;
		}
	}
	
	private static String findDisplacementMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_DISPLACEMENT_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		
		String newPath = checkPossibleLocations(dir, name, "diff", "disp", "displacement", "dispMap", "displacementMap", "dispmap", "displacementmap");
		if (newPath == null)
			newPath = checkPossibleLocations(dir, name, "diffuse", "disp", "displacement", "dispMap", "displacementMap", "dispmap", "displacementmap");
		if (newPath == null)
			return returnval;
		else {
			GameRegistry.registerTexture(newPath);
			return newPath;
		}
	}
	
	private static String findAOMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_AO_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		
		String newPath = checkPossibleLocations(dir, name, "diff", "AO", "ao", "ambientocclusion", "ambientOcclusion");
		if (newPath == null)
			newPath = checkPossibleLocations(dir, name, "diffuse", "AO", "ao", "ambientocclusion", "ambientOcclusion");
		if (newPath == null)
			return returnval;
		else {
			GameRegistry.registerTexture(newPath);
			return newPath;
		}
	}
	
	private static String findSpecMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_SPEC_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		
		String newPath = checkPossibleLocations(dir, name, "diff", "spec", "specular", "specMap", "specmap");
		if (newPath == null)
			newPath = checkPossibleLocations(dir, name, "diffuse", "spec", "specular", "specMap", "specmap");
		if (newPath == null)
			return returnval;
		else {
			GameRegistry.registerTexture(newPath);
			return newPath;
		}
	}
	
	private static String checkPossibleLocations(String dir, String name, String find, String... replace) {
		
		for (int i = 0; i < replace.length; i++) {
			String replaceName = name.replace(find, replace[i]);
			if (replaceName == name)
				continue;
			String fullPath = dir + "/" + replaceName;
			fullPath = fullPath.replace("//", "/");
			Logging.logger.trace("Trying to load: " + fullPath);
			if (new File(fullPath).exists())
				return fullPath;
		}
		
		Logging.logger.warn("Failed to load all possible resource locations! :(");
		
		return null;
	}
	
	private static String getName(String path) {
		return new File(path).getName();
	}
	
	private static String getParentDir(String path) {
		return new File(path).getParent();
	}
	
}
