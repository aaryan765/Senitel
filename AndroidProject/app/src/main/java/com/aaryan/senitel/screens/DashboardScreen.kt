package com.aaryan.senitel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.components.Header
import com.aaryan.senitel.components.NavigationPanel
import com.aaryan.senitel.components.ProgressPanel
import com.aaryan.senitel.components.RadarPanel
import com.aaryan.senitel.components.RecentActivity
import com.aaryan.senitel.components.ScanControlPanel
import com.aaryan.senitel.components.StatusCard

@Composable
fun DashboardScreen() {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {

        Header()

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        StatusCard()

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            NavigationPanel()

            Spacer(modifier = Modifier.width(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(320.dp)
            ) {

                RadarPanel()

            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        ScanControlPanel()

        Spacer(modifier = Modifier.height(20.dp))

        ProgressPanel()

        Spacer(modifier = Modifier.height(20.dp))

        RecentActivity()

    }

}