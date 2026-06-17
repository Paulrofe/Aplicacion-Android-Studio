package com.fic.mobile_app_base_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fic.mobile_app_base_compose.ui.screens.interfaz_UI
import com.fic.mobile_app_base_compose.ui.theme.MobileappbasecomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileappbasecomposeTheme {
                interfaz_UI()
            }
        }
    }
}
