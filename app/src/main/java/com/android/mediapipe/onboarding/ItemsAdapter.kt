package com.android.mediapipe.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.mediapipe.R

class ItemsAdapter(private val lotti:List<Int>) :
    RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_slides_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.lottie_animation.setAnimation(lotti[position])

    }

    override fun getItemCount(): Int {
        return lotti.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lottie_animation: LottieAnimationView

        init {
            lottie_animation = itemView.findViewById(R.id.lottie_animation)
        }
    }
}