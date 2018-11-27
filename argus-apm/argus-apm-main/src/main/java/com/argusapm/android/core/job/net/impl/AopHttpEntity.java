
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.core.job.net.i.IStreamCompleteListener;
import com.argusapm.android.utils.LogX;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopHttpEntity implements HttpEntity, IStreamCompleteListener {

    private static final String SUB_TAG = "AopHttpEntity";
    protected final HttpEntity mHttpEntity;

    protected NetInfo mData;

    public AopHttpEntity(HttpEntity e, NetInfo data) {
        mHttpEntity = e;
        this.mData = data;
    }

    @Override
    public boolean isRepeatable() {
        return mHttpEntity.isRepeatable();
    }

    @Override
    public boolean isChunked() {
        return mHttpEntity.isChunked();
    }

    @Override
    public long getContentLength() {
        return mHttpEntity.getContentLength();
    }

    @Override
    public Header getContentType() {
        return mHttpEntity.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return mHttpEntity.getContentEncoding();
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContent()");
        }
        try {
            AopInputStream in = new AopInputStream(mHttpEntity.getContent());
            in.setStreamCompleteListener(this);
            return in;
        } catch (IOException e) {
            // TODO:
            throw (e);
        } catch (IllegalStateException e) {
            // TODO:
            throw (e);
        }
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "writeTo()");
        }
    }

    @Override
    public boolean isStreaming() {
        return mHttpEntity.isStreaming();
    }

    @Override
    public void consumeContent() throws IOException {
        try {
            this.mHttpEntity.consumeContent();
        } catch (IOException e) {
            throw (e);
        }
    }

    @Override
    public void onInputstreamComplete(long size) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "onInputstreamComplete: " + size);
        }
    }

    @Override
    public void onOutputstreamComplete(long size) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "onOutputstreamComplete: " + size);
        }
    }

    @Override
    public void onInputstreamError(long size) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "onInputstreamError: " + size);
        }
    }

    @Override
    public void onOutputstreamError(long size) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "onOutputstreamError: " + size);
        }
    }
}
