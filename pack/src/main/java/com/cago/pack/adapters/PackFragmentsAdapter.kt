package com.cago.pack.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cago.pack.fragments.usage.PackageInputFragment
import com.cago.pack.fragments.usage.PackageOutputFragment

class PackFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    
    companion object {
        private val inputFragment = PackageInputFragment()
        private val outputFragment = PackageOutputFragment()
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment  =
        when(position){
            0 -> inputFragment
            1 -> outputFragment
            else -> throw IllegalArgumentException()
        }
}