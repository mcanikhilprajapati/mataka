package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.GameDataList
import com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes.JodiGameActivity
import kotlinx.android.synthetic.main.item_game_points.view.*

class JodiGameAdapter(
    val context: Context,
    var jodiGameList: ArrayList<GameDataList>,
    val jodiGameActivity: JodiGameActivity
) :
    RecyclerView.Adapter<JodiGameAdapter.GamesViewHolder>() {

    class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val GameNumber = itemView.lblNumber
        val UserNumber = itemView.txtNumberAnswer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_points, parent, false)
        return GamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {

        val data = jodiGameList.get(position)

        var digits = ""
        if (data.numbers.toInt() < 10) {
            digits = "0${data.numbers}"
        } else {
            digits = data.numbers
        }

        holder.GameNumber.text = digits

        holder.UserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if (text.toString().isEmpty()) {
                    jodiGameList.set(position, GameDataList(data.numbers, 0))
                }
                else if (text.toString().toInt() < 10) {
                    jodiGameList.set(position, GameDataList(data.numbers, 0))
                }
                else {

                    if (data.numbers.toInt() < 10) {
                        jodiGameList.set(
                            position,
                            GameDataList(data.numbers, text.toString().toInt())
                        )
                    }
                    else {
                        jodiGameList.set(
                            position,
                            GameDataList(data.numbers, text.toString().toInt())
                        )
                    }
                }
                jodiGameActivity.TotalPointsAdded()
            }

        })
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = jodiGameList.size

}