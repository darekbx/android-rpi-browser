package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.rpifilebrowser.R

class FileContentsDialog : DialogFragment() {

    companion object {
        var TAG = "FileContentsDialog"
    }

    var contents: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        with(inflater.inflate(R.layout.dialog_file_preview, container, false)) {
            findViewById<EditText>(R.id.content).setText(contents)
            findViewById<View>(R.id.close_button).setOnClickListener { dialog?.dismiss() }
            return this
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window.setLayout(width, height)
        }
    }
}