package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.GameModes
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.RatanGameModesActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes.RatanDoublePannaGameActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes.RatanSingleGameActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes.RatanSinglePannaGameActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.ratan.gametypes.RatanTriplePannaGameActivity
import kotlinx.android.synthetic.main.item_game_modes.view.*

@SuppressLint("RestrictedApi")
class RatanGameModesAdapter(
    val context: Context,
    val gamesList: ArrayList<GameModes>
) :
    RecyclerView.Adapter<RatanGameModesAdapter.RatanGamesViewHolder>() {

    class RatanGamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val GameImage = itemView.imgGameMode
        val GameName = itemView.lblGameNameAnswer
        val btnGame = itemView.btnSelectGame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatanGamesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_modes, parent, false)
        return RatanGamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatanGamesViewHolder, position: Int) {

        val data = gamesList.get(position)

        holder.GameImage.setImageDrawable(data.gameImage)
        holder.GameName.text = data.gameName
        holder.GameName.setTextColor(data.gameColor!!)

        holder.btnGame.setSafeOnClickListener {
            selection(data.gameId!!, data.gameCatId!!, data.gameName!!, data.Status!!,
            data.GameDate!!, data.GameTime!!, data.GameNumbers!!)
        }
    }

    override fun getItemCount(): Int = gamesList.size

    fun selection(
        gameId: String,
        catId: String,
        gameName: String,
        status: String,
        date: String,
        time: String,
        numbers: String
    ) {

        var intent: Intent? = null

        if (gameName.contentEquals(context.getString(R.string.single_cap))) {
            intent = Intent(context, RatanSingleGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.single_panna_cap))) {
            intent = Intent(context, RatanSinglePannaGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.double_panna_cap))) {
            intent = Intent(context, RatanDoublePannaGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.triple_panna_cap))) {
            intent = Intent(context, RatanTriplePannaGameActivity::class.java)
        }

        intent!!.putExtra("gameId", gameId)
        intent.putExtra("mode", gameName)
        intent.putExtra("catId", catId)
        intent.putExtra("status", status)
        intent.putExtra("date", date)
        intent.putExtra("time", time)
        intent.putExtra("numbers", numbers)
        (context as RatanGameModesActivity).startActivityForResult(intent, 2)

    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}