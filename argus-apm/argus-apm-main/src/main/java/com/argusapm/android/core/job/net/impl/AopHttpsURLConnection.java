
package com.argusapm.android.core.job.net.impl;

import com.argusapm.android.core.job.net.NetInfo;
import com.argusapm.android.core.job.net.i.IStreamCompleteListener;
import com.argusapm.android.utils.LogX;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;

import static com.argusapm.android.Env.DEBUG;
import static com.argusapm.android.Env.TAG;

/**
 * @author ArgusAPM Team
 */
public class AopHttpsURLConnection extends HttpsURLConnection implements IStreamCompleteListener {

    private static final String SUB_TAG = "AopHttpsURLConnection";
    private HttpsURLConnection myConnection;

    private NetInfo myData;

    public AopHttpsURLConnection(HttpsURLConnection con) {
        super(con.getURL());
        myConnection = con;
    }

    @Override
    public String getCipherSuite() {
        return this.myConnection.getCipherSuite();
    }

    @Override
    public Certificate[] getLocalCertificates() {
        return this.myConnection.getLocalCertificates();
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        try {
            return this.myConnection.getServerCertificates();
        } catch (SSLPeerUnverifiedException e) {
            recordException(e);
            throw e;
        }
    }

    @Override
    public void addRequestProperty(String field, String newValue) {
        this.myConnection.addRequestProperty(field, newValue);
    }

    @Override
    public void disconnect() {
        recordTheData(this.myData);
        this.myConnection.disconnect();
    }

