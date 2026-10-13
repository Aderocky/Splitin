package com.example.splitin.screens.addFriends

import androidx.lifecycle.ViewModel
import com.example.splitin.data.model.Friend
import com.example.splitin.data.repository.SplitinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddFriendsViewModel @Inject constructor(
    private val repository: SplitinRepository
) : ViewModel() {

    val friends: StateFlow<List<Friend>> = repository.friends

    fun addFriend(name: String): String? {
        return repository.addFriend(name)
    }

    fun removeFriend(friend: Friend) {
        repository.removeFriend(friend)
    }
}
