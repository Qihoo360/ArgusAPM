
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.i.IStreamCompleteListener;
import com.argusapm.android.utils.LogX;

import java.io.IOException;
import java.io.InputStream;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopInputStream extends InputStream {
    private final String SUB_TAG = "AopInputStream";
    private final InputStream mInputStream;
    private long mSize = 0;
    private IStreamCompleteListener myListener = null;

    public AopInputStream(InputStream paramInputStream) {
        this.mInputStream = paramInputStream;
    }

    public void setStreamCompleteListener(IStreamCompleteListener l) {
        myListener = l;
    }

    public void removeStreamCompleteListener() {
        myListener = null;
    }

    private void onStreamComplete() {
        if (myListener != null) {
            myListener.onInputstreamComplete(mSize);
        }
    }

    private void onStreamError() {
        if (myListener != null) {
            myListener.onInputstreamError(mSize);
        }
    }

    @Override
    public int read() throws IOException {
        try {
            int l = this.mInputStream.read();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "read(): " + mSize + " , " + l);
            }
            if (l >= 0) {
                this.mSize += l;
            } else {
                onStreamComplete();
            }
            return l;
        } catch (IOException e) {
            onStreamError();
            throw (e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        try {
            int s = this.mInputStream.read(b, 0, b.length);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "read(byte[] b): " + mSize + " ,b.length: " + b.length + " ,s: " + s);
            }
            if (s >= 0) {
                this.mSize += s;
            } else {
                onStreamComplete();
            }
            return s;
        } catch (IOException e) {
            onStreamError();
            throw e;
        }
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        int i = 0;
        try {
            i = this.mInputStream.read(b, offset, length);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "read(): " + mSize + " ,offset: " + offset + " ,length: " + length + " , i: " + i);
            }
            if (i >= 0) {
                this.mSize += i;
            } else {
                onStreamComplete();
            }
            return i;
        } catch (IOException e) {
            onStreamError();
            throw e;
        }
    }

    @Override
    public long skip(long length) throws IOException {
        try {
            long l = this.mInputStream.skip(length);
            this.mSize += l;
            return l;
        } catch (IOException e) {
            onStreamError();
            throw e;
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return this.mInputStream.available();
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.mInputStream.close();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "close(): " + mSize);
            }
            onStreamComplete();
        } catch (IOException e) {
            onStreamError();
            throw e;
        }
    }

    @Override
    public void mark(int readlimit) {
        if (!markSupported()) {
            return;
        }
        this.mInputStream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return this.mInputStream.markSupported();
    }

    @Override
    public void reset() throws IOException {
        if (!markSupported()) {
            return;
        }
        try {
            this.mInputStream.reset();
        } catch (IOException e) {
            throw e;
        }
    }
}
