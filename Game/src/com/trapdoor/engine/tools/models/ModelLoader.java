package com.trapdoor.engine.tools.models;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.trapdoor.engine.datatypes.IndexingFloatArrayList;
import com.trapdoor.engine.datatypes.IndexingIntArrayList;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.assimp.ModelNotFoundException;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.models.materials.MaterialFSFormater;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class ModelLoader {
	
	// TODO: Maybe use the transform and have individual transforms per submesh?
	public static final int DEFAULT_FLAGS = Assimp.aiProcess_JoinIdenticalVertices 
												| Assimp.aiProcess_CalcTangentSpace 
												| Assimp.aiProcess_Triangulate 
												| Assimp.aiProcess_FixInfacingNormals 
												| Assimp.aiProcess_PreTransformVertices
												| Assimp.aiProcess_ImproveCacheLocality
												| Assimp.aiProcess_RemoveRedundantMaterials
												| Assimp.aiProcess_OptimizeMeshes;
	
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
			
			Vector3f ambient = Material.DEFAULT_COLOUR;
		    int result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        ambient = new Vector3f(colour.r(), colour.g(), colour.b());
		    }

		    Vector3f diffuse = Material.DEFAULT_COLOUR;
		    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_BASE_COLOR, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        diffuse = new Vector3f(colour.r(), colour.g(), colour.b());
		    }

		    Vector3f specular = Material.DEFAULT_COLOUR;
		    result = Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, colour);
		    if (result == 0) {
		        specular = new Vector3f(colour.r(), colour.g(), colour.b());
		    }
		    
			AIString normalPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_NORMALS, 0, normalPath, (IntBuffer) null, null, null, null, null, null);
			String normalTexture = normalPath.dataString();
			String normalTexturePath = (texturesDir + "/" + normalTexture).replace("//", "/");
			
			if (normalTexture != null && normalTexture.length() > 0) {
				Logging.logger.error(normalTexture);
			} else
				normalTexturePath = findNormalMap(texturePath);
			
			AIString displacementPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DISPLACEMENT, 0, displacementPath, (IntBuffer) null, null, null, null, null, null);
			String displacementTexture = displacementPath.dataString();
			String displacementTexturePath = (texturesDir + "/" + displacementTexture).replace("//", "/");
			
			if (displacementTexture != null && displacementTexture.length() > 0) {
				Logging.logger.error(displacementTexturePath);
			} else
				displacementTexturePath = findDisplacementMap(texturePath);
			
			AIString AOPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_AMBIENT_OCCLUSION, 0, AOPath, (IntBuffer) null, null, null, null, null, null);
			String AOTexture = AOPath.dataString();
			String AOTexturePath = (texturesDir + "/" + AOTexture).replace("//", "/");
			
			if (AOTexture != null && AOTexture.length() > 0) {
				Logging.logger.error(AOTexturePath);
			} else
				AOTexturePath = findAOMap(texturePath);
			
			AIString SpecPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_SPECULAR, 0, SpecPath, (IntBuffer) null, null, null, null, null, null);
			String SpecTexture = SpecPath.dataString();
			String SpecTexturePath = (texturesDir + "/" + SpecTexture).replace("//", "/");
			
			if (SpecTexture != null && SpecTexture.length() > 0) {
				Logging.logger.error(SpecTexturePath);
			} else
				SpecTexturePath = findSpecMap(texturePath);
			
			AIString bumpPath = AIString.calloc();
			Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE_ROUGHNESS, 0, bumpPath, (IntBuffer) null, null, null, null, null, null);
			String bumpTexture = bumpPath.dataString();
			String bumbTexturePath = (texturesDir + "/" + bumpTexture).replace("//", "/");
			
			if (bumpTexture != null && bumpTexture.length() > 0) {
				Logging.logger.error(bumbTexturePath);
			} else
				bumbTexturePath = findSpecMap(texturePath);
			
			if (normalTexturePath == GameRegistry.DEFAULT_EMPTY_NORMAL_MAP && displacementTexturePath == GameRegistry.DEFAULT_EMPTY_DISPLACEMENT_MAP &&
					AOTexturePath == GameRegistry.DEFAULT_EMPTY_AO_MAP && SpecTexturePath == GameRegistry.DEFAULT_EMPTY_SPEC_MAP && bumbTexturePath == GameRegistry.DEFAULT_EMPTY_SPEC_MAP)
				Logging.logger.warn("Failed to load extra texture maps after checking possible resource locations! :(");
			
			String diffuseTexture = (texturesDir + "/" + materialTexturePath).replace("//", "/");
			
			if (!materialTexturePath.contains("NORENDER"))
				GameRegistry.registerMaterialTextures(diffuseTexture, normalTexturePath, displacementTexturePath, SpecTexturePath);
			
			/*Logging.logger.fatal("\n\t " +
					texturePath + " \n\t " + normalTexturePath + " \n\t " 
								+ displacementTexturePath + " \n\t " + AOTexturePath + " \n\t " + SpecTexturePath + " \n\t " 
								+ diffuse + "\n\t " + ambient + " \n\t " + specular);*/
			
			return GameRegistry.registerMaterial2(
					new Material(
							texturePath, 
							normalTexturePath, 
							displacementTexturePath, 
							AOTexturePath, 
							SpecTexturePath, 
							diffuse, 
							ambient, 
							specular
						)
					);
		}
		// can't load texture, meaning we need to load error material
		return GameRegistry.getErrorMaterial();
	}
	
	private static Mesh processMesh(AIMesh mesh, Material[] materials) {
		ArrayList<com.jme3.math.Vector3f> faceV = new ArrayList<com.jme3.math.Vector3f>();
		IndexingFloatArrayList vertices = new IndexingFloatArrayList();
		IndexingFloatArrayList textures = new IndexingFloatArrayList();
		IndexingFloatArrayList normals = new IndexingFloatArrayList();
		IndexingFloatArrayList tangents = new IndexingFloatArrayList();
		IndexingFloatArrayList bitangents = new IndexingFloatArrayList();
		IndexingIntArrayList indices = new IndexingIntArrayList();
		
		AxisAlignedBoundingBox aabb = processVertices(mesh, vertices, faceV);
	    processNormals(mesh, normals, tangents, bitangents);
	    processTextCoords(mesh, textures);
	    processIndices(mesh, indices);
	    
	    int[] indcies = new int[indices.size()];
	    
	    indices.generateStructures(indcies);
	    textures.generateStructures();
	    tangents.generateStructures();
	    bitangents.generateStructures();
	    normals.generateStructures();
	    vertices.generateStructures();
	    
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
	    
	    com.jme3.math.Vector3f[] positions = new com.jme3.math.Vector3f[faceV.size()];
	    
	    // TODO: remove this?
	    for (int i = 0; i < positions.length; i++) {
	    	positions[i] = faceV.get(i);
	    }
	    
	    IndexedMesh meshInfo = new IndexedMesh(positions, indcies);
	    
	    IntBuffer indexBuffer = indices.toIntBuffer();
	    FloatBuffer vertexBuffer = vertices.toFloatBuffer();
	    FloatBuffer textureBuffer = textures.toFloatBuffer();
	    FloatBuffer normalBuffer = normals.toFloatBuffer();
	    FloatBuffer bitangentBuffer = bitangents.toFloatBuffer();
	    FloatBuffer tangentBuffer = tangents.toFloatBuffer();
	    
		return new Mesh(m, aabb, vertexBuffer, textureBuffer, normalBuffer, tangentBuffer, bitangentBuffer, indexBuffer, meshInfo);
	}
	
	private static AxisAlignedBoundingBox processVertices(AIMesh mesh, IndexingFloatArrayList vertices, ArrayList<com.jme3.math.Vector3f> faceV) {
		float maxX = 0, maxY = 0, maxZ = 0;
		float minX = 0, minY = 0, minZ = 0;
	    AIVector3D.Buffer verts = mesh.mVertices();
	    while (verts.remaining() > 0) {
	        AIVector3D aiVertex = verts.get();
	        float x = aiVertex.x(), y = aiVertex.y(), z = aiVertex.z();
	        
	        if (maxX < x)
	        	maxX = x;
	        if (maxY < y)
	        	maxY = y;
	        if (maxZ < z)
	        	maxZ = z;
	        if (minX > x)
	        	minX = x;
	        if (minY > y)
	        	minY = y;
	        if (minZ > z)
	        	minZ = z;
	        
	        vertices.add(x);
	        vertices.add(y);
	        vertices.add(z);
	        faceV.add(new com.jme3.math.Vector3f(x,y,z));
	    }
	    return new AxisAlignedBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	private static void processNormals(AIMesh mesh, IndexingFloatArrayList normals, IndexingFloatArrayList tangents, IndexingFloatArrayList bitangents) {
	    AIVector3D.Buffer norms = mesh.mNormals();
	    while (norms.remaining() > 0) {
	        AIVector3D aiVertex = norms.get();
	        normals.add(aiVertex.x());
	        normals.add(aiVertex.y());
	        normals.add(aiVertex.z());
	    }
	    AIVector3D.Buffer tangs = mesh.mTangents();
	    while (tangs.remaining() > 0) {
	    	AIVector3D v = tangs.get();
	    	tangents.add(v.x());
	    	tangents.add(v.y());
	    	tangents.add(v.z());
	    }
	    AIVector3D.Buffer bitangs = mesh.mBitangents();
	    while (bitangs.remaining() > 0) {
	    	AIVector3D v = bitangs.get();
	    	bitangents.add(v.x());
	    	bitangents.add(v.y());
	    	bitangents.add(v.z());
	    }
	}
	
	private static void processTextCoords(AIMesh mesh, IndexingFloatArrayList textures) {
        AIVector3D.Buffer textCoords = mesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }
	
	private static void processIndices(AIMesh mesh, IndexingIntArrayList indices) {
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
	
	private static String findNormalMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_NORMAL_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		// TODO: add more?
		String name = getName(diffuseTexturePath);
		String newPath = findPathForMap(dir, name, new String[] {"diff", "diffuse"}, new String[] {"nor", "normal", "normalmap", "normalMap", "norMap", "normap"});

		if (newPath == null)
			return returnval;
		else {
			return newPath;
		}
	}
	
	private static String findDisplacementMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_DISPLACEMENT_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		String newPath = findPathForMap(dir, name, new String[] {"diff", "diffuse"}, new String[] {"disp", "displacement", "dispMap", "displacementMap", "dispmap", "displacementmap"});
		
		if (newPath == null)
			return returnval;
		else {
			return newPath;
		}
	}
	
	private static String findAOMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_AO_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		String newPath = findPathForMap(dir, name, new String[] {"diff", "diffuse"}, new String[] {"AO", "ao", "ambientocclusion", "ambientOcclusion"});
		
		if (newPath == null)
			return returnval;
		else {
			return newPath;
		}
	}
	
	private static String findSpecMap(String diffuseTexturePath) {
		String returnval = GameRegistry.DEFAULT_EMPTY_SPEC_MAP;
		
		String dir = getParentDir(diffuseTexturePath);
		if (dir == null)
			return returnval;
		
		String name = getName(diffuseTexturePath);
		String newPath = findPathForMap(dir, name, new String[] {"diff", "diffuse"}, new String[] {"spec", "specular", "specMap", "specmap"});
		
		if (newPath == null)
			return returnval;
		else {
			return newPath;
		}
	}
	
	private static String findPathForMap(String dir, String name, String[] find, String[] replace) {
		String path = null;
		for (int i = 0; i < find.length; i++) {
			path = checkPossibleLocations(dir, name, find[i], replace);
			if (path != null)
				break;
		}
		return path;
	}
	
	private static String checkPossibleLocations(String dir, String name, String find, String[] replace) {
		
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
		
		return null;
	}
	
	private static String getName(String path) {
		return new File(path).getName();
	}
	
	private static String getParentDir(String path) {
		return new File(path).getParent();
	}
	
}
