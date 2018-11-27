package com.argusapm.sample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.argusapm.android.api.Client;
import com.argusapm.sample.test.TestActivity;
import com.argusapm.sample.test.TestBlockActivity;
import com.argusapm.sample.test.TestOkHttp3Activity;
import com.argusapm.sample.test.TestUrlConnection;
import com.argusapm.sample.utils.FileUtils;

/**
 * Demo主页面
 *
 * @author ArgusAPM Team
 */
public class MainActivity extends Activity implements View.OnClickListener {
    // 要申请的权限
    private String[] mPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
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
            }
        }
    }

    @Override
    public void onClick(View v) {
        int what = v.getId();
        switch (what) {
            case R.id.startwork_btn:
                Toast.makeText(MainActivity.this, "请查看Log是否开启成功", Toast.LENGTH_SHORT).show();
                Client.startWork();
                break;
            case R.id.test_cloud_rule_file_btn:
                if (FileUtils.isDownloadApmConfigFileFromServer(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "云规则文件下载\"成功\"", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "云规则文件下载\"失败\"", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.test_cloud_rule_file_is_from_sd_btn:
                if (FileUtils.isContainApmConfigFileOfSdcard()) {
                    Toast.makeText(MainActivity.this, "SD卡下/360/Apm/目录里导入了云规则文件", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "SD卡下/360/Apm/目录里~未~导入了云规则文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.test_activity:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            case R.id.test_urlconnection:
                TestUrlConnection.urlConnectionRequest();
                break;
            case R.id.test_okhttp3:
                startActivity(new Intent(MainActivity.this, TestOkHttp3Activity.class));
                break;
            case R.id.test_block:
                startActivity(new Intent(MainActivity.this, TestBlockActivity.class));
                break;
            default:
                break;
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
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}