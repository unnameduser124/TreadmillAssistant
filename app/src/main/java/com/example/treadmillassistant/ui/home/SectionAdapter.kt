package com.example.treadmillassistant.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.treadmillassistant.ui.home.calendarTab.CalendarPlaceholderFragment
import com.example.treadmillassistant.ui.home.trainingTab.TrainingTabPlaceholderFragment

private val TAB_TITLES = arrayOf(
    "Calendar",
    "Training"
)

class SectionAdapter (fm: FragmentManager): FragmentPagerAdapter(fm){

    override fun getItem(pageNumber: Int): Fragment {
        return if(pageNumber==0){
            CalendarPlaceholderFragment.newInstance(pageNumber)
        } else {
            TrainingTabPlaceholderFragment.newInstance(pageNumber)
        }

    }
    override fun getPageTitle(pageNumber: Int): CharSequence{
        return TAB_TITLES[pageNumber]
    }

    override fun getCount(): Int {
        return 2
    }

}