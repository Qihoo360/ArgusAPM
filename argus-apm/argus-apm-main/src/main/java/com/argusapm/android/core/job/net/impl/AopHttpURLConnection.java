
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.core.job.net.i.IStreamCompleteListener;
import com.argusapm.android.utils.LogX;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopHttpURLConnection extends HttpURLConnection implements IStreamCompleteListener {

    private static final String SUB_TAG = "AopHttpURLConnection";
    private final HttpURLConnection myConnection;

    private NetInfo myData;

    public AopHttpURLConnection(HttpURLConnection con) {
        super(con.getURL());
        myConnection = con;
    }

    private void inspectAndInstrumentResponse(NetInfo data, HttpURLConnection concection) {
        int len = concection.getContentLength();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "inspectAndInstrumentResponse --len: " + len);
        }
        if (len >= 0) {
            data.setReceivedBytes(len);
        }

        try {
            data.setStatusCode(concection.getResponseCode());
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "inspectAndInstrumentResponse --statusCode: " + concection.getResponseCode());
            }
        } catch (IOException e) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, e.toString());
            }
        } catch (NullPointerException e) {
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, e.toString());
            }
        }
    }

    private NetInfo intAndGetMyData() {
        if (this.myData == null) {
            myData = new NetInfo();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "intAndGetMyData --url: " + myConnection.getURL().toString());
            }
            myData.setURL(myConnection.getURL().toString());
        }
        return myData;
    }

    @SuppressWarnings("unused")
    private void recordException(Exception e) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "recordException --e: " + e);
        }
    }

    private void recordData(NetInfo data) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "save the data: " + data.toString());
        }
        data.end();
    }

    @Override
    public void addRequestProperty(String field, String newValue) {
        this.myConnection.addRequestProperty(field, newValue);
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "disconnect. ");
        }
        recordData(this.myData);
        this.myConnection.disconnect();
    }

    @Override
    public boolean usingProxy() {
        return this.myConnection.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "connect. ");
        }
        intAndGetMyData();
        try {
            this.myConnection.connect();
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean getAllowUserInteraction() {
        return this.myConnection.getAllowUserInteraction();
    }

    @Override
    public int getConnectTimeout() {
        return this.myConnection.getConnectTimeout();
    }

    @Override
    public Object getContent() throws IOException {
        intAndGetMyData();
        Object obj;
        try {
            obj = this.myConnection.getContent();
        } catch (IOException e) {
            // TODO
            throw e;
        }
        int len = this.myConnection.getContentLength();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContent. len: " + len);
        }
        if (len >= 0) {
            NetInfo data = intAndGetMyData();
            data.setReceivedBytes(len);
            recordData(data);
        }
        return obj;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getContent(Class[] types) throws IOException {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContent. 2");
        }
        intAndGetMyData();
        Object obj;
        try {
            obj = this.myConnection.getContent(types);
        } catch (IOException localIOException) {
            // TODO:
            throw localIOException;
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return obj;
    }

    @Override
    public String getContentEncoding() {
        intAndGetMyData();
        String encoding = this.myConnection.getContentEncoding();
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContentEncoding. encoding: " + encoding);
        }
        return encoding;
    }

    @Override
    public int getContentLength() {
        intAndGetMyData();
        int len = this.myConnection.getContentLength();

        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContentLength. len: " + len);
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return len;
    }

    @Override
    public String getContentType() {
        intAndGetMyData();
        String type = this.myConnection.getContentType();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getContentType. type: " + type);
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return type;
    }

    @Override
    public long getDate() {
        intAndGetMyData();
        long t = this.myConnection.getDate();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getDate. date: " + t);
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return t;
    }

    @Override
    public InputStream getErrorStream() {
        intAndGetMyData();
        AopInputStream locala;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "getErrorStream.: ");
        }
        try {
            locala = new AopInputStream(this.myConnection.getErrorStream());
            // TODO:
        } catch (Exception e) {
            return this.myConnection.getErrorStream();
        }
        return locala;
    }

    @Override
    public long getHeaderFieldDate(String field, long defaultValue) {
        intAndGetMyData();
        long date = this.myConnection.getHeaderFieldDate(field, defaultValue);
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return date;
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return this.myConnection.getInstanceFollowRedirects();
    }

    @Override
    public java.security.Permission getPermission() throws IOException {
        return this.myConnection.getPermission();
    }

    @Override
    public String getRequestMethod() {
        return this.myConnection.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException {
        intAndGetMyData();
        int code = -1;
        try {
            code = this.myConnection.getResponseCode();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "getResponseCode.code: " + code);
            }
        } catch (IOException e) {
            // TODO
            throw e;
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return code;
    }

    @Override
    public String getResponseMessage() throws IOException {
        intAndGetMyData();
        String str;
        try {
            str = this.myConnection.getResponseMessage();
            if (DEBUG) {
                LogX.d(TAG, SUB_TAG, "getResponseMessage.str: " + str);
            }
        } catch (IOException localIOException) {
            // TODO:
            throw localIOException;
        }
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return str;
    }

    @Override
    public void setChunkedStreamingMode(int chunkLength) {
        this.myConnection.setChunkedStreamingMode(chunkLength);
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        this.myConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.myConnection.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        intAndGetMyData();
        try {
            this.myConnection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw e;
        }
    }

    @Override
    public boolean getDefaultUseCaches() {
        return this.myConnection.getDefaultUseCaches();
    }

    @Override
    public boolean getDoInput() {
        return this.myConnection.getDoInput();
    }

    @Override
    public boolean getDoOutput() {
        return this.myConnection.getDoOutput();
    }

    @Override
    public long getExpiration() {
        intAndGetMyData();
        long l = this.myConnection.getExpiration();
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return l;
    }

    @Override
    public String getHeaderField(int pos) {
        intAndGetMyData();
        String str = this.myConnection.getHeaderField(pos);
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return str;
    }

    @Override
    public String getHeaderField(String key) {
        intAndGetMyData();
        String str = this.myConnection.getHeaderField(key);
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return str;
    }

    @Override
    public int getHeaderFieldInt(String field, int defaultValue) {
        intAndGetMyData();
        int i = this.myConnection.getHeaderFieldInt(field, defaultValue);
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return i;
    }

    @Override
    public String getHeaderFieldKey(int paramInt) {
        intAndGetMyData();
        String str = this.myConnection.getHeaderFieldKey(paramInt);
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return str;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public Map<String, List<String>> getHeaderFields() {
        intAndGetMyData();
        Map localMap = this.myConnection.getHeaderFields();
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return localMap;
    }

    @Override
    public long getIfModifiedSince() {
        intAndGetMyData();
        long l = this.myConnection.getIfModifiedSince();
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return l;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        NetInfo data = intAndGetMyData();
        AopInputStream inputStream;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...getInputStream() ");
        }
        try {
            inputStream = new AopInputStream(this.myConnection.getInputStream());
            inspectAndInstrumentResponse(data, this.myConnection);
            // TODO:
        } catch (IOException e) {
            // TODO:
            throw e;
        }
        inputStream.setStreamCompleteListener(this);
        return inputStream;
    }

    @Override
    public long getLastModified() {
        intAndGetMyData();
        long l = this.myConnection.getLastModified();
        inspectAndInstrumentResponse(intAndGetMyData(), this.myConnection);
        return l;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        intAndGetMyData();
        AopOutputStream localb;
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...OutputStream() ");
        }
        try {
            localb = new AopOutputStream(this.myConnection.getOutputStream());
        } catch (IOException e) {
            throw e;
        }
        localb.setStreamCompleteListener(this);
        return localb;
    }

    @Override
    public int getReadTimeout() {
        return this.myConnection.getReadTimeout();
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return this.myConnection.getRequestProperties();
    }

    @Override
    public String getRequestProperty(String field) {
        return this.myConnection.getRequestProperty(field);
    }

    @Override
    public URL getURL() {
        return this.myConnection.getURL();
    }

    @Override
    public boolean getUseCaches() {
        return this.myConnection.getUseCaches();
    }

    @Override
    public void setAllowUserInteraction(boolean newValue) {
        this.myConnection.setAllowUserInteraction(newValue);
    }

    @Override
    public void setConnectTimeout(int timeout) {
        this.myConnection.setConnectTimeout(timeout);
    }

    @Override
    public void setDefaultUseCaches(boolean newValue) {
        this.myConnection.setDefaultUseCaches(newValue);
    }

    @Override
    public void setDoInput(boolean newValue) {
        this.myConnection.setDoInput(newValue);
    }

    @Override
    public void setDoOutput(boolean newValue) {
        this.myConnection.setDoOutput(newValue);
    }

    @Override
    public void setIfModifiedSince(long newValue) {
        this.myConnection.setIfModifiedSince(newValue);
    }

    @Override
    public void setReadTimeout(int timeout) {
        this.myConnection.setReadTimeout(timeout);
    }

    @Override
    public void setRequestProperty(String field, String newValue) {
        this.myConnection.setRequestProperty(field, newValue);
    }

    @Override
    public void setUseCaches(boolean newValue) {
        this.myConnection.setUseCaches(newValue);
    }

    @Override
    public String toString() {
        if (myConnection == null) {
            return "this connection object is null";
        } else {
            return this.myConnection.toString();
        }
    }

    @Override
    public void onInputstreamComplete(long size) {
        this.myData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...onInputstreamComplete() " + size);
        }

        myData.end();
    }

    @Override
    public void onInputstreamError(long size) {
        this.myData.setReceivedBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...onInputstreamError() " + size);
        }

        myData.end();
    }

    @Override
    public void onOutputstreamComplete(long size) {
        this.myData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...onOutputstreamComplete() " + size);
        }

        myData.end();
    }

    @Override
    public void onOutputstreamError(long size) {
        this.myData.setSendBytes(size);
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "... ...onOutputstreamComplete() " + size);
        }

        myData.end();
    }

}
