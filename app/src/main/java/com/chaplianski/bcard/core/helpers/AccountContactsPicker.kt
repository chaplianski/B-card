package com.chaplianski.bcard.core.helpers

import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

class AccountContactsPicker(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (isHavePermition: Boolean) -> Unit
) {

    private val requestPermissionLauncher = activityResultRegistry.register(
        REGISTRY_KEY_PERMISSION,
        ActivityResultContracts.RequestPermission()
    ) {result ->
        if (result) callback(true)
    }

    fun checkPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
    }

    companion object {
        val REGISTRY_KEY_PERMISSION = "registry key permission"

    }
}