package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavigationPanel() {
    Column(
        modifier = Modifier
            .width(72.dp),
        verticalArrangement = Arrangement.Top
    ) {
        NavItem("🎯", "SCAN", isSelected = true)
        NavItem("🖥️", "DEVICES")
        NavItem("📄", "REPORTS")
        NavItem("🔧", "TOOLS")
        NavItem("⠿", "CTOS APPS")
        NavItem("💬", "MESSAGES")
        NavItem("⚙️", "SETTINGS")
    }
}

@Composable
private fun NavItem(icon: String, label: String, isSelected: Boolean = false) {
    val tint = if (isSelected) Color(0xFF33AAFF) else Color.White.copy(alpha = 0.5f)
    val borderColor = if (isSelected) Color(0xFF33AAFF) else Color.White.copy(alpha = 0.2f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(1.dp, borderColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = icon,
                fontSize = 20.sp,
                color = tint
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = tint,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}
