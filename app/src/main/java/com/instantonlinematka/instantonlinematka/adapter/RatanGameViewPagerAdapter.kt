package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.PannaGameDataList
import com.instantonlinematka.instantonlinematka.model.PannaList
import kotlinx.android.synthetic.main.dynamic_entry_view_item.view.*

@SuppressLint("RestrictedApi")
class RatanGameViewPagerAdapter(
    var gameList: ArrayList<PannaList>,
    var context: Context,
    var lblTotalPoints: AppCompatTextView,
    var pannaActivity: AppCompatActivity,
    var tablayout: TabLayout,
    var pannaGameList: ArrayList<PannaGameDataList>
) : PagerAdapter(), TextWatcher {

    private var currentPosition: Int? = 0
    private var layoutInflater: LayoutInflater? = null
    var pannas = ArrayList<String>()

    var focusedViewId = 0
    var totalPoints = 0

    var NumberLeft: AppCompatTextView? = null
    var NumberAnswerLeft: AppCompatEditText? = null
    var NumberRight: AppCompatTextView? = null
    var NumberAnswerRight: AppCompatEditText? = null

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        container.removeView(view)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount(): Int {
        return gameList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view: View = layoutInflater!!.inflate(R.layout.dynamic_views_page, container, false)
        currentPosition = position
        val llgr = view.findViewById<LinearLayout>(R.id.ll_digit_layout)
        llgr.removeAllViews()
        val layout_child: LinearLayout
        layout_child = LinearLayout(context)
        layout_child.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        layout_child.orientation = LinearLayout.VERTICAL

        if (llgr.childCount <= 10) {
            llgr.addView(layout_child) //add layout into parent view
        }

        pannas = gameList.get(position).pannas

        for (i in 0 until pannas.size step 2) {

            val child: View =
                LayoutInflater.from(context).inflate(R.layout.dynamic_entry_view_item, null)
            NumberLeft = child.lblNumberLeft
            NumberAnswerLeft = child.txtNumberAnswerLeft
            NumberRight = child.lblNumberRight
            NumberAnswerRight = child.txtNumberAnswerRight

            val secondLayout = child.constraintLayout11
            if (i == pannas.size - 1) {
                secondLayout.visibility = View.INVISIBLE
            }

            NumberLeft!!.setId((position + 1) * 100 + i)
            NumberAnswerLeft!!.setId((position + 1) * 100000 + i)
            NumberRight!!.setId((position + 1) * 100 + i + 1)
            NumberAnswerRight!!.setId((position + 1) * 100000 + i + 1)

            try {
                NumberLeft!!.setText(pannas[i])
                NumberRight!!.setText(pannas[i + 1])
            } catch (e: Exception) {
            }

            for (items in pannaGameList) {

                if (NumberLeft!!.getText().toString().contentEquals(items.numbers.toString())) {
                    NumberAnswerLeft!!.setText(items.points.toString())
                }
                if (NumberRight!!.getText().toString().contentEquals(items.numbers.toString())) {
                    NumberAnswerRight!!.setText(items.points.toString())
                }
            }

            NumberAnswerLeft!!.setOnFocusChangeListener(focusListener)
            NumberAnswerLeft!!.addTextChangedListener(this)
            NumberAnswerRight!!.setOnFocusChangeListener(focusListener)
            NumberAnswerRight!!.addTextChangedListener(this)

            layout_child.addView(child)

        }


        //Change guardian giving static
        container.addView(view)

        return view
    }

    private val focusListener =
        View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                focusedViewId = v.id
            }
        }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        totalPoints = 0

        val firstdigit = focusedViewId / 100000
        val lastdigit = focusedViewId % 100000

        val tv: TextView = pannaActivity.findViewById(firstdigit * 100 + lastdigit) as TextView

        if (isthere(tv.text.toString())) {
            if (s.length > 1 && s.toString().toInt() > 9) {
                for (i in 0 until pannaGameList.size) {
                    if (pannaGameList.get(i).numbers.toString()
                            .contentEquals(tv.text.toString().trim())
                    ) {
                        pannaGameList.set(
                            i,
                            PannaGameDataList(
                                tv.text.toString(),
                                s.toString().toInt(),
                                tablayout.getSelectedTabPosition().toString().toInt()
                            )
                        )
                    }
                }
            } else {
                for (i in 0 until pannaGameList.size) {
                    if (pannaGameList.get(i).numbers.toString()
                            .contentEquals(tv.text.toString().trim())
                    ) {
                        pannaGameList.removeAt(i)
                        break
                    }
                }
            }
        } else {
            if (s.length > 1 && s.toString().toInt() > 9) {
                pannaGameList.add(
                    PannaGameDataList(
                        tv.text.toString(),
                        s.toString().toInt(),
                        tablayout.getSelectedTabPosition().toString().toInt()
                    )
                )
            }
        }

        for (si in pannaGameList) {
            totalPoints = totalPoints + si.points
        }

        lblTotalPoints.text = "₹ ${totalPoints}"

    }

    override fun afterTextChanged(s: Editable?) {}

    fun isthere(data: String?): Boolean {
        var flag = false
        for (j in pannaGameList.indices) {
            if (pannaGameList.get(j).numbers.toString().contentEquals(data.toString())) {
                flag = true
            }
        }
        return flag
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return gameList.get(position).digits
    }

}