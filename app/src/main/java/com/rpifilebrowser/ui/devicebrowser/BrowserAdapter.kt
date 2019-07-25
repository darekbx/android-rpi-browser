package com.rpifilebrowser.ui.devicebrowser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.rpifilebrowser.R
import com.rpifilebrowser.databinding.AdapterBrowserItemBinding
import com.rpifilebrowser.model.BrowserItem

class BrowserAdapter(context: Context) : ArrayAdapter<BrowserItem>(context, -1) {

    var onItemClick: ((item: BrowserItem) -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val browserItem = getItem(position)
        return (when (convertView) {
            null -> DataBindingUtil.inflate(inflater, R.layout.adapter_browser_item, parent, false)
            else -> DataBindingUtil.getBinding<AdapterBrowserItemBinding>(convertView)
        } as AdapterBrowserItemBinding)
            .apply {
                item = browserItem
                root.setOnClickListener { onItemClick?.invoke(browserItem) }
            }
            .root
    }

    private val inflater by lazy { LayoutInflater.from(context) }
}