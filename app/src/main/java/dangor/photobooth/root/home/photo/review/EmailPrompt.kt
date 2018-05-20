package dangor.photobooth.root.home.photo.review

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatDialog
import android.util.Patterns.EMAIL_ADDRESS
import android.view.inputmethod.InputMethodManager
import dangor.photobooth.R
import dangor.photobooth.extensions.clicks
import dangor.photobooth.extensions.textChanges
import dangor.photobooth.extensions.withLatestFrom
import io.reactivex.Observable
import kotlinx.android.synthetic.main.email_dialog.cancel_button
import kotlinx.android.synthetic.main.email_dialog.email_input
import kotlinx.android.synthetic.main.email_dialog.send_button

class EmailPrompt(context: Context) : AppCompatDialog(context) {

    private val textChanges: Observable<String>

    init {
        window.setBackgroundDrawableResource(R.drawable.dialog_background)
        setContentView(R.layout.email_dialog)
        window.setDimAmount(0.3.toFloat())

        setOnShowListener {
            email_input.apply {
                requestFocus()
                postDelayed({
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }, 100)
            }
        }

        textChanges = email_input.textChanges.map { it.trim() }.share()

        textChanges
                .map { EMAIL_ADDRESS.matcher(it).matches() }
                .subscribe(send_button::setEnabled)
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(email_input.windowToken, 0)
    }

    val cancelClicks: Observable<Unit> by lazy {
        cancel_button.clicks
                .doOnNext { hideKeyboard() }
                .share()
    }
    val emailGiven: Observable<String> by lazy {
        send_button.clicks
                .withLatestFrom(textChanges, { _, text -> text })
                .doOnNext { hideKeyboard() }
                .share()
    }
}