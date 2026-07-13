package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaryan.senitel.models.ScanState
import com.aaryan.senitel.utils.isValidCIDR
import com.aaryan.senitel.utils.isValidHostname
import com.aaryan.senitel.utils.isValidIPv4
import com.aaryan.senitel.utils.scanTypes
import com.aaryan.senitel.viewmodel.DashboardViewModel

@Composable
fun ScanControlPanel(
    dashboardViewModel: DashboardViewModel
) {
    var target by remember {
        mutableStateOf("192.168.1.0/24")
    }

    var validationMessage by remember {
        mutableStateOf("")
    }

    var validationColor by remember {
        mutableStateOf(Color.White)
    }

    var selectedScan by remember {
        mutableStateOf(scanTypes.first())
    }

    val scanStatus by dashboardViewModel
        .scanStatus
        .collectAsStateWithLifecycle()

    val scanState by dashboardViewModel
        .scanState
        .collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "TARGET",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = target,
            onValueChange = {
                target = it
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = scanState != ScanState.SCANNING
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SCAN TYPE",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScanSelector(
            selectedScan = selectedScan,
            onScanSelected = {
                selectedScan = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = scanState != ScanState.SCANNING,
                onClick = {
                    when {
                        isValidIPv4(target) || isValidCIDR(target) || isValidHostname(target) -> {
                            validationMessage = "✓ Valid Target"
                            validationColor = Color.Green
                            dashboardViewModel.startScan(
                                target = target,
                                scanType = selectedScan
                            )
                        }
                        else -> {
                            validationMessage = "✗ Invalid Target"
                            validationColor = Color.Red
                            dashboardViewModel.reset()
                        }
                    }
                }
            ) {
                Text(if (scanState == ScanState.SCANNING) "SCANNING..." else "RUN SCAN")
            }

            if (scanState == ScanState.SCANNING) {
                Button(
                    modifier = Modifier.weight(0.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        dashboardViewModel.stopScan()
                    }
                ) {
                    Text("STOP", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "STATUS",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = scanStatus,
            color = Color.Cyan
        )

        if (validationMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validationMessage,
                color = validationColor
            )
        }
    }
}
