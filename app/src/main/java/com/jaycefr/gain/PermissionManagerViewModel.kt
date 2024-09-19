package com.jaycefr.gain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class PermissionManagerViewModel() : ViewModel(){

    @RequiresApi(Build.VERSION_CODES.Q)
    public val permissionList = mutableStateListOf(
        android.Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
    )

    private val grantedPermissions = mutableStateListOf<String>()

    fun notify_permission_granted(permission: String, isGranted : Boolean, context: Context) : Unit{
        if (isGranted){
            Toast.makeText(context, "Permission Granted Successfully", Toast.LENGTH_SHORT).show()
            grantedPermissions.add(permission)
        }
        else{
            Toast.makeText(context, "Permission Not Granted, App may not work as intended", Toast.LENGTH_SHORT).show()
        }
    }

    fun isGranted(permission: String) : Boolean{
        return permission in grantedPermissions
    }

    //Modifies granted permission list with all the granted permssions and returns an array of non granted permssions
    @RequiresApi(Build.VERSION_CODES.Q)
    fun permission_health_checker(context: Context) : Array<String>{
        val declinedPermission = mutableListOf<String>()
        for (permission : String in permissionList){
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                declinedPermission.add(permission)
            }
            else{
                grantedPermissions.add(permission)
            }
        }
        return declinedPermission.toTypedArray()
    }

}
