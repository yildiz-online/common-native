cmake . -DCMAKE_BUILD_TYPE=Release -DCMAKE_COLOR_MAKEFILE=on -DLIBRARY_OUTPUT_PATH="../../../../target/classes/win32" -DCMAKE_TOOLCHAIN_FILE=mingw-toolchain.cmake

make

rm -R CMakeFiles
rm CMakeCache.txt
rm cmake_install.cmake
rm Makefile

cp physfs/bin/libphysfs.dll ../../../../target/classes/win32/libphysfs.dll
cp libstdc++-6.dll ../../../../target/classes/win32/libstdc++-6.dll
cp libgcc_s_sjlj-1.dll ../../../../target/classes/win32/libgcc_s_sjlj-1.dll