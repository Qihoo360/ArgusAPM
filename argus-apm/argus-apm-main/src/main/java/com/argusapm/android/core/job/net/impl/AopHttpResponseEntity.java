package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.utils.LogX;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.OutputStream;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopHttpResponseEntity extends AopHttpEntity {

    private static final String SUB_TAG = "AopHttpResponseEntity";

    public AopHttpResponseEntity(HttpEntity e, NetInfo data) {
        super(e, data);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...oncreat: " + e.getContentLength());
        }
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...writeTo()");
        }
        try {
            AopOutputStream ou = new AopOutputStream(outstream);
            ou.setStreamCompleteListener(this);
            mHttpEntity.writeTo(ou);
        } catch (IOException e) {
            throw (e);
        }
    }

    @Override
    public void onOutputstreamComplete(long size) {
        this.mData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...onOutstreamComplete: " + this.mData.toString());
        }
        mData.end();
    }

    @Override
    public void onOutputstreamError(long size) {
        this.mData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...onOUtstreamError: " + this.mData.toString());
        }
        mData.end();
    }

    @Override
    public void onInputstreamComplete(long size) {
        this.mData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...onInputstreamComplete: " + size);
        }
        mData.end();

    }

    @Override
    public void onInputstreamError(long size) {
        this.mData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpResponseEntity...onInputstreamError: " + size);
        }
        mData.end();
    }
}
