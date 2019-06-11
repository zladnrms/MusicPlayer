package project.app.artistproject.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_alert.*
import project.app.artistproject.R


class AlertDialog {

    companion object {
        val instance = AlertDialog()
    }

    private val LAYOUT = R.layout.dialog_alert

    private var dialog: Dialog? = null

    fun show(context: Context, content: String) {
        if(dialog == null)
        {
            dialog = Dialog(context)
            dialog?.apply {
                this.requestWindowFeature(Window.FEATURE_NO_TITLE)
                this.setContentView(LAYOUT)
                this.setCancelable(false)
                this.show()

                val tv_content = this.findViewById(R.id.tv_content) as TextView
                tv_content.text = content

                button_close.setOnClickListener {
                    this.dismiss()
                }
            }
        }
    }

    fun dismiss() {
        dialog?.apply {
            this.dismiss()
        }
    }
}
