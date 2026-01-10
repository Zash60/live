package com.example.liveapp.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {

    const val REQUEST_CODE_PERMISSIONS = 1001

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS
    )

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasPermissions(context, *REQUIRED_PERMISSIONS)
    }

    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>, vararg permissions: String) {
        launcher.launch(permissions as Array<String>)
    }

    fun requestRequiredPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        requestPermissions(launcher, *REQUIRED_PERMISSIONS)
    }

    fun shouldShowRationale(activity: androidx.activity.ComponentActivity, permission: String): Boolean {
        return androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}