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

        ScanSelector()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DESCRIPTION",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Discover live hosts on the selected network.",
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(

            onClick = {

                when {

                    isValidIPv4(target) -> {
                        validationMessage = "✓ Valid IPv4 Address"
                        validationColor = Color.Green
                    }

                    isValidCIDR(target) -> {
                        validationMessage = "✓ Valid Network Range"
                        validationColor = Color.Green
                    }

                    isValidHostname(target) -> {
                        validationMessage = "✓ Valid Hostname"
                        validationColor = Color.Green
                    }

                    else -> {
                        validationMessage = "✗ Invalid Target"
                        validationColor = Color.Red
                    }

                }

            }

        ) {

            Text("RUN SCAN")

        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = validationMessage,
            color = validationColor
        )

    }

}