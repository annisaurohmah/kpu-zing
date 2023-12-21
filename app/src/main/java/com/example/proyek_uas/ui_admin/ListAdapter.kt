package com.example.proyek_uas.ui_admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_uas.Movie
import com.example.proyek_uas.databinding.ItemLayoutBinding


class ListAdapter(private val context: Context, private val listData: List<Movie>, private val onClickData: (Movie) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ItemDataViewHolder>() {

    inner class ItemDataViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Movie) {
            with(binding) {
                // Lakukan binding data ke tampilan
                titleTextView.text = data.title
                rating.text = "⭐ " + data.rating.toString()
                duration.text = data.duration.toString() + " minutes"
                Glide.with(context).load(data.poster)
                    .centerCrop().into(imageView)

                // Atur listener untuk onClick
                itemView.setOnClickListener {
                    onClickData(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDataViewHolder {
        val binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemDataViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}
