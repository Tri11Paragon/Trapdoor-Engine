# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.19

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
CMAKE_COMMAND = /opt/jetbrains/clion/bin/cmake/linux/bin/cmake

# The command to remove a file.
RM = /opt/jetbrains/clion/bin/cmake/linux/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project"

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug"

# Utility rule file for glad-generate-files.

# Include the progress variables for this target.
include depends/glad/CMakeFiles/glad-generate-files.dir/progress.make

depends/glad/CMakeFiles/glad-generate-files: depends/glad/include/glad/glad.h
depends/glad/CMakeFiles/glad-generate-files: depends/glad/src/glad.c


depends/glad/include/glad/glad.h:
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold --progress-dir="/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Generating GLAD"
	cd "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/glad" && /usr/bin/python3.10 -m glad --profile=compatibility --out-path=/home/brett/Documents/Brock/Gamers/Java\ Brock\ 2021\ Project/Engine\ 2:\ Electric\ boogaloo/project/cmake-build-debug/depends/glad --api= --generator=c --extensions= --spec=gl

depends/glad/src/glad.c: depends/glad/include/glad/glad.h
	@$(CMAKE_COMMAND) -E touch_nocreate depends/glad/src/glad.c

glad-generate-files: depends/glad/CMakeFiles/glad-generate-files
glad-generate-files: depends/glad/include/glad/glad.h
glad-generate-files: depends/glad/src/glad.c
glad-generate-files: depends/glad/CMakeFiles/glad-generate-files.dir/build.make

.PHONY : glad-generate-files

# Rule to build all files generated by this target.
depends/glad/CMakeFiles/glad-generate-files.dir/build: glad-generate-files

.PHONY : depends/glad/CMakeFiles/glad-generate-files.dir/build

depends/glad/CMakeFiles/glad-generate-files.dir/clean:
	cd "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/glad" && $(CMAKE_COMMAND) -P CMakeFiles/glad-generate-files.dir/cmake_clean.cmake
.PHONY : depends/glad/CMakeFiles/glad-generate-files.dir/clean

depends/glad/CMakeFiles/glad-generate-files.dir/depend:
	cd "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project" "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/glad" "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug" "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/glad" "/home/brett/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-debug/depends/glad/CMakeFiles/glad-generate-files.dir/DependInfo.cmake" --color=$(COLOR)
.PHONY : depends/glad/CMakeFiles/glad-generate-files.dir/depend

