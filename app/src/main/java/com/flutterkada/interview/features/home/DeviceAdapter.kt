package com.flutterkada.interview.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flutterkada.interview.R
import com.flutterkada.interview.core.device.domain.model.Device
import com.flutterkada.interview.databinding.ItemDeviceBinding

class DeviceAdapter(
    private val onItemClicked: (Device) -> Unit
) : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceViewHolder(
        private val binding: ItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(getItem(adapterPosition))
            }
        }

        fun bind(device: Device) {
            binding.deviceName.text = device.name
            binding.deviceIp.text = device.ipAddress
            
            val statusColor = if (device.isOnline) {
                R.color.status_online
            } else {
                R.color.status_offline
            }
            binding.statusIndicator.setBackgroundColor(
                ContextCompat.getColor(itemView.context, statusColor)
            )
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }
    }
}
