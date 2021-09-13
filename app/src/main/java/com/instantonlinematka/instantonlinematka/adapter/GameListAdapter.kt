package com.instantonlinematka.instantonlinematka.adapter

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.GameListData
import com.instantonlinematka.instantonlinematka.utility.ConvertTime
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.view.activity.DrawerActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.market.MarketGameModesActivity
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.item_game_list_final_old.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.CountDownTimer

import android.util.SparseArray
import android.R.attr.data



interface onTimerCompleteListener {
    fun onTimerComplete(item: Int)
}



class GameListAdapter(val context: Context, val gameList: ArrayList<GameListData>,listener: onTimerCompleteListener ) :
    RecyclerView.Adapter<GameListAdapter.GameHolder>(

    ) {
    private val countDownMap: SparseArray<CountDownTimer> =   SparseArray();
    lateinit var GameOpenResults: String
    lateinit var GameCenterOpenResults: String
    lateinit var GameCenterCloseResults: String
    lateinit var GameCloseResults: String
      var onTimerCompleteListener=listener


    class GameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var countDownTimer: CountDownTimer? = null
        val lblGameName = itemView.txtGameName
        val lblGameNumber = itemView.txtNumbers
        val lblOpenTime = itemView.txtOpenTime
        val lblCloseTime = itemView.txtCloseTime
        val lblBiddingStatus = itemView.txtBiddingStatus
        val lblPlayGame = itemView.lblPlayGame
        val imgPlayButton = itemView.constraintLayout12
        val imgPlayStatus = itemView.imgPlayStatus
        val lblStatusTime = itemView.lblStatusTime
       // val btnPlayGame = itemView.btnPlayGame
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_list_final_old, parent, false)

        return GameHolder(v)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: GameHolder, @SuppressLint("RecyclerView") position: Int) {

        if (holder.countDownTimer != null) {
            holder.countDownTimer!!.cancel();
        }
        val gameData = gameList.get(position)

        val GameName = gameData.game_type_name!!
        val OpenTime = gameData.open_time!!
        val CloseTime = gameData.close_time!!
        val OpenResults = gameData.open_result!!
        val CloseResults = gameData.close_result!!
        val CenterOpenResults = gameData.center_open_result!!
        val CenterCloseResults = gameData.center_close_result!!
        val GameStatus = gameData.game_status!!



        if (GameName.isEmpty())
            holder.lblGameName.text = "- - -"
        else holder.lblGameName.text = GameName

        if (OpenTime.isEmpty())
            holder.lblOpenTime.text = "- - -"
        else holder.lblOpenTime.text = ConvertTime.ConvertTimeToPM(OpenTime)

        if (CloseTime.isEmpty())
            holder.lblCloseTime.text = "- - -"
        else holder.lblCloseTime.text = ConvertTime.ConvertTimeToPM(CloseTime)

        if (OpenResults.isEmpty())
            GameOpenResults = "***"
        else GameOpenResults = OpenResults

        if (CenterOpenResults.isEmpty())
            GameCenterOpenResults = "*"
        else GameCenterOpenResults = CenterOpenResults

        if (CenterCloseResults.isEmpty())
            GameCenterCloseResults = "*"
        else GameCenterCloseResults = CenterCloseResults

        if (CloseResults.isEmpty())
            GameCloseResults = "***"
        else GameCloseResults = CloseResults

        holder.lblGameNumber.text =
            "$GameOpenResults-$GameCenterOpenResults$GameCenterCloseResults-$GameCloseResults"


        //holder.lblStatusTime.text = ConvertTime.ConvertTimeDiff("23:55")
//        Toast.makeText(context,"Close time = "+ConvertTime.ConvertTimeDiff("18:00"),Toast.LENGTH_SHORT).show()
        if (GameStatus.contentEquals("0")) {
//            holder.lblBiddingStatus.text = context.getString(R.string.bidding_is_closed_for_today)
            holder.lblPlayGame.text = "Closed"
            holder.lblBiddingStatus.text = "Market Closed"
            holder.lblBiddingStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.Red
                )
            )
            holder.lblStatusTime.setText("00:00:00")
            holder.imgPlayStatus.setImageResource(R.drawable.ic_close_2)
            holder.imgPlayButton.setBackgroundResource(R.drawable.round_red_corner_2)
//            holder.imgPlayButton.setImageDrawable(
//                ContextCompat.getDrawable(
//                    context,
//                    R.drawable.play_button
//                )
//            )
        } else if (GameStatus.contentEquals("1")) {
//            holder.lblBiddingStatus.text = context.getString(R.string.bidding_is_running_for_today)
            holder.lblBiddingStatus.text = "Market Open"
            holder.lblPlayGame.text = "Play"
            holder.lblBiddingStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.Green
                )
            )
            val time : Long= ConvertTime.getTimeDiff(OpenTime)
            if (time > 0) {
                holder.countDownTimer = object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
//                        Toast.makeText(context,"LoadING",Toast.LENGTH_SHORT).show()
                        // onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText(
                            ConvertTime.getCountTimeByLong(
                                millisUntilFinished
                            )
                        )
                        Log.e("TAG", "===>>" + ConvertTime.getCountTimeByLong(millisUntilFinished))
                    }

                    override fun onFinish() {
                        onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText("00:00:00")
                    }
                }.start()

                countDownMap.put(holder.lblStatusTime.hashCode(), holder.countDownTimer)
            } else {
                holder.lblStatusTime.setText("00:00:00")
            }

            holder.imgPlayStatus.setImageResource(R.drawable.ic_open)
            holder.imgPlayButton.setBackgroundResource(R.drawable.round_green_corner_2)
