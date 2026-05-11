// ui/adapters/MediaAdapter.kt
package com.gallery.app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gallery.app.R
import com.gallery.app.data.model.MediaItem
import com.gallery.app.databinding.ItemMediaBinding
import java.text.SimpleDateFormat
import java.util.*

class MediaAdapter(
    private val context: Context,
    private var items: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    
    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .placeholder(R.drawable.ic_image_placeholder)
        .error(R.drawable.ic_image_error)
    
    var currentPosition = 0
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
    
    override fun getItemCount(): Int = items.size
    
    fun updateItems(newItems: List<MediaItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    
    inner class ViewHolder(private val binding: ItemMediaBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: MediaItem) {
            binding.textFileName.text = item.name
            binding.textFileSize.text = formatFileSize(item.size)
            
            when (item.type) {
                com.gallery.app.data.model.MediaType.IMAGE -> {
                    Glide.with(context)
                        .load(item.uri)
                        .apply(requestOptions)
                        .thumbnail(0.25f)
                        .into(binding.imageThumbnail)
                    binding.videoDuration.visibility = android.view.View.GONE
                }
                com.gallery.app.data.model.MediaType.VIDEO -> {
                    Glide.with(context)
                        .load(item.uri)
                        .apply(requestOptions)
                        .thumbnail(0.25f)
                        .into(binding.imageThumbnail)
                    binding.videoDuration.visibility = android.view.View.VISIBLE
                    binding.videoDuration.text = formatDuration(item.duration)
                }
                else -> {
                    binding.imageThumbnail.setImageResource(getFileIcon(item.type))
                    binding.videoDuration.visibility = android.view.View.GONE
                }
            }
            
            binding.root.setOnClickListener {
                currentPosition = absoluteAdapterPosition
                onItemClick(item)
            }
            
            binding.root.setOnLongClickListener {
                // Show context menu for multi-select
                true
            }
        }
    }
    
    private fun formatFileSize(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var sizeDouble = size.toDouble()
        var unitIndex = 0
        while (sizeDouble >= 1024 && unitIndex < units.size - 1) {
            sizeDouble /= 1024
            unitIndex++
        }
        return String.format("%.1f %s", sizeDouble, units[unitIndex])
    }
    
    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
    
    private fun getFileIcon(type: com.gallery.app.data.model.MediaType): Int {
        return when (type) {
            com.gallery.app.data.model.MediaType.PDF -> R.drawable.ic_pdf
            com.gallery.app.data.model.MediaType.DOCUMENT -> R.drawable.ic_doc
            com.gallery.app.data.model.MediaType.TEXT -> R.drawable.ic_txt
            com.gallery.app.data.model.MediaType.PRESENTATION -> R.drawable.ic_ppt
            com.gallery.app.data.model.MediaType.SPREADSHEET -> R.drawable.ic_xls
            else -> R.drawable.ic_file
        }
    }
}