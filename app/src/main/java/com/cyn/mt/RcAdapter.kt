package com.cyn.mt

import android.content.Context
import android.view.LayoutInflater
import android.view.LayoutInflater.*
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rc_item.view.*

/**
 * RecyclerView Adapter
 */
class RcAdapter(var context: Context) : RecyclerView.Adapter<RcAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflate = from(context).inflate(R.layout.rc_item, parent, false)
        return Holder(inflate)
    }

    override fun getItemCount(): Int {
        //模拟30条数据
        return 30
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.testText.text = "模拟数据position=$position"
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testText: TextView = itemView.testText
    }
}