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
import com.aaryan.senitel.models.Host

@Composable
fun RecentActivity(
    hosts: List<Host> = emptyList()
) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .padding(16.dp)

    ) {

        Text(
            text = "DISCOVERED HOSTS (${hosts.size})",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (hosts.isEmpty()) {
            Text(
                text = "No active hosts discovered yet.",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            hosts.takeLast(10).reversed().forEach { host ->
                ActivityItem(
                    ip = host.ip,
                    hostname = host.hostname ?: "Unknown Host",
                    ports = host.openPorts.joinToString(", ")
                )
                HorizontalDivider(color = Color.DarkGray)
            }
        }

    }

}

@Composable
fun ActivityItem(
    ip: String,
    hostname: String,
    ports: String
) {

    Column(
        modifier = Modifier.padding(vertical = 10.dp)
    ) {

        Text(
            text = ip,
            color = Color.Cyan,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = hostname,
            color = Color.White
        )
        
        if (ports.isNotEmpty()) {
            Text(
                text = "Ports: $ports",
                color = Color.Green,
                fontFamily = FontFamily.Monospace
            )
        }

    }

}
