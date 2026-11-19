package com.example.propcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.propcare.domain.model.UserRole
import com.example.propcare.navigation.PropCareNavGraph
import com.example.propcare.ui.theme.PropCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PropCareTheme {
                val navController = rememberNavController()
                var currentRole by rememberSaveable { mutableStateOf(UserRole.TENANT) }

                PropCareNavGraph(
                    navController = navController,
                    currentRole = currentRole,
                    onRoleChange = { currentRole = it }
                )
            }
        }
    }
}