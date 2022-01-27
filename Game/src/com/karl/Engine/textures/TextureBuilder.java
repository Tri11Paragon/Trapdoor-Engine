package com.karl.Engine.textures;

import com.karl.Engine.utils.MyFile;
import com.trapdoor.engine.TextureLoader;
import com.trapdoor.engine.datatypes.ogl.TextureData;

public class TextureBuilder {
	
	private boolean clampEdges = false;
	private boolean mipmap = false;
	private boolean anisotropic = true;
	private boolean nearest = false;
	
	private MyFile file;
	
	protected TextureBuilder(MyFile textureFile){
		this.file = textureFile;
	}
	
	public Texture create(){
		TextureData textureData = TextureLoader.decodeTextureToSize(file.getPath(), false, false, 0, 0);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId, textureData.getWidth());
	}
	
	public TextureBuilder clampEdges(){
		this.clampEdges = true;
		return this;
	}
	
	public TextureBuilder normalMipMap(){
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}
	
	public TextureBuilder nearestFiltering(){
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}
	
	public TextureBuilder anisotropic(){
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}
	
	protected boolean isClampEdges() {
		return clampEdges;
	}

	protected boolean isMipmap() {
		return mipmap;
	}

	protected boolean isAnisotropic() {
		return anisotropic;
	}

	protected boolean isNearest() {
		return nearest;
	}

}
