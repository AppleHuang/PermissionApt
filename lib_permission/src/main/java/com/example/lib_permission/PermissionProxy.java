package com.example.lib_permission;

public abstract class PermissionProxy {
    public void grant(int requestCode, String[] permissions){

    }
    public void reject(int requestCode, String[] permissions){

    }
    public boolean rational(int requestCode, String[] permissions, PermissionRationalCallBack permissionRationalCallBack){
        return false;
    }
}
