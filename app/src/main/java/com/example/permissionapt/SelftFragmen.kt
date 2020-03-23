package com.example.permissionapt

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lib_anno_permission.PermissionGrant
import com.example.lib_anno_permission.PermissionRational
import com.example.lib_anno_permission.PermissionReject
import com.example.lib_permission.PermissionProvider
import com.example.lib_permission.PermissionRationalCallBack

class SelftFragmen : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PermissionProvider.requestPermission(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            MainActivity.sRequestCodeWriteSD
        )
    }

    @PermissionGrant(MainActivity.sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionSuccess(permission: Array<String>){
        log("打开sd权限成功")
    }

    @PermissionGrant(MainActivity.sRequestCodeCamera)
    fun onRequestCameraSuccess(){
        log("打开相机权限成功")
    }

    @PermissionReject(MainActivity.sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionReject(){
        log("打开sd权限失败")
    }

    @PermissionRational(MainActivity.sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionRationnal(permissions: Array<String>, permissionRationalCallBack: PermissionRationalCallBack): Boolean {
        AlertDialog.Builder(activity)
            .setTitle("fragment权限需求")
            .setMessage("请打开读取SD卡权限")
            .setPositiveButton("确定") { dialog, which ->
                permissionRationalCallBack.onSure()
                dialog.dismiss()
            }
            .setNegativeButton("取消"){ dialog, which ->
                permissionRationalCallBack.onCancle()
                dialog.dismiss()
            }
            .show()
        return true
    }

}