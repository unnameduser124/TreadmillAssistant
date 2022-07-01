package com.example.treadmillassistant.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel: ViewModel() {
    private val indeks = MutableLiveData<Int>()

    fun setIndex(index: Int){
        indeks.value = index
    }
}