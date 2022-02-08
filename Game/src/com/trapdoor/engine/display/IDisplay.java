package com.trapdoor.engine.display;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.trapdoor.engine.TextureLoader;
import com.trapdoor.engine.datatypes.ogl.Texture;
import com.trapdoor.engine.datatypes.ogl.TextureData;
import com.trapdoor.engine.registry.annotations.AnnotatedClass;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 * IDisplay implements annotated class, meaning it can subscribe to any number of events
 * 
 * to register your models with the engine you can create a STATIC method
 * which is annotated with the @RegistrationEventSubscriber
 * This method will then be called at runtime before the loading screen runs (ie during registration).
 */
public abstract class IDisplay implements AnnotatedClass {
	
	public IDisplay() {
		
	}
	
	private final Vector4f[] skyStorage = {new Vector4f(), new Vector4f()};
	private final Vector3f skyColor1 = new Vector3f(0.5444f, 0.62f, 0.69f);
	private final Vector3f skyColor2 = new Vector3f(0.3444f, 0.62f, 0.69f);
	private final String[] skyTexture = {null, null, null, 	null, null, null};
	private Texture glTexture;
	
	// called when the game loads
	public abstract void onCreate();
	
	// called when this display is switched to
	public abstract void onSwitch();
	
	// called on every render update
	public abstract void render();
	
	// called on every physics update
	public abstract void update();
	
	// called when this display is left
	public abstract void onLeave();
	
	// called when the game quits
	public abstract void onDestory();
	
	public Vector4f[] getSkyColors() {
		this.skyStorage[0].x = this.skyColor1.x;
		this.skyStorage[0].y = this.skyColor1.y;
		this.skyStorage[0].z = this.skyColor1.z;
		this.skyStorage[1].x = this.skyColor2.x;
		this.skyStorage[1].y = this.skyColor2.y;
		this.skyStorage[1].z = this.skyColor2.z;
		return this.skyStorage;
	}
	
	public IDisplay setSkyColor(float r, float g, float b) {
		this.skyColor1.x = r;
		this.skyColor1.y = g;
		this.skyColor1.z = b;
		this.skyColor2.x = r;
		this.skyColor2.y = g;
		this.skyColor2.z = b;
		return this;
	}
	
	public boolean usingSkyTexture() {
		return this.skyTexture[0] != null;
	}
	
	public String[] getSkyTextures() {
		return this.skyTexture;
	}
	
	public Texture getSkyTexture() {
		return glTexture;
	}
	
	public void setSkyTextures(String right, String left, String top, String bottom, String back, String front) {
		this.skyTexture[0] = right;
		this.skyTexture[1] = left;
		this.skyTexture[2] = top;
		this.skyTexture[3] = bottom;
		this.skyTexture[4] = back;
		this.skyTexture[5] = front;
		loadSkyTexture();
	}
	
	public void loadSkyTexture() {
		if (!usingSkyTexture())
			return;
		TextureData[] textures = new TextureData[this.skyTexture.length];
		for (int i = 0; i < this.skyTexture.length; i++) {
			textures[i] = TextureLoader.decodeTextureToSize(this.skyTexture[i], false, false, 0, 0);
		}
		
		glTexture = TextureLoader.loadCubeMap(textures);
	}
	
	public IDisplay setSkyColor(float r, float g, float b, float r2, float b2, float g2) {
		this.skyColor1.x = r;
		this.skyColor1.y = g;
		this.skyColor1.z = b;
		this.skyColor2.x = r2;
		this.skyColor2.y = g2;
		this.skyColor2.z = b2;
		this.skyTexture[0] = null;
		glTexture = null;
		return this;
	}
	
	public float getSky1R() {
		return skyColor1.x;
	}
	
	public float getSky1G() {
		return skyColor1.y;
	}
	
	public float getSky1B() {
		return skyColor1.z;
	}
	
	public float getSky2R() {
		return skyColor2.x;
	}
	
	public float getSky2G() {
		return skyColor2.y;
	}
	
	public float getSky2B() {
		return skyColor2.z;
	}
	
}
