package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.util.SparseArray
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

import kotlinx.android.synthetic.main.item_ratan_starline_game_list_old.view.*
import kotlinx.android.synthetic.main.item_ratan_starline_game_list_old.view.lblPlayGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
class RatanStarlineGameAdapter(val context: Context, val ratanGameList: ArrayList<RatanStarlineGameData>) :
    RecyclerView.Adapter<RatanStarlineGameAdapter.RatanStarlineViewHolder>() {
    private val countDownMap: SparseArray<CountDownTimer> =   SparseArray();
    class RatanStarlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var countDownTimer: CountDownTimer? = null
        val lblTime = itemView.txtTime
        val lblStatus = itemView.txtStatus
        val lblResult = itemView.txtResult
        val imgButton = itemView.cl_imageViewPlayStarlineGame
        val lblPlayGame = itemView.lblPlayGame
        val imgPlayStatus = itemView.imageViewPlayButton
        val lblStatusTime = itemView.lblStatusTime


    }


    fun cancelAllTimers() {
        if (countDownMap == null) {
            return
        }
        Log.e("TAG", "size :  " + countDownMap.size())
        var i = 0
        val length = countDownMap.size()
        while (i < length) {
            val cdt = countDownMap[countDownMap.keyAt(i)]
            cdt?.cancel()
            i++
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatanStarlineViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ratan_starline_game_list_old, parent, false)
        return RatanStarlineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatanStarlineViewHolder, position: Int) {

        val data = ratanGameList.get(position)
        val CloseTime = data.close_time!!
        holder.lblTime.text = ConvertTime.ConvertTimeToPM(data.open_time!!)
        val GameStatus = data.game_status!!

        if (GameStatus.contentEquals("0")) {
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblPlayGame.text = "Closed"
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                context, R.color.BgRed))

            holder.imgPlayStatus.setImageResource(R.drawable.ic_close_2)
            holder.imgButton.setBackgroundResource(R.drawable.round_red_corner_2)
        }
        else if (GameStatus.contentEquals("1")) {
            holder.lblStatus.text = context.getString(R.string.bidding_is_running_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                context, R.color.Green))

            holder.lblPlayGame.text = "Play"
            holder.imgPlayStatus.setImageResource(R.drawable.ic_open)
            holder.imgButton.setBackgroundResource(R.drawable.round_green_corner_2)

            val time : Long= ConvertTime.getTimeDiff(CloseTime)
            if (time > 0) {
                holder.countDownTimer = object : CountDownTimer(time, 60000) {
                    override fun onTick(millisUntilFinished: Long) {
                        holder.lblStatusTime.setText(
                            ConvertTime.getCountTimeByLong(
                                millisUntilFinished
                            )
                        )
                        Log.e("TAG", "===>>" + ConvertTime.getCountTimeByLong(millisUntilFinished))
                    }

                    override fun onFinish() {
                        holder.lblStatusTime.setText("00:00:00")
                    }
                }.start()

                countDownMap.put(holder.lblStatusTime.hashCode(), holder.countDownTimer)
            } else {
                holder.lblStatusTime.setText("00:00:00")
            }

        }
        else if (GameStatus.contentEquals("6")) {
            holder.lblPlayGame.text = "Closed"
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.BgRed))
            holder.imgPlayStatus.setImageResource(R.drawable.ic_close_2)
            holder.imgButton.setBackgroundResource(R.drawable.round_red_corner_2)
        }
        else {
            holder.lblPlayGame.text = "Closed"
            holder.lblStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.BgRed))

            holder.imgPlayStatus.setImageResource(R.drawable.ic_close_2)
            holder.imgButton.setBackgroundResource(R.drawable.round_red_corner_2)
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