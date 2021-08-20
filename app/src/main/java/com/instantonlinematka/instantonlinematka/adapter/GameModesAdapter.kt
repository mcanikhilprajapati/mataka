package com.instantonlinematka.instantonlinematka.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.GameModes
import com.instantonlinematka.instantonlinematka.utility.Connectivity
import com.instantonlinematka.instantonlinematka.utility.Constants
import com.instantonlinematka.instantonlinematka.utility.SafeClickListener
import com.instantonlinematka.instantonlinematka.view.activity.games.market.MarketGameModesActivity
import com.instantonlinematka.instantonlinematka.view.activity.games.market.gametypes.*
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_single_game.*
import kotlinx.android.synthetic.main.alert_game_type.*
import kotlinx.android.synthetic.main.alert_submit_game.*
import kotlinx.android.synthetic.main.item_account_statement.view.lblGameNameAnswer
import kotlinx.android.synthetic.main.item_game_modes.view.*

@SuppressLint("RestrictedApi")
class GameModesAdapter(
    val context: Context,
    val gamesList: ArrayList<GameModes>
) :
    RecyclerView.Adapter<GameModesAdapter.GamesViewHolder>() {

    class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val GameImage = itemView.imgGameMode
        val GameName = itemView.lblGameNameAnswer
        val btnGame = itemView.btnSelectGame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_modes, parent, false)
        return GamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {

        val data = gamesList.get(position)

        holder.GameImage.setImageDrawable(data.gameImage)
        holder.GameName.text = data.gameName
        holder.GameName.setTextColor(data.gameColor!!)

        holder.btnGame.setSafeOnClickListener {

            if (data.gameColor == ContextCompat.getColor(context, R.color.LightGrey)) {

                val mBottomSheetDialog: BottomSheetMaterialDialog =
                    BottomSheetMaterialDialog.Builder(context as MarketGameModesActivity)
                        .setTitle(data.gameName!!)
                        .setMessage(context.getString(R.string.game_not_available))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.okay)) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()

                mBottomSheetDialog.show()

            } else if (data.gameName!!.contentEquals(context.getString(R.string.jodi_cap))) {

                val intent = Intent(context, JodiGameActivity::class.java)
                intent.putExtra("gameTypeId", data.gameTypeId!!)
                intent.putExtra("gameId", data.gameId!!)
                intent.putExtra("type", "")
                intent.putExtra("mode", data.gameName)
                intent.putExtra("catId", data.gameCatId!!)
                (context as MarketGameModesActivity).startActivityForResult(intent, 1)
            } else if (data.gameName.contentEquals(context.getString(R.string.half_sangam_cap))) {

                val intent = Intent(context, HalfSangamActivity::class.java)
                intent.putExtra("gameTypeId", data.gameTypeId!!)
                intent.putExtra("gameId", data.gameId!!)
                intent.putExtra("type", "")
                intent.putExtra("mode", data.gameName)
                intent.putExtra("catId", data.gameCatId!!)
                (context as MarketGameModesActivity).startActivityForResult(intent, 1)
            } else if (data.gameName.contentEquals(context.getString(R.string.full_sangam_cap))) {

                val intent = Intent(context, FullSangamActivity::class.java)
                intent.putExtra("gameTypeId", data.gameTypeId!!)
                intent.putExtra("gameId", data.gameId!!)
                intent.putExtra("type", "")
                intent.putExtra("mode", data.gameName)
                intent.putExtra("catId", data.gameCatId!!)
                (context as MarketGameModesActivity).startActivityForResult(intent, 1)
            } else {
                val mDialog = Dialog(context)
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                mDialog.setContentView(R.layout.alert_game_type)
                mDialog.window!!.setGravity(Gravity.CENTER)

                val imgOpen = mDialog.imgOpenGameType
                val imgClose = mDialog.imgCloseGameType

                val lblOpen = mDialog.lblOpenGameType
                val lblClose = mDialog.lblCloseGameType

                val btnOpen = mDialog.btnOpenType
                val btnClose = mDialog.btnCloseType

                if (data.expired!!) {
//                    imgOpen.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.ic_dice_gray
//                        )
//                    )
                    lblOpen.setTextColor(ContextCompat.getColor(context, R.color.LightGrey))
//                    imgClose.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.ic_dice_red
//                        )
//                    )
                    lblClose.setTextColor(ContextCompat.getColor(context, R.color.Black))
                    btnOpen.isEnabled = false
                } else {
//                    imgOpen.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.ic_dice_red
//                        )
//                    )
                    lblOpen.setTextColor(ContextCompat.getColor(context, R.color.Black))
//                    imgClose.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.ic_dice_red
//                        )
//                    )
                    lblClose.setTextColor(ContextCompat.getColor(context, R.color.Black))
                    btnOpen.isEnabled = true
                }

                btnOpen.setSafeOnClickListener {
                    selection(
                        data.gameTypeId!!,
                        context.getString(R.string.open_type),
                        data.gameName,
                        data.gameId!!,
                        data.gameCatId!!
                    )
                    mDialog.dismiss()
                }

                btnClose.setSafeOnClickListener {
                    selection(
                        data.gameTypeId!!, context.getString(R.string.close_type), data.gameName,
                        data.gameId!!, data.gameCatId!!
                    )
                    mDialog.dismiss()
                }

                mDialog.show()

            }
        }
    }

    override fun getItemCount(): Int = gamesList.size

    fun selection(
        gameTypeId: String,
        type: String,
        gameName: String,
        gameId: String,
        catId: String
    ) {

        var intent: Intent? = null

        if (gameName.contentEquals(context.getString(R.string.single_cap))) {
            intent = Intent(context, SingleGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.single_panna_cap))) {
            intent = Intent(context, SinglePannaGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.double_panna_cap))) {
            intent = Intent(context, DoublePannaGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.triple_panna_cap))) {
            intent = Intent(context, TriplePannaGameActivity::class.java)
        } else if (gameName.contentEquals(context.getString(R.string.half_sangam_cap))) {
            intent = Intent(context, HalfSangamActivity::class.java)
        }

        intent!!.putExtra("gameTypeId", gameTypeId)
        intent.putExtra("gameId", gameId)
        intent.putExtra("type", type)
        intent.putExtra("mode", gameName)
        intent.putExtra("catId", catId)
        (context as MarketGameModesActivity).startActivityForResult(intent, 1)

    }

    // Exxtension Function
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}