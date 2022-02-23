package com.trapdoor.engine.renderer.debug;

import org.joml.Matrix4f;

import com.trapdoor.engine.renderer.ShaderProgram;

public class TextureShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "texture.vs";
    private static final String FRAGMENT_FILE = "texture.fs";
     
    private int location_transformationMatrix;
    
    public TextureShader() {
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
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
	
}
