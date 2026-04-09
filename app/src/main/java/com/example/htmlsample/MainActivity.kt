package com.example.htmlsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.htmlsample.ui.chat.ChatScreen
import com.example.htmlsample.ui.theme.HtmlSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HtmlSampleTheme {
                ChatScreen()
            }
        }
    }
}