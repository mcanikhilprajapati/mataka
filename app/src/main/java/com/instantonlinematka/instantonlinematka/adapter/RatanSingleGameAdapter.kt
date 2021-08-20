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
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes.RatanSingleGameActivity
import kotlinx.android.synthetic.main.item_game_points.view.*

class RatanSingleGameAdapter(
    val context: Context,
    var singleGameList: ArrayList<GameDataList>,
    val singleGameActivity: RatanSingleGameActivity
) :
    RecyclerView.Adapter<RatanSingleGameAdapter.GamesViewHolder>() {

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

        val data = singleGameList.get(position)
        holder.GameNumber.text = data.numbers.toString()

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
                    singleGameList.set(position, GameDataList(data.numbers, 0))
                }
                else if (text.toString().toInt() < 10) {
                    singleGameList.set(position, GameDataList(data.numbers, 0))
                } else {
                    singleGameList.set(
                        position,
                        GameDataList(data.numbers, text.toString().toInt())
                    )
                }
                singleGameActivity.TotalPointsAdded()
            }

        })
    }

    override fun getItemCount(): Int = singleGameList.size

}