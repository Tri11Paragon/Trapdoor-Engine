package com.trapdoor.engine.world.entities.components;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import com.trapdoor.engine.datatypes.sound.SoundFile;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Feb. 11, 2022
 * 
 */
public class SoundSource extends IComponent {

	private Transform entityTransform;
	private final int sourceId;

    public SoundSource() {
    	this(false, true);
    }
    
    public SoundSource(boolean loop, boolean relative) {
        this.sourceId = AL10.alGenSources();

        if (loop) {
        	AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
        }
        if (relative) {
        	AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
        }
    }

    public SoundSource setSound(SoundFile file) {
        stop();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, file.getBufferId());
        return this;
    }

    public SoundSource setPosition(Vector3f position) {
    	setPosition(position.x, position.y, position.z);
    	return this;
    }
    
    public SoundSource setPosition(float x, float y, float z) {
    	AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
    	return this;
    }

    public SoundSource setSpeed(Vector3f speed) {
    	setSpeed(speed.x, speed.y, speed.z);
    	return this;
    }
    
    public SoundSource setSpeed(float x, float y, float z) {
    	AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
    	return this;
    }

    public void setGain(float gain) {
    	AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
    	AL10.alSourcef(sourceId, param, value);
    }
    
    public void play() {
    	AL10.alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void pause() {
        AL10.alSourcePause(sourceId);
    }

    public void stop() {
    	AL10.alSourceStop(sourceId);
    }

    public void cleanup() {
        stop();
        AL10.alDeleteSources(sourceId);
    }
	
	@Override
	public void render() {
	}

	@Override
	public void update() {
		if (this.entityTransform != null)
			this.setPosition(this.entityTransform.getX(), this.entityTransform.getY(), this.entityTransform.getZ());
	}
	
	@Override
	public void setAssociatedEntity(Entity e) {
		super.setAssociatedEntity(e);
		this.entityTransform = (Transform) e.getComponent(Transform.class);
	}

}
