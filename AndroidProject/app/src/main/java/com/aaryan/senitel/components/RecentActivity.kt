package com.aaryan.senitel.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaryan.senitel.models.ActivityLog

@Composable
fun RecentActivity(logs: List<ActivityLog> = emptyList()) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CTOS FEED ${if (expanded) "∨" else "∧"}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (expanded) "COLLAPSE" else "EXPAND >",
                color = Color(0xFF33AAFF),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (!expanded) {
            if (logs.isEmpty()) {
                Text(
                    text = "NO RECENT ACTIVITY",
                    color = Color(0xFF33AAFF).copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                logs.takeLast(1).forEach { log ->
                    Text(
                        text = "[${log.timestamp}] ${log.message.uppercase()}",
                        color = Color(0xFF33AAFF),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }
        } else {
            Box(modifier = Modifier.height(200.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true
                ) {
                    items(logs.reversed()) { log ->
                        Text(
                            text = "[${log.timestamp}]\n${log.message.uppercase()}",
                            color = Color(0xFF33AAFF),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔒 ",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = "SECURE CONNECTION ESTABLISHED",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "CTOS v2.7.4  📶",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
