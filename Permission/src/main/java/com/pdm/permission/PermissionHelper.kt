package com.pdm.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

typealias PermissionCallback = (grantedPermissions: List<String>, deniedPermissions: List<String>) -> Unit

class PermissionHelper(
    private val activity: ComponentActivity? = null,
    private val fragment: Fragment? = null
) {

    private var permissionCallback: PermissionCallback? = null

    private val requestPermissionLauncher =
        (activity
            ?: fragment?.activity)?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { results ->
            val grantedPermissions = results.filterValues { it }.keys.toList()
            val deniedPermissions = results.filterValues { !it }.keys.toList()
            permissionCallback?.invoke(grantedPermissions, deniedPermissions)
        }

    fun requestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        permissionCallback = callback

        val context = activity ?: fragment?.context
        if (context == null || requestPermissionLauncher == null) {
            callback(emptyList(), permissions.toList())
            return
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            callback(permissions.toList(), emptyList())
        } else {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    fun isPermissionGranted(permission: String): Boolean {
        val context = activity ?: fragment?.context ?: return false
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}