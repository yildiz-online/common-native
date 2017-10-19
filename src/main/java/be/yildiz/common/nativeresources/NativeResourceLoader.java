/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 * Copyright (c) 2017 Grégory Van den Borre
 *
 * More infos available: https://www.yildiz-games.be
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 */

package be.yildiz.common.nativeresources;

import be.yildiz.common.collections.Lists;
import be.yildiz.common.collections.Maps;
import be.yildiz.common.resource.ZipUtil;
import be.yildiz.common.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Utility class to load the native library from the classpath or a jar.
 *
 * @author Grégory Van den Borre
 */
public final class NativeResourceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeResourceLoader.class);

    /**
     * Directory containing the native libraries, can be win32, win34, linux32,
     * linux64 depending on the operating system and the underlying
     * architecture.
     */
    public final String directory;

    /**
     * Will contains the native libraries to be loaded.
     */
    public final File libDirectory;

    /**
     * Library file extension, can be .dll on windows, .so on linux.
     */
    public final String libraryExtension;

    /**
     * Contains the found native libraries and their full path.
     */
    private final Map<String, String> availableLib = Maps.newMap();

    private NativeResourceLoader(boolean decompress, NativeOperatingSystem... systemToSupport) {
        this(System.getProperty("user.home") + File.separator + "app-root" + File.separator + "data", decompress, systemToSupport);
    }

    private NativeResourceLoader(boolean decompress, Systems... systemToSupport) {
        this(decompress, Arrays.stream(systemToSupport).map(Systems::getSystem).toArray(NativeOperatingSystem[]::new));
    }

    private NativeResourceLoader(String path, boolean decompress, Systems... systemToSupport) {
        this(path, decompress, Arrays.stream(systemToSupport).map(Systems::getSystem).toArray(NativeOperatingSystem[]::new));
    }

    private NativeResourceLoader(String path, boolean decompress, NativeOperatingSystem... systemToSupport) {
        super();
        NativeOperatingSystem nos = this.findSystem(systemToSupport);
        this.libraryExtension = nos.getExtension();
        this.directory = nos.getName();
        this.libDirectory = new File(path);
        if (decompress) {
            Arrays.stream(System.getProperty("java.class.path", "").split(File.pathSeparator))
                    .filter(s -> s.endsWith(".jar"))
                    .map(File::new)
                    .filter(File::exists)
                    .forEach(app -> ZipUtil.extractFilesFromDirectory(app, this.directory, libDirectory.getAbsolutePath()));
        }
        this.registerLibInDir();
    }

    /**
     * Retrieve the libraries in the class pass, decompress them and register them.
     * @param systemToSupport The list of system to support.
     * @return The created loader.
     */
    public static NativeResourceLoader inJar(NativeOperatingSystem... systemToSupport) {
        return new NativeResourceLoader(true, systemToSupport);
    }

    public static NativeResourceLoader inJar(Systems... systemToSupport) {
        return new NativeResourceLoader(true, systemToSupport);
    }

    public static NativeResourceLoader inJar() {
        return new NativeResourceLoader(true, Systems.values());
    }

    /**
     * Retrieve the libraries in the class pass, decompress them in the provided path and register them.
     * @param path Directory where the libs will be copied.
     * @param systemToSupport The list of system to support.
     * @return The created loader.
     */
    public static NativeResourceLoader inJar(String path, NativeOperatingSystem... systemToSupport) {
        return new NativeResourceLoader(path, true, systemToSupport);
    }

    public static NativeResourceLoader inJar(String path, Systems... systemToSupport) {
        return new NativeResourceLoader(path, true, systemToSupport);
    }

    public static NativeResourceLoader inJar(String path) {
        return new NativeResourceLoader(path, true, Systems.values());
    }

    public static NativeResourceLoader inPath(String path, NativeOperatingSystem... systemToSupport) {
        return new NativeResourceLoader(path,false, systemToSupport);
    }

    public static NativeResourceLoader inPath(String path, Systems... systemToSupport) {
        return new NativeResourceLoader(path,false, systemToSupport);
    }

    public static NativeResourceLoader inTestPath(NativeOperatingSystem... systemToSupport) {
        return new NativeResourceLoader(new File("").getAbsolutePath() + "/target/classes",false, systemToSupport);
    }

    public static NativeResourceLoader inTestPath(Systems... systemToSupport) {
        return new NativeResourceLoader(new File("").getAbsolutePath() + "/target/classes",false, systemToSupport);
    }

    /**
     * Use libraries from a given path and register them.
     * @param  systemToSupport The list of system to support.
     * @return The created loader.
     */
    public static NativeResourceLoader external(NativeOperatingSystem... systemToSupport) {
        String path = new File("").getParentFile().getAbsolutePath();
        return new NativeResourceLoader(path, false, systemToSupport);
    }

    private NativeOperatingSystem findSystem(NativeOperatingSystem[] systemToSupport) {
        return Arrays
                .stream(systemToSupport)
                .filter(NativeOperatingSystem::getCondition)
                .findFirst()
                .orElseThrow(AssertionError::new);
    }

    /**
     * Give the full path of a registered native library.
     *
     * @param lib Library to check.
     * @return The absolute path of the given library.
     */
    public String getLibPath(final String lib) {
        if(lib == null) {
            throw new AssertionError("lib cannot be null.");
        }
        File f = new File(lib.endsWith(libraryExtension) ? lib : lib + libraryExtension);
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        String nativePath = this.availableLib.get(lib);
        if (nativePath == null) {
            nativePath = "/usr/lib/x86_64-linux-gnu/" + lib + ".so";
            if(!new File(nativePath).exists()) {
                if(!lib.startsWith("lib")) {
                    return getLibPath("lib" + lib);
                }
                throw new AssertionError(lib + " has not been found in path.");
            }
        }
        return nativePath;
    }

    /**
     * Load a native library, it will check if it is contained in a jar, if so,
     * the library will be extracted in a temporary place and loaded from there.
     *
     * @param libs Native library name to load.
     */
    public void loadLibrary(final String... libs) {
        String nativePath;
        for (String lib : libs) {
            LOGGER.debug("Loading native : " + lib);
            nativePath = getLibPath(lib);
            System.load(nativePath);
            LOGGER.debug(nativePath + " loaded.");
        }
    }

    /**
     * Register the found libraries in a directory to be ready to be loaded.
     *
     * @param dir Directory holding the libraries.
     */
    private void registerLibInDir(final File dir) {
        if (dir.exists() && dir.isDirectory()) {
            Optional
                    .ofNullable(dir.listFiles(p -> p.isFile() && p.getName().endsWith(this.libraryExtension)))
                    .ifPresent(files -> Arrays
                            .stream(files)
                            .forEach(f -> this.availableLib
                                    .put(f.getName().replace(this.libraryExtension, ""), f.getAbsolutePath())
                    ));
        }
    }

    private void registerLibInDir() {
        registerLibInDir(new File(libDirectory.getAbsolutePath() + File.separator + this.directory));
    }

    /**
     * To load the shared libraries, only used for windows, on linux, will not
     * load anything.
     *
     * @param libs Libraries to be loaded only on windows.
     */
    public void loadBaseLibrary(String... libs) {
        if (!Util.isLinux()) {
            if(Util.isX86()) {
                loadLibrary("libgcc_s_sjlj-1.dll", "libstdc++-6.dll");
            } else {
                loadLibrary("libgcc_s_seh-1.dll", "libstdc++-6.dll");
            }
            if(libs != null && libs.length > 0) {
                loadLibrary(libs);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getLoadedLibraries() {
        try {
            Field lib = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            lib.setAccessible(true);
            return Lists.newList(Vector.class.cast(lib.get(ClassLoader.getSystemClassLoader())));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
