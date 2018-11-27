package com.argusapm.sample.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.argusapm.sample.R;
import com.argusapm.sample.utils.HttpHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OKHTTP3测试页面
 *
 * @author ArgusAPM Team
 */
public class TestOkHttp3Activity extends Activity implements View.OnClickListener {
    private String[] mPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private HttpHelper mHttpHelper = new HttpHelper();
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ok_http3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sync_get_btn:
                mHttpHelper.syncGetRequest();
                break;
            case R.id.async_get_btn:
                mHttpHelper.asyncGetRequest();
                break;
            case R.id.sync_post_btn:
                mHttpHelper.syncPostRequest();
                break;
            case R.id.async_post_btn:
                mHttpHelper.asyncPostRequest();
                break;
            case R.id.file_upload_btn:
                requestPermission();
                break;
            case R.id.file_download_btn:
                downloadFile();
                break;
            default:
                break;
        }
    }


    private void downloadFile() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String saveFilePath = getCacheDir().getPath() + "/argus_apm";
                mHttpHelper.downloadFile(saveFilePath);
            }
        }.start();
    }


    private void upload() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String fileDir = Environment.getExternalStorageDirectory().getPath();
                copyFile(fileDir);
                mHttpHelper.uploadFile(fileDir, "demotest.exe");
            }
        }.start();
    }


    private void copyFile(String fileDir) {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("demotest.exe");
            File file = new File(fileDir + "/demotest.exe");
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(fileDir + "/demotest.exe");
                // 写入缓存
                byte[] buff = new byte[512];
                int count = is.read(buff);
                while (count != -1) {
                    fos.write(buff);
                    count = is.read(buff);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = checkSelfPermission(mPermissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            } else {
                upload();
            }
        }
    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("由于ArgusAPM需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用ArgusAPM")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mPermissions, 321);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        mDialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许APM使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    upload();
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = checkSelfPermission(mPermissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    upload();
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
