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
public class AopHttpRequestEntity extends AopHttpEntity {

    private static final String SUB_TAG = "AopHttpRequestEntity";

    public AopHttpRequestEntity(HttpEntity e, NetInfo data) {
        super(e, data);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...oncreat: " + e.getContentLength());
        }
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...writeTo()");
        }
        try {
            AopOutputStream ou = new AopOutputStream(outstream);
            ou.setStreamCompleteListener(this);
            mHttpEntity.writeTo(ou);
            mData.setSendBytes(ou.getCount());
        } catch (IOException e) {
            throw (e);
        }
    }

    @Override
    public void onOutputstreamComplete(long size) {
        this.mData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...onOutstreamComplete: " + this.mData.toString());
        }
        mData.end();
    }

    @Override
    public void onOutputstreamError(long size) {
        this.mData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...onOutstreamError: " + this.mData.toString());
        }
        mData.end();
    }


    @Override
    public void onInputstreamComplete(long size) {
        this.mData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...onInputstreamComplete: " + size);
        }
        mData.end();
    }

    @Override
    public void onInputstreamError(long size) {
        this.mData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpRequestEntity...onInputstreamError: " + size);
        }
        mData.end();
    }
}
