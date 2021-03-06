package dangor.photobooth.root.home.photo.review

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import dangor.photobooth.R
import dangor.photobooth.extensions.Bitmaps
import dangor.photobooth.extensions.clicks
import dangor.photobooth.extensions.isVisible
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.review_view.view.done_button
import kotlinx.android.synthetic.main.review_view.view.photo_progress
import kotlinx.android.synthetic.main.review_view.view.photo_strip
import kotlinx.android.synthetic.main.review_view.view.saved_notification
import kotlinx.android.synthetic.main.review_view.view.share_button
import kotlinx.android.synthetic.main.review_view.view.share_button_text
import kotlinx.android.synthetic.main.review_view.view.taken_photos
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * Top level view for Review
 *
 * Builder: [ReviewBuilder]
 * Interactor: [ReviewInteractor]
 * Router: [ReviewRouter]
 */
class ReviewView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), ReviewInteractor.ReviewPresenter {

    override val shareClicks: Observable<Unit> by lazy { share_button.clicks.filter { share_button.alpha == 1f }.share() }
    override val doneClicks: Observable<Unit> by lazy { done_button.clicks.share() }

    private val externalStoragePermissionRequestSubject = PublishSubject.create<Unit>()
    override val externalStoragePermissionRequests: Observable<Unit> get() = externalStoragePermissionRequestSubject.hide()

    private val photoStripSavedSubject = PublishSubject.create<File>()
    override val photoStripSaved: Observable<File> get() = photoStripSavedSubject.hide()

    private val emailAddressSubject = PublishSubject.create<String>()
    override val emailAddressGiven: Observable<String> get() = emailAddressSubject.hide()

    override fun showEmailAddressDialog() {
        val prompt = EmailPrompt(context)
        prompt.emailGiven
                .subscribe {
                    emailAddressSubject.onNext(it)
                    prompt.dismiss()
                }
        prompt.cancelClicks
                .subscribe { prompt.dismiss() }
        prompt.show()
    }

    override fun setIsShareEnabled(enabled: Boolean) {
        share_button.alpha = when {
            enabled -> 1f
            else -> 0.26f
        }
    }

    override fun setPictures(pictures: List<Bitmap>) {
        pictures.forEach {
            val imageView = LayoutInflater.from(context).inflate(R.layout.image_view, taken_photos, false) as ImageView
            imageView.setImageBitmap(it)
            taken_photos.addView(imageView)
        }
        photo_progress.isVisible = false

        this.post {
            savePhotoStrip()
        }
    }

    override fun externalStoragePermissionGranted() {
        savePhotoStrip()
    }

    override fun showEmailSentNotification() {
        Toast.makeText(context, "Email sent!", Toast.LENGTH_LONG).show()
        @SuppressLint("SetTextI18n")
        share_button_text.text = "Sent! Send another?"
    }

    override fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun setIsSaveNotificationVisible(visible: Boolean) {
        saved_notification.visibility = when {
            visible -> View.VISIBLE
            else -> View.INVISIBLE
        }
    }

    private fun savePhotoStrip() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            externalStoragePermissionRequestSubject.onNext(Unit)
            return
        }

        // Write to bitmap
        val b = Bitmap.createBitmap(
                (photo_strip.width * SAVED_FILE_SCALE).roundToInt(),
                (photo_strip.height * SAVED_FILE_SCALE).roundToInt(),
                Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        c.scale(SAVED_FILE_SCALE, SAVED_FILE_SCALE)
        photo_strip.draw(c)

        // Write to file
        val timestamp = DateTime.now().toString("yyyyMMdd_HHmmss")

        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME)
        folder.mkdirs()

        val file = File(folder, "${FILE_PREFIX}_$timestamp.png")
        file.createNewFile()

        val stream = FileOutputStream(file)
        stream.use {
            b.compress(Bitmap.CompressFormat.PNG, SAVED_FILE_QUALITY, it)
        }

        photoStripSavedSubject.onNext(file)
    }

    companion object {
        private const val ALBUM_NAME = "DangPhotobooth"
        private const val FILE_PREFIX = "dang_photobooth"
        private const val SAVED_FILE_QUALITY = 100
        private const val SAVED_FILE_SCALE = 2f
    }
}
