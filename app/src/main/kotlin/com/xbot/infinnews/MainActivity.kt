package com.xbot.infinnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xbot.designsystem.theme.InfinNewsTheme
import com.xbot.infinnews.ui.InfinNewsApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfinNewsTheme {
                InfinNewsApp()
            }
        }
    }
}
