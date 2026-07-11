package com.aaryan.senitel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.components.Header

@Composable
fun DashboardScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        // Reusable Header
        Header()

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = Color.Black)

        Spacer(modifier = Modifier.height(12.dp))

        // Information Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
                .padding(12.dp)
        ) {

            Text("OPERATOR : RYAN")

            Spacer(modifier = Modifier.height(6.dp))

            Text("SERVER     : DELL G15")

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "STATUS     : CONNECTED",
                color = Color(0xFF00AA00),
                fontWeight = FontWeight.Bold
            )
        }
    }
}