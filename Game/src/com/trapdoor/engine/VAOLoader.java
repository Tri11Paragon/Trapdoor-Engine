package com.trapdoor.engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.obj.ModelData;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.tools.Logging;

/*
 * THIS CLASS IS NOT TO BE TOUCHED BY ANYONE
 * PEROID
 * I WILL REJECT GIT CHANGES BY ANYONE OTHER THAN ME.
 */


/**
 * @author brett
 * This is one of the first classes made.
 * 
 * The loader class handles all loading of models and textures into the GPU.
 * 
 * Something to note.
 *  VAO = Vertex Array Object. VAOs tell OpenGL how to draw things, if by index or by array.
 *  They hold a list of pointers to objects called VBOs
 *  VBO = Vertex Buffer Object. VBOs store float[]s for future rendering on the GPU
 *  
 *  Generally we use 3 float[]s and one int[] for drawing
 *  
 *  the first float array is normally defined in triplets and store position data for the vertex.
 *  the second float array is normally defined in pairs and store UV coords for the vertex (positions on the texture)
 *  the third float array is normally the normals and are defined in triplets as well for each vertex.
 *  
 *  the int[] is normally the indices used for drawing. Indices are ints that are used as indexs for the arrays.
 *  This can save storage on the GPU and be faster as instead of having to define multiple of the same position you just use the index.
 *  In theory you can get savings of upwards of 90%
 * 
 *  if drawing doesn't use an int[] that means that OpenGL uses only the float[]s.
 *  Which means that instead of generating trinagles based on the indexes of the float[]s it will use the entire float[]
 *  as if it had already been built. 
 *  This means that all vertices need to be defined to draw the triangles, even if they are repetitive. 
 *  
 *  Hope that makes sense.
 */
public class VAOLoader {

	/**
	 * list of the VBOs, textures and VAOs.
	 */
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	
	public static int getVAOS() {
		return vaos.size();
	}
	
	public static int getVBOS() {
		return vbos.size();
	}
	
	public static VAO loadToVAO(float[] data, int[] indicies) {
		int vao = createVAO();
		
		int[] vbos = new int[2];
		
		bindIndicesBuffer(indicies);
		
		vbos[0] = storeDataInAttributeList(0, 3, 20, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 20, 12, data);
		
		unbindVAO();
		return new VAO(vao, vbos, indicies.length);
	}
	
	public static VAO loadToVAO(float[] positions, int dimensions, int numOfVaosUsed) {
		// create the VAO
		int vaoID = createVAO();
		for (int i = 0; i < numOfVaosUsed; i++)
			GL33.glEnableVertexAttribArray(i);
		int[] vbos = new int[1];
		// store data in its first position
		vbos[0] = storeDataInAttributeList(0, 2, positions);
		// unbind the vao
		unbindVAO();
		// return this as a ModelVAO object.
		return new VAO(vaoID, vbos, positions.length/2);
		
	}
	
	public static VAO loadToVAO(float[] data) {
		int vao = createVAO();
		
		int[] vbos = new int[2];
		
		vbos[0] = storeDataInAttributeList(0, 2, 16, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 16, 8, data);
		
		unbindVAO();
		return new VAO(vao, vbos, data.length/2);
	}
	
	public static VAO loadToVAOTile(float[] data) {
		int vao = createVAO();
		
		int[] vbos = new int[3];
		
		vbos[0] = storeDataInAttributeList(0, 2, 24, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 24, 16, data);
		vbos[2] = storeDataInAttributeList(2, 2, 24, 8, data);
		
		unbindVAO();
		return new VAO(vao, vbos, data.length/3);
	}
	
	/**
	 * loads to VAO using ModelData
	 */
	public static VAO loadToVAO(ModelData data) {
		// create the VAO
		int vaoID = createVAO();
		// get the indices
		int[] indices = data.getIndices();
		// bind the indices buffer
		bindIndicesBuffer(indices);
		/**
		 * I should note its not actually storing the data itself into the VAO, but a pointer to the VBO
		 * you still need to enable the VBO when rendering.
		 */
		// store data into the vao
		int[] vbos = new int[3];
		vbos[0] = storeDataInAttributeList(0,3,data.getVertices());
		vbos[1] = storeDataInAttributeList(1,2,data.getTextureCoords());
		vbos[2] = storeDataInAttributeList(2,3,data.getNormals());
		// unbind the VAO
		unbindVAO();
		// return the model
		return new VAO(vaoID, vbos, indices.length);
	}
	
	public static void loadToVAO(Model model) {
		Mesh[] meshes = model.getMeshes();
		for (int i = 0; i < meshes.length; i++) {
			Mesh mesh = meshes[i];
			// create the VAO
			int vaoID = createVAO();
			for (int j = 0; j < 5; j++)
				GL33.glEnableVertexAttribArray(j);
			// bind the indices buffer
			bindIndicesBuffer(mesh.getIndices());
			/**
			 * I should note its not actually storing the data itself into the VAO, but a pointer to the VBO
			 * you still need to enable the VBO when rendering.
			 */
			// store data into the vao
			int[] vbos = new int[5];
			vbos[0] = storeDataInAttributeList(0,3,mesh.getVertices());
			vbos[1] = storeDataInAttributeList(1,2,mesh.getTextures());
			vbos[2] = storeDataInAttributeList(2,3,mesh.getNormals());
			vbos[3] = storeDataInAttributeList(3,3,mesh.getTangents());
			vbos[4] = storeDataInAttributeList(4,3,mesh.getBitangents());
			// unbind the VAO
			unbindVAO();
			// return the model
			mesh.assignVAO(new VAO(vaoID, vbos, mesh.getIndices().capacity()));
		}
	}

