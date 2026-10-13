package com.example.splitin.screens.addItems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.splitin.data.model.Friend
import com.example.splitin.data.model.OrderItem
import com.example.splitin.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

@Composable
fun AddItemsScreen(
    navController: NavController,
    viewModel: AddItemsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val friendsFromRepo by viewModel.friends.collectAsState()
    val items by viewModel.items.collectAsState()

    // Form States
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    val selectedEaterIds = remember { mutableStateListOf<String>() }

    // Validation States
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var eaterError by remember { mutableStateOf<String?>(null) }

    fun addItem() {
        var hasError = false

        if (itemName.trim().isEmpty()) {
            nameError = "Nama item wajib diisi"
            hasError = true
        } else {
            nameError = null
        }

        val price = itemPrice.toLongOrNull() ?: 0L
        if (price <= 0) {
            priceError = "Masukkan harga item"
            hasError = true
        } else {
            priceError = null
        }

        if (selectedEaterIds.isEmpty()) {
            eaterError = "Pilih minimal satu orang"
            hasError = true
        } else {
            eaterError = null
        }

        if (!hasError) {
            val error = viewModel.addItem(itemName.trim(), price, selectedEaterIds.toList())
            if (error == null) {
                // Reset Form
                itemName = ""
                itemPrice = ""
                selectedEaterIds.clear()
                nameError = null
                priceError = null
                eaterError = null
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun handleContinue() {
        val validationError = viewModel.validateItemsAndFriends()
        if (validationError != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(validationError)
            }
        } else {
            onContinue()
        }
    }

    Scaffold(
        topBar = { AddItemTopBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBackground,
        bottomBar = {
            ContinueCalculationButton(
                enabled = items.isNotEmpty(),
                onContinue = { handleContinue() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                // Form Card Section
                item {
                    AddItemForm(
                        itemName = itemName,
                        onItemNameChange = { itemName = it; nameError = null },
                        itemPrice = itemPrice,
                        onItemPriceChange = { itemPrice = it; priceError = null },
                        friends = friendsFromRepo,
                        selectedEaterIds = selectedEaterIds,
                        onEaterToggle = { id ->
                            if (selectedEaterIds.contains(id)) selectedEaterIds.remove(id)
                            else selectedEaterIds.add(id)
                            eaterError = null
                        },
                        onAddItem = { addItem() },
                        nameError = nameError,
                        priceError = priceError,
                        eaterError = eaterError
                    )
                }
                // Header Section for Order List
                item {
                    Text(
                        text = "Daftar Pesanan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (items.isEmpty()) {
                    item {
                        EmptyOrderState()
                    }
                } else {
                    items(items, key = { it.id }) { item ->
                        OrderItemCard(
                            item = item,
                            friends = friendsFromRepo,
                            onRemove = { viewModel.removeItem(item) }
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Tambah Item",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF5C2C00)
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF5C2C00)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppBackground
        )
    )
}

@Composable
fun AddItemForm(
    itemName: String,
    onItemNameChange: (String) -> Unit,
    itemPrice: String,
    onItemPriceChange: (String) -> Unit,
    friends: List<Friend>,
    selectedEaterIds: List<String>,
    onEaterToggle: (String) -> Unit,
    onAddItem: () -> Unit,
    nameError: String?,
    priceError: String?,
    eaterError: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Nama Item
            Text(
                text = "Nama Item",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = itemName,
                onValueChange = onItemNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Mis: Nasi Goreng Spesial", color = Color(0xFF9E9E9E), fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryText,
                    unfocusedTextColor = PrimaryText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                singleLine = true,
                isError = nameError != null
            )
            if (nameError != null) {
                Text(nameError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Harga
            Text(
                text = "Harga (Rp)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = itemPrice,
                onValueChange = { if (it.all { char -> char.isDigit() }) onItemPriceChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "0",
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryText,
                    unfocusedTextColor = PrimaryText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = priceError != null
            )
            if (priceError != null) {
                Text(priceError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Siapa yang makan
            Text(
                text = "Siapa yang makan?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            OptInFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                friends.forEach { friend ->
                    val isSelected = selectedEaterIds.contains(friend.id)
                    FriendPill(
                        name = friend.name,
                        isSelected = isSelected,
                        onClick = { onEaterToggle(friend.id) }
                    )
                }
            }
            if (eaterError != null) {
                Text(
                    text = eaterError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isFormValid = itemName.trim().isNotEmpty() &&
                    (itemPrice.toLongOrNull() ?: 0L) > 0 &&
                    selectedEaterIds.isNotEmpty() &&
                    nameError == null && priceError == null && eaterError == null

            // Button "+ Tambahkan Item"
            Button(
                onClick = onAddItem,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftOrange,
                    disabledContainerColor = Color(0xFFE5E5E5)
                ),
                elevation = null
            ) {
                Text(
                    text = "+  Tambahkan Item",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isFormValid) PrimaryOrange else Color(0xFF9E9E9E),
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
fun FriendPill(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) PrimaryOrange else Color(0xFFE5E5E5),
        contentColor = if (isSelected) Color.White else Color(0xFF555555)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun OrderItemCard(
    item: OrderItem,
    friends: List<Friend>,
    onRemove: () -> Unit
) {
    val eaterNames = friends.filter { item.eaterIds.contains(it.id) }.joinToString(", ") { it.name }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    )
                    Text(
                        text = formatCurrency(item.price),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = eaterNames,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = SecondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOrderState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Belum ada pesanan",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        )
        Text(
            text = "Tambahkan makanan atau minuman yang dipesan",
            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
        )
    }
}

@Composable
fun ContinueCalculationButton(
    enabled: Boolean,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        color = Color.Transparent
    ) {
        Button(
            onClick = onContinue,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange,
                disabledContainerColor = Color(0xFFE5E5E5)
            )
        ) {
            Text(
                text = "Lanjut ke Perhitungan",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (enabled) Color.White else SecondaryText
                )
            )
        }
    }
}

fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace("Rp", "Rp").replace(",00", "")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptInFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
