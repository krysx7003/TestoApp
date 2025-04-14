package com.napnap.testoapp.ui.screens.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.BuildConfig

@Composable
fun InfoScreen(values: PaddingValues){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.Start,
    ){
        val context = LocalContext.current.applicationContext
        val viewModel = InfoViewModel(context)
        val items = viewModel.changeLog.collectAsState()

        val versionName = BuildConfig.VERSION_NAME
        Text("Testownik",fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)
        Text("Autor: Krzysztof Zalewa",fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
        Text("Wersja: $versionName",fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
        Text("Co nowego?",fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)
        BulletList(items = items.value)
    }
}
@Composable
fun BulletList(
    modifier: Modifier = Modifier,
    indent: Dp = 20.dp,
    lineSpacing: Dp = 0.dp,
    items: List<String>,
) {
    Column(modifier = modifier) {
        items.forEach {
            Row {
                Text(
                    text = "\u2022",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.width(indent),
                )
                Text(
                    text = it,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f, fill = true),
                )
            }
            if (lineSpacing > 0.dp && it != items.last()) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}
