
#include <iostream>
#include "config/version.h"
#include "display/DisplayManager.h"
#include "tools/data/ArrayList.h"

int main(){

    std::cout << "Loading " << TRAPDOOR_NAME << " Engine v";
    printClean();

    ArrayList<int> arrayList;
    arrayList.add(340);

    //createDisplay();

    //while (!isCloseRequested()){
        //updateDisplay();
    //}

    //closeDisplay();

}