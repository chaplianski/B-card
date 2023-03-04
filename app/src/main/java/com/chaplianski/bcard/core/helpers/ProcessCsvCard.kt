package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.UiText
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.ContactCsv
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.FileWriter
import java.io.InputStream
import java.io.OutputStream

class ProcessCsvCard {

    fun getCardList(context: Context, uri: Uri): List<Card>{
        val inputStream = context.contentResolver?.openInputStream(uri)
        return if (inputStream != null) {
            val csvContactList = readCsvFile<ContactCsv>(inputStream)
            csvContactList
                .filter { !it.familyName.isNullOrEmpty() && !it.givenName.isNullOrEmpty()}
                .map {
                    if (it.familyName.isNullOrEmpty() && !it.givenName.isNullOrEmpty()) {
                        it.familyName = it.givenName
                        it.givenName = ""
                    } else {
                        it.givenName = it.givenName?.let { it1 ->
                            it.additionalName?.let { it2 ->
                                UiText.StringResource(R.string.two_value_without_comma,
                                    it1, it2
                                ).asString(context)
                            }
                        }
                    }
                    it
                }
                .map { it.mapContactCsvToCard() }
        } else emptyList<Card>()
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

    inline fun <reified T> writeCsvFile(data: Collection<T>, outputStream: OutputStream) {
        outputStream.use { writer ->
            csvMapper.writer(csvMapper.schemaFor(T::class.java).withHeader())
                .writeValues(writer)
                .writeAll(data)
                .close()
        }
    }
}