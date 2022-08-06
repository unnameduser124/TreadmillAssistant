package com.example.treadmillassistant.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.treadmillassistant.backend.tabLayout
import com.example.treadmillassistant.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
         val sectionPageAdapter = SectionAdapter(childFragmentManager)
        val viewPager: ViewPager = binding.homeViewPager
        viewPager.adapter = sectionPageAdapter
        val tabs: TabLayout = binding.homeTabs
        tabs.setupWithViewPager(viewPager)
        tabLayout = tabs
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}