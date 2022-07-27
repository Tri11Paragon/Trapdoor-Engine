//
// Created by brett on 27/07/22.
//

#include "util.h"
#include <random>
#include "clock.h"

namespace TD {

    random::random(int min, int max): random((long) (691 * TD::getFrameTimeMilliseconds()), min, max) {}

    random::random(long seed, int min, int max) {
        this->gen = std::mt19937(rd());
        this->gen.seed(seed);
        this->intDistr = std::uniform_int_distribution<int>(min, max);
        this->longDistr = std::uniform_int_distribution<long>(min, max); // define the range
        this->floatDistr = std::uniform_real_distribution<float>(min, max);
        this->doubleDistr = std::uniform_real_distribution<double>(min, max);
    }

} // TD