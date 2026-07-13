package com.aaryan.senitel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaryan.senitel.models.ScanState
import com.aaryan.senitel.utils.ScanType
import com.aaryan.senitel.utils.scanTypes
import com.aaryan.senitel.viewmodel.DashboardViewModel

@Composable
fun ScanControlPanel(dashboardViewModel: DashboardViewModel) {
    val scanState by dashboardViewModel.scanState.collectAsStateWithLifecycle()
    val selectedScan by dashboardViewModel.selectedScanType.collectAsStateWithLifecycle()
    var target by remember { mutableStateOf("192.168.1.0/24") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Target & Scan Type Section
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlField("TARGET", target, onValueChange = { target = it }, trailingIcon = "🎯")
                
                ScanTypeDropdown(
                    selectedScan = selectedScan,
                    onScanSelected = { dashboardViewModel.updateScanType(it) },
                    enabled = scanState != ScanState.SCANNING
                )
                
                Text(
                    text = "DESCRIPTION",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = selectedScan.description,
                    color = Color(0xFF33AAFF),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp
                )
            }

            // Quick Actions Section
            Column(modifier = Modifier.width(140.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                scanTypes.forEach { type ->
                    QuickActionItem(type.name) {
                        if (scanState != ScanState.SCANNING) {
                            dashboardViewModel.updateScanType(type)
                        }
                    }
                }
            }
        }

        // Run Scan Button
        Button(
            onClick = { 
                if (scanState == ScanState.SCANNING) dashboardViewModel.stopScan()
                else dashboardViewModel.startScan(target, selectedScan) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, Color(0xFF33AAFF).copy(alpha = 0.5f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF33AAFF)
            ),
            shape = RectangleShape
        ) {
            Text(
                text = if (scanState == ScanState.SCANNING) "STOP SCAN" else "RUN SCAN",
                letterSpacing = 4.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ControlField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit = {}, 
    trailingIcon: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            ),
            trailingIcon = if (trailingIcon != null) {
                { Text(text = trailingIcon, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedBorderColor = Color.White.copy(alpha = 0.6f),
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            ),
            shape = RectangleShape,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanTypeDropdown(
    selectedScan: ScanType,
    onScanSelected: (ScanType) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "SCAN TYPE",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedScan.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .menuAnchor(),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color.White.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                ),
                shape = RectangleShape,
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.Black).border(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                scanTypes.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = type.name,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        },
                        onClick = {
                            onScanSelected(type)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .border(1.dp, Color.White.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
