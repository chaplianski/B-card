package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.chaplianski.bcard.core.dialogs.LoadCardListFromVCFDialog
import javax.inject.Inject

class ContactPicker @Inject constructor(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (uri: Uri) -> Unit //cardList: List<Card>) -> Unit
) {

    private val pickFileLauncher = activityResultRegistry.register(
        LoadCardListFromVCFDialog.REGISTRY_KEY_LOAD_FILE,
        ActivityResultContracts.OpenDocument()
    ){ result ->
        if (result != null) {
            callback.invoke(result)
        }
    }

//    fun loadFiles(){pickFileLauncher.launch (arrayOf("*/*"))}
    fun checkReadFilesPermission(){pickFileLauncher.launch (arrayOf("application/vcf"))}

}