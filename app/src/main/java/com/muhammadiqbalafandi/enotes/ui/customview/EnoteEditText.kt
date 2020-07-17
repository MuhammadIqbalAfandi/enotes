package com.muhammadiqbalafandi.enotes.ui.customview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Spanned
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.muhammadiqbalafandi.enotes.R

class EnoteEditText : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        init()
    }

    private fun init() {
        isFocusableInTouchMode = true
        inputType =
            EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        val padding = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        setPadding(padding, padding, padding, padding)
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        var id = id
        if (id == android.R.id.paste) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                id = android.R.id.pasteAsPlainText
            } else {
                onInterceptClipDataToPlainText()
            }
        }
        return super.onTextContextMenuItem(id)
    }

    private fun onInterceptClipDataToPlainText() {
        val clipboard: ClipboardManager = context
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = clipboard.primaryClip
        if (clip != null) {
            for (i in 0 until clip.itemCount) {
                val paste: CharSequence?
                // Get an item as text and remove all spans by toString().
                val text = clip.getItemAt(i).coerceToText(context)
                paste = (text as? Spanned)?.toString() ?: text
                if (paste != null) {
                    ClipBoards.copyToClipBoard(
                        context,
                        paste
                    )
                }
            }
        }
    }

    object ClipBoards {
        fun copyToClipBoard(
            context: Context,
            text: CharSequence
        ) {
            val clipData = ClipData.newPlainText("rebase_copy", text)
            val manager = context
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            manager.setPrimaryClip(clipData)
        }
    }
}