package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.utils.ScanType
import com.aaryan.senitel.utils.scanTypes

@Composable
fun ScanSelector(

    selectedScan: ScanType,

    onScanSelected: (ScanType) -> Unit

) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    expanded = !expanded

                }
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(

                text = "> ${selectedScan.name}",

                color = Color.White,

                fontFamily = FontFamily.Monospace,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(

                text = if (expanded) "-" else "+",

                color = Color.White,

                fontWeight = FontWeight.Bold

            )

        }

        if (expanded) {

            HorizontalDivider(color = Color.DarkGray)

            scanTypes.forEach { scan ->

                if (scan != selectedScan) {

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                onScanSelected(scan)

                                expanded = false

                            }
                            .padding(14.dp)

                    ) {

                        Text(

                            text = scan.name,

                            color = Color.LightGray,

                            fontFamily = FontFamily.Monospace

                        )

                    }

                    HorizontalDivider(color = Color.DarkGray)

                }

            }

        }

    }

}