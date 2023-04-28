package com.twincom.chatgpt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twincom.chatgpt.databinding.ItemAssistanceBinding
import com.twincom.chatgpt.response.Choice

class AdapterOpenAi(private val choice : List<Choice>): RecyclerView.Adapter<AdapterOpenAi.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemAssistanceBinding,
        val context: Context
    ): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(position: Int) {
            binding.tvMessagesAssistance.text = choice[position].text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAssistanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int = choice.size
}