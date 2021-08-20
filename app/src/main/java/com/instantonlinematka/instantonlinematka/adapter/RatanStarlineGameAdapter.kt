package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.RatanStarlineGameData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.RatanGameModesActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.item_ratan_starline_game_list.view.*
import kotlinx.android.synthetic.main.item_ratan_starline_game_list.view.txtResult
import kotlinx.android.synthetic.main.item_ratan_starline_game_list.view.txtStatus
import kotlinx.android.synthetic.main.item_ratan_starline_game_list.view.txtTime
import kotlinx.android.synthetic.main.item_ratan_starline_game_list_new.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
class RatanStarlineGameAdapter(val context: Context, val ratanGameList: ArrayList<RatanStarlineGameData>) :
    RecyclerView.Adapter<RatanStarlineGameAdapter.RatanStarlineViewHolder>() {

    class RatanStarlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblTime = itemView.txtTime
        val lblStatus = itemView.txtStatus
        val lblResult = itemView.txtResult
        val imgButton = itemView.cl_imageViewPlayStarlineGame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatanStarlineViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ratan_starline_game_list_new, parent, false)
        return RatanStarlineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatanStarlineViewHolder, position: Int) {

        val data = ratanGameList.get(position)

        holder.lblTime.text = ConvertTime.ConvertTimeToPM(data.open_time!!)

        val GameStatus = data.game_status!!

        if (GameStatus.contentEquals("0")) {
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                context, R.color.colorPrimary))
        }
        else if (GameStatus.contentEquals("1")) {
            holder.lblStatus.text = context.getString(R.string.bidding_is_running_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                context, R.color.Green))
        }
        else if (GameStatus.contentEquals("6")) {
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colorPrimary))
        }
        else {
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colorPrimary))
        }

        holder.lblResult.text = "${data.panna_result} - ${data.single_digit_result}"

        holder.imgButton.setSafeOnClickListener {

            YoYo.with(Techniques.RubberBand)
                .duration(400)
                .repeat(0)
                .playOn(holder.imgButton)

            if (GameStatus.contentEquals("0") ||
                   GameStatus.contentEquals("6")) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as DrawerActivity)
                        .setTitle(context.getString(R.string.game_closed))
                        .setMessage(context.getString(R.string.bidding_is_closed_for_today))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            }
            else {

                CoroutineScope(Dispatchers.Main).launch {
                    delay(600L)

                    val intent = Intent(context, RatanGameModesActivity::class.java)
                    intent.putExtra("gameId", data.game_id)
                    intent.putExtra("gameDate", data.game_date)
                    intent.putExtra("gameTime", data.open_time)
                    intent.putExtra("gameNumbers", "${data.panna_result}-${data.single_digit_result}")
                    intent.putExtra("status", holder.lblStatus.text.toString().trim())
                    context.startActivity(intent)
                }
            }
        }

    }

    override fun getItemCount(): Int = ratanGameList.size

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}