	/**
	 * deletes an actual model from the graphics card
	 */
	public static VAO deleteVAO(VAO model) {
		try {
			// if this is a block then we will delete the VBOs
			if (model instanceof VAO) {
				// list of all the VBOs for this model
				int[] vbos = (model).getVbos();
				// remove them from the graphics card
				for (int i = 0; i < vbos.length; i++) {
					GL15.glDeleteBuffers(vbos[i]);
					// make sure we remove them from the list.
					for (int j = 0; j < VAOLoader.vbos.size(); j++) {
						if (VAOLoader.vbos.get(i) == vbos[i])
							VAOLoader.vbos.remove(j);
					}
				}
			}
			// make sure we remove it from the VAO list
			for (int i = 0; i < vaos.size(); i++) {
				if (vaos.get(i) == model.getVaoID()) {
					vaos.remove(i);
				}
			}
			// delete the VAO
			GL30.glDeleteVertexArrays(model.getVaoID());
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * deletes all the VAOs, VBOs and Textures from the graphics card.
	 */
	public static void cleanUp(){
		Logging.logger.debug("Cleaning up VAOs");
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		Logging.logger.debug("Cleaning up VBOs");
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		Logging.logger.debug("Cleaning up textures");
		TextureLoader.cleanup();
		Logging.logger.debug("Cleaning up UBOs");
		UBOLoader.cleanup();
		Logging.logger.info("GPU objects cleanup completed");
	}
	
	/**
	 * deletes a vao
	 */
	public static void deleteVAO(int vaoID) {
		// remove it since we already deleted it
		VAOLoader.vaos.remove((Integer) vaoID);
		// deletes the vertex array from the graphics card
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	/**
	 * deletes a vbo
	 */
	public static void deleteVBO(int vboID) {
		// remove it since we already deleted it
		VAOLoader.vbos.remove((Integer) vboID); // needed to make this work
		// delete it from the graphics card
		GL15.glDeleteBuffers(vboID);
	}
	
	/**
	 * creates a VAO
	 */
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
	
	public static int createEmptyVBO(int bytes) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		// GL_STREAM_DRAW is used because we will be updating the VBO often.
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bytes, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	
	/**
	 * TODO: doccument this and the rest of the loader class
	 * @param vao
	 * @param vbo
	 * @param attribute
	 * @param datasize
	 * @param stride Data length of each instance (in bytes, so multiply by 4)
	 * @param offset offset of the data inside the array.
	 */
	public static void addInstancedAttribute(int vao, int vbo, int attribute, int datasize, int stride, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, datasize, GL11.GL_FLOAT, false, stride, offset);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	// there are ways of making this better
	public static void updateVBO(int vbo, FloatBuffer buffer) {
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * stores a float[] into a vbo for drawing. This stores on the graphics card.
	 * @param attributeNumber is the pointer for this float[] to be used inside the shader as the layout position
	 * the layout position needs to be then defined inside the shader or bound when creating the shader class.
	 * @param coordinateSize how many floats are to be passed each vertex. 1 is just one float, 2 would be a 2d vector, 3 would be a 3d vector and so on
	 * @param data float[] the data
	 */
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
	
	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize, FloatBuffer data){
		// creates a VBO
		int vboID = GL15.glGenBuffers();
		// add for later deletion
		vbos.add(vboID);
		// binds the buffer as just a standard array buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// puts the data into the VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
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
	
	/**
	 * unbinds any active VAO
	 */
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * puts the indices into the element buffer for index based drawing.
	 */
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
	
	private static void bindIndicesBuffer(IntBuffer indices){
		// generate the vbo
		int vboID = GL15.glGenBuffers();
		// add it for later deletion
		vbos.add(vboID);
		// bind the buffer as the element buffer (used to defined indexes inside the arrays for drawing)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		// but the int buffer into the VBO
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
	}
	
	/**
	 * stores the int[] into a int buffer
	 */
	private static IntBuffer storeDataInIntBuffer(int[] data){
		// create the int buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		// put the data 
		buffer.put(data);
		// flip for reading
		buffer.flip();
		return buffer;
	}
	
	/**
	 * stores the float[] into a float buffer
	 */
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		// create a float buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// put the data
		buffer.put(data);
		// flip it for reading.
		buffer.flip();
		return buffer;
	}
	
	/**
	 * prints the sizes of all the maps
	 */
	public static void printSizes() {
		Logging.logger.info("VAOs Size: " + vaos.size());
		Logging.logger.info("VBOs Size: " + vbos.size());
		TextureLoader.print();
	}
	
}
