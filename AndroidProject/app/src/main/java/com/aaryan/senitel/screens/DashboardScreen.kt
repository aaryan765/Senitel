package com.aaryan.senitel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaryan.senitel.components.*
import com.aaryan.senitel.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen() {
    val dashboardViewModel: DashboardViewModel = viewModel()
    val scrollState = rememberScrollState()
    
    val scanStatus by dashboardViewModel.scanStatus.collectAsStateWithLifecycle()
    val progress by dashboardViewModel.progress.collectAsStateWithLifecycle()
    val activityLogs by dashboardViewModel.activityLogs.collectAsStateWithLifecycle()
    val scannedCount by dashboardViewModel.scannedCount.collectAsStateWithLifecycle()
    val totalToScan by dashboardViewModel.totalToScan.collectAsStateWithLifecycle()
    val elapsedTime by dashboardViewModel.elapsedTime.collectAsStateWithLifecycle()
    val hosts by dashboardViewModel.hosts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Header()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        StatusCard(dashboardViewModel)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            NavigationPanel()
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    RadarPanel(hosts = hosts)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ProgressPanel(
                    status = scanStatus,
                    progress = progress,
                    scanned = scannedCount,
                    total = totalToScan,
                    elapsedMs = elapsedTime
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ScanControlPanel(dashboardViewModel = dashboardViewModel)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        RecentActivity(logs = activityLogs)
    }
}