    @Override
    public boolean usingProxy() {
        return this.myConnection.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        initAndGetMyData();
        try {
            this.myConnection.connect();
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
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
        initAndGetMyData();
        Object localObject;
        try {
            localObject = this.myConnection.getContent();
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
        }
        int i = this.myConnection.getContentLength();
        if (i >= 0) {
            NetInfo locali = initAndGetMyData();
            locali.setReceivedBytes(i);
            recordTheData(locali);
        }
        return localObject;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getContent(Class[] types) throws IOException {
        initAndGetMyData();
        Object localObject;
        try {
            localObject = this.myConnection.getContent(types);
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
        }
        inspectAndInstrumentResponse();
        return localObject;
    }

    @Override
    public String getContentEncoding() {
        initAndGetMyData();
        String str = this.myConnection.getContentEncoding();
        inspectAndInstrumentResponse();
        return str;
    }

    @Override
    public int getContentLength() {
        initAndGetMyData();
        int i = this.myConnection.getContentLength();
        inspectAndInstrumentResponse();
        return i;
    }

    @Override
    public String getContentType() {
        initAndGetMyData();
        String str = this.myConnection.getContentType();
        inspectAndInstrumentResponse();
        return str;
    }

    @Override
    public long getDate() {
        initAndGetMyData();
        long l = this.myConnection.getDate();
        inspectAndInstrumentResponse();
        return l;
    }

    @Override
    public InputStream getErrorStream() {
        initAndGetMyData();
        AopInputStream locala;
        try {
            locala = new AopInputStream(this.myConnection.getErrorStream());
        } catch (Exception localException) {
            // c.error(localException.toString());
            return this.myConnection.getErrorStream();
        }
        return locala;
    }

    @Override
    public long getHeaderFieldDate(String field, long defaultValue) {
        initAndGetMyData();
        long l = this.myConnection.getHeaderFieldDate(field, defaultValue);
        inspectAndInstrumentResponse();
        return l;
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return this.myConnection.getInstanceFollowRedirects();
    }

    @Override
    public Permission getPermission() throws IOException {
        return this.myConnection.getPermission();
    }

    @Override
    public String getRequestMethod() {
        return this.myConnection.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException {
        initAndGetMyData();
        int i;
        try {
            i = this.myConnection.getResponseCode();
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
        }
        inspectAndInstrumentResponse();
        return i;
    }

    @Override
    public String getResponseMessage() throws IOException {
        initAndGetMyData();
        String str;
        try {
            str = this.myConnection.getResponseMessage();
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
        }
        inspectAndInstrumentResponse();
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
        try {
            this.myConnection.setRequestMethod(method);
        } catch (ProtocolException localProtocolException) {
            recordException(localProtocolException);
            throw localProtocolException;
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
        initAndGetMyData();
        long l = this.myConnection.getExpiration();
        inspectAndInstrumentResponse();
        return l;
    }

    @Override
    public String getHeaderField(int pos) {
        initAndGetMyData();
        String str = this.myConnection.getHeaderField(pos);
        inspectAndInstrumentResponse();
        return str;
    }

    @Override
    public String getHeaderField(String key) {
        initAndGetMyData();
        String str = this.myConnection.getHeaderField(key);
        inspectAndInstrumentResponse();
        return str;
    }

    @Override
    public int getHeaderFieldInt(String field, int defaultValue) {
        initAndGetMyData();
        int i = this.myConnection.getHeaderFieldInt(field, defaultValue);
        inspectAndInstrumentResponse();
        return i;
    }

    @Override
    public String getHeaderFieldKey(int posn) {
        initAndGetMyData();
        String str = this.myConnection.getHeaderFieldKey(posn);
        inspectAndInstrumentResponse();
        return str;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    public Map<String, List<String>> getHeaderFields() {
        initAndGetMyData();
        Map localMap = this.myConnection.getHeaderFields();
        inspectAndInstrumentResponse();
        return localMap;
    }

    @Override
    public long getIfModifiedSince() {
        initAndGetMyData();
        long l = this.myConnection.getIfModifiedSince();
        inspectAndInstrumentResponse();
        return l;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        NetInfo locali = initAndGetMyData();
        AopInputStream locala;
        try {
            locala = new AopInputStream(this.myConnection.getInputStream());
            inspectAndInstrumentResponse(locali, this.myConnection);
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
        }
        locala.setStreamCompleteListener(this);
        return locala;
    }

    @Override
    public long getLastModified() {
        initAndGetMyData();
        long l = this.myConnection.getLastModified();
        inspectAndInstrumentResponse();
        return l;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        initAndGetMyData();
        AopOutputStream localb;
        try {
            localb = new AopOutputStream(this.myConnection.getOutputStream());
        } catch (IOException localIOException) {
            recordException(localIOException);
            throw localIOException;
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
    public void setUseCaches(boolean paramBoolean) {
        this.myConnection.setUseCaches(paramBoolean);
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
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.myConnection.getPeerPrincipal();
    }

    @Override
    public Principal getLocalPrincipal() {
        return this.myConnection.getLocalPrincipal();
    }

    @Override
    public void setHostnameVerifier(HostnameVerifier v) {
        this.myConnection.setHostnameVerifier(v);
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return this.myConnection.getHostnameVerifier();
    }

    @Override
    public void setSSLSocketFactory(SSLSocketFactory sf) {
        this.myConnection.setSSLSocketFactory(sf);
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return this.myConnection.getSSLSocketFactory();
    }

    private void inspectAndInstrumentResponse() {
        inspectAndInstrumentResponse(initAndGetMyData(), this.myConnection);
    }

    private NetInfo initAndGetMyData() {
        if (this.myData == null) {
            this.myData = new NetInfo();
            this.myData.setURL(this.myConnection.getURL().toString());
        }
        return this.myData;
    }

    private void recordException(Exception e) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection----recordException: " + e);
        }
    }

    private void recordTheData(NetInfo data) {
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection----recordTheData");
        }
        data.end();
    }

    private void inspectAndInstrumentResponse(NetInfo data, HttpsURLConnection concection) {
        int len = concection.getContentLength();
        if (len >= 0) {
            data.setReceivedBytes(len);
        }

        try {
            data.setStatusCode(concection.getResponseCode());
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

    @Override
    public void onInputstreamComplete(long size) {
        this.myData.setReceivedBytes(size);
        myData.end();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection ...onInputstreamComplete() " + size);
        }
    }

    @Override
    public void onInputstreamError(long size) {
        this.myData.setReceivedBytes(size);
        myData.end();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection ...onInputstreamError() " + size);
        }
    }

    @Override
    public void onOutputstreamComplete(long size) {
        this.myData.setSendBytes(size);
        myData.end();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection ...onOutputstreamComplete() " + size);
        }
    }

    @Override
    public void onOutputstreamError(long size) {
        this.myData.setSendBytes(size);
        myData.end();
        if (DEBUG) {
            LogX.d(TAG, SUB_TAG, "AopHttpsURLConnection...onOutputstreamComplete() " + size);
        }
    }

}
