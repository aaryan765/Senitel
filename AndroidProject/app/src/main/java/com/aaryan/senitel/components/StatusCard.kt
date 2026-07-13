package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaryan.senitel.viewmodel.DashboardViewModel

@Composable
fun StatusCard(viewModel: DashboardViewModel) {
    val operatorName by viewModel.operatorName.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EditableStatusLine("USER", operatorName) { viewModel.updateOperatorName(it) }
            StatusLine("DEVICE", "MOBILE TERMINAL")
            StatusLine("STATUS", "CONNECTED", Color(0xFF33AAFF))
        }

        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.3f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("❖", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "192.168.1.15",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun StatusLine(label: String, value: String, valueColor: Color = Color.White) {
    Row {
        Text(
            text = label.padEnd(8),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = ": ",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun EditableStatusLine(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label.padEnd(8),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = ": ",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(Color.White),
            singleLine = true
        )
    }
}
