package com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantonlinematka.instantonlinematka.R

class CustomDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var mBehavior: BottomSheetBehavior<FrameLayout>

    override fun setContentView(view: View) {
        super.setContentView(view)
        val bottomSheet = window?.decorView?.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}
