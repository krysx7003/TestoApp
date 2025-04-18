package com.napnap.testoapp.ui.screens.main

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.QuizData
import com.napnap.testoapp.data.classes.BASE_DIR_NAME
import com.napnap.testoapp.data.classes.HIST_JSON
import com.napnap.testoapp.data.classes.SAVE_JSON
import com.napnap.testoapp.data.stores.SettingsStore
import kotlinx.coroutines.flow.first
import java.io.BufferedInputStream
import java.io.File
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun emptyDir(dir:File,context: Context){
    findAndDelete(context,dir)
    if(dir.isDirectory){
        val items = dir.listFiles()
        if (items != null) {
            for (file in items) {
                file.delete()
            }
        }
    }
}

suspend fun handleZipFile(uri: Uri, context: Context){
    val settingsStore = SettingsStore()
    val startAmount = settingsStore.read("startAmount",context).first().toString().toIntOrNull() ?: 2
    settingsStore.read("repeatAmount",context)
    Log.i("HandlingZip","Starting to unzip ${uri.path.toString()}")
    if(uri.path.toString().isNotEmpty()){
        val outputDir = File(context.filesDir,BASE_DIR_NAME)
        val questionFileList = ArrayList<QuestionFile>()
        var zipName = ""
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
                var zipEntry: ZipEntry?
                while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                    zipEntry?.let { entry ->
                        val file = File(outputDir, entry.name)
                        if (entry.isDirectory) {
                            if(file.exists()){
                                emptyDir(file,context)
                            }else{
                                file.mkdirs()
                            }
                            zipName = entry.name.removeSuffix("/")
                            File(file,SAVE_JSON).createNewFile()
                        } else {
                            file.outputStream().use { fileOutput ->
                                if (!file.name.endsWith(".json")){
                                    if(file.name.endsWith(".txt")){
                                        questionFileList.add(QuestionFile(file.name,startAmount))
                                    }
                                    val bytes = zipInputStream.readBytes()
                                    fileOutput.write(bytes)
                                }else{
                                    Log.w("HandlingZip","File named ${file.name} not saved")
                                }
                            }
                        }
                    }
                }
                zipInputStream.close()
                Log.i("HandlingZip", "Unzipped successfully to ${outputDir.absolutePath}")
                writeJson(context, zipName, questionFileList)
                appendJson(context,zipName,QuizData(zipName,0.0F,"0:00", LocalDateTime.now().toString()))
            } ?: Log.e("HandlingZip", "Failed to open input stream")

        } catch (e: Exception) {
            Log.e("HandlingZip", "Error unzipping file: ${e.message}")
        }
    }

}

fun writeJson(context: Context,zipName: String,questionFileList: MutableList<QuestionFile> ){
    val questionJson = Gson().toJson(questionFileList)
    val jsonFileQ = context.filesDir.resolve("$BASE_DIR_NAME/$zipName/$SAVE_JSON")
    Log.i("WriteJson","Written $questionJson to file $zipName/$SAVE_JSON")
    jsonFileQ.writeText(questionJson)
}

fun isJsonArr(string: String):Boolean{
    return !string.startsWith("[") && !string.endsWith("]")
}

fun appendJson(context: Context, zipName:String,quizData: QuizData){
    val jsonFileH = context.filesDir.resolve("$BASE_DIR_NAME/$HIST_JSON")
    if(jsonFileH.exists()){
        val jsonString = jsonFileH.bufferedReader().use{ it.readText() }
        if(jsonString.isNotEmpty()) {
            val data: MutableList<QuizData> = Gson().fromJson(jsonString, object : TypeToken<MutableList<QuizData>>() {}.type)
            data.add(quizData)
            var historyJson  = Gson().toJson(data)
            if(data.size==1 && isJsonArr(historyJson) ){
                historyJson = "[$historyJson]"
            }
            jsonFileH.writeText(historyJson)
        }else{
            var historyJson  = Gson().toJson(QuizData(zipName,0.0F,"0:00", LocalDateTime.now().toString()))
            if(isJsonArr(historyJson)){
                historyJson = "[$historyJson]"
            }
            jsonFileH.writeText(historyJson)
        }
    }
}

fun findAndDelete(context: Context,dir: File){
    val jsonFile = context.filesDir.resolve("$BASE_DIR_NAME/$HIST_JSON")
    if(jsonFile.exists()){
        val jsonString = jsonFile.bufferedReader().use{ it.readText() }
        if(jsonString.isNotEmpty()) {
            val data: MutableList<QuizData> = Gson().fromJson(jsonString, object : TypeToken<MutableList<QuizData>>() {}.type)
            val updatedList = data.filter { it.name != dir.name }
            jsonFile.writeText(Gson().toJson(updatedList))
        }
    }
}
