//
// Created by brett on 13/08/22.
//

#ifndef ENGINE_USER_H
#define ENGINE_USER_H

#include <iostream>
#include <string>

#ifdef linux
    #include <unistd.h>
    #include <pwd.h>
    #include <sys/types.h>
#endif

#ifdef _WIN32
    #include <Windows.h>
#endif

namespace TD {
    std::string getUserName();
    std::string getUserHome();
}

#endif //ENGINE_USER_H
