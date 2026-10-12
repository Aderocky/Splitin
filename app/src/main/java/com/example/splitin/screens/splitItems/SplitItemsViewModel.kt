package com.example.splitin.screens.splitItems

import androidx.lifecycle.ViewModel
import com.example.splitin.data.repository.SplitinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplitItemsViewModel @Inject constructor(
    private val repository: SplitinRepository
) : ViewModel() {

    val taxPercentage: StateFlow<Int> = repository.taxPercentage
    val servicePercentage: StateFlow<Int> = repository.servicePercentage
    val discountNominal: StateFlow<Long> = repository.discountNominal

    fun updateTax(tax: Int) {
        repository.updateTax(tax)
    }

    fun updateService(service: Int) {
        repository.updateService(service)
    }

    fun updateDiscount(discount: Long) {
        repository.updateDiscount(discount)
    }

    fun getSubtotal(): Long = repository.getSubtotal()

}
