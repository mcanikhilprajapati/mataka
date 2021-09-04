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
import kotlinx.android.synthetic.main.item_bid_history_new.view.*
import android.text.Html

import android.R.id




class RatanBidHistoryAdapter(val context: Context, val biddingList: ArrayList<RatanBidHistoryData>) :
    RecyclerView.Adapter<RatanBidHistoryAdapter.BiddingViewHolder>() {

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

//        var lblMarketNameAnswer = itemView.lblMarketNameAnswer
//        var lblPlayOnAnswer = itemView.lblPlayOnAnswer
//        var lblPlayOffAnswer = itemView.lblPlayOffAnswer
//        var lblBidIdAnswer = itemView.lblBidIdAnswer
//        var lblDigitAnswer = itemView.lblDigitAnswer
//        var lblPointsAnswer = itemView.lblPointsAnswer
//        var lblBidTimeAnswer = itemView.lblBidTimeAnswer
//        var lblYouWonAnswer = itemView.lblYouWonAnswer
//        var imgEmoji = itemView.imgEmoji
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiddingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid_history_new, parent, false)
        return BiddingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BiddingViewHolder, position: Int) {

        val data = biddingList.get(position)

        val timeinPM = ConvertTime.ConvertTimeToPM(data.bided_on_time!!)
        holder.lblPlayOnAnswer.setText(data.bided_on+ " "+timeinPM)

        holder.lblMarketNameAnswer.setText(data.category_name)
        holder.lblPlayOnAnswer.setText(data.bided_on)
        //holder.lblPlayOffAnswer.setText(data.bided_for)
        holder.lblBidIdAnswer.setText(data.bid_id)

        if (!data.panna_result.isNullOrEmpty()) {
            holder.lblDigitAnswer.setText(data.panna_result)
        }
        else if (!data.single_digit_result.isNullOrEmpty()) {
            holder.lblDigitAnswer.setText(data.single_digit_result)
        }

        holder.lblPointsAnswer.setText(data.bid_amount)
//        val timeinPM = ConvertTime.ConvertTimeToPM(data.bided_on_time!!)
//        holder.lblBidTimeAnswer.setText(timeinPM)
        if (data.status!!.contentEquals("1")) {

             holder.lblYouWonAnswer.text = context.getString(R.string.congratulations)
            holder.lblYouWonAnswerAmount.visibility =  View.VISIBLE
             holder.lblYouWonAnswerAmount.text =  data.won_amount
            holder.lblYouWonAnswer.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.Gray
                )
            )
            holder.imgEmoji.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.win))
        } else if (data.status.contentEquals("2")) {
            holder.lblYouWonAnswerAmount.visibility =  View.GONE
            holder.lblYouWonAnswer.text = context.getString(R.string.results_not_announced)
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