package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.test.ui.UserProfileEditorScreen
import com.example.test.ui.UserProfileScreen
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TestTheme {
          // Hoist shared state here – both screens can read/write it
          var profile by remember { mutableStateOf(UserProfile.DEFAULT) }
          var showEditor by remember { mutableStateOf(false) }

          if (showEditor) {
              UserProfileEditorScreen(
                  profile = profile,
                  onSave = { updatedProfile ->
                      profile = updatedProfile
                      showEditor = false
                  },
                  onCancel = { showEditor = false },
              )
          } else {
              UserProfileScreen(
                  profile = profile,
                  onEditClick = { showEditor = true },
              )
          }
      }
    }
  }
}
