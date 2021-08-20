package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.AccountStatementData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import kotlinx.android.synthetic.main.item_account_statement.view.*

class AccountStatementAdapter(
    val context: Context,
    val accountStatementList: ArrayList<AccountStatementData>
) :
    RecyclerView.Adapter<AccountStatementAdapter.AccStatementViewHolder>() {

    val REFERRAL_FUND = "referal_fund"
    val BONUS = "bonus"
    val ADD_FUND = "add_fund"
    val BID_POINT = "bid_point"
    val WITHDRAW_FUND = "withdrawal_fund"
    val WON_AMOUNT = "won_amount"
    val REFUND = "refund"

    class AccStatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val GameName = itemView.lblGameNameAnswer
        val CategoryName = itemView.lblCategoryNameAnswer
        val Status = itemView.lblStatusAnswer
        val Date = itemView.lblDate
        val Time = itemView.lblTime
        val Amount = itemView.lblAmountAnswer
      //  val lblGameName = itemView.lblGameName
      //  val lblCategoryName = itemView.lblCategoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccStatementViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account_statement, parent, false)
        return AccStatementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccStatementViewHolder, position: Int) {

        val data = accountStatementList.get(position)

        if (!data.biddata!!.game_type_name.isNullOrEmpty())
            holder.GameName.text = data.biddata.game_type_name
        else {
          //  holder.lblGameName.visibility = View.GONE
            holder.GameName.visibility = View.GONE
        }

        if (!data.biddata.category_name.isNullOrEmpty())
            holder.CategoryName.text = data.biddata.category_name
        else {
          //  holder.lblCategoryName.visibility = View.GONE
            holder.CategoryName.visibility = View.GONE
        }

        if (!data.amount.isNullOrEmpty())
            holder.Amount.text = data.amount else holder.Amount.visibility = View.GONE

        if (!data.date.isNullOrEmpty()) {
            val finalDate = data.date
            val finalTime = ConvertTime.ConvertTimeToPM(data.time!!)
            holder.Date.text = "${finalTime}"
            holder.Time.text = "${finalDate}"
        } else {
            holder.Date.visibility = View.GONE
            holder.Time.visibility = View.GONE
        }

        val status = data.statement_type!!

        if (status.contentEquals(REFERRAL_FUND)) {

            holder.Amount.setText("₹ " + data.amount)
            holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.Status.setText(context.getString(R.string.referral_fund_added))
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.GameName.visibility = View.GONE
            holder.CategoryName.visibility = View.GONE
        }
        else if (status.contentEquals(BONUS)) {

            holder.Amount.setText("₹ " + data.amount)
            holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.Status.setText(context.getString(R.string.bonus_points))
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.GameName.visibility = View.GONE
            holder.CategoryName.visibility = View.GONE
        }
        else if (status.contentEquals(ADD_FUND) || status.contentEquals(BID_POINT)) {

            holder.Amount.setText("₹ " + data.amount)
            holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.Status.setText(context.getString(R.string.points_added_in_the_wallet))
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
            holder.GameName.visibility = View.GONE
            holder.CategoryName.visibility = View.GONE
        }
        else if (status.contentEquals(WITHDRAW_FUND)) {

            holder.Amount.setText("₹ " + data.amount)
            holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.Status.setText(context.getString(R.string.your_withdrawal_amount))
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.GameName.visibility = View.GONE
            holder.CategoryName.visibility = View.GONE
        }
        else if (status.contentEquals(WON_AMOUNT)) {

            if (data.biddata.category_name!!.contentEquals(context.getString(R.string.half_sangam_cap))) {

                if (data.biddata.close_digit!!.isEmpty()) {
                    holder.CategoryName.setText(data.biddata.open_digit + "-" + data.biddata.close_panna +
                            "   \u00b7   " + "₹ " + data.biddata.bid_amount)
                }
                else {
                    holder.CategoryName.setText(data.biddata.close_digit + "-" + data.biddata.open_panna +
                            "   \u00b7   " + "₹ " + data.biddata.bid_amount)
                }
                holder.Amount.setText("₹ " + data.amount)
                holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
                holder.Status.setText(context.getString(R.string.you_won_this_game))
                holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
             //   holder.lblGameName.visibility = View.VISIBLE
             //   holder.lblCategoryName.visibility = View.VISIBLE
                holder.GameName.visibility = View.VISIBLE
                holder.CategoryName.visibility = View.GONE//
            }
            else if (data.biddata.category_name.contentEquals(context.getString(R.string.full_sangam_cap))) {

                holder.CategoryName.setText(data.biddata.open_panna + "-" + data.biddata.close_panna +
                        "   \u00b7   " + "₹ " + data.biddata.bid_amount)
                holder.Amount.setText("₹ " + data.amount)
                holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
                holder.Status.setText(context.getString(R.string.you_won_this_game))
                holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
              //  holder.lblGameName.visibility = View.VISIBLE
             //   holder.lblCategoryName.visibility = View.VISIBLE
                holder.GameName.visibility = View.VISIBLE
                holder.CategoryName.visibility = View.GONE//
            }
            else if (data.biddata.game_type_name!!.contentEquals(context.getString(R.string.ratan_starline_game))) {

                holder.CategoryName.setText(data.biddata.open_panna + "-" + data.biddata.close_panna +
                        "   \u00b7   " + "₹ " + data.biddata.bid_amount)
                holder.Amount.setText("₹ " + data.amount)
                holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
                holder.Status.setText(context.getString(R.string.you_won_this_game))
                holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
              //  holder.lblGameName.visibility = View.VISIBLE
              //  holder.lblCategoryName.visibility = View.GONE
                holder.GameName.visibility = View.VISIBLE
                holder.CategoryName.visibility = View.GONE
            }
            else {
                holder.CategoryName.setText(data.biddata.number + "  \u00b7  " + "₹ " + data.biddata.bid_amount)
                holder.Amount.setText("₹ " + data.amount)
                holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.Green))
                holder.Status.setText(context.getString(R.string.you_won_this_game))
                holder.Status.setTextColor(ContextCompat.getColor(context, R.color.Green))
             //   holder.lblGameName.visibility = View.VISIBLE
             //   holder.lblCategoryName.visibility = View.VISIBLE
                holder.GameName.visibility = View.VISIBLE
                holder.CategoryName.visibility = View.GONE//
            }
        }
        else if (status.contentEquals(REFUND)) {

            holder.Amount.setText("₹ " + data.amount)
            holder.Amount.setTextColor(ContextCompat.getColor(context, R.color.DarkYellow))
            holder.Status.setText(context.getString(R.string.refund_points))
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.DarkYellow))
            holder.GameName.setVisibility(View.GONE)
            holder.CategoryName.setVisibility(View.GONE)
        }

    }

    override fun getItemCount(): Int = accountStatementList.size
}