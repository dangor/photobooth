package dangor.photobooth.extensions

import android.view.View
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

val View.clicks: Observable<Unit>
    get() {
        val observable: Observable<Unit> = Observable.create { e ->
            this.setOnClickListener { e.onNext(Unit) }
        }

        return observable.throttleFirst(10, TimeUnit.MILLISECONDS)
    }