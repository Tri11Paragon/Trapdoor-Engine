package com.game.engine.renderer;

import java.util.HashMap;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import com.game.engine.Loader;
import com.game.engine.TextureLoader;
import com.game.engine.datatypes.ogl.ModelVAO;
import com.game.engine.datatypes.world.Tile;
import com.game.engine.shaders.TileShader;
import com.game.engine.tools.math.Maths;

/**
 * @author laptop
 * @date Oct. 25, 2021
 * 
 */
public class TileRenderer {

	private Tile[][] tiles;
	private int[] atlases = new int[16];
	private HashMap<Integer, Integer> atlasesmaped = new HashMap<Integer, Integer>();
	private ModelVAO vao;
	private TileShader shader;
	
	public TileRenderer(Tile[][] tiles) {
		this.tiles = tiles;
		float[] verts = new float[0];
		int last = 0;
		// fuck you
		// fuck this
		// no
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (last > 15) {
					System.err.println("Hey mate somehow you are trying to load more than 16 texture atlases to the GPU\n"
							+ "Unfortunately, due to limitations with OpenGL, this isn't possible. \n"
							+ "I kindly ask: HOW THE FUCK ARE YOU USING 16\n"
							+ "Try consolidating your texture atlases");
					// we cannot recover from this safely.
					System.exit(-1);
				}
				int id = TextureLoader.getTextureAtlas(tiles[i][j].getTexture());
				boolean f = false;
				for (int k = 0; k < atlases.length; k++) {
					if (atlases[k] == id) {
						f = true;
						break;
					}
				}
				if (!f) {
					atlasesmaped.put(id, last);
					atlases[last] = id;
					last++;
				}
			}
		}
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				verts = combineArrays(verts,
						translateVerticies(i, j, atlasesmaped.get(TextureLoader.getTextureAtlas(tiles[i][j].getTexture())),
								TextureLoader.getTextureAtlasID(tiles[i][j].getTexture())));
			}
		}
		vao = Loader.loadToVAOTile(verts);
		shader = new TileShader("tile.vs", "tile.fs");
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void DrawTiles(Matrix4f viewmatrix) {
		// single draw call for the whole tile map
		// get fucked
		GL30.glBindVertexArray(vao.getVaoID());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		shader.start();
		shader.loadViewMatrix(viewmatrix);
		
		for (int i = 0; i < texts.length; i++) {
			// don't activate textures we are not using
			if (atlases[i] == 0)
				continue;
			// just to make sure OpenGL doesn't do anything stupid (I know GL_Texture1..15 are +1..15 from texture0)
			GL30.glActiveTexture(texts[i]);
			GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, atlases[i]);
		}
		
		shader.loadTranslationMatrix(Maths.createTransformationMatrix(0, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT));
		
		GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, vao.getVertexCount());
		
		shader.stop();
		// someone says we don't have to do this if we do it while creating VAO
		// TODO: check that out
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public Tile[][] getTiles(){
		return this.tiles;
	}
	
	private static float[] translateVerticies(float x, float y, int aid, int id) {
		float[] ret = new float[VERTICES.length];
		System.arraycopy(VERTICES, 0, ret, 0, VERTICES.length);
		
		// i don't mind the extra wasted GPU memory
		// lol each square takes 34 floats
		// 400 tiles would take 54 400 bytes
		// which isn't really that bad
		// most GPUs can handle that
		// all computer would rather that all at once vs individually
		ret[0] += x;
		ret[1] += y;
		ret[2] = aid;
		ret[3] = id;
		
		ret[6] += x;
		ret[7] += y;
		ret[8] = aid;
		ret[9] = id;
		
		ret[12] += x;
		ret[13] += y;
		ret[14] = aid;
		ret[15] = id;
		
		ret[18] += x;
		ret[19] += y;
		ret[20] = aid;
		ret[21] = id;
		
		ret[24] += x;
		ret[25] += y;
		ret[26] = aid;
		ret[27] = id;
		
		ret[30] += x;
		ret[31] += y;
		ret[32] = aid;
		ret[33] = id;
		
		return ret;
	}
	
	private static float[] combineArrays(float[] a, float[] b) {
		float[] ret = new float[a.length + b.length];
		System.arraycopy(a, 0, ret, 0, a.length);
		System.arraycopy(b, 0, ret, a.length, b.length);
		return ret;
	}
	
	public static final int[] texts = {
			GL30.GL_TEXTURE0,
			GL30.GL_TEXTURE1,
			GL30.GL_TEXTURE2,
			GL30.GL_TEXTURE3,
			GL30.GL_TEXTURE4,
			GL30.GL_TEXTURE5,
			GL30.GL_TEXTURE6,
			GL30.GL_TEXTURE7,
			GL30.GL_TEXTURE8,
			GL30.GL_TEXTURE9,
			GL30.GL_TEXTURE10,
			GL30.GL_TEXTURE11,
			GL30.GL_TEXTURE12,
			GL30.GL_TEXTURE13,
			GL30.GL_TEXTURE14,
			GL30.GL_TEXTURE15,
	};
	
	private static final float[] VERTICES = {
			// first 
			1f,  1f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
			1f,  0f, 0.0f, 0.0f, 1.0f, 0.0f, // bottom right
			0f,  1f, 0.0f, 0.0f, 0.0f, 1.0f, // top left 
			
			//second
			1f,  0f, 0.0f, 0.0f, 1.0f, 0.0f,   // bottom right
			0f,  0f, 0.0f, 0.0f, 0.0f, 0.0f,   // bottom left
			0f,  1f, 0.0f, 0.0f, 0.0f, 1.0f    // top left 
	};
	
}
