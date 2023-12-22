package com.example.proyek_uas

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_uas.databinding.ItemMovieBinding
import com.example.room1.database.Movie

class HomeAdapter(private val context: Context, private val listData: List<Movie>, private val onClickData: (Movie) -> Unit) :
    RecyclerView.Adapter<HomeAdapter.ItemDataViewHolder>() {

    inner class ItemDataViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Movie) {
            with(binding) {
                // Lakukan binding data ke tampilan
                titleElemental.text = data.title
                rateElemental.text = "⭐ " + data.rating.toString()
                Glide.with(context).load(data.poster)
                    .centerCrop().into(elemental)

                // Atur listener untuk onClick
                itemView.setOnClickListener {
                    onClickData(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDataViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemDataViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}
