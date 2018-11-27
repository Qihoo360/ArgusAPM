
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.utils.LogX;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopResponseHandler<T> implements ResponseHandler<T> {

    private final String SUB_TAG = "AopResponseHandler";
    private final ResponseHandler<T> myHandler;

    private final NetInfo myData;

    private AopResponseHandler(ResponseHandler<T> responseHandler, NetInfo data) {
        this.myHandler = responseHandler;
        this.myData = data;
    }

    @Override
    public T handleResponse(HttpResponse response) throws IOException {
        myData.setStatusCode(response.getStatusLine().getStatusCode());
        Header[] headers = response.getHeaders("Content-Length");
        if ((headers != null) && (headers.length > 0)) {
            try {
                long l = Long.parseLong(headers[0].getValue());
                myData.setReceivedBytes(l);
                if (DEBUG) {
                    LogX.d(TAG, SUB_TAG, "-AopResponseHandler-------Response handler--end--1");
                }
                myData.end();
            } catch (NumberFormatException e) {

            }
        } else if (response.getEntity() != null) {
            response.setEntity(new AopHttpResponseEntity(response.getEntity(), myData));
        } else {
            myData.setReceivedBytes(0);
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "--AopResponseHandler------Response handler--end--2");
            }
            myData.end();
        }
        return myHandler.handleResponse(response);
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static <T> ResponseHandler<? extends T> wrap(ResponseHandler<? extends T> responseHandler, NetInfo data) {
        return new AopResponseHandler(responseHandler, data);
    }
}
