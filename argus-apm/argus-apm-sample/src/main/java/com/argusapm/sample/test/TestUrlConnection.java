package com.argusapm.sample.test;

import android.util.Log;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * UrlConnection测试类
 *
 * @author ArgusAPM Team
 */
public class TestUrlConnection {
    private static final String TAG = "TestUrlConnection";

    public static void urlConnectionRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "start urlConnectionRequest");
                    URL url = new URL("http://e.dangdang.com/media/api2.go?action=block&code=paymentSwitch&dang_name=dang_name&returnType=json&deviceType=Android&channelId=30066&clientVersionNo=5.9.5&serverVersionNo=1.2.1&permanentId=20170526065421752454273325035678487&deviceSerialNo=c817017a50b5b442a68c0ae46b83dfa1&macAddr=1c%3Acd%3Ae5%3A10%3A44%3A9d&resolution=720*1280&clientOs=5.1&platformSource=DDDS-P&channelType=&token=");
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.connect();
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(httpUrlConnection.getInputStream());
                    //判断是否正常响应数据
                    if (httpUrlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "网络错误异常！!!!");
                    }
                    Log.d(TAG, "end urlConnectionRequest end");
                    httpUrlConnection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e.getCause());
                }
            }
        }).start();
    }
}
