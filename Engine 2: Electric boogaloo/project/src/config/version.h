//
// Created by brett on 2022-01-31.
//

#ifndef TRAPDOOR_VERSION_H

    #include <iostream>
    #include "config/config.h"

    void printMajor(){
        std::cout << TRAPDOOR_VERSION_MAJOR;
    }
    void printMinor(){
        std::cout << TRAPDOOR_VERSION_MINOR;
    }
    void printPatch(){
        std::cout << TRAPDOOR_VERSION_PATCH;
    }

    void printClean(){
        printMajor();
        std::cout << ".";
        printMinor();
        std::cout << ".";
        printPatch();
        std::cout << std::endl;
    }

    void printAll() {
        std::cout << "Version: ";
        printClean();
    }

    void printAllLn() {
        std::cout << "Version: \n";
        std::cout << "\tMajor: ";
        printMajor();
        std::cout << "\n\tMinor: ";
        printMinor();
        std::cout << "\n\tPatch: ";
        printPatch();
        std::cout << std::endl;
    }

    #define TRAPDOOR_VERSION_H

#endif //TRAPDOOR_VERSION_H
