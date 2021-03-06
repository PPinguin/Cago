package com.cago.pack.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cago.pack.fragments.edit.EditPackageInputFragment
import com.cago.pack.fragments.edit.EditPackageOutputFragment

class EditPackFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    
    companion object{
        val inputFragment = EditPackageInputFragment()
        val outputFragment = EditPackageOutputFragment()
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> inputFragment
            1 -> outputFragment
            else -> throw IllegalArgumentException()
        }
}
