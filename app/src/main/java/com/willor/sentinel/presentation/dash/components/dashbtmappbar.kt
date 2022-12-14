package com.willor.sentinel.presentation.dash.components

import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun DashBottomAppBar(
    editWatchlistOnClick: () -> Unit,
    startSentinelOnClick: () -> Unit
){

    BottomAppBar(
        backgroundColor = MaterialTheme.colorScheme.primary,
    ) {

        // Edit Sentinel Watchlist
        BottomNavigationItem(
            selected = false,
            label = {
                Text("Edit Watchlist", fontSize = 9.sp, color = Color.White)
            },
            alwaysShowLabel = true,
            onClick = {
                editWatchlistOnClick()
            },
            icon = {
                Icon(Icons.Filled.EditNote, "edit watchlist", tint = Color.White)
            }
        )

        // Sentinel Start Selected
        BottomNavigationItem(
            selected = false,
            label = {
                    Text("Start Sentinel", fontSize = 9.sp, color = Color.White)
            },
            alwaysShowLabel = true,
            onClick = {
                startSentinelOnClick()
            },
            icon = {
                Icon(Icons.Filled.Radar, "start sentinel", tint = Color.White)
            }
        )
    }
}