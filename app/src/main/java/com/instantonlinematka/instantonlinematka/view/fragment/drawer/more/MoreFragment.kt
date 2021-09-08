package com.instantonlinematka.instantonlinematka.view.fragment.drawer.more

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.databinding.MoreFragmentBinding
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.WelcomeActivity
import com.instantonlinematka.instantonlinematka.view.activity.totalwinning.TotalWinningActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.account_statement.AccountStatementsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bankdetails.BankDetailsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.bonus.BonusFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.AddFundsActivity
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.accounts.funds.WithdrawFundsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.bidding_history.BiddingHistoryFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.contactus.ContactUsFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.gamerates.GameRatesFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.noticeboard.NoticeBoardFragment
import com.instantonlinematka.instantonlinematka.view.fragment.drawer.refer.ReferFragment
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_drawer.*

class MoreFragment : Fragment() {

    lateinit var binding: MoreFragmentBinding

    lateinit var contextMore: Context

    lateinit var sessionPrefs: SessionPrefs

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = MoreFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextMore = inflater.context

        sessionPrefs = SessionPrefs(contextMore)

        binding.btnAddFund.setSafeOnClickListener {

            val intent = Intent(contextMore, AddFundsActivity::class.java)
            startActivity(intent)
        }

        val name = sessionPrefs.getString(Constants.BANK_HOLDER_NAME)

        if (name.isEmpty()) {
            binding.lblProfileName.visibility = View.GONE
        } else {
            binding.lblProfileName.text = name
            binding.lblProfileName.visibility = View.VISIBLE
        }

        binding.btnBiddingHistory.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, BiddingHistoryFragment(),
                "BIDDING_HISTORY_FRAGMENT", bundle
            )
        }

        binding.btnWithdrawFund.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, WithdrawFundsFragment(),
                "WITHDRAW_FUND_FRAGMENT", bundle
            )
        }

        binding.btnShowBankDetails.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, BankDetailsFragment(),
                "BANK_DETAILS_FRAGMENT", bundle
            )
        }

        binding.btnShowAccountStatements.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, AccountStatementsFragment(),
                "ACCOUNT_STATEMENT_FRAGMENT", bundle
            )
        }

        binding.btnShowWinningGraph.setSafeOnClickListener {

            val intent = Intent(contextMore, TotalWinningActivity::class.java)
            startActivity(intent)
        }

        binding.btnShowBonuses.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, BonusFragment(),
                "BONUS_FRAGMENT", bundle
            )
        }

        binding.btnHowToPlay.setSafeOnClickListener {

            val items = arrayOf("English", "Hindi", "Telugu", "Kannada")

            val builder = AlertDialog.Builder(contextMore)
            builder.setTitle(getString(R.string.make_your_selection))
            builder.setItems(items) { dialog, which ->

                if (which == 0) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/we5WLIh_sHU")
                    )
                    startActivity(webIntent)

                } else if (which == 1) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/WNeryxEdpFo")
                    )
                    startActivity(webIntent)

                } else if (which == 2) {

                    Toast.makeText(contextMore, "Coming Soon!!", Toast.LENGTH_SHORT).show()

                } else if (which == 3) {

                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://youtu.be/gwD7qqFZvXI")
                    )
                    startActivity(webIntent)
                }
            }

            val alert = builder.create()
            alert.show()
        }

        binding.btnGameRates.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, GameRatesFragment(),
                "GAME_RATES_FRAGMENT", bundle
            )
        }

        binding.btnNoticeBoard.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, NoticeBoardFragment(),
                "NOTICE_BOARD_FRAGMENT", bundle
            )
        }

        binding.btnRefer.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, ReferFragment(),
                "REFER_FRAGMENT", bundle
            )
        }

        binding.btnContactUs.setSafeOnClickListener {

            val bundle = Bundle()
            Connectivity.switchDrawer(
                activity as DrawerActivity, ContactUsFragment(),
                "REFER_FRAGMENT", bundle
            )
        }

        binding.btnSignOut.setSafeOnClickListener {

            val mBottomSheetDialog: BottomSheetMaterialDialog =
                BottomSheetMaterialDialog.Builder(activity as DrawerActivity)
                    .setTitle(getString(R.string.question))
                    .setMessage(getString(R.string.are_you_sure_logout))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.okay)) { dialogInterface, which ->

                        sessionPrefs.removeAll()
                        (activity as DrawerActivity).finish()

                        val intent = Intent(contextMore, WelcomeActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()

            mBottomSheetDialog.show()
        }

        return view
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}