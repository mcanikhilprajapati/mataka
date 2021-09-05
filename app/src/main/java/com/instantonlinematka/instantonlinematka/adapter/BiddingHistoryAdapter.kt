package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.BiddingDataByGame
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import kotlinx.android.synthetic.main.item_bid_history_new.view.*
import kotlinx.android.synthetic.main.item_bid_history_new.view.imgEmoji
import kotlinx.android.synthetic.main.item_bid_history_new.view.lblDigitAnswer
import kotlinx.android.synthetic.main.item_bid_history_new.view.lblMarketNameAnswer
import kotlinx.android.synthetic.main.item_bid_history_new.view.lblPlayOnAnswer
import kotlinx.android.synthetic.main.item_bid_history_new.view.lblPointsAnswer
import kotlinx.android.synthetic.main.item_bid_history_new.view.lblYouWonAnswer

class BiddingHistoryAdapter(val context: Context, val biddingList: ArrayList<BiddingDataByGame>) :
    RecyclerView.Adapter<BiddingHistoryAdapter.BiddingViewHolder>() {
    
    class BiddingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        var lblMarketNameAnswer = itemView.lblMarketNameAnswer
        var lblPlayOnAnswer = itemView.lblPlayOnAnswer
        //var lblPlayOffAnswer = itemView.lblPlayOffAnswer
        var lblBidIdAnswer = itemView.lblBidIdAnswer
        var lblDigitAnswer = itemView.lblDigitAnswer
        var lblPointsAnswer = itemView.lblPointsAnswer
       // var lblBidTimeAnswer = itemView.lblBidTimeAnswer
        var lblYouWonAnswer = itemView.lblYouWonAnswer
        var lblYouWonAnswerAmount = itemView.lblYouWonAnswerAmount
        var imgEmoji = itemView.imgEmoji
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiddingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid_history_new, parent, false)
        return BiddingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BiddingViewHolder, position: Int) {

        val data = biddingList.get(position)

        holder.lblMarketNameAnswer.setText(
            data.game_type_name + "(${data.category_name})"
        )
        val timeinPM = ConvertTime.ConvertTimeToPM(data.bided_on_time!!)
        holder.lblPlayOnAnswer.setText(data.bided_on+ " "+timeinPM)
       // holder.lblPlayOffAnswer.setText(data.bided_for)
        holder.lblBidIdAnswer.setText(data.bid_id)
        holder.lblDigitAnswer.setText(data.number)
        holder.lblPointsAnswer.setText(data.bid_amount)

        //holder.lblBidTimeAnswer.setText(timeinPM)
        if (data.status!!.contentEquals("1")) {

            holder.lblYouWonAnswer.text = context.getString(R.string.congratulations)
            holder.lblYouWonAnswerAmount.text =  data.won_amount
            holder.lblYouWonAnswerAmount.visibility =  View.VISIBLE
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Gray
                )
            )
            holder.imgEmoji.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.win))
        } else if (data.status.contentEquals("2")) {
            holder.lblYouWonAnswer.text = context.getString(R.string.results_not_announced)
            holder.lblYouWonAnswerAmount.visibility =  View.GONE
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Gray
                )
            )
            holder.imgEmoji.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.not_announced
                )
            )
        } else if (data.status.contentEquals("0")) {
            holder.lblYouWonAnswer.text = context.getString(R.string.better_luck_next_time)
            holder.lblYouWonAnswerAmount.visibility =  View.GONE
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Gray
                )
            )
            holder.imgEmoji.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.loss
                )
            )
        }
    }

    override fun getItemCount(): Int = biddingList.size
}