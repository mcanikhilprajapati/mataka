package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home.ratan.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.instantonlinematka.instantonlinematka.databinding.RatanStarlineTermsConditionsFragmentBinding
import com.romainpiel.shimmer.Shimmer

class RatanStarlineTermsAndConditionsFragment : Fragment() {

    lateinit var binding: RatanStarlineTermsConditionsFragmentBinding

    lateinit var shimmer: Shimmer

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RatanStarlineTermsConditionsFragmentBinding.inflate(
            inflater, container, false
        )
        val view = binding.root

        shimmer = Shimmer()
        shimmer.start(binding.lblPlayRatanGames)

        val myUrl = """
                    <html>
                    <body>
                    
                    <p text-align="justify"><b>Ratan starline will  Run Every Day From Monday To Sunday 10:00 AM to 09:00 PM.There is No Jodi(Bracket) Game in Ratan Starline.You Can Play Bet InSingle Ank And Panna Only.Below Are Game Ratio Details.</b></p>
                    
                    <ul>
                      <li>Single Ank :- 10₹ : 90₹</li>
                      <li>Single Panna :- 10₹ : 140₹</li>
                      <li>Double Panna :- 10₹ : 2800₹</li>
                      <li>Triple Panna :- 10₹ : 6500</li>
                    </ul>
                    <p text-align="justify"><b>Dont Forget To Switch On Ratan Starline Notification Form DashBoard To Receive Result Notification.</b></p>
                    <p text-align="justify"><b>You Can Check The My Starline Bid History From The StarLineine Dash Board.</b></p>
                    <p text-align="justify"><b>Game Bets Open Before 1hr From The Result Time And Bet Closses Before 5mins Of Result Declaration Time.</b></p>
                    
                    </body>
                    </html>
                    """

        binding.wp10progressBar.showProgressBar()

        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadData(myUrl, "text/html", "UTF-8")

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                binding.wp10progressBar.hideProgressBar()
            }
        }

        return view
    }
}