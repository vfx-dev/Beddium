package com.ventooth.beddium.stubpackage.org.lwjgl.system;

import com.ventooth.beddium.stubpackage.org.lwjgl.PointerBuffer;

import java.nio.IntBuffer;

public class MemoryStack implements AutoCloseable {
    public static MemoryStack stackPush() {
        return null;
    }
    public IntBuffer callocInt(int size) {
        return null;
    }

    public PointerBuffer mallocPointer(int size) {
        return null;
    }

    @Override
    public void close() {

    }
}
