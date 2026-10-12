package com.example.splitin.screens.addItems

import androidx.lifecycle.ViewModel
import com.example.splitin.data.repository.SplitinRepository
import com.example.splitin.screens.addFriends.Friend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddItemsViewModel @Inject constructor(
    private val repository: SplitinRepository
) : ViewModel() {

    val friends: StateFlow<List<Friend>> = repository.friends
    val items: StateFlow<List<OrderItem>> = repository.items

    fun addItem(name: String, price: Long, eaterIds: List<String>): String? {
        return repository.addItem(name, price, eaterIds)
    }

    fun removeItem(item: OrderItem) {
        repository.removeItem(item)
    }

    fun validateItemsAndFriends(): String? {
        return repository.validateItemsAndFriends()
    }
}
