package com.aaryan.senitel.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
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
import com.aaryan.senitel.ui.theme.CtosBlue
import com.aaryan.senitel.ui.theme.CtosSurface

@Composable
fun RecentActivity(logs: List<ActivityLog> = emptyList()) {
    var expanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new logs arrive if expanded
    LaunchedEffect(logs.size) {
        if (expanded && logs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(CtosSurface.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (logs.isNotEmpty()) CtosBlue else Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CTOS FEED",
                    color = Color.White.copy(alpha = 0.7f),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                )
            }
            Text(
                text = if (expanded) "CLOSE_TERMINAL" else "OPEN_TERMINAL >",
                color = CtosBlue,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall
            )
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.1f)
        )
        
        if (!expanded) {
            Box(modifier = Modifier.height(24.dp)) {
                if (logs.isEmpty()) {
                    Text(
                        text = "IDLE_STATE: AWAITING INPUT...",
                        color = CtosBlue.copy(alpha = 0.5f),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    )
                } else {
                    val latestLog = logs.last()
                    Text(
                        text = "[${latestLog.timestamp}] > ${latestLog.message.uppercase()}",
                        color = CtosBlue,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        } else {
            Box(modifier = Modifier.height(250.dp)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false // We'll show newest at the bottom or top? 
                    // Usually terminals show newest at bottom. But mobile feeds often newest at top.
                    // Let's do newest at TOP for ease of reading on mobile.
                ) {
                    items(logs.asReversed(), key = { it.id }) { log ->
                        LogEntry(log)
                    }
                }
            }
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.1f)
        )
        
        FeedFooter()
    }
}

@Composable
private fun LogEntry(log: ActivityLog) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row {
                Text(
                    text = "[${log.timestamp}]",
                    color = Color.White.copy(alpha = 0.4f),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "> ${log.message.uppercase()}",
                    color = CtosBlue,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FeedFooter() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SYSTEM_STATUS:",
                color = Color.White.copy(alpha = 0.4f),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "ENCRYPTED",
                color = Color(0xFF44FF44).copy(alpha = 0.6f),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                fontSize = 9.sp
            )
        }
        Text(
            text = "CTOS_NODE_783B",
            color = Color.White.copy(alpha = 0.3f),
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontSize = 9.sp
        )
    }
}
