package com.example.splitin.data.repository

import android.content.Context
import com.example.splitin.data.model.Friend
import com.example.splitin.data.model.FriendSplitResult
import com.example.splitin.data.model.OrderItem
import com.example.splitin.data.model.SplitHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SplitinRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("splitin_prefs", Context.MODE_PRIVATE)

    private var currentSplitId: String = UUID.randomUUID().toString()

    private val _activityName = MutableStateFlow<String>("")

    private val _selectedDate = MutableStateFlow<Long>(System.currentTimeMillis())

    var activityName: String = ""
    var selectedDate: Long = System.currentTimeMillis()

    var total: Long = 0
    private val _totalAmount = MutableStateFlow<Long>(0L)

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _items = MutableStateFlow<List<OrderItem>>(emptyList())
    val items: StateFlow<List<OrderItem>> = _items.asStateFlow()

    private val _taxPercentage = MutableStateFlow<Int>(11)
    val taxPercentage: StateFlow<Int> = _taxPercentage.asStateFlow()

    private val _servicePercentage = MutableStateFlow<Int>(5)
    val servicePercentage: StateFlow<Int> = _servicePercentage.asStateFlow()

    private val _discountNominal = MutableStateFlow<Long>(0L)
    val discountNominal: StateFlow<Long> = _discountNominal.asStateFlow()

    private val _selectedPayer = MutableStateFlow<Friend?>(null)
    val selectedPayer: StateFlow<Friend?> = _selectedPayer.asStateFlow()

    private val _historyList = MutableStateFlow<List<SplitHistory>>(loadHistoryFromPrefs())
    val historyList: StateFlow<List<SplitHistory>> = _historyList.asStateFlow()

    private fun loadHistoryFromPrefs(): List<SplitHistory> {
        val jsonString = prefs.getString("key_history_list", null) ?: return emptyList()
        val list = mutableListOf<SplitHistory>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SplitHistory(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        activityName = obj.optString("activityName", ""),
                        date = obj.optLong("date", System.currentTimeMillis()),
                        totalPeople = obj.optInt("totalPeople", 0),
                        totalAmount = obj.optLong("totalAmount", 0L)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveHistoryToPrefs(historyList: List<SplitHistory>) {
        try {
            val jsonArray = JSONArray()
            for (history in historyList) {
                val obj = JSONObject().apply {
                    put("id", history.id)
                    put("activityName", history.activityName)
                    put("date", history.date)
                    put("totalPeople", history.totalPeople)
                    put("totalAmount", history.totalAmount)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString("key_history_list", jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetCurrentSplit() {
        currentSplitId = UUID.randomUUID().toString()
        activityName = ""
        selectedDate = System.currentTimeMillis()
        total = 0
        _activityName.value = ""
        _selectedDate.value = System.currentTimeMillis()
        _totalAmount.value = 0L
        _friends.value = emptyList()
        _items.value = emptyList()
        _taxPercentage.value = 11
        _servicePercentage.value = 5
        _discountNominal.value = 0L
        _selectedPayer.value = null
    }

    fun saveData(name: String, date: Long) {
        this.activityName = name
        this.selectedDate = date
        this._activityName.value = name
        this._selectedDate.value = date
    }

    fun saveHistory() {
        val history = SplitHistory(
            id = currentSplitId,
            activityName = activityName,
            date = selectedDate,
            totalPeople = _friends.value.size,
            totalAmount = getFinalTotal()
        )
        val existingIndex = _historyList.value.indexOfFirst { it.id == currentSplitId }
        val updatedList = if (existingIndex >= 0) {
            val list = _historyList.value.toMutableList()
            list[existingIndex] = history
            list
        } else {
            _historyList.value + history
        }
        _historyList.value = updatedList
        saveHistoryToPrefs(updatedList)
    }

    fun addFriend(name: String): String? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return "Nama teman tidak boleh kosong"
        }
        if (_friends.value.any { it.name.equals(trimmedName, ignoreCase = true) }) {
            return "Teman sudah ditambahkan"
        }
        _friends.value += Friend(name = trimmedName)
        return null
    }

    fun removeFriend(friend: Friend) {
        _friends.value -= friend
    }

    fun addItem(name: String, price: Long, eaterIds: List<String>): String? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return "Nama item wajib diisi"
        }
        if (price <= 0) {
            return "Masukkan harga item"
        }
        if (eaterIds.isEmpty()) {
            return "Pilih minimal satu orang"
        }
        val newItem = OrderItem(
            name = trimmedName,
            price = price,
            eaterIds = eaterIds
        )
        _items.value += newItem
        return null
    }

    fun removeItem(item: OrderItem) {
        _items.value -= item
    }

    fun updateTax(tax: Int) {
        _taxPercentage.value = tax.coerceAtLeast(0)
    }

    fun updateService(service: Int) {
        _servicePercentage.value = service.coerceAtLeast(0)
    }

    fun updateDiscount(discount: Long) {
        _discountNominal.value = discount.coerceAtLeast(0L)
    }

    fun selectPayer(friend: Friend) {
        _selectedPayer.value = friend
    }

    fun getSubtotal(): Long {
        return _items.value.sumOf { it.price }
    }

    fun getTaxAmount(): Long {
        return (getSubtotal() * _taxPercentage.value) / 100
    }

    fun getServiceAmount(): Long {
        return (getSubtotal() * _servicePercentage.value) / 100
    }

    fun getFinalTotal(): Long {
        val subtotal = getSubtotal()
        val tax = getTaxAmount()
        val service = getServiceAmount()
        val discount = _discountNominal.value
        val calculatedTotal = subtotal + tax + service - discount
        return if (_totalAmount.value > 0) _totalAmount.value else calculatedTotal.coerceAtLeast(0L)
    }

    fun calculateProportionalSplit(): List<FriendSplitResult> {
        val friendsList = _friends.value
        val itemsList = _items.value
        val payer = _selectedPayer.value

        if (friendsList.isEmpty()) return emptyList()

        val friendSubtotals = mutableMapOf<String, Double>()
        for (friend in friendsList) {
            friendSubtotals[friend.id] = 0.0
        }

        for (item in itemsList) {
            if (item.eaterIds.isNotEmpty()) {
                val pricePerEater = item.price.toDouble() / item.eaterIds.size
                for (eaterId in item.eaterIds) {
                    friendSubtotals[eaterId] = (friendSubtotals[eaterId] ?: 0.0) + pricePerEater
                }
            }
        }

        val totalSubtotal = friendSubtotals.values.sum()
        val totalTax = getTaxAmount().toDouble()
        val totalService = getServiceAmount().toDouble()
        val totalDiscount = _discountNominal.value.toDouble()

        return friendsList.map { friend ->
            val subtotal = friendSubtotals[friend.id] ?: 0.0
            val ratio = if (totalSubtotal > 0) subtotal / totalSubtotal else 0.0

            val taxShare = totalTax * ratio
            val serviceShare = totalService * ratio
            val discountShare = totalDiscount * ratio
            val finalAmountDouble = subtotal + taxShare + serviceShare - discountShare
            val finalAmount = finalAmountDouble.coerceAtLeast(0.0).toLong()

            FriendSplitResult(
                friend = friend,
                subtotal = subtotal,
                taxShare = taxShare,
                serviceShare = serviceShare,
                discountShare = discountShare,
                finalAmount = finalAmount,
                isPayer = friend.id == payer?.id
            )
        }
    }

    fun validateItemsAndFriends(): String? {
        val currentItems = _items.value

        if (currentItems.isEmpty()) {
            return "Daftar pesanan masih kosong. Tambahkan minimal 1 item."
        }

        for (item in currentItems) {
            if (item.eaterIds.isEmpty()) {
                return "Item '${item.name}' belum dipilih siapa pemesannya."
            }
        }

        return null
    }
}
