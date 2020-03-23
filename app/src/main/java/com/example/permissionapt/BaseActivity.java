package com.example.permissionapt;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_permission.PermissionProvider;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("huangtao", "申请权限返回");
        PermissionProvider.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
