package com.buzzwaretech.gymphysique.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.buzzwaretech.gymphysique.fragments.HomeFragment
import com.buzzwaretech.gymphysique.fragments.KartFragment
import com.buzzwaretech.gymphysique.fragments.ProfileFragment
import com.buzzwaretech.gymphysique.fragments.SearchFragment

class MainPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4 // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> KartFragment()
            2 -> SearchFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
