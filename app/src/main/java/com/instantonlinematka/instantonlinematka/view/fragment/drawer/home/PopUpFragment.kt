package com.instantonlinematka.instantonlinematka.view.fragment.drawer.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener

class PopUpFragment : DialogFragment() {

    var contextPopup: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.contextPopup = getActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.popup_fragment, container, false)
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgClose: AppCompatImageButton = v.findViewById(R.id.imgButton)
        imgClose.setSafeOnClickListener { dismiss() }
        val imgPopupImage: AppCompatImageView = v.findViewById(R.id.imgPopupImage)
        Glide.with(context!!)
            .load("https://www.instantonlinematka.com/upload/promotion_banner.jpeg")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(ContextCompat.getDrawable(context!!, R.drawable.no_image_found))
            .into(imgPopupImage)
        return v
    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}