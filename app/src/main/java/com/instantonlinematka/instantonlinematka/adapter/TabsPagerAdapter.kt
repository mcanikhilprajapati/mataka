package com.instantonlinematka.instantonlinematka.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.wallet.NewWalletFragment

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle,
                       private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {


    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Deposit history Fragment")
                val depositFragment = NewWalletFragment()
                depositFragment.arguments = bundle
                return depositFragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Withdraw history Fragment")
                val withdrawFragment = NewWalletFragment()
                withdrawFragment.arguments = bundle
                return withdrawFragment
            }
            else -> return NewWalletFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}