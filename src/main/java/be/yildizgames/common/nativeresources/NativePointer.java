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

package be.yildizgames.common.nativeresources;

/**
 * Wrapper class for an address pointer in native code.
 *
 * @author Grégory Van den Borre
 */
public final class NativePointer {

    /**
     * Address to the native object pointer.
     */
    private final long address;

    /**
     * Flag to check if the pointer is deleted.
     */
    private boolean deleted;


    /**
     * Full constructor, initialize The pointer address.
     *
     * @param pointerAddress Address to the native object.
     */
    private NativePointer(final long pointerAddress) {
        super();
        this.address = pointerAddress;
    }

    /**
     * Construction method, build a new NativePointer from an address.
     *
     * @param address Pointer address.
     * @return The built NativePointer.
     */
    public static NativePointer create(final long address) {
        return new NativePointer(address);
    }

    /**
     * Ensure to call this when the pointer is deleted native side.
     */
    public void delete() {
        this.deleted = true;
    }

    /**
     * Provide the address.
     * @return The address value.
     * @throws IllegalArgumentException if the pointer is deleted.
     */
    public long getPointerAddress() {
        if(this.deleted) {
            throw new IllegalArgumentException("The pointer is deleted.");
        }
        return this.address;
    }

    /**
     * Check if the pointer is deleted.
     * @return True if the pointer is deleted, false otherwise.
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * @param other Other object to test.
     * @return True if the other object is not null, is a NativePointer and have
     * the same pointer address.
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof NativePointer) {
            return this.address == ((NativePointer) other).address;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) this.address;
    }

    /**
     * @return The description of the native address value.
     */
    @Override
    public String toString() {
        return String.valueOf(this.address);
    }
}
