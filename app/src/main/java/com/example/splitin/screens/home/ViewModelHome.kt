package com.example.splitin.screens.home

import androidx.lifecycle.ViewModel
import com.example.splitin.data.model.SplitHistory
import com.example.splitin.data.repository.SplitinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ViewModelHome @Inject constructor(
    private val splitRepository: SplitinRepository
) : ViewModel() {

    val historyList: StateFlow<List<SplitHistory>> = splitRepository.historyList

    fun saveSplitDetail(
        activityName: String,
        date: Long
    ) {
        splitRepository.resetCurrentSplit()
        splitRepository.saveData(activityName, date)
    }
}
