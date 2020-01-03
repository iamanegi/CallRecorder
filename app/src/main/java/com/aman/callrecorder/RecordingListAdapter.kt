package com.aman.callrecorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RecordingListAdapter(private val list: Array<out File>?) :
    RecyclerView.Adapter<RecordingListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = list!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(list?.get(position))
    }


    class MyViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtTitle = itemView.findViewById<TextView>(R.id.itemTitle)

        fun setData(f: File?) {
            txtTitle.text = f?.name
        }
    }
}