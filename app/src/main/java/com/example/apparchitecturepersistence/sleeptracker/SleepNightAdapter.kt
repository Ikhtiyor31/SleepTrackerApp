package com.example.apparchitecturepersistence.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apparchitecturepersistence.R
import com.example.apparchitecturepersistence.convertNumericQualityToString
import com.example.apparchitecturepersistence.database.SleepNight
import com.example.apparchitecturepersistence.databinding.ListItemSleepNightBinding

class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<SleepNight,
SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder (val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            val res = itemView.context.resources
            binding.cardItem.setOnClickListener{clickListener.onClick(item)}
           /* binding.sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)*/
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(
                when (item.sleepQuality) {
                    0 -> R.drawable.ic_sleep_0
                    1 -> R.drawable.ic_sleep_1
                    2 -> R.drawable.ic_sleep_2
                    3 -> R.drawable.ic_sleep_3
                    4 -> R.drawable.ic_sleep_4
                    5 -> R.drawable.ic_sleep_5
                    else -> R.drawable.is_sleep_active
                }
            )
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}