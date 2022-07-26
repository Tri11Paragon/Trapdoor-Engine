# Install script for directory: /home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/include/bullet3/Extras/BulletRoboticsGUI

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "1")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

# Set default install directory permissions.
if(NOT DEFINED CMAKE_OBJDUMP)
  set(CMAKE_OBJDUMP "/usr/bin/objdump")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25")
    file(RPATH_CHECK
         FILE "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25"
         RPATH "")
  endif()
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE SHARED_LIBRARY FILES "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/BulletRoboticsGUI/libBulletRoboticsGUId.so.3.25")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25")
    file(RPATH_CHANGE
         FILE "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25"
         OLD_RPATH "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ExampleBrowser:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/BulletRobotics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/InverseDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/Serialize/BulletWorldImporter:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/Serialize/BulletFileLoader:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletSoftBody:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletCollision:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletInverseDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/LinearMath:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/OpenGLWindow:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ThirdPartyLibs/Gwen:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ThirdPartyLibs/BussIK:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/Bullet3Common:"
         NEW_RPATH "")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/usr/bin/strip" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so.3.25")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so")
    file(RPATH_CHECK
         FILE "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so"
         RPATH "")
  endif()
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE SHARED_LIBRARY FILES "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/BulletRoboticsGUI/libBulletRoboticsGUId.so")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so")
    file(RPATH_CHANGE
         FILE "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so"
         OLD_RPATH "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ExampleBrowser:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/BulletRobotics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/InverseDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/Serialize/BulletWorldImporter:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/Serialize/BulletFileLoader:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletSoftBody:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletCollision:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/BulletInverseDynamics:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/LinearMath:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/OpenGLWindow:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ThirdPartyLibs/Gwen:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/examples/ThirdPartyLibs/BussIK:/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/src/Bullet3Common:"
         NEW_RPATH "")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/usr/bin/strip" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libBulletRoboticsGUId.so")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/pkgconfig" TYPE FILE FILES "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/C++ Engine/build/include/bullet3/Extras/BulletRoboticsGUI/bullet_robotics_gui.pc")
endif()

