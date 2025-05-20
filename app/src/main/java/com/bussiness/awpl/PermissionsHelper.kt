package com.bussiness.awpl

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {

    private val permissions = mutableListOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )

    fun hasPermissions(context: Context): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            1001
        )
    }

    fun handleResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode == 1001) {
            return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        }
        return false
    }

    fun shouldShowRationale(activity: Activity): Boolean {
        return permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }
}