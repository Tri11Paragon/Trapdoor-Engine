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
CMAKE_BINARY_DIR = "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug"

# Include any dependencies generated for this target.
include depends/TUtils/CMakeFiles/TUtils.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include depends/TUtils/CMakeFiles/TUtils.dir/compiler_depend.make

# Include the progress variables for this target.
include depends/TUtils/CMakeFiles/TUtils.dir/progress.make

# Include the compile flags for this target's objects.
include depends/TUtils/CMakeFiles/TUtils.dir/flags.make

depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o: depends/TUtils/CMakeFiles/TUtils.dir/flags.make
depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o: ../depends/TUtils/src/library.cpp
depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o: depends/TUtils/CMakeFiles/TUtils.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o -MF CMakeFiles/TUtils.dir/src/library.cpp.o.d -o CMakeFiles/TUtils.dir/src/library.cpp.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/TUtils/src/library.cpp"

depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/TUtils.dir/src/library.cpp.i"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/TUtils/src/library.cpp" > CMakeFiles/TUtils.dir/src/library.cpp.i

depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/TUtils.dir/src/library.cpp.s"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/TUtils/src/library.cpp" -o CMakeFiles/TUtils.dir/src/library.cpp.s

# Object files for target TUtils
TUtils_OBJECTS = \
"CMakeFiles/TUtils.dir/src/library.cpp.o"

# External object files for target TUtils
TUtils_EXTERNAL_OBJECTS =

depends/TUtils/libTUtils.a: depends/TUtils/CMakeFiles/TUtils.dir/src/library.cpp.o
depends/TUtils/libTUtils.a: depends/TUtils/CMakeFiles/TUtils.dir/build.make
depends/TUtils/libTUtils.a: depends/TUtils/CMakeFiles/TUtils.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX static library libTUtils.a"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && $(CMAKE_COMMAND) -P CMakeFiles/TUtils.dir/cmake_clean_target.cmake
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/TUtils.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
depends/TUtils/CMakeFiles/TUtils.dir/build: depends/TUtils/libTUtils.a
.PHONY : depends/TUtils/CMakeFiles/TUtils.dir/build

depends/TUtils/CMakeFiles/TUtils.dir/clean:
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" && $(CMAKE_COMMAND) -P CMakeFiles/TUtils.dir/cmake_clean.cmake
.PHONY : depends/TUtils/CMakeFiles/TUtils.dir/clean

depends/TUtils/CMakeFiles/TUtils.dir/depend:
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/TUtils" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/TUtils/CMakeFiles/TUtils.dir/DependInfo.cmake" --color=$(COLOR)
.PHONY : depends/TUtils/CMakeFiles/TUtils.dir/depend

