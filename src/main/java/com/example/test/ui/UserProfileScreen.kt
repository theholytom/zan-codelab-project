package com.example.test.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.PlaygroundRepository
import com.example.test.R
import com.example.test.UserProfile
import com.example.test.ui.theme.TestTheme

@Composable
fun UserProfileScreen(
    profile: UserProfile,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showImages by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            UserProfileAppBar(onEditClick = onEditClick)
        },
        bottomBar = { UserProfileBottomNavigation() },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        UserProfileContent(
            profile = profile,
            showImages = showImages,
            onShowImagesChange = { showImages = it },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserProfileScreenPreview() {
    TestTheme {
        UserProfileScreen(
            profile = UserProfile.DEFAULT,
            onEditClick = { }
        )
    }
}

@Composable
fun UserProfileBottomNavigation() {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.swing_icon),
                    contentDescription = "Playgrounds",
                    modifier = Modifier.size(24.dp),
                )
            },
            label = { Text("Playgrounds") },
            selected = false,
            onClick = {},
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.person_24px),
                    contentDescription = "User profile",
                    modifier = Modifier.size(24.dp),
                )
            },
            label = { Text("User profile") },
            selected = true,
            onClick = {},
        )
    }
}

@Composable
fun UserProfileContent(
    profile: UserProfile,
    showImages: Boolean,
    onShowImagesChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    val scrollableColumnState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollableColumnState),
    ) {
        Image(
            painter = painterResource(R.drawable.profile_placeholder),
            contentDescription = "User profile icon",
            modifier = Modifier
                .padding(top = 24.dp)
                .height(250.dp),
        )
        Text(
            text = "${profile.name} ${profile.surname}",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 32.dp),
        )
        Text(
            text = "${profile.numberOfKids} kids",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Notifications: ${profile.notificationFrequency.displayName()}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "LAST VISITED PLAYGROUNDS",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = showImages,
                onCheckedChange = onShowImagesChange,
            )
        }

        PlaygroundRepository.samplePlaygrounds.forEach {
            playground -> PlaygroundItem(
                showImage = showImages,
                modifier = Modifier.padding(top = 16.dp),
                name = playground.name,
                address = playground.address,
                features = playground.features,
                imageRes = playground.imageRes
            )
        }
    }
}

@Composable
fun PlaygroundItem(
    showImage: Boolean,
    modifier: Modifier = Modifier,
    name: String,
    address: String,
    features: String,
    imageRes: Int
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (showImage) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Playground $name",
                modifier = Modifier.height(80.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            SingleLineText(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp),
            )
            SingleLineText(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp),
            )
            SingleLineText(
                text = features,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp),
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun SingleLineText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = style,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileAppBar(
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = "User Profile") },
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "Edit user profile",
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(R.drawable.logout_24px),
                    contentDescription = "Log out",
                )
            }
        },
        modifier = modifier,
    )
}
