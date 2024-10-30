package com.example.kpu_app.ui_admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kpu_app.databinding.ItemLayoutBinding
import com.example.room1.database.Pemilih


class ListAdapter(private val context: Context, private val listData: List<Pemilih>, private val onClickData: (Pemilih) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ItemDataViewHolder>() {

    inner class ItemDataViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Pemilih) {
            with(binding) {
                // Lakukan binding data ke tampilan
                fullname.text = data.fullname
                gender.text = data.gender
                registerdate.text = data.registerdate
                Glide.with(context).load(data.picture)
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
