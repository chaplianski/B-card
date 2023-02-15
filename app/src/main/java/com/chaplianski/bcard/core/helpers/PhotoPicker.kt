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

//********************************************************
// start launcher
// photoPicker = PhotoPicker(requireContext(), requireActivity().activityResultRegistry) { uri ->
//                savePhotoContact(uri)
//            }
// then photoPicker.getAndCropPhoto() if want crop or photoPicker.pickPhoto() (get photo from gallery)
// or photoPicker.takePhoto() (get photo from camera)
// For crop need add dependency
// api 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
//    implementation "androidx.activity:activity-ktx:1.7.0-alpha04"
// add to Manifest:
//<activity android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
//            android:theme="@style/Base.Theme.AppCompat" />
//***************************************************************

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
//        callback.invoke(uri)
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
        val REGISTERY_KEY_CROP_PHOTO = "registry key crop photo"
    }


}