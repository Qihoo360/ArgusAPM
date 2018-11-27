package com.argusapm.android.core.job.net.i;

/**
 * @author ArgusAPM Team
 */
public interface IStreamCompleteListener {

    void onInputstreamComplete(long size);

    void onOutputstreamComplete(long size);

    void onInputstreamError(long size);

    void onOutputstreamError(long size);
}
