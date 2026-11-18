package com.example.cse226_ca2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.cse226_ca2.ui.TaskEntryRoute
import com.example.cse226_ca2.ui.theme.CSE226_CA2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CSE226_CA2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskEntryRoute(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
