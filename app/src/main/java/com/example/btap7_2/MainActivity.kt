package com.example.btap7_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.btap7_2.ui.theme.Btap7_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Btap7_2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LightControlScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LightControlScreen(
    modifier: Modifier = Modifier,
    viewModel: LightControlViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Điều Khiển Đèn",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )
        
        // Server Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Cấu hình Server",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                OutlinedTextField(
                    value = state.serverAddress,
                    onValueChange = { viewModel.updateServerAddress(it) },
                    label = { Text("Địa chỉ IP") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isConnected,
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = state.serverPort,
                    onValueChange = { viewModel.updateServerPort(it) },
                    label = { Text("Cổng") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isConnected,
                    singleLine = true
                )
            }
        }
        
        // Connection Button
        Button(
            onClick = {
                if (state.isConnected) {
                    viewModel.disconnect()
                } else {
                    viewModel.connect()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isConnected) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (state.isConnected) "Ngắt kết nối" else "Kết nối",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Light Bulb Display
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = if (state.isLightOn) Color(0xFFFFEB3B) else Color.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Đèn",
                modifier = Modifier.size(120.dp),
                tint = if (state.isLightOn) Color.White else Color.DarkGray
            )
        }
        
        Text(
            text = if (state.isLightOn) "Đèn ĐANG BẬT" else "Đèn ĐANG TẮT",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (state.isLightOn) Color(0xFFFFA000) else Color.Gray
        )
        
        // Control Button
        Button(
            onClick = { viewModel.toggleLight() },
            enabled = state.isConnected,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isLightOn) 
                    Color(0xFFFF5722) 
                else 
                    Color(0xFF4CAF50)
            )
        ) {
            Text(
                text = if (state.isLightOn) "TẮT ĐÈN" else "BẬT ĐÈN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Status Message
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Trạng thái:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.message.ifEmpty { "Chưa có thông tin" },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
