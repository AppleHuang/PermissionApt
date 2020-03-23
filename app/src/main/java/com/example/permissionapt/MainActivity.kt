package com.example.permissionapt

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import com.example.lib_anno_permission.PermissionGrant
import com.example.lib_anno_permission.PermissionRational
import com.example.lib_anno_permission.PermissionReject
import com.example.lib_permission.PermissionProvider
import com.example.lib_permission.PermissionRationalCallBack

class MainActivity : BaseActivity() {

    companion object {
        const val sRequestCodeWriteSD = 10
        const val sRequestCodeCamera= 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        PermissionProvider.requestPermission(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), sRequestCodeWriteSD)
        findViewById<View>(R.id.aaaaaaView).setOnClickListener {
            startActivity(Intent(this, SecoendActivity::class.java))
        }
    }

    @PermissionGrant(sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionSuccess(permission: Array<String>){
        log("打开sd权限成功")
    }

    @PermissionGrant(sRequestCodeCamera)
    fun onRequestCameraSuccess(){
        log("打开相机权限成功")
    }

    @PermissionReject(sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionReject(){
        log("打开sd权限失败")
    }

    @PermissionRational(sRequestCodeWriteSD)
    fun onRequestWriteSDPermissionRationnal(permissions: Array<String>, permissionRationalCallBack: PermissionRationalCallBack): Boolean {
        AlertDialog.Builder(this)
            .setTitle("权限需求")
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

fun log(any: Any?){
    Log.e("huangtao", any.toString()+"    =====")
}
