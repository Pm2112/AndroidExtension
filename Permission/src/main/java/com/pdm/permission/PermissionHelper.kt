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
    private var callback: PermissionCallback? = null

    private val launcher = (activity ?: fragment?.activity)?.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        callback?.invoke(
            results.filterValues { it }.keys.toList(),
            results.filterValues { !it }.keys.toList()
        )
    }

    fun request(vararg permissions: String, cb: PermissionCallback) {
        callback = cb
        val context = activity ?: fragment?.context ?: return cb(emptyList(), permissions.toList())
        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequest.isEmpty()) {
            cb(permissions.toList(), emptyList())
        } else {
            launcher?.launch(
                toRequest.toTypedArray()
            )
        }
    }

    fun isGranted(permission: String): Boolean {
        val context = activity ?: fragment?.context ?: return false
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
