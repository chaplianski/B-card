package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.chaplianski.bcard.BuildConfig
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import javax.inject.Inject

class PhotoPicker @Inject constructor(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (image: Uri?) -> Unit
) {

    private lateinit var photoUri: Uri

    private val cropPhotoContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }
    }

   private val cropPhotoLauncher = activityResultRegistry.register(
        REGISTERY_KEY_CROP_PHOTO,
        cropPhotoContract
    ) { uri ->
        callback.invoke(uri)
    }

    private val getContentLauncher = activityResultRegistry.register(
        REGISTRY_KEY_GET_CONTENT,
        ActivityResultContracts.GetContent()
    ) { uri ->
          if (uri != null) {
              getAndCropPhoto()
          }
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

    fun getAndCropPhoto(){
        cropPhotoLauncher.launch(null)
    }
    fun pickPhoto() {
        getContentLauncher.launch("image/*")
    }

    fun takePhoto() {
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun getTmpFileUri(): Uri {

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
        val REGISTERY_KEY_CROP_PHOTO = "registry key crop photo"
    }
}