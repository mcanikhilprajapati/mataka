package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.RatesData
import kotlinx.android.synthetic.main.item_game_rates.view.*

class GameRatesAdapter(val context: Context,
                       val ratesList: ArrayList<RatesData>)
    : RecyclerView.Adapter<GameRatesAdapter.GamesViewHolder>(){

    class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val GameName = itemView.lblGame
        val WinningRatio = itemView.lblWinningRatio
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.item_game_rates,
            parent, false)
        return GamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {

        val ratioData = ratesList.get(position)

        holder.GameName.text = "${ratioData.category_name}"
        holder.WinningRatio.text = "₹ ${ratioData.winning_ratio}"
    }

    override fun getItemCount(): Int = ratesList.size

}