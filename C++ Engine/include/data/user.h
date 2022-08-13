//
// Created by brett on 13/08/22.
//

#ifndef ENGINE_USER_H
#define ENGINE_USER_H

#include <iostream>
#include <string>

#ifdef _POSIX_
#include <unistd.h>
#include <pwd.h>
#endif

#ifdef _WIN32
#include <Windows.h>
#endif

#endif //ENGINE_USER_H
