/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2018 Grégory Van den Borre
 *
 *  More infos available: https://www.yildiz-games.be
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 *
 */

package be.yildizgames.common.nativeresources;

import be.yildizgames.common.nativeresources.systems.SystemLinux64;
import be.yildizgames.common.nativeresources.systems.SystemWin64;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Grégory Van den Borre
 */
class NativeResourceLoaderTest {

    @Nested
    class GetLibPath {

        private final NativeOperatingSystem[] systems = {new SystemLinux64(), new SystemWin64()};

        @Test
        void withExistingFileWithExtension() throws IOException {
            Path folder = Files.createTempDirectory("test");
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.toAbsolutePath().toFile().getAbsolutePath(), this.systems);
            File f = getFile("lib_out" + nrl.libraryExtension);
            assertEquals(f.getAbsolutePath(), nrl.getLibPath(f.getAbsolutePath()));
            Files.delete(folder);
        }

        @Test
        void withExistingFileWithoutExtension() throws IOException {
            Path folder = Files.createTempDirectory("test");
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.toAbsolutePath().toFile().getAbsolutePath(), this.systems);
            File f = getFile("lib_out" + nrl.libraryExtension);
            assertEquals(f.getAbsolutePath(), nrl.getLibPath(f.getAbsolutePath().replace(nrl.libraryExtension, "")));
            Files.delete(folder);
        }

        @Test
        void withNotExistingFileNotRegistered() throws IOException {
            Path folder = Files.createTempDirectory("test");
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.toAbsolutePath().toFile().getAbsolutePath(), this.systems);
            assertThrows(AssertionError.class, () -> nrl.getLibPath("lib"));
            Files.delete(folder);
        }

        @Test
        void withNullFilePath() throws IOException {
            Path folder = Files.createTempDirectory("test");
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.toAbsolutePath().toFile().getAbsolutePath(), this.systems);
            assertThrows(AssertionError.class, () -> nrl.getLibPath(null));
            Files.delete(folder);
        }
    }

    @Nested
    class LoadLibrary {

        private final NativeOperatingSystem[] systems = {new SystemLinux64(), new SystemWin64()};

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.toAbsolutePath().toFile().getAbsolutePath(), this.systems);

            String[] libs = new String[]{getFile("lib_one" + nrl.libraryExtension).getAbsolutePath(),
                getFile("lib_two" + nrl.libraryExtension).getAbsolutePath(),
                getFile("lib_three" + nrl.libraryExtension).getAbsolutePath()};

            assertThrows(UnsatisfiedLinkError.class, () -> nrl.loadLibrary(libs));
            Files.delete(folder);
        }
    }
    
    private static File getFile(String name) {
        return new File(NativeResourceLoader.class.getClassLoader().getResource(name).getFile()).getAbsoluteFile();
    }
}
