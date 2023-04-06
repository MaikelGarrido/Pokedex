package com.example.pokedex.utils

import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment

class PermissionRequesterFragment(
    fragment: Fragment,
    private val permission: String = "",
    private val permissionsGlobal: Array<String> = arrayOf(),
    private val onRational: () -> Unit = {},
    private val onDenied: () -> Unit = {}
) {

    private var onGranted: () -> Unit = { }

    /** Logic for different permissions */
    private val permissionMultipleLauncher = fragment.registerForActivityResult(RequestMultiplePermissions()) { permissions ->
        val neverAskAgainPermissions = mutableSetOf<String>()
        val rationalPermissions = mutableSetOf<String>()

        permissions.entries.forEach {
            val permission = it.key
            val permissionIsGranted = it.value

            if (!permissionIsGranted) {
                if (fragment.shouldShowRequestPermissionRationale(permission)) {
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

    fun runWithPermissions(body: () -> Unit) {
        onGranted = body
        permissionMultipleLauncher.launch(permissionsGlobal)
    }



    /** Logic for a single permission */
    private val permissionLauncher = fragment.registerForActivityResult(RequestPermission()) { isGranted ->
        when {
            isGranted -> { onGranted() }
            fragment.shouldShowRequestPermissionRationale(permission) -> { onRational() }
            else -> { onDenied() }
        }
    }

    fun runWithPermission(body: () -> Unit) {
        onGranted = body
        permissionLauncher.launch(permission)
    }

}