package com.game.engine.datatypes.world;

import org.joml.Vector3f;

import com.game.engine.TextureLoader;
import com.game.engine.renderer.EntityRenderer;

/**
 * @author brett
 * @date Oct. 19, 2021
 * 
 */
public class Entity {
	
	// TODO: add sorting based on entity Z
	private float x = 0;
	private float y = 0;
	private float z = 0;
	private float width = 32;
	private float height = 32;
	private float rotation;
	private String texture;
	private boolean atlas = true;
	private boolean enabled = false;
	private int atlasID = 0;
	private int textureID = 0;
	
	
	/**
	 * @param atlas true if the texture is found inside an atlas
	 */
	public Entity(float x, float y, float width, float height, String texture, boolean atlas) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		if (atlas) {
			this.atlasID = TextureLoader.getTextureAtlas(this.texture);
			this.textureID = TextureLoader.getTextureAtlasID(this.texture);
		}
		EntityRenderer.addEntity(this);
	}
	
	/**
	 * @param texture filename (including extension) of the entity's texture
	 * @param atlas true if the texture is found inside an atlas
	 */
	public Entity(float x, float y, float width, float height, float rotation, String texture, boolean atlas) {
		this(x,y,width,height,texture,atlas);
		this.rotation = rotation;
	}
	
	/**
	 * called when the game renders this entity
	 */
	public void onRender() {
		
	}
	
	/**
	 * called when the game updates this entity
	 * NOTE: this may be called from another thread. 
	 * No rendering can occur here.
	 * also be careful for race conditions.
	 */
	public void onUpdate() {
		
	}
	
	/**
	 * deletes the entity from the renderer (TODO: world as well)
	 */
	public void delete() {
		EntityRenderer.deleteEntity(this);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public Entity setEnabled(boolean b) {
		this.enabled = b;
		return this;
	}
	public Entity enable() {
		this.enabled = true;
		return this;
	}
	public Entity disable() {
		this.enabled = false;
		return this;
	}
	public boolean getIsUsingAtlas() {
		return atlas;
	}
	public String getTexture() {
		return texture;
	}
	public float x() {
		return x;
	}
	public float y() {
		return y;
	}
	public float z() {
		return z;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getZ() {
		return z;
	}
	private static final Vector3f store = new Vector3f();
	public Vector3f getPosition() {
		store.x = x;
		store.y = y;
		store.z = z;
		return store;
	}
	public Entity setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
		return this;
	}
	public Entity setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	public Entity setPosition(float x, float y, float z) {
		setPosition(x, y);
		this.z = z;
		return this;
	}
	public float getRotation() {
		return rotation;
	}
	public Entity setRotation(float rotation) {
		this.rotation = rotation;
		return this;
	}
	public float getWidth() {
		return width;
	}
	public Entity setWidth(float width) {
		this.width = width;
		return this;
	}
	public float getHeight() {
		return height;
	}
	public Entity setHeight(float height) {
		this.height = height;
		return this;
	}
	public int getAtlasID() {
		return atlasID;
	}

	public int getTextureID() {
		return textureID;
	}

	public void setTexture(String texture) {
		this.texture = texture;
		if (atlas) {
			this.atlasID = TextureLoader.getTextureAtlas(this.texture);
			this.textureID = TextureLoader.getTextureAtlasID(this.texture);
		}
	}

}
