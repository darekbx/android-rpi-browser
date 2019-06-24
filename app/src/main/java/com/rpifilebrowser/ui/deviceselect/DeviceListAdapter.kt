package com.rpifilebrowser.ui.deviceselect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rpifilebrowser.model.RemoteDevice
import com.rpifilebrowser.databinding.AdapterDeviceBinding

@BindingAdapter("app:signalIcon")
fun setSignalIcon(view: View, device: RemoteDevice) {
    val imageView = view as TextView
    imageView.setCompoundDrawablesWithIntrinsicBounds(device.signalStrengthIcon(), 0, 0, 0)
}

class DeviceListAdapter(context: Context) : RecyclerView.Adapter<DeviceListAdapter.RemoteDeviceViewHolder>() {

    var items = listOf<RemoteDevice>()

    fun swapData(items: List<RemoteDevice>) {
        this.items = items
        notifyDataSetChanged()
    }

    var onDeviceClick: ((device: RemoteDevice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteDeviceViewHolder {
        val binding = AdapterDeviceBinding.inflate(inflater, parent, false)
        return RemoteDeviceViewHolder(binding, onDeviceClick)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RemoteDeviceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private val inflater by lazy { LayoutInflater.from(context) }

    class RemoteDeviceViewHolder(
        val binding: AdapterDeviceBinding,
        val onDeviceClick: ((device: RemoteDevice) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: RemoteDevice) {
            binding.device = device
            binding.root.setOnClickListener {
                onDeviceClick?.invoke(device)
            }
            binding.executePendingBindings()
        }
    }
}