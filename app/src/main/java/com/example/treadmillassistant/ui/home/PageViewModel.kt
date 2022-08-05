package com.example.treadmillassistant.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel: ViewModel() {
    private val index = MutableLiveData<Int>()

    fun setIndex(index: Int){
        this.index.value = index
    }
}