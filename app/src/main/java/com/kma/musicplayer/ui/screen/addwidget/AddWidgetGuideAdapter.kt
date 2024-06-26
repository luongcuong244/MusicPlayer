package com.kma.musicplayer.ui.screen.addwidget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kma.musicplayer.databinding.LayoutItemAddWidgetGuideBinding
import com.kma.musicplayer.model.Theme

class AddWidgetGuideAdapter : RecyclerView.Adapter<AddWidgetGuideAdapter.ViewHolder>() {

    private var theme = Theme.DARK

    fun setTheme(theme: Theme) {
        this.theme = theme
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemAddWidgetGuideBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(AddWidgetGuideModel.entries[position])
    }

    override fun getItemCount(): Int = AddWidgetGuideModel.entries.size

    inner class ViewHolder(val binding: LayoutItemAddWidgetGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: AddWidgetGuideModel) {
            binding.imgGuide.setImageResource(model.img)
            binding.tvTitle.setText(binding.root.context.getString(model.title))
            binding.tvDescription.setText(binding.root.context.getString(model.content))

            binding.tvTitle.setTextColor(binding.root.context.resources.getColor(theme.titleTextColorRes))
            binding.tvDescription.setTextColor(binding.root.context.resources.getColor(theme.descriptionTextColorRes))
            binding.root.setBackgroundColor(binding.root.context.resources.getColor(theme.backgroundColorRes))
        }
    }
}