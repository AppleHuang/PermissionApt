package com.example.permissionapt;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lib_permission.PermissionProvider;
import com.example.lib_permission.PermissionProxy;

public class BaseFragment extends Fragment {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionProvider.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
