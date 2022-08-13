//
// Created by brett on 27/07/22.
//

#ifndef ENGINE_UTIL_H
#define ENGINE_UTIL_H

#include <random>
#include "glm.h"

namespace TD {

    class random {
    private:
        std::random_device rd; // obtain a random number from hardware
        std::mt19937 gen; // seed the generator
        std::uniform_int_distribution<int> intDistr; // define the range
        std::uniform_int_distribution<long> longDistr; // define the range
        std::uniform_real_distribution<float> floatDistr;
        std::uniform_real_distribution<double> doubleDistr;
    public:
        random(long seed, int min, int max);
        random(int min, int max);
        inline int getInt() {
            return intDistr(gen);
        }

        inline long getLong() {
            return longDistr(gen);
        }

        inline float getFloat() {
            return floatDistr(gen);
        }

        inline float getDouble() {
            return doubleDistr(gen);
        }

        inline glm::vec2 getVec2() {
            return glm::vec2(getFloat(), getFloat());
        }

        inline glm::vec3 getVec3() {
            return glm::vec3(getFloat(), getFloat(), getFloat());
        }

        inline glm::vec4 getVec4() {
            return glm::vec4(getFloat(), getFloat(), getFloat(), getFloat());
        }
    };

} // TD

#endif //ENGINE_UTIL_H
