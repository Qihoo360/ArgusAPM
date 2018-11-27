package com.argusapm.sample.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求帮助类
 *
 * @author ArgusAPM Team
 */
public class HttpHelper {
    private static final String TAG = "argus-apm";
    public static final String GET_URL = "http://bz.budejie.com/?typeid=2&ver=3.4.3&no_cry=1&client=android&c=wallPaper&a=wallPaperNew&index=1&size=60&bigid=0";
    public static final String TYPE = "application/octet-stream";
    public static final String POST_URL = "http://zhushou.72g.com/app/gift/gift_list/";

    public void syncGetRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String url = "https://reg.163.com/logins.jsp?id=helloworld&pwd=android";
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void asyncGetRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url(GET_URL)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
                        Log.i(TAG, "onResponse: " + string);
                    }
                });
            }
        }.start();
    }

    public void syncPostRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String url = "https://reg.163.com/logins.jsp";
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

                RequestBody body = new FormBody.Builder()
                        .add("id", "helloworld")
                        .add("pwd", "android")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void asyncPostRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

                //    请求条件：platform=2&gifttype=2&compare=60841c5b7c69a1bbb3f06536ed685a48
                //    请求参数：page=1&code=news&pageSize=20&parentid=0&type=1
                RequestBody requestBodyPost = new FormBody.Builder()
                        .add("page", "1")
                        .add("code", "news")
                        .add("pageSize", "20")
                        .add("parentid", "0")
                        .add("type", "1")
                        .build();
                Request requestPost = new Request.Builder()
                        .url(POST_URL)
                        .post(requestBodyPost)
                        .build();
                okHttpClient.newCall(requestPost).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
                        Log.i(TAG, "onResponse: " + string);
                    }
                });
            }
        }.start();
    }


    //http://docs.bmob.cn/data/Restful/b_developdoc/doc/index.html#%E6%96%87%E4%BB%B6%E7%AE%A1%E7%90%86
    public void uploadFile(final String uploadFileDir, final String fileName) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                File file = new File(uploadFileDir, fileName);
                if (!file.exists()) {
                    Log.d(TAG, "文件不存在");
                } else {
                    RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
                    RequestBody requestBody = new MultipartBody.Builder().addFormDataPart("filename", file.getName(), fileBody).build();

                    Request requestPostFile = new Request.Builder()
                            .url(" https://api.bmob.cn/2/files/" + fileName)
                            .post(requestBody)
                            .addHeader("X-Bmob-Application-Id", "2fd34b9716e7c3a6a8c6b26a74eeb677")
                            .addHeader("X-Bmob-REST-API-Key", "6bbcecc63319cad58949d7c69da020df")
                            .addHeader("Content-Type", "application/x-jpg")
                            .build();
                    okHttpClient.newCall(requestPostFile).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "上传失败");
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            Log.d(TAG, "上传成功");
                        }
                    });
                }
            }
        }.start();
    }

    public void downloadFile(final String saveFilePath) {
        String downloadUrl = "http://dldir1.qq.com/qqfile/qq/QQ8.9.1/20453/QQ8.9.1.exe";
        DownloadUtil.getInstance().download(downloadUrl, saveFilePath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                Log.d(TAG, "下载成功");
            }

            @Override
            public void onDownloading(int progress) {
                Log.d(TAG, "已下载" + progress + "%");
            }

            @Override
            public void onDownloadFailed() {
                Log.d(TAG, "下载失败");
            }
        });

    }

    public void putRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                FormBody build = new FormBody.Builder()
                        .build();
                Request build1 = new Request.Builder()
                        .url(POST_URL)
                        .put(build)//Put请求
                        .build();

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string != null) {
                        }
                    }
                });
            }
        }.start();
    }

    public void headRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                Request build1 = new Request.Builder()
                        .url(POST_URL)
                        .head()//HEAD请求
                        .build();

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string != null) {
                        }
                    }
                });
            }
        }.start();
    }

    public void deleteRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                Request build1 = new Request.Builder()
                        .url(POST_URL)
                        .delete()//DELETE请求
                        .build();

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string != null) {
                        }
                    }
                });
            }
        }.start();
    }


}
