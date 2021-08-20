package com.instantonlinematka.instantonlinematka.view.fragment.drawer.refer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.databinding.ReferFragmentBinding
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.utility.SessionPrefs

class ReferFragment : Fragment() {

    lateinit var binding: ReferFragmentBinding

    lateinit var contextRefer: Context

    lateinit var sessionPrefs: SessionPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ReferFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        contextRefer = inflater.context

        sessionPrefs = SessionPrefs(contextRefer)

        binding.lblReferralCode.text = sessionPrefs.getString(Constants.REFERRAL)

        binding.btnShareCode.setSafeOnClickListener {

            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Do you know you can win Free Real Cash by Matka Online?\n" +
                            "\n" +
                            "\n" +
                            "Just Download & Try App on: http://instantonlinematka.com/instantonlinematka.apk" +
                            "\n" +
                            "\n" +
                            "Use my referral code " + "*" +
                            sessionPrefs.getString(Constants.REFERRAL) + "*" +
                            " and earn 5% more."
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } catch (e: Exception) {
                Log.e("ShareApp", (e.message)!!)
            }

        }

        binding.copyCode.setSafeOnClickListener {
            copyShareCode()
        }

        return view
    }

    fun copyShareCode() {

        val clipboard = contextRefer.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip =
            ClipData.newPlainText("Copied To Clipboard", binding.lblReferralCode.text.toString().trim())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(contextRefer, "Copied To Clipboard", Toast.LENGTH_SHORT)
            .show()
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}