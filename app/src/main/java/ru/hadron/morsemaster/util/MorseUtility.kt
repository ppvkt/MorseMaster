package ru.hadron.morsemaster.util

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object MorseUtility {
    fun hasPermissions(context: Context) = EasyPermissions.hasPermissions(
        context,
        Manifest.permission.CAMERA
    )
}