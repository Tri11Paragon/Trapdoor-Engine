package com.trapdoor.engine.renderer.debug;

import org.joml.Matrix4f;

import com.trapdoor.engine.renderer.ShaderProgram;

public class TextureArrayShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "textureArray.vs";
    private static final String FRAGMENT_FILE = "textureArray.fs";
     
    private int location_transformationMatrix;
    private int location_level;
    
    public TextureArrayShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        this.start();
        setUniformBlockLocation("Matricies", 1);
        this.stop();
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_level = super.getUniformLocation("level");
    }
    
    public void loadLevel(int level) {
    	super.loadFloat(location_level, level);
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
	
}
