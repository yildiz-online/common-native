/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2017 Grégory Van den Borre
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

package be.yildiz.common.nativeresources;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * @author Grégory Van den Borre
 */
@RunWith(Enclosed.class)
public class NativeResourceLoaderTest {

    public static class GetLibPath {

        private final NativeOperatingSystem[] systems = {new SystemLinux64(), new SystemWin32(), new SystemWin64()};

        @Rule
        public final TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void withExistingFileWithExtension() throws IOException {
            NativeResourceLoader nrl = NativeResourceLoader.inPath(this.folder.newFolder().getAbsolutePath(), this.systems);
            File f = getFile("lib_out" + nrl.libraryExtension);
            Assert.assertEquals(f.getAbsolutePath(), nrl.getLibPath(f.getAbsolutePath()));
        }

        @Test
        public void withExistingFileWithoutExtension() throws IOException {
            NativeResourceLoader nrl = NativeResourceLoader.inPath(this.folder.newFolder().getAbsolutePath(), this.systems);
            File f = getFile("lib_out" + nrl.libraryExtension);
            Assert.assertEquals(f.getAbsolutePath(), nrl.getLibPath(f.getAbsolutePath().replace(nrl.libraryExtension, "")));
        }

        @Test(expected = AssertionError.class)
        public void withNotExistingFileNotRegistered() throws IOException {
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.newFolder().getAbsolutePath(), this.systems);
            Assert.assertEquals(null, nrl.getLibPath("lib"));
        }

        @Test(expected = AssertionError.class)
        public void withNullFilePath() throws IOException {
            NativeResourceLoader nrl = NativeResourceLoader.inPath(folder.newFolder().getAbsolutePath(), this.systems);
            nrl.getLibPath(null);
        }
    }
    
    public static class LoadLibrary {

        private final NativeOperatingSystem[] systems = {new SystemLinux64(), new SystemWin32(), new SystemWin64()};

        @Rule
        public final TemporaryFolder folder = new TemporaryFolder();

        @Test(expected = UnsatisfiedLinkError.class)
        public void happyFlow() throws IOException {
            NativeResourceLoader nrl = NativeResourceLoader.inPath(this.folder.newFolder().getAbsolutePath(), this.systems);

            String[] libs = new String[]{getFile("lib_one" + nrl.libraryExtension).getAbsolutePath(),
                getFile("lib_two" + nrl.libraryExtension).getAbsolutePath(),
                getFile("lib_three" + nrl.libraryExtension).getAbsolutePath()};

            nrl.loadLibrary(libs);
        }
    }
    
    private static File getFile(String name) {
        return new File(NativeResourceLoader.class.getClassLoader().getResource(name).getFile()).getAbsoluteFile();
    }
}
