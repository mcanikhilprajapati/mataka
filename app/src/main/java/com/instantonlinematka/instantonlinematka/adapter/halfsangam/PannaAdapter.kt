package com.instantonlinematka.instantonlinematka.adapter.halfsangam

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.SangamGameData
import com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes.HalfSangamActivity
import java.util.*

class PannaAdapter(
    var pannaList: List<SangamGameData>,
    contextFullSangam: Context,
    var activityHalfSangam: HalfSangamActivity
) : ArrayAdapter<SangamGameData>(contextFullSangam, 0, pannaList) {

    private val digitFilter: Filter = object : Filter() {

        val tempItems = ArrayList<SangamGameData>(pannaList)

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val results = FilterResults()
            val suggestions: MutableList<SangamGameData> = ArrayList()

            if (constraint == null || constraint.length == 0) {
                suggestions.addAll(tempItems)
            } else {
                val filterPattern = constraint.toString().trim { it <= ' ' }
                for (item in tempItems) {
                    if (item.points.contains(filterPattern)) {
                        suggestions.add(item)
                    }
                }
            }

            results.values = suggestions
            results.count = suggestions.size
            Log.e("test", "search result size-->>" + suggestions.size)
            return results

        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            val filterList: List<SangamGameData> = results!!.values as ArrayList<SangamGameData>
            if (results.count > 0) {
                clear()
                for (items in filterList) {
                    add(items)
                    notifyDataSetChanged()
                }
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            activityHalfSangam.pannaModel = resultValue as SangamGameData
            return resultValue.points
        }
    }

    override fun getFilter(): Filter = digitFilter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(
                R.layout.item_autocomplete_text, parent, false
            )
        }

        val txtNumber = convertView!!.findViewById<TextView>(R.id.txtNumber)
        val DigitModel = getItem(position)
        if (DigitModel != null) {
            txtNumber.setText(DigitModel.points)
        }
        return convertView
    }

}