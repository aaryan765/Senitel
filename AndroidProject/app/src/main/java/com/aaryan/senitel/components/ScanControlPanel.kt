package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.utils.isValidCIDR
import com.aaryan.senitel.utils.isValidHostname
import com.aaryan.senitel.utils.isValidIPv4
import com.aaryan.senitel.utils.scanTypes

@Composable
fun ScanControlPanel() {

    var target by remember {
        mutableStateOf("192.168.1.0/24")
    }

    var validationMessage by remember {
        mutableStateOf("")
    }

    var validationColor by remember {
        mutableStateOf(Color.White)
    }

    // NEW
    var scanStatus by remember {
        mutableStateOf("READY")
    }

    var selectedScan by remember {
        mutableStateOf(scanTypes.first())
    }

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
            modifier = Modifier.fillMaxWidth()
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DESCRIPTION",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedScan.description,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(

            onClick = {

                when {

                    isValidIPv4(target) -> {

                        validationMessage = "✓ Valid IPv4 Address"
                        validationColor = Color.Green

                        scanStatus = "Starting ${selectedScan.name}..."

                    }

                    isValidCIDR(target) -> {

                        validationMessage = "✓ Valid Network Range"
                        validationColor = Color.Green

                        scanStatus = "Starting ${selectedScan.name}..."

                    }

                    isValidHostname(target) -> {

                        validationMessage = "✓ Valid Hostname"
                        validationColor = Color.Green

                        scanStatus = "Starting ${selectedScan.name}..."

                    }

                    else -> {

                        validationMessage = "✗ Invalid Target"
                        validationColor = Color.Red

                        scanStatus = "READY"

                    }

                }

            }

        ) {

            Text("RUN SCAN")

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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = validationMessage,
            color = validationColor
        )

    }

}