package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.i.IStreamCompleteListener;
import com.argusapm.android.utils.LogX;

import java.io.IOException;
import java.io.OutputStream;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopOutputStream extends OutputStream {

    private final String SUB_TAG = "AopOutputStream";
    private final OutputStream mOutputStream;

    private long mSize = 0L;

    private IStreamCompleteListener myListener = null;

    public AopOutputStream(OutputStream paramOutputStream) {
        this.mOutputStream = paramOutputStream;
    }

    public void setStreamCompleteListener(IStreamCompleteListener l) {
        myListener = l;
    }

    public void removeStreamCompleteListener() {
        myListener = null;
    }

    private void onStreamComplete() {
        if (myListener != null) {
            myListener.onOutputstreamComplete(mSize);
        }
    }

    private void onStreamError() {
        if (myListener != null) {
            myListener.onOutputstreamError(mSize);
        }
    }

    public long getCount() {
        return this.mSize;
    }

    @Override
    public void write(int oneBytet) throws IOException {
        try {
            this.mOutputStream.write(oneBytet);
            this.mSize += 1;
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "write one Byte: " + mSize);
            }
        } catch (IOException localIOException) {
            throw localIOException;
        }
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        try {
            this.mOutputStream.write(buffer);
            this.mSize += buffer.length;
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "write to buffer: " + mSize + " , " + buffer.length);
            }
        } catch (IOException localIOException) {
            throw localIOException;
        }
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        try {
            this.mOutputStream.write(buffer, offset, count);
            this.mSize += count;
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "write to buffer: " + mSize + " , " + offset + " , " + count);
            }
        } catch (IOException localIOException) {
            throw localIOException;
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            this.mOutputStream.flush();
        } catch (IOException localIOException) {
            throw localIOException;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.mOutputStream.close();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "close: " + mSize);
            }
            onStreamComplete();
        } catch (IOException localIOException) {
            onStreamError();
            throw localIOException;
        }
    }
}
