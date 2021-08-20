package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.RatanBidHistoryData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import kotlinx.android.synthetic.main.item_bid_history.view.*

class RatanBidHistoryAdapter(val context: Context, val biddingList: ArrayList<RatanBidHistoryData>) :
    RecyclerView.Adapter<RatanBidHistoryAdapter.BiddingViewHolder>() {

    class BiddingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lblMarketNameAnswer = itemView.lblMarketNameAnswer
        var lblPlayOnAnswer = itemView.lblPlayOnAnswer
        var lblPlayOffAnswer = itemView.lblPlayOffAnswer
        var lblBidIdAnswer = itemView.lblBidIdAnswer
        var lblDigitAnswer = itemView.lblDigitAnswer
        var lblPointsAnswer = itemView.lblPointsAnswer
        var lblBidTimeAnswer = itemView.lblBidTimeAnswer
        var lblYouWonAnswer = itemView.lblYouWonAnswer
        var imgEmoji = itemView.imgEmoji
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiddingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid_history, parent, false)
        return BiddingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BiddingViewHolder, position: Int) {

        val data = biddingList.get(position)

        holder.lblMarketNameAnswer.setText(data.category_name)
        holder.lblPlayOnAnswer.setText(data.bided_on)
        holder.lblPlayOffAnswer.setText(data.bided_for)
        holder.lblBidIdAnswer.setText(data.bid_id)

        if (!data.panna_result.isNullOrEmpty()) {
            holder.lblDigitAnswer.setText(data.panna_result)
        }
        else if (!data.single_digit_result.isNullOrEmpty()) {
            holder.lblDigitAnswer.setText(data.single_digit_result)
        }

        holder.lblPointsAnswer.setText(data.bid_amount)
        val timeinPM = ConvertTime.ConvertTimeToPM(data.bided_on_time!!)
        holder.lblBidTimeAnswer.setText(timeinPM)
        if (data.status!!.contentEquals("1")) {
            holder.lblYouWonAnswer.text = context.getString(R.string.congratulations) + " " + data.won_amount
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Green
                )
            )
            holder.imgEmoji.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_won))
        } else if (data.status.contentEquals("2")) {
            holder.lblYouWonAnswer.text = context.getString(R.string.results_not_announced)
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            holder.imgEmoji.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_no_results
                )
            )
        } else if (data.status.contentEquals("0")) {
            holder.lblYouWonAnswer.text = context.getString(R.string.better_luck_next_time)
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            holder.imgEmoji.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_lost
                )
            )
        }
    }

    override fun getItemCount(): Int = biddingList.size
}