package com.example.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.NotificationFrequency
import com.example.test.R
import com.example.test.UserProfile
import com.example.test.ui.theme.TestTheme

@Composable
fun UserProfileEditorScreen(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var editedName by remember { mutableStateOf(profile.name) }
    var editedSurname by remember { mutableStateOf(profile.surname) }
    var editedNumberOfKids by remember { mutableStateOf(profile.numberOfKids) }

    var editedFrequency by remember { mutableStateOf(profile.notificationFrequency) }

    Scaffold(
        topBar = {
            UserProfileEditorAppBar(
                onCancel = onCancel,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onSave(
                        profile.copy(
                            name = editedName,
                            surname = editedSurname,
                            numberOfKids = editedNumberOfKids,
                            notificationFrequency = editedFrequency,
                        )
                    )
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.save_24px),
                    contentDescription = "Save profile",
                )
            }
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        UserProfileEditorContent(
            name = editedName,
            onNameChange = { editedName = it },
            surname = editedSurname,
            onSurnameChange = { editedSurname = it },
            numberOfKids = editedNumberOfKids,
            onNumberOfKidsChange = { editedNumberOfKids = it },
            modifier = Modifier.padding(innerPadding),
            notificationFrequency = editedFrequency,
            onNotificationFrequencyChange = {editedFrequency = it}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserProfileEditorPreview() {
    TestTheme() {
        UserProfileEditorScreen(
            profile = UserProfile.DEFAULT,
            onSave = {},
            onCancel = {},
        )
    }
}

@Composable
fun UserProfileEditorContent(
    name: String,
    onNameChange: (String) -> Unit,
    surname: String,
    onSurnameChange: (String) -> Unit,
    numberOfKids: Int,
    onNumberOfKidsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    notificationFrequency: NotificationFrequency,
    onNotificationFrequencyChange: (NotificationFrequency) -> Unit,
) {
    val textFieldModifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Image(
            painter = painterResource(R.drawable.profile_placeholder),
            contentDescription = "User profile icon",
            modifier = Modifier
                .padding(top = 24.dp)
                .height(250.dp),
        )
        UserProfileEditorTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Name",
            icon = painterResource(R.drawable.cancel_24px),
            modifier = textFieldModifier,
        )
        UserProfileEditorTextField(
            value = surname,
            onValueChange = onSurnameChange,
            label = "Surname",
            icon = painterResource(R.drawable.cancel_24px),
            modifier = textFieldModifier,
        )
        Text(
            text = "Number of kids: $numberOfKids",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
        Slider(
            value = numberOfKids.toFloat(),
            onValueChange = { onNumberOfKidsChange(it.toInt()) },
            valueRange = 0f..10f,
            steps = 9,   // 9 intermediate steps → 11 positions (0,1,...,10)
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )
        Text(
            text = "Send notifications",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
        )

// One RadioButton per enum value
        NotificationFrequency.entries.forEach { frequency ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNotificationFrequencyChange(frequency) },
            ) {
                RadioButton(
                    selected = notificationFrequency == frequency,
                    onClick = { onNotificationFrequencyChange(frequency) },
                )
                Text(
                    text = frequency.displayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileEditorAppBar(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = "Edit profile") },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = "Cancel editing",
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
fun UserProfileEditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    onTrailingIconClick: () -> Unit = {},
    label: String = "",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        trailingIcon = {
            IconButton(onClick = onTrailingIconClick) {
                Icon(
                    painter = icon,
                    contentDescription = "Clear text",
                )
            }
        },
    )
}
