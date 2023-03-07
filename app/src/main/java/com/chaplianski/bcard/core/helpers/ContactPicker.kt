package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.chaplianski.bcard.core.dialogs.LoadCardListFromFileDialog
import com.chaplianski.bcard.core.utils.CSV_TYPE
import com.chaplianski.bcard.core.utils.VCF_TYPE
import javax.inject.Inject

class ContactPicker @Inject constructor(
    val context: Context,
    activityResultRegistry: ActivityResultRegistry,
    private val callback: (uri: Uri) -> Unit //cardList: List<Card>) -> Unit
) {

    private val pickFileLauncher = activityResultRegistry.register(
        LoadCardListFromFileDialog.REGISTRY_KEY_LOAD_FILE,
        ActivityResultContracts.OpenDocument()
    ){ result ->
        if (result != null) {
            callback.invoke(result)
        }
    }
    fun checkReadFilesPermission(){pickFileLauncher.launch (arrayOf(VCF_TYPE, CSV_TYPE))}
}