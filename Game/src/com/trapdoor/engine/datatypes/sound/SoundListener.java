package com.trapdoor.engine.datatypes.sound;

import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

/**
 * @author laptop
 * @date Feb. 11, 2022
 * 
 */
public class SoundListener {

    public SoundListener() {
        this(0,0,0);
    }

    public SoundListener(Vector3f position) {
    	AL11.alListener3f(AL11.AL_POSITION, position.x, position.y, position.z);
    	AL11.alListener3f(AL11.AL_VELOCITY, 0, 0, 0);
    }
    
    public SoundListener(float x, float y, float z) {
    	AL11.alListener3f(AL11.AL_POSITION, x, y, z);
    	AL11.alListener3f(AL11.AL_VELOCITY, 0, 0, 0);
    }
    
    public void setSpeed(Vector3f speed) {
    	AL11.alListener3f(AL11.AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setPosition(Vector3f position) {
    	AL11.alListener3f(AL11.AL_POSITION, position.x, position.y, position.z);
    }
    
    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        AL11.alListenerfv(AL11.AL_ORIENTATION, data);
    }
}
