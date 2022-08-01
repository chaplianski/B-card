package com.chaplianski.bcard.presenter.helpers

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.chaplianski.bcard.BuildConfig
import java.io.File
import javax.inject.Inject

class PhotoPicker @Inject constructor(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (image: Uri?) -> Unit
) {

    private lateinit var photoUri: Uri

    private val getContentLauncher = activityResultRegistry.register(
        REGISTRY_KEY_GET_CONTENT,
        ActivityResultContracts.GetContent()
    ) { uri ->
        callback.invoke(uri)
    }

    private val takePhotoLauncher = activityResultRegistry.register(
        REGISTRY_KEY_TAKE_PHOTO,
        ActivityResultContracts.TakePicture()
    ) { result ->
        if (result && this::photoUri.isInitialized) callback.invoke(photoUri)
    }

    private val requestPermissionLauncher = activityResultRegistry.register(
        REGISTRY_KEY_PERMISSION,
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            photoUri = getTmpFileUri()
            takePhotoLauncher.launch(photoUri)
        }
    }

    fun pickPhoto() { getContentLauncher.launch("image/*") }

    fun takePhoto() { requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)}

    private fun getTmpFileUri(): Uri {

        //     val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //    val tmpFile = File.createTempFile("br", ".jpg", storageDir)
        val tmpFile = File(context.cacheDir, "temp.jpg")
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    companion object {
        val REGISTRY_KEY_PERMISSION = "registry key permission"
        val REGISTRY_KEY_TAKE_PHOTO = "registry key take photo"
        val REGISTRY_KEY_GET_CONTENT = "registry key get content"
    }



}