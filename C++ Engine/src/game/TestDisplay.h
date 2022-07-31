//
// Created by brett on 31/07/22.
//

#ifndef ENGINE_TESTDISPLAY_H
#define ENGINE_TESTDISPLAY_H

#include "../window.h"

namespace TD {

    class TestDisplay : Display {
        TestDisplay(std::string name);
        virtual void onSwitch();
        virtual void render();
        virtual void update();
        virtual void onLeave();
        ~TestDisplay();
    };

} // TD

#endif //ENGINE_TESTDISPLAY_H
