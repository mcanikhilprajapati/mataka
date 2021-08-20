package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.BonusData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import kotlinx.android.synthetic.main.item_bonus_list.view.*

class BonusAdapter(val context: Context,
                   val bonusList: ArrayList<BonusData>)
    : RecyclerView.Adapter<BonusAdapter.BonusViewHolder>(){

    class BonusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val date = itemView.lblDateTime
        val Status = itemView.lblStatusAnswer
        val Amount = itemView.lblAmountAnswer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonusViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_bonus_list,
            parent, false)
        return BonusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BonusViewHolder, position: Int) {

        val bonusData = bonusList.get(position)

        holder.date.text = "${bonusData.date} ${ConvertTime.ConvertTimeToPM(bonusData.time!!)}"

        holder.Status.text = context.getString(R.string.bonus_points)
        holder.Amount.text = "₹ ${bonusData.amount}"
    }

    override fun getItemCount(): Int = bonusList.size

}