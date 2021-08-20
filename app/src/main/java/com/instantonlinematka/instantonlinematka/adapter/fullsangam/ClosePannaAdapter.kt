package com.instantonlinematka.instantonlinematka.adapter.fullsangam

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.FullSangamGameData
import com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes.FullSangamActivity
import java.util.*

class ClosePannaAdapter(
    var pannaList: List<FullSangamGameData>,
    contextFullSangam: Context,
    var activityFullSangam: FullSangamActivity
) : ArrayAdapter<FullSangamGameData>(contextFullSangam, 0, pannaList) {

    private val digitFilter: Filter = object : Filter() {

        val tempItems = ArrayList<FullSangamGameData>(pannaList)

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val results = FilterResults()
            val suggestions: MutableList<FullSangamGameData> = ArrayList()

            if (constraint == null || constraint.length == 0) {
                suggestions.addAll(tempItems)
            } else {
                val filterPattern = constraint.toString().trim { it <= ' ' }
                for (item in tempItems) {
                    if (item.value.contains(filterPattern)) {
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

            val filterList: List<FullSangamGameData> = results!!.values as ArrayList<FullSangamGameData>
            if (results.count > 0) {
                clear()
                for (items in filterList) {
                    add(items)
                    notifyDataSetChanged()
                }
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            activityFullSangam.closePannaModel = resultValue as FullSangamGameData
            return resultValue.value
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
            txtNumber.setText(DigitModel.value)
        }
        return convertView
    }

}