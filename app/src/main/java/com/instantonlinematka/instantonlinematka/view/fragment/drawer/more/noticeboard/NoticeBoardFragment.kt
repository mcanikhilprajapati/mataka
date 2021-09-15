package com.instantonlinematka.instantonlinematka.view.fragment.drawer.more.noticeboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.databinding.NoticeBoardFragmentBinding
import com.romainpiel.shimmer.Shimmer

@SuppressLint("SetJavaScriptEnabled")
class NoticeBoardFragment : Fragment() {

    lateinit var binding: NoticeBoardFragmentBinding

    lateinit var shimmer: Shimmer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = NoticeBoardFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        shimmer = Shimmer()
      //  shimmer.start(binding.lblPlayRatanGames)

        val myUrl = """
                    <html>
                    <body>
                    
                    <p text-align="justify">This is premium category application please kindly co-operate.</p>
                    <label text-align="justify">Minimum deposit amount - 500/-</label>
                    <br>
                    <label text-align="justify">Minimum withdrawal amount - 1000/-</label>
                    <br>
                    <p><label><b><h4>WITHDRAWAL</h4></b></label></p>
                    
                    <ul>
                      <li>Withdrawal request will be accepted anytime.</li>
                      <li>The amount will be credited to your bank account in 24 hours.</li>
                      <li>Withdrawal is not applicable on Sunday and on Bank Holidays.</li>
                    </ul>
                    
                    <label> <b><H4>NOTICE</H4></b></label>

                    <p text-align="justify">All customers are requested to update your bank account details with IFSC code in your profile before remittance. Its mandatory kindly cooperate. Kindly note for all withdrawing transactions 2% of transaction charges will be applicable.</p>
                    <label align="center"> <b><H4>WEEKLY BONUS TERMS AND CONDITION</H4></b></label>
                    <b>Get 1% cash bonus for weekly deposit of 5000 and more</b>
                    
                    <label align="center"> <b><H4>WEEKLY BONUS AMOUNT WILL BE CREDITED ON SUNDAY NIGHT</H4></b></label>
                    
                    <label align="center"> <b><H4>YOUR DEPOSIT AMOUNT WILL BE CONSIDERED FROM MONDAY MORNING TO SUNDAY NIGHT</H4></b></label>
                    
                    </body>
                    </html>
                    """

        binding.wp10progressBar.visibility = View.VISIBLE

        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadData(myUrl, "text/html", "UTF-8")

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                binding.wp10progressBar.visibility = View.GONE
            }
        }

        return view
    }
}