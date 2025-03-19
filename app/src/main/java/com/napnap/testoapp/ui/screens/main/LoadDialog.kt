package com.napnap.testoapp.ui.screens.main

import Folder
import Github
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.Quiz
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.data.stores.SettingsStore
import java.io.BufferedInputStream
import java.io.File
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


@Composable
fun LoadDialog(
    onDismiss:()->Unit,
    getUri: ActivityResultLauncher<String>
){

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("Wczytaj z:",
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = { loadFromGithub() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = Github,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("GitHub",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            getUri.launch("application/zip")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = Folder,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Pamięć",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text("Anuluj",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

fun loadFromGithub(){
    Log.i("LoadQuiz","Loading Quiz from GitHub ")
}

fun emptyDir(dir:File,context: Context){
    //FIXME - Usuwanie jsona można u ogólnić
    val jsonFile = context.filesDir.resolve("$baseDirName/history.json")
    if(jsonFile.exists()){
        val jsonString = jsonFile.bufferedReader().use{ it.readText() }
        if(jsonString.isNotEmpty()) {
            val data: MutableList<Quiz> = Gson().fromJson(jsonString, object : TypeToken<MutableList<Quiz>>() {}.type)
            val updatedList = data.filter { it.name != dir.name }
            jsonFile.writeText(Gson().toJson(updatedList))
        }
    }

    if(dir.isDirectory){
        val items = dir.listFiles()
        if (items != null) {
            for (file in items) {
                file.delete()
            }
        }
    }
}

fun handleZipFile(uri: Uri,context: Context){

    //FIXME - HECKING CHONKER
    val settingsStore = SettingsStore()
    settingsStore.read("repeatAmount",context)
    Log.i("HandlingZip","Starting to unzip ${uri.path.toString()}")
    if(uri.path.toString().isNotEmpty()){
        val outputDir = File(context.filesDir,baseDirName)
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
                            zipName = entry.name
                            File(file,"progress.json").createNewFile()
                        } else {
                            file.outputStream().use { fileOutput ->
                                zipInputStream.copyTo(fileOutput)
                            }
                            //TODO - liczba powtórzeń powinna być zmienna
                            if (!file.name.endsWith(".json")){
                                questionFileList.add(QuestionFile(file.name,2))
                            }else{}
                        }
                    }
                }
                zipInputStream.close()
                Log.i("HandlingZip", "Unzipped successfully to ${outputDir.absolutePath}")

                //FIXME - Pisanie do jsona można u ogólnić
                val questionJson = Gson().toJson(questionFileList)
                val jsonFileQ = context.filesDir.resolve("$baseDirName/$zipName/progress.json")
                jsonFileQ.writeText(questionJson)

                //FIXME - Dodawanie do jsona można u ogólnić
                val jsonFileH = context.filesDir.resolve("$baseDirName/history.json")
                if(jsonFileH.exists()){
                    val jsonString = jsonFileH.bufferedReader().use{ it.readText() }
                    if(jsonString.isNotEmpty()) {
                        val data: MutableList<Quiz> = Gson().fromJson(jsonString, object : TypeToken<MutableList<Quiz>>() {}.type)
                        data.add(Quiz(zipName,0.0,"0:00", LocalDate.now().toString()))
                        var historyJson  = Gson().toJson(data)
                        if(data.size==1){
                            historyJson = "[$historyJson]"
                        }
                        jsonFileH.writeText(historyJson)
                    }else{
                        var historyJson  = Gson().toJson(Quiz(zipName,0.0,"0:00", LocalDate.now().toString()))
                        historyJson = "[$historyJson]"
                        jsonFileH.writeText(historyJson)
                    }
                }
            } ?: Log.e("HandlingZip", "Failed to open input stream")

        } catch (e: Exception) {
            Log.e("HandlingZip", "Error unzipping file: ${e.message}")
        }
    }

}