package com.example.commit.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.request.form.CommissionFormScreen

class CommissionFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommitTheme {
                CommissionFormScreen()
            }
        }
    }
}