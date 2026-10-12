package com.example.splitin.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.splitin.data.model.SplitHistory
import com.example.splitin.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreens(
    navController: NavController,
    onNext: () -> Unit = {},
    viewModel: ViewModelHome = hiltViewModel()
) {
    var activityName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) {
                    Text("OK", color = PrimaryOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val historyList by viewModel.historyList.collectAsState()

    Scaffold(
        containerColor = AppBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Header Section
            item {
                HomeHeader()
            }
            // 2. Main Card Section
            item {
                CreateSplitCard(
                    activityName = activityName,
                    onActivityNameChange = { activityName = it },
                    selectedDate = selectedDate,
                    onDatePickerClick = { showDatePicker = true },
                    onCreateSplit = {
                        viewModel.saveSplitDetail(activityName, selectedDate)
                        onNext()
                    }
                )
            }
            // 3. Quick Shortcut Section
            item {
                QuickShortcutSection(
                    onShortcutClick = { activityName = it }
                )
            }
            // 4. Recent Split Section
            item {
                RecentSplitSection(historyList = historyList)
            }
            // 5. Motivational Card
            item {
                MotivationalCard()
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD1A4),
                        SoftOrange
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Text(
                text = "Halo 👋",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = PrimaryText
                )
            )
            Text(
                text = "Siap bagi tagihan hari ini?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    fontSize = 14.sp
                )
            )
        }
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Hero Illustration",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 10.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CreateSplitCard(
    activityName: String,
    onActivityNameChange: (String) -> Unit,
    selectedDate: Long,
    onDatePickerClick: () -> Unit,
    onCreateSplit: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = sdf.format(Date(selectedDate))

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-40).dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Mau split apa hari ini?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PrimaryText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_agenda),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .offset(y = (-10).dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = activityName,
                    onValueChange = onActivityNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contoh: Makan KFC", color = SecondaryText, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(20.dp))
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = Color(0xFFEEEEEE),
                        unfocusedBorderColor = Color(0xFFEEEEEE)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    onClick = onDatePickerClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = InputBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PrimaryText,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val isButtonEnabled = activityName.trim().isNotEmpty()

                Button(
                    onClick = onCreateSplit,
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange,
                        disabledContainerColor = Color(0xFFE5E5E5)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Buat Split Baru",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isButtonEnabled) Color.White else SecondaryText
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isButtonEnabled) Color.White.copy(alpha = 0.2f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isButtonEnabled) Color.White else SecondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickShortcutSection(onShortcutClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .offset(y = (-20).dp)
    ) {
        Text(
            text = "Shortcut Cepat",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShortcutCard(
                title = "Makan",
                icon = Icons.Default.Restaurant, 
                iconTint = PrimaryOrange,
                iconBg = Color(0xFFFFF1E5),
                modifier = Modifier.weight(1f)
            ) { onShortcutClick("Makan") }

            ShortcutCard(
                title = "Nongkrong", 
                icon = Icons.Default.Coffee, 
                iconTint = Color(0xFF1A5BAE),
                iconBg = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f)
            ) { onShortcutClick("Nongkrong") }
            
            ShortcutCard(
                title = "Patungan", 
                icon = Icons.Default.Groups, 
                iconTint = Color(0xFF2E9B70),
                iconBg = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f)
            ) { onShortcutClick("Patungan") }
            
            ShortcutCard(
                title = "Belanja", 
                icon = Icons.Default.ShoppingBag, 
                iconTint = Color(0xFFFF7A00),
                iconBg = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
            ) { onShortcutClick("Belanja") }
        }
    }
}

@Composable
fun ShortcutCard(
    title: String, 
    icon: ImageVector, 
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier, 
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.85f),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun RecentSplitSection(historyList: List<SplitHistory>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Riwayat Split Terakhir",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada riwayat split",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryText
                        )
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                historyList.forEach { history ->
                    HistoryItemCard(history)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(history: SplitHistory) {
    val initial = history.activityName.firstOrNull()?.uppercase() ?: "?"
    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val formattedDate = sdf.format(Date(history.date))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SoftOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.activityName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                )
                Text(
                    text = "${history.totalPeople} orang • $formattedDate",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecondaryText
                    )
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(history.totalAmount),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SuccessBg
                ) {
                    Text(
                        text = "Selesai",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SuccessGreen,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MotivationalCard() {
    Surface(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SoftOrange
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_save),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Splitin, patungan jadi\nlebih mudah dan adil!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

private fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace("Rp", "Rp").replace(",00", "")
}
