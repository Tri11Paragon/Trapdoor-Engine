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
include depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/compiler_depend.make

# Include the progress variables for this target.
include depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/progress.make

# Include the compile flags for this target's objects.
include depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/flags.make

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/flags.make
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o: ../depends/bullet3/examples/ThirdPartyLibs/clsocket/src/SimpleSocket.cpp
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o -MF CMakeFiles/clsocket.dir/src/SimpleSocket.o.d -o CMakeFiles/clsocket.dir/src/SimpleSocket.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/SimpleSocket.cpp"

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/clsocket.dir/src/SimpleSocket.i"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/SimpleSocket.cpp" > CMakeFiles/clsocket.dir/src/SimpleSocket.i

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/clsocket.dir/src/SimpleSocket.s"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/SimpleSocket.cpp" -o CMakeFiles/clsocket.dir/src/SimpleSocket.s

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/flags.make
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o: ../depends/bullet3/examples/ThirdPartyLibs/clsocket/src/ActiveSocket.cpp
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o -MF CMakeFiles/clsocket.dir/src/ActiveSocket.o.d -o CMakeFiles/clsocket.dir/src/ActiveSocket.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/ActiveSocket.cpp"

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/clsocket.dir/src/ActiveSocket.i"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/ActiveSocket.cpp" > CMakeFiles/clsocket.dir/src/ActiveSocket.i

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/clsocket.dir/src/ActiveSocket.s"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/ActiveSocket.cpp" -o CMakeFiles/clsocket.dir/src/ActiveSocket.s

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/flags.make
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o: ../depends/bullet3/examples/ThirdPartyLibs/clsocket/src/PassiveSocket.cpp
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o -MF CMakeFiles/clsocket.dir/src/PassiveSocket.o.d -o CMakeFiles/clsocket.dir/src/PassiveSocket.o -c "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/PassiveSocket.cpp"

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/clsocket.dir/src/PassiveSocket.i"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/PassiveSocket.cpp" > CMakeFiles/clsocket.dir/src/PassiveSocket.i

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/clsocket.dir/src/PassiveSocket.s"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket/src/PassiveSocket.cpp" -o CMakeFiles/clsocket.dir/src/PassiveSocket.s

# Object files for target clsocket
clsocket_OBJECTS = \
"CMakeFiles/clsocket.dir/src/SimpleSocket.o" \
"CMakeFiles/clsocket.dir/src/ActiveSocket.o" \
"CMakeFiles/clsocket.dir/src/PassiveSocket.o"

# External object files for target clsocket
clsocket_EXTERNAL_OBJECTS =

depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/SimpleSocket.o
depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/ActiveSocket.o
depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/src/PassiveSocket.o
depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/build.make
depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a: depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir="/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_4) "Linking CXX static library libclsocket.a"
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && $(CMAKE_COMMAND) -P CMakeFiles/clsocket.dir/cmake_clean_target.cmake
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/clsocket.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/build: depends/bullet3/examples/ThirdPartyLibs/clsocket/libclsocket.a
.PHONY : depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/build

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/clean:
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" && $(CMAKE_COMMAND) -P CMakeFiles/clsocket.dir/cmake_clean.cmake
.PHONY : depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/clean

depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/depend:
	cd "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/depends/bullet3/examples/ThirdPartyLibs/clsocket" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket" "/home/laptop/Documents/Brock/Gamers/Java Brock 2021 Project/Engine 2: Electric boogaloo/project/cmake-build-release/depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/DependInfo.cmake" --color=$(COLOR)
.PHONY : depends/bullet3/examples/ThirdPartyLibs/clsocket/CMakeFiles/clsocket.dir/depend

