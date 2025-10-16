package com.xbot.infinnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xbot.designsystem.theme.InfinNewsTheme
import com.xbot.infinnews.ui.InfinNewsApp
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                InfinNewsTheme {
                    InfinNewsApp()
                }
            }
        }
    }
}
