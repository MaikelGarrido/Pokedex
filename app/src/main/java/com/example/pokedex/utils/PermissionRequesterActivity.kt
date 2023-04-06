package com.example.pokedex.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission

class PermissionRequesterActivity(
    activity: ComponentActivity,
    private val permission: String = "",
    private val permissionsGlobal: Array<String> = arrayOf(),
    private val onRational: () -> Unit = {},
    private val onDenied: () -> Unit = {}
) {

    private var onGranted: () -> Unit = { }

    /** Logic for different permissions */
    private val permissionMultipleLauncher = activity.registerForActivityResult(RequestMultiplePermissions()) { permissions ->
        val neverAskAgainPermissions = mutableSetOf<String>()
        val rationalPermissions = mutableSetOf<String>()

        permissions.entries.forEach {
            val permission = it.key
            val permissionIsGranted = it.value

            if (!permissionIsGranted) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    rationalPermissions.add(permission)
                    if (neverAskAgainPermissions.isNotEmpty()) {
                        neverAskAgainPermissions.add(permission)
                    }
                } else {
                    neverAskAgainPermissions.add(permission)
                    neverAskAgainPermissions.addAll(rationalPermissions)
                }
            }
        }

        when {
            neverAskAgainPermissions.isNotEmpty() -> { onDenied() }
            rationalPermissions.isNotEmpty() -> { onRational() }
            else -> { onGranted() }
        }

    }

    /** Logic for a single permission */
    private val permissionLauncher = activity.registerForActivityResult(RequestPermission()) { isGranted ->
        when {
            isGranted -> { onGranted() }
            activity.shouldShowRequestPermissionRationale(permission) -> { onRational() }
            else -> { onDenied() }
        }
    }

    fun runWithPermission(body: () -> Unit) {
        onGranted = body
        permissionLauncher.launch(permission)
    }

    fun runWithPermissions(body: () -> Unit) {
        onGranted = body
        permissionMultipleLauncher.launch(permissionsGlobal)
    }



}