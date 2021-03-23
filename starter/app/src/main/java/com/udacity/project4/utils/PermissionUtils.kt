package com.udacity.project4.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


val RUNNING_Q_OR_LATER = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.Q

@TargetApi(29)
fun locationPermissionsApproved(context: Context): Boolean {
    val foregroundLocationPermissionApproved = (
            PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION))
    val backgroundLocationPermissionApproved =
        if (RUNNING_Q_OR_LATER) {
            PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
        } else {
            true
        }
    return foregroundLocationPermissionApproved && backgroundLocationPermissionApproved
}