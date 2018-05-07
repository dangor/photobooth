package dangor.photobooth.root.home.photo

import android.animation.ObjectAnimator
import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import dangor.photobooth.R
import dangor.photobooth.extensions.clicks
import dangor.photobooth.extensions.isVisible
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.photo_view.view.camera_capture
import kotlinx.android.synthetic.main.photo_view.view.start_button
import kotlinx.android.synthetic.main.photo_view.view.taken_photos
import kotlinx.android.synthetic.main.photo_view.view.timer_progress
import kotlinx.android.synthetic.main.photo_view.view.timer_text
import java.io.File

/**
 * Top level view for {@link PhotoBuilder.PhotoScope}.
 */
class PhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), PhotoInteractor.PhotoPresenter {

    override val startClicks: Observable<Unit> get() = start_button.clicks

    private val timerDoneSubject = PublishSubject.create<Unit>()
    override val timerDone: Observable<Unit> get() = timerDoneSubject.hide()

    override fun setStartButtonVisible(visible: Boolean) {
        start_button.isVisible = visible
    }

    override fun hideTimer() {
        timer_text.isVisible = false
        timer_progress.isVisible = false
    }

    override fun startTimer(seconds: Int) {
        timer_text.isVisible = true
        timer_progress.isVisible = true

        CountDown(seconds) { secondsLeft ->
            timer_text.text = secondsLeft.toString()
        }.start()

        val animation = ObjectAnimator.ofInt(timer_progress, "progress", 0, 500)
        animation.duration = seconds * 1000L
        animation.interpolator = LinearInterpolator()
        animation.start()
    }

    private inner class CountDown(
            seconds: Int,
            private val onTick: (Int) -> Unit
    ) : CountDownTimer(seconds * 1000L, 1000L) {

        override fun onFinish() {
            timerDoneSubject.onNext(Unit)
        }

        override fun onTick(millisUntilFinished: Long) {
            this.onTick.invoke(Math.ceil(millisUntilFinished / 1000.0).toInt())
        }
    }

    override val cameraPermissionRequests: Observable<Unit> get() = camera_capture.cameraPermissionRequests

    override fun cameraPermissionGranted() {
        camera_capture.openCamera()
    }

    override val fileSaved: Observable<File> get() = camera_capture.fileSaved

    override fun takePhoto() {
        camera_capture.takePhoto()
    }

    override fun addPhotoPreview(file: File) {
        val imageView = LayoutInflater.from(context).inflate(R.layout.image_view, taken_photos, false) as ImageView
        imageView.setImageURI(Uri.fromFile(file))
        taken_photos.addView(imageView)
        taken_photos.isVisible = true
    }
}