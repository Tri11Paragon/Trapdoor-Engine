package com.trapdoor.engine.tools.old;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class OldLoader {
	
	/*private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();
	// map of loaded textures.
	public static Map<String, Integer> textureMap = new HashMap<String, Integer>();
	
	public static ModelVAO loadToVAO(float[] positions,float[] textureCoords,int[] indices){
		// create a VAO
		int vaoID = createVAO();
		// bind the index buffer
		bindIndicesBuffer(indices);
		// store the data into vbos
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		// unbind the vbos
		unbindVAO();
		// return the model
		return new ModelVAO(vaoID,indices.length);
	}
	
	public static BlockModelVAO loadToVAO(float[] positions,float[] textureCoords, int dimensions){
		// standard stuff that this point
		// create VAO
		int vaoID = createVAO();
		// we want to keep reference of vbos for runtime deletion
		int[] vbos = new int[2];
		// store the data into the vbos
		vbos[0] = storeDataInAttributeList(0, dimensions, positions);
		vbos[1] = storeDataInAttributeList(1, dimensions, textureCoords);
		// unbind the VAO
		unbindVAO();
		return new BlockModelVAO(vaoID, vbos, positions.length);
	}
	
	public static ModelVAO loadToVAO(float[] positions, int dimensions) {
		// create the VAO
		int vaoID = createVAO();
		// store data in its first position
		storeDataInAttributeList(0, 2, positions);
		// unbind the vao
		unbindVAO();
		// return this as a ModelVAO object.
		return new ModelVAO(vaoID, positions.length/2);
		
	}
	
	private static int createVAO(){
		// create the VAO
		int vaoID = GL30.glGenVertexArrays();
		// store for later deletion
		vaos.add(vaoID);
		// bind it
		GL30.glBindVertexArray(vaoID);
		// return
		return vaoID;
	}
	

	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		// creates a VBO
		int vboID = GL15.glGenBuffers();
		// add for later deletion
		vbos.add(vboID);
		// binds the buffer as just a standard array buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// creates a float buffer from the data
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// puts the data into the VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// assign this VBO to the pointer and coordinate size. We are using floats, they are not normalized and start at 0
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		// unbind the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// return the VBO
		return vboID;
	}
	
	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize, int stride, int offset, float[] data){
		// creates a VBO
		int vboID = GL15.glGenBuffers();
		// add for later deletion
		vbos.add(vboID);
		// binds the buffer as just a standard array buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// creates a float buffer from the data
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// puts the data into the VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// assign this VBO to the pointer and coordinate size. We are using floats, they are not normalized and start at 0
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,stride,offset);
		// unbind the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// return the VBO
		return vboID;
	}
	
	private static void bindIndicesBuffer(int[] indices){
		// generate the vbo
		int vboID = GL15.glGenBuffers();
		// add it for later deletion
		vbos.add(vboID);
		// bind the buffer as the element buffer (used to defined indexes inside the arrays for drawing)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		// create the int buffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		// but the int buffer into the VBO
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public static int loadCubeMap(String[] textureFiles) {
		// generate a texture buffer
		int texID = GL11.glGenTextures();
		// enable texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// bind buffer this time as a cube map
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		// loop through all files
		for(int i=0;i<textureFiles.length;i++) {
			// load the texture from the skybox location
			TextureData data = decodeTextureFile("resources/textures/terrain/skyboxes/" + textureFiles[i] + ".png");
			// load the texture into its position in the cube map
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); 
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		// add texture for later deletion.
		textures.add(texID);
		return texID;
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data){
		// create the int buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		// put the data 
		buffer.put(data);
		// flip for reading
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		// create a float buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// put the data
		buffer.put(data);
		// flip it for reading.
		buffer.flip();
		return buffer;
	}
	
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}*/

}
