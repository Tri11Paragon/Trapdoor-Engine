# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.21

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /home/laptop/Documents/clion/clion-2021.3.3/bin/cmake/linux/bin/cmake

# The command to remove a file.
RM = /home/laptop/Documents/clion/clion-2021.3.3/bin/cmake/linux/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project"

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release"

# Include any dependencies generated for this target.
include CMakeFiles/Trapdoor.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include CMakeFiles/Trapdoor.dir/compiler_depend.make

# Include the progress variables for this target.
include CMakeFiles/Trapdoor.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/Trapdoor.dir/flags.make

CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o: ../src/display/Camera.cpp
CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o -MF CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Camera.cpp"

CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Camera.cpp" > CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.i

CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Camera.cpp" -o CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.s

CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o: ../src/display/DisplayManager.cpp
CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o -MF CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/DisplayManager.cpp"

CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/DisplayManager.cpp" > CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.i

CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/DisplayManager.cpp" -o CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.s

CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o: ../src/display/Scene.cpp
CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o -MF CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Scene.cpp"

CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Scene.cpp" > CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.i

CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/display/Scene.cpp" -o CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.s

CMakeFiles/Trapdoor.dir/src/main.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/main.cpp.o: ../src/main.cpp
CMakeFiles/Trapdoor.dir/src/main.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object CMakeFiles/Trapdoor.dir/src/main.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/main.cpp.o -MF CMakeFiles/Trapdoor.dir/src/main.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/main.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/main.cpp"

CMakeFiles/Trapdoor.dir/src/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/main.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/main.cpp" > CMakeFiles/Trapdoor.dir/src/main.cpp.i

CMakeFiles/Trapdoor.dir/src/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/main.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/main.cpp" -o CMakeFiles/Trapdoor.dir/src/main.cpp.s

CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o: ../src/tools/data/ArrayList.cpp
CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o -MF CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/data/ArrayList.cpp"

CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/data/ArrayList.cpp" > CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.i

CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/data/ArrayList.cpp" -o CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.s

CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o: CMakeFiles/Trapdoor.dir/flags.make
CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o: ../src/tools/maths.cpp
CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o: CMakeFiles/Trapdoor.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_6) "Building CXX object CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o -MF CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o.d -o CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/maths.cpp"

CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/maths.cpp" > CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.i

CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/src/tools/maths.cpp" -o CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.s

# Object files for target Trapdoor
Trapdoor_OBJECTS = \
"CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o" \
"CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o" \
"CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o" \
"CMakeFiles/Trapdoor.dir/src/main.cpp.o" \
"CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o" \
"CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o"

# External object files for target Trapdoor
Trapdoor_EXTERNAL_OBJECTS =

Trapdoor: CMakeFiles/Trapdoor.dir/src/display/Camera.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/src/display/DisplayManager.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/src/display/Scene.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/src/main.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/src/tools/data/ArrayList.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/src/tools/maths.cpp.o
Trapdoor: CMakeFiles/Trapdoor.dir/build.make
Trapdoor: /usr/lib/x86_64-linux-gnu/libglfw.so.3.3
Trapdoor: depends/glad/libglad.a
Trapdoor: /usr/lib/x86_64-linux-gnu/libGLX.so
Trapdoor: /usr/lib/x86_64-linux-gnu/libOpenGL.so
Trapdoor: CMakeFiles/Trapdoor.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_7) "Linking CXX executable Trapdoor"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/Trapdoor.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/Trapdoor.dir/build: Trapdoor
.PHONY : CMakeFiles/Trapdoor.dir/build

CMakeFiles/Trapdoor.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/Trapdoor.dir/cmake_clean.cmake
.PHONY : CMakeFiles/Trapdoor.dir/clean

CMakeFiles/Trapdoor.dir/depend:
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles/Trapdoor.dir/DependInfo.cmake" --color=$(COLOR)
.PHONY : CMakeFiles/Trapdoor.dir/depend

