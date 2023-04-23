package com.app.sambaaccesssmb.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.ui.design.SmbTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SmbApp(
    windowSizeClass: WindowSizeClass,
    smbAppState: SmbAppState = rememberSmbAppState(windowSizeClass = windowSizeClass)
) {
    SmbTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    shape = CircleShape,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(id = R.string.add_servers))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SmbNavHost(navController = smbAppState.navController)
            }
        }
    }
}
