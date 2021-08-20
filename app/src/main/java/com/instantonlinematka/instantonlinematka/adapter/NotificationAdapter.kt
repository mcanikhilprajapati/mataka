package com.instantonlinematka.instantonlinematka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instantonlinematka.instantonlinematka.R
import com.instantonlinematka.instantonlinematka.model.NotificationData
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(
    val context: Context,
    val notificationList: ArrayList<NotificationData>
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var DateTime = itemView.lblDateTime
        var Title = itemView.lblTitle
        var Message = itemView.lblMessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val data = notificationList.get(position)

        holder.DateTime.text = data.created_date
        holder.Title.text = data.title + "  -- ${data.created_date}"
        holder.Message.text = data.msg

    }

    override fun getItemCount(): Int = notificationList.size
}