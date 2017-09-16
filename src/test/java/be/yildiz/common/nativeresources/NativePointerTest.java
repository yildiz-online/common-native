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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for NativePointer.
 *
 * @author Grégory Van den Borre
 */
final class NativePointerTest {

    /***/
    @Test
    void testEquals() {
        NativePointer p1 = NativePointer.create(1);
        NativePointer p2 = NativePointer.create(1);
        assertEquals(p1, p2);
        assertEquals(p1, p1);
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(1));
        assertFalse(p1.equals(NativePointer.create(2)));
    }

    /***/
    @Test
    void testHashcode() {
        int value = 456;
        NativePointer p1 = NativePointer.create(value);
        assertEquals(value, p1.hashCode());
    }

    /***/
    @Test
    void testNewPointer() {
        long value = 458L;
        NativePointer p = NativePointer.create(value);
        assertEquals(value, p.getPointerAddress());
    }

    /***/
    @Test
    void testToString() {
        int value = 456;
        NativePointer p1 = NativePointer.create(value);
        assertEquals(String.valueOf(value), p1.toString());
    }

    @Test
    void testDelete() {
        NativePointer p = NativePointer.create(10L);
        p.delete();
        assertThrows(IllegalArgumentException.class, p::getPointerAddress);
    }

    @Test
    void testIsDeleted() {
        NativePointer p = NativePointer.create(10L);
        assertFalse(p.isDeleted());
        p.delete();
        assertTrue(p.isDeleted());
    }

}
