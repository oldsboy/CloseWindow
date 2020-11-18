package com.oldsboy.closewindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oldsboy.closewindow.provider.AdminReceiver;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main";

    private static final int REQUEST_DevicePolicy = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lock_layout);

        ComponentName adminReceiver = new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager systemService = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if (!requestLockAdmins(systemService, adminReceiver)){
            Toast.makeText(this, "点击按钮申请权限后可以息屏", Toast.LENGTH_SHORT).show();
        }
        ((TextView)this.findViewById(R.id.tv)).setOnClickListener(v -> {
            if (requestLockAdmins(systemService, adminReceiver)){
                systemService.lockNow();
            }
        });
    }

    /**
     *申请设备管理员权限
     */
    private boolean requestLockAdmins(DevicePolicyManager systemService, ComponentName adminReceiver) {
        if (systemService == null) return false;
        //检查是否已经获取设备管理权限
        boolean active = systemService.isAdminActive(adminReceiver);
        if (!active) {
            //打开DevicePolicyManager管理器，授权页面
            Intent intent = new Intent();
            //授权页面Action -->  DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            //设置DEVICE_ADMIN，告诉系统申请管理者权限的Component/DeviceAdminReceiver
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
            //设置 提示语--可不添加
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "DevicePolicyManager涉及的管理权限,一次性激活!");
            startActivityForResult(intent, REQUEST_DevicePolicy);
        }
        return active;
    }
}