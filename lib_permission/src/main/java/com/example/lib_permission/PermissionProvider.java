package com.example.lib_permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class PermissionProvider {

    public static final String suffix = "$Proxy";

    public static void requestPermission(final Activity activity, final String[] permissions, final int requestCode){
        PermissionProxy permissionProxy = findProxy(activity);
        if (hasPermission(activity, permissions)){
            permissionProxy.grant(requestCode, permissions);
            return;
        }
        if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.M){
            permissionProxy.grant(requestCode, permissions);
        } else {
            String[] deniedPermissions = findDeniedPermissions(activity, permissions);
            if (deniedPermissions.length != 0) {
                boolean rational = permissionProxy.rational(requestCode, permissions, new PermissionRationalCallBack() {
                    @Override
                    public void onSure() {
                        doRequestPermission(activity, permissions, requestCode, null);
                    }

                    @Override
                    public void onCancle() {

                    }
                });
                if (!rational) {
                    doRequestPermission(activity, permissions, requestCode, null);
                }
            } else {
                doRequestPermission(activity, permissions, requestCode, null);
            }
        }
    }

    public static boolean hasPermission(Activity activity, String[] permissions){
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public static void requestPermission(final Fragment fragment, final String[] permissions, final int requestCode){
        PermissionProxy permissionProxy = findProxy(fragment);
        final FragmentActivity activity = fragment.getActivity();
        if (hasPermission(activity, permissions)){
            permissionProxy.grant(requestCode, permissions);
            return;
        }
        if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.M){
            permissionProxy.grant(requestCode, permissions);
        } else {
            String[] deniedPermissions = findDeniedPermissions(activity, permissions);
            if (deniedPermissions.length != 0) {
                boolean rational = permissionProxy.rational(requestCode, permissions, new PermissionRationalCallBack() {
                    @Override
                    public void onSure() {
                        doRequestPermission(activity, permissions, requestCode, fragment);
                    }

                    @Override
                    public void onCancle() {

                    }
                });
                if (!rational) {
                    doRequestPermission(activity, permissions, requestCode, fragment);
                }
            } else {
                doRequestPermission(activity, permissions, requestCode, fragment);
            }
        }
    }

    public static String[] findDeniedPermissions(Activity activity, String[] permissions){
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                list.add(permission);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private static PermissionProxy findProxy(Object object){
        Class<?> clazz = object.getClass();
        String proxyName = clazz.getName() + suffix;
        try {
            Class<?> proxyClass = Class.forName(proxyName);
            Constructor<?> constructor = proxyClass.getConstructor(clazz);
            return (PermissionProxy) constructor.newInstance(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void doRequestPermission(Activity activity, String[] permissions, int requestCode, Fragment fragment){
        if (fragment == null){
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            fragment.requestPermissions(permissions, requestCode);
        }
    }

    public static void onRequestPermissionsResult(Object object, int requestCode, String[] permissions, int[] grantResults){
        PermissionProxy permissionProxy = findProxy(object);
        List<String> deniedPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                deniedPermissionList.add(permissions[i]);
            }
        }
        if (deniedPermissionList.isEmpty()){
            permissionProxy.grant(requestCode, permissions);
        } else {
            permissionProxy.reject(requestCode, deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
        }
    }

}