//            holder.imgPlayButton.setImageDrawable(
//                ContextCompat.getDrawable(
//                    context,
//                    R.drawable.play_button_green
//                )
//            )
        } else if (GameStatus.contentEquals("6")) {
//            holder.lblBiddingStatus.text =                context.getString(R.string.bidding_is_running_for_close_bids)
          //  Toast.makeText(context,"Close time = "+CloseTime,Toast.LENGTH_SHORT).show()

            val dateFormat = SimpleDateFormat("HH.mm")
            val currentDate: String = dateFormat.format(Date()).toString()



            val time : Long= ConvertTime.getTimeDiff(CloseTime)
            if (time > 0) {
                 holder.countDownTimer = object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
//                        Toast.makeText(context,"LoadING",Toast.LENGTH_SHORT).show()
                       // onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText(
                            ConvertTime.getCountTimeByLong(
                                millisUntilFinished
                            )
                        )
                        Log.e("TAG", "===>>" + ConvertTime.getCountTimeByLong(millisUntilFinished))
                    }

                    override fun onFinish() {
                        onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText("00:00:00")
                    }
                }.start()

                countDownMap.put(holder.lblStatusTime.hashCode(), holder.countDownTimer)
            } else {
                holder.lblStatusTime.setText("00:00:00")
            }

            holder.lblBiddingStatus.text =         "Market Running"
            holder.lblPlayGame.text = "Play"
            holder.lblStatusTime.text = ConvertTime.ConvertTimeDiff(CloseTime)
            holder.lblBiddingStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.DarkYellow
                )
            )
            holder.imgPlayStatus.setImageResource(R.drawable.ic_play_2)
            holder.imgPlayButton.setBackgroundResource(R.drawable.round_orange_corner_2)
//            holder.imgPlayButton.setImageDrawable(
//                ContextCompat.getDrawable(
//                    context,
//                    R.drawable.play_button_yellow
//                )
//            )
        } else {
//            holder.lblBiddingStatus.text =
//                context.getString(R.string.bidding_is_running_for_close_bids)
                    holder.lblBiddingStatus.text ="Market Open"
                context.getString(R.string.bidding_is_running_for_close_bids)
            holder.lblBiddingStatus.setTextColor(
                ContextCompat.getColor(
                    context, R.color.Green
                )
            )
            val time : Long= ConvertTime.getTimeDiff(OpenTime)
            if (time > 0) {
                holder.countDownTimer = object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
//                        Toast.makeText(context,"LoadING",Toast.LENGTH_SHORT).show()
                        // onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText(
                            ConvertTime.getCountTimeByLong(
                                millisUntilFinished
                            )
                        )
                        Log.e("TAG", "===>>" + ConvertTime.getCountTimeByLong(millisUntilFinished))
                    }

                    override fun onFinish() {
                          onTimerCompleteListener.onTimerComplete(position)
                        holder.lblStatusTime.setText("00:00:00")
                    }
                }.start()

                countDownMap.put(holder.lblStatusTime.hashCode(), holder.countDownTimer)
            } else {
                holder.lblStatusTime.setText("00:00:00")
            }

            holder.lblPlayGame.text = "Play"
            holder.imgPlayStatus.setImageResource(R.drawable.ic_open)
            holder.imgPlayButton.setBackgroundResource(R.drawable.round_green_corner_2)

//            holder.imgPlayButton.setImageDrawable(
//                ContextCompat.getDrawable(
//                    context,
//                    R.drawable.play_button_green
//                )
//            )
        }

        holder.imgPlayButton.setSafeOnClickListener {

//            YoYo.with(Techniques.RubberBand)
//                .duration(400)
//                .repeat(0)
//                .playOn(holder.imgPlayButton)

            if (GameStatus.contentEquals("0")) {
                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as DrawerActivity)
                        .setTitle(GameName)
                        .setMessage(context.getString(R.string.bidding_is_closed_for_today))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()
            } else {

                CoroutineScope(Main).launch {
                    delay(600L)
                    val intent = Intent(context, MarketGameModesActivity::class.java)
                    intent.putExtra("gameTypeId", gameData.game_type_id)
                    intent.putExtra("status", holder.lblBiddingStatus.text.toString().trim())
                    intent.putExtra("game_name", holder.lblGameName.text.toString().trim())
                    intent.putExtra("game_number", holder.lblGameNumber.text.toString().trim())
                    intent.putExtra("open_bid", holder.lblOpenTime.text.toString().trim())
                    intent.putExtra("close_bid", holder.lblCloseTime.text.toString().trim())
                    context.startActivity(intent)

                }
            }
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