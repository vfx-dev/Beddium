package com.ventooth.beddium.stubpackage.org.lwjgl.opengl;


import java.nio.IntBuffer;

public class GL32C {
    public static void glWaitSync(long sync, int flags, long timeout) {

    }
    public static void glDeleteSync(long sync) {

    }
    public static int glGetSynci(long sync, int pname, IntBuffer length) {
        return 0;
    }
    public static void nglMultiDrawElementsBaseVertex(int mode, long count, int type, long indices, int drawcount, long basevertex) {

    }
    /** Accepted as the {@code pname} parameter of GetSynciv. */
    public static final int
            GL_OBJECT_TYPE    = 0x9112,
            GL_SYNC_CONDITION = 0x9113,
            GL_SYNC_STATUS    = 0x9114,
            GL_SYNC_FLAGS     = 0x9115;
    /** Accepted in the {@code flags} parameter of ClientWaitSync. */
    public static final int GL_SYNC_FLUSH_COMMANDS_BIT = 0x1;
    /** Returned in {@code values} for GetSynciv {@code pname} SYNC_STATUS. */
    public static final int
            GL_UNSIGNALED = 0x9118,
            GL_SIGNALED   = 0x9119;
}
