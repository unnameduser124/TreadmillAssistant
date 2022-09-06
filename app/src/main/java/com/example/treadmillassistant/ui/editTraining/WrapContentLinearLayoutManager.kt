package com.example.treadmillassistant.ui.editTraining

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.treadmillassistant.ui.addTraining.AddTraining


class WrapContentLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            println("${e.message} ${e.stackTrace}")
            AddTraining.popupWindow.dismiss()
            EditTraining.popupWindow.dismiss()
        }
    }
}