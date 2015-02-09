package com.suse.saltstack.netapi.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mocked InputStream that throws IOException when closed more than once.
 */
public class MockedInputStream extends InputStream {
    private boolean closed = false;

    boolean isClosed() {
        return closed;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("Stream already closed");
        }
        closed = true;
    }
}

