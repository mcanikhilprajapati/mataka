package com.instantonlinematka.instantonlinematka.view.fragment.drawer.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.FragmentNewWalletBinding
import com.instantonlinematka.instantonlinematka.retrofit.ApiInterface
import com.instantonlinematka.instantonlinematka.retrofit.RetrofitClient
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.AddFundsActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.WithdrawFundsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.market.MarketFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.RatanStarlineGamesFragment
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.fragment_new_wallet.*
import kotlinx.android.synthetic.main.fragment_new_wallet.view.*

@SuppressLint("RestrictedApi")
class NewWalletFragment : Fragment() {

    lateinit var binding: FragmentNewWalletBinding
    lateinit var contextAccount: Context
    lateinit var sessionPrefs: SessionPrefs
    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       // val rootView = inflater.inflate(R.layout.fragment_new_wallet, container, false)

        binding = FragmentNewWalletBinding.inflate(inflater, container, false)
        val rootView = binding.root
        contextAccount = inflater.context


        sessionPrefs = SessionPrefs(contextAccount)

        val wallet = sessionPrefs.getString(Constants.WALLET)
        if (wallet.isEmpty()) {
            binding.walletamount.text = "- - -"
        } else {
            binding.walletamount.text ="₹ "+wallet
        }


        sessionPrefs = SessionPrefs(contextAccount)

        apiInterface = RetrofitClient.getRetrfitInstance()


        rootView.btnAddFunds.setSafeOnClickListener {

            val intent = Intent(contextAccount, AddFundsActivity::class.java)
            startActivity(intent)

        }

        rootView.btnWithdrawFunds.setSafeOnClickListener {
            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, WithdrawFundsFragment(),
                "WITHDRAW_FUNDS_FRAGMENT", bundle
            )
        }




        val adapter = FragmentPagerItemAdapter(
            this.childFragmentManager, FragmentPagerItems.with(activity)
                .add(R.string.deposit_history, DepositHistoryFragment::class.java)
                .add(R.string.withdraw_history, WithdrawHistoryFragment::class.java)
                .create()
        )

        val viewPager = rootView.findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = adapter



        val tabs = rootView.findViewById(R.id.tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)


        return rootView
    }

//    private fun setCustomTabTitles() {
//
//        val vg = tab_layout.getChildAt(0) as ViewGroup
//        val tabsCount = vg.childCount
//
//        for (j in 0 until tabsCount) {
//            val vgTab = vg.getChildAt(j) as ViewGroup
//
//            val tabChildCount = vgTab.childCount
//
//            for (i in 0 until tabChildCount) {
//                val tabViewChild = vgTab.getChildAt(i)
//                if (tabViewChild is TextView) {
//
//                    // Change Font and Size
//                    tabViewChild.typeface = Typeface.DEFAULT_BOLD
////                    val font = ResourcesCompat.getFont(this, R.font.myFont)
////                    tabViewChild.typeface = font
////                    tabViewChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
//                }
//            }
//        }
//    }


    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

}
