package com.game.engine;

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

import com.game.engine.datatypes.ogl.ModelVAO;
import com.game.engine.tools.Logger;
import com.game.engine.tools.models.ModelData;

/*
 * THIS CLASS IS NOT TO BE TOUCHED BY ANYONE
 * PEROID
 * I WILL REJECT GIT CHANGES BY ANYONE OTHER THAN ME.
 */

/**
 * @author ${user}
 * @date ${date}
 * 
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
public class Loader {

	/**
	 * list of the VBOs, textures and VAOs.
	 */
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	
	public static int loadTexture(String filename) {
		return TextureLoader.loadTexture(filename);
	}
	
	public static int loadTexture(String filename, float bias) {
		return TextureLoader.loadTexture(filename, bias);
	}
	
	public static int loadTexture(String texture, float bias, int minmag_filter, int minmag_mipmap) {
		return TextureLoader.loadTexture(texture, bias, minmag_filter, minmag_mipmap);
	}
	
	public static int loadTexture(String texture, int width, int height) {
		return TextureLoader.loadTexture(texture, width, height);
	}
	
	public static ModelVAO loadToVAO(float[] data, int[] indicies) {
		int vao = createVAO();
		
		int[] vbos = new int[2];
		
		bindIndicesBuffer(indicies);
		
		vbos[0] = storeDataInAttributeList(0, 3, 20, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 20, 12, data);
		
		unbindVAO();
		return new ModelVAO(vao, vbos, indicies.length);
	}
	
	public static ModelVAO loadToVAO(float[] data) {
		int vao = createVAO();
		
		int[] vbos = new int[2];
		
		vbos[0] = storeDataInAttributeList(0, 2, 16, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 16, 8, data);
		
		unbindVAO();
		return new ModelVAO(vao, vbos, data.length/2);
	}
	
	public static ModelVAO loadToVAOTile(float[] data) {
		int vao = createVAO();
		
		int[] vbos = new int[3];
		
		vbos[0] = storeDataInAttributeList(0, 2, 24, 0, data);
		vbos[1] = storeDataInAttributeList(1, 2, 24, 16, data);
		vbos[2] = storeDataInAttributeList(2, 2, 24, 8, data);
		
		unbindVAO();
		return new ModelVAO(vao, vbos, data.length/3);
	}
	
	/**
	 * loads to VAO using ModelData
	 */
	public static ModelVAO loadToVAO(ModelData data) {
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
		return new ModelVAO(vaoID, vbos, indices.length);
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
	public static void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * deletes an actual model from the graphics card
	 */
	public static ModelVAO deleteVAO(ModelVAO model) {
		try {
			// if this is a block then we will delete the VBOs
			if (model instanceof ModelVAO) {
				// list of all the VBOs for this model
				int[] vbos = ((ModelVAO) model).getVbos();
				// remove them from the graphics card
				for (int i = 0; i < vbos.length; i++) {
					GL15.glDeleteBuffers(vbos[i]);
					// make sure we remove them from the list.
					for (int j = 0; j < Loader.vbos.size(); j++) {
						if (Loader.vbos.get(i) == vbos[i])
							Loader.vbos.remove(j);
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
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		TextureLoader.cleanup();
	}
	
	/**
	 * deletes a vao
	 */
	public static void deleteVAO(int vaoID) {
		// remove it since we already deleted it
		Loader.vaos.remove((Integer) vaoID);
		// deletes the vertex array from the graphics card
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	/**
	 * deletes a vbo
	 */
	public static void deleteVBO(int vboID) {
		// remove it since we already deleted it
		Loader.vbos.remove((Integer) vboID); // needed to make this work
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
		Logger.writeln("VAOs Size: " + vaos.size());
		Logger.writeln("VBOs Size: " + vbos.size());
		TextureLoader.print();
	}
	
}
