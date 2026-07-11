package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RecentActivity() {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .padding(16.dp)

    ) {

        Text(
            text = "RECENT ACTIVITY",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActivityItem(
            "22:13",
            "Host Discovery Started"
        )

        HorizontalDivider(color = Color.DarkGray)

        ActivityItem(
            "22:14",
            "5 Active Hosts Found"
        )

        HorizontalDivider(color = Color.DarkGray)

        ActivityItem(
            "22:16",
            "Port Scan Completed"
        )

    }

}

@Composable
fun ActivityItem(
    time: String,
    message: String
) {

    Column(
        modifier = Modifier.padding(vertical = 10.dp)
    ) {

        Text(
            text = time,
            color = Color.Gray,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = message,
            color = Color.White
        )

    }

}
