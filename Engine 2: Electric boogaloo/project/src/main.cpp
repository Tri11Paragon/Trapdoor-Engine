
#include <iostream>
#include "config/version.h"
#include "display/DisplayManager.h"

int main(){

    std::cout << "Loading " << TRAPDOOR_NAME << " Engine v";
    printClean();

    createDisplay();

    while (!isCloseRequested()){
        updateDisplay();
    }

    closeDisplay();

}