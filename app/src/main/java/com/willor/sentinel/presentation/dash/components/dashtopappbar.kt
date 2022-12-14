package com.willor.sentinel.presentation.dash.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.willor.sentinel.ui.theme.SentinelTheme
import com.willor.sentinel.ui.theme.Sizes


@Composable
fun DashTopAppBar(
    settingsOnClick: () -> Unit,
    logoutOnClick: () -> Unit,
) {

    SentinelTheme {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(Sizes.TOP_APP_BAR_HEIGHT)
                .padding(Sizes.HORIZONTAL_EDGE_PADDING, Sizes.VERTICAL_EDGE_PADDING),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Settings navigation
            Button(
                onClick = {
                    Log.d("INFO", "Dashboard Top App bar logout clicked")
                    logoutOnClick()
                }
            ){
                Icon(Icons.Filled.Logout, "log off", tint = Color.White)
            }

            // App bar text
            Text(
                "Sentinel Dashboard",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                color = Color.White
            )

            // Settings navigation
            Button(
                onClick = {
                    Log.d("INFO", "Dashboard Top App bar nav Icon clicked")
                    settingsOnClick()
                }
            ){
                Icon(Icons.Filled.Settings, "settings", tint = Color.White)
            }
        }
    }
}

