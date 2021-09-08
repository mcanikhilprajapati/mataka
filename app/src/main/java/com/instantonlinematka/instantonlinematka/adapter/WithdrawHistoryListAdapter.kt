package com.instantonlinematka.instantonlinematka.adapter

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import java.util.*
import kotlin.collections.ArrayList
import android.os.CountDownTimer

import android.util.SparseArray
import android.R.attr.data
import com.instantonlinematka.instantonlinematka.model.Data
import kotlinx.android.synthetic.main.list_item_deposit_history.view.*


class WithdrawHistoryListAdapter(val context: Context, val gameList: ArrayList<Data>) :
    RecyclerView.Adapter<WithdrawHistoryListAdapter.GameHolder>(

    ) {

    class GameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblTitle = itemView.lblTitle
        val lblDateTime = itemView.lblDateTime
        val lblStatus = itemView.lblStatus
        val lblAmount = itemView.lblAmount

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_withdraw_history, parent, false)

        return GameHolder(v)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: GameHolder, @SuppressLint("RecyclerView") position: Int) {

          val gameData = gameList.get(position)
        holder.lblAmount.text = gameData.amount
        holder.lblDateTime.text = gameData.date + " "+gameData.time

        if(gameData.status.equals("0")){
            holder.lblStatus.text = "Pending"
            holder.lblStatus.setBackgroundResource(R.drawable.round_light_yellow_corner)
            holder.lblStatus.setTextColor(context.resources.getColor(R.color.DarkYellow))
        }
        else if(gameData.status.equals("1")){
            holder.lblStatus.text = "Success"
            holder.lblStatus.setBackgroundResource(R.drawable.round_light_green_corner)
            holder.lblStatus.setTextColor(context.resources.getColor(R.color.Green))
        }
        else if(gameData.status.equals("2")){
            holder.lblStatus.text = "Failed"
            holder.lblStatus.setBackgroundResource(R.drawable.round_light_red_corner)
            holder.lblStatus.setTextColor(context.resources.getColor(R.color.Red))
        }

    }

    override fun getItemCount() = gameList.size

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}