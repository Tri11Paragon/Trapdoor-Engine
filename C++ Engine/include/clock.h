//
// Created by brett on 23/07/22.
//

#ifndef ENGINE_CLOCK_H
#define ENGINE_CLOCK_H

namespace TD {
    extern double lastTime;
    extern double frameTimeMS, frameTimeS, fps;
    static double getFrameTimeSeconds(){
        return frameTimeS;
    }
    static double getFrameTimeMilliseconds(){
        return frameTimeMS;
    }
    static double getFPS(){
        return fps;
    }
    static void updateClock(double currentTime){
        double delta = currentTime - lastTime;
        lastTime = currentTime;
        frameTimeMS = delta;
        frameTimeS = delta / 1000.0;
        fps = 1000.0 / frameTimeMS;
    }
}

#endif //ENGINE_CLOCK_H
