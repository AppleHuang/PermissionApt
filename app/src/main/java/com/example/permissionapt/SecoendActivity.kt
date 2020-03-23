package com.example.permissionapt

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.lib_anno_permission.PermissionGrant
import com.example.lib_anno_permission.PermissionRational
import com.example.lib_anno_permission.PermissionReject
import com.example.lib_permission.PermissionProvider
import com.example.lib_permission.PermissionRationalCallBack

class SecoendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secoend)
        supportFragmentManager.beginTransaction().replace(R.id.rootView, SelftFragmen()).commit()
    }

    fun log(any: Any?){
        Log.e("huangtao", any.toString()+"    =====")
    }
}
