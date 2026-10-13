package com.example.splitin.screens.result

import androidx.lifecycle.ViewModel
import com.example.splitin.data.model.Friend
import com.example.splitin.data.model.FriendSplitResult
import com.example.splitin.data.repository.SplitinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: SplitinRepository
) : ViewModel() {

    val activityNameFlow: StateFlow<String> = repository.activityNameFlow
    val selectedPayer: StateFlow<Friend?> = repository.selectedPayer

    fun getSplitResults(): List<FriendSplitResult> {
        return repository.calculateProportionalSplit()
    }

    fun getPayer(): Friend? {
        return repository.selectedPayer.value
    }

    fun saveHistory() {
        repository.saveHistory()
    }

    fun getTotalCollectedFromOthers(): Long {
        val results = repository.calculateProportionalSplit()
        return results.filter { !it.isPayer }.sumOf { it.finalAmount }
    }
}
