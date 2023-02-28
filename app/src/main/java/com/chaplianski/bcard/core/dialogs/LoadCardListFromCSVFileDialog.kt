package com.chaplianski.bcard.core.dialogs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.chaplianski.bcard.databinding.DialogLoadCardListFromCSVFileBinding
import com.chaplianski.bcard.domain.model.ContactCsv
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream


class LoadCardListFromCSVFileDialog :
    BasisDialogFragment<DialogLoadCardListFromCSVFileBinding>(DialogLoadCardListFromCSVFileBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkAllCheckBox = binding.checkBoxLoadCardDialogCheckAll
        val currentUri = arguments?.getString(LoadCardListFromVCFDialog.CURRENT_URI)


        currentUri?.toUri().also {
            val inputStream = it?.let { it1 -> context?.contentResolver?.openInputStream(it1) }
            if (inputStream != null) {
                val rrr = readCsvFile<ContactCsv>(inputStream)
                Log.d("MyLog", "rrrrrrrrrr = $rrr")
            }
        }
    }

    val csvMapper = CsvMapper().apply {
        registerModule(KotlinModule())
    }
    inline fun <reified T> readCsvFile(inputStream: InputStream): List<T> {
        inputStream.use { reader ->
            return csvMapper
                .readerFor(T::class.java)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues<T>(reader)
                .readAll()
                .toList()
        }
    }

}