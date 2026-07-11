package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProgressPanel(

    status: String = "READY",

    progress: Float = 0f

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .padding(16.dp)

    ) {

        Text(

            text = "STATUS",

            color = Color.White,

            fontFamily = FontFamily.Monospace,

            fontWeight = FontWeight.Bold

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(

            text = status,

            color = Color.Cyan,

            fontFamily = FontFamily.Monospace

        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(

            text = "PROGRESS",

            color = Color.White,

            fontFamily = FontFamily.Monospace,

            fontWeight = FontWeight.Bold

        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(

            progress = { progress },

            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(

            text = "${(progress * 100).toInt()}%",

            color = Color.LightGray,

            fontFamily = FontFamily.Monospace

        )

    }

}

