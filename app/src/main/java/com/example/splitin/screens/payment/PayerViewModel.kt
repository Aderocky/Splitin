package com.example.splitin.screens.payment

import androidx.lifecycle.ViewModel
import com.example.splitin.data.repository.SplitinRepository
import com.example.splitin.screens.addFriends.Friend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PayerViewModel @Inject constructor(
    private val repository: SplitinRepository
) : ViewModel() {

    val friends: StateFlow<List<Friend>> = repository.friends
    val selectedPayer: StateFlow<Friend?> = repository.selectedPayer

    fun selectPayer(friend: Friend) {
        repository.selectPayer(friend)
    }

    fun getFinalTotal(): Long {
        return repository.getFinalTotal()
    }
}
