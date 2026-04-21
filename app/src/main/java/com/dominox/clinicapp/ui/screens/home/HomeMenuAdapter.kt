package com.dominox.clinicapp.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.google.android.material.card.MaterialCardView

class HomeMenuAdapter(
    private val items: List<HomeMenuItem>,
    private val onItemClick: (HomeMenuItem) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val card: MaterialCardView = view.findViewById(R.id.menuCard)
        private val icon: ImageView = view.findViewById(R.id.menuIcon)
        private val title: TextView = view.findViewById(R.id.menuTitle)
        private val description: TextView = view.findViewById(R.id.menuDescription)

        fun bind(item: HomeMenuItem, onItemClick: (HomeMenuItem) -> Unit) {
            icon.setImageResource(item.iconResId)
            title.text = item.title
            description.text = item.description
            card.setOnClickListener { onItemClick(item) }
        }
    }
}
