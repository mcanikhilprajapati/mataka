package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import kotlinx.android.synthetic.main.item_ratan_result_history_list.view.*

class RatanStarlineResultAdapter(val context: Context, val ratanGameList: ArrayList<RatanStarlineGameData>) :
    RecyclerView.Adapter<RatanStarlineResultAdapter.RatanStarlineViewHolder>() {

    class RatanStarlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblTime = itemView.lblTime
        val lblResult = itemView.lblResult
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatanStarlineViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ratan_result_history_list, parent, false)
        return RatanStarlineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatanStarlineViewHolder, position: Int) {

        val data = ratanGameList.get(position)

        holder.lblTime.text = ConvertTime.ConvertTimeToPM(data.open_time!!)

        holder.lblResult.text = "${data.panna_result}-${data.single_digit_result}"

    }

    override fun getItemCount(): Int = ratanGameList.size

}