package com.example.splitin.screens.addFriends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.splitin.data.model.Friend
import com.example.splitin.ui.theme.*

@Composable
fun AddFriendsScreen(
    navController: NavController,
    viewModel: AddFriendsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val friends by viewModel.friends.collectAsState()
    var friendName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleAddFriend() {
        val error = viewModel.addFriend(friendName)
        if (error != null) {
            errorMessage = error
        } else {
            friendName = ""
            errorMessage = null
        }
    }

    Scaffold(
        topBar = { AddFriendTopBar(onBack = onBack) },
        containerColor = AppBackground,
        bottomBar = {
            NextButton(
                enabled = friends.isNotEmpty(),
                onNext = onNext
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AddFriendInput(
                value = friendName,
                onValueChange = { 
                    friendName = it
                    errorMessage = null 
                },
                onAddClick = { handleAddFriend() },
                error = errorMessage
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Daftar Teman",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                )
                Text(
                    text = "${friends.size} Orang",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecondaryText
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (friends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum ada teman",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        )
                        Text(
                            text = "Tambahkan teman yang ikut split",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SecondaryText
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(friends, key = { it.id }) { friend ->
                        FriendItem(
                            friend = friend,
                            onRemove = { viewModel.removeFriend(friend) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Tambah Teman",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    fontSize = 20.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppBackground
        )
    )
}
@Composable
fun AddFriendInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
    error: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Masukkan nama teman...", color = SecondaryText, fontSize = 14.sp) },
            trailingIcon = {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PrimaryText,
                unfocusedTextColor = PrimaryText,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = Color(0xFFEAEAEA)
            ),
            singleLine = true,
            isError = error != null
        )
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    onRemove: () -> Unit
) {
    val initial = friend.name.firstOrNull()?.uppercase() ?: "?"
    val avatarColors = listOf(Color(0xFFFFCC80), Color(0xFF90CAF9), Color(0xFFA5D6A7), Color(0xFFF48FB1), Color(0xFFCE93D8))
    val avatarColor = remember(friend.id) { avatarColors[friend.id.hashCode().coerceAtLeast(0) % avatarColors.size] }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Name
            Text(
                text = friend.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = PrimaryText
                )
            )
            // Remove Button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = SecondaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun NextButton(
    enabled: Boolean,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        color = Color.Transparent
    ) {
        Button(
            onClick = onNext,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange,
                disabledContainerColor = Color(0xFFEAEAEA)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Selanjutnya",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (enabled) Color.White else SecondaryText
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (enabled) Color.White else SecondaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
