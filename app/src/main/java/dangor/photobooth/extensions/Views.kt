package dangor.photobooth.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

val View.clicks: Observable<Unit>
    get() {
        val observable: Observable<Unit> = Observable.create { e ->
            this.setOnClickListener { e.onNext(Unit) }
        }

        return observable.throttleFirst(10, TimeUnit.MILLISECONDS)
    }

val EditText.textChanges: Observable<String>
    get() {
        return Observable.create { e ->
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    e.onNext(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
            })
        }
    }

var View.isVisible: Boolean
    get() = this.visibility == VISIBLE
    set(value) {
        this.visibility = if (value) VISIBLE else GONE
    }