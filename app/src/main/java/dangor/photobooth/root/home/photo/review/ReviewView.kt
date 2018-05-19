package dangor.photobooth.root.home.photo.review

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import dangor.photobooth.R
import dangor.photobooth.extensions.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.review_view.view.done_button
import kotlinx.android.synthetic.main.review_view.view.share_button
import kotlinx.android.synthetic.main.review_view.view.taken_photos
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

    override val shareClicks: Observable<Unit> by lazy { share_button.clicks.share() }
    override val doneClicks: Observable<Unit> by lazy { done_button.clicks.share() }

    private val externalStoragePermissionRequestSubject = PublishSubject.create<Unit>()
    override val externalStoragePermissionRequests: Observable<Unit> get() = externalStoragePermissionRequestSubject.hide()

    override fun setPictures(pictures: List<File>) {
        pictures.forEachIndexed { index, file ->
            val imageView = LayoutInflater.from(context).inflate(R.layout.image_view_large, taken_photos, false) as ImageView
            imageView.setImageURI(Uri.fromFile(file))
            taken_photos.addView(imageView, index) // always keep footer at bottom
        }

        this.post {
            savePhotoStrip()
        }
    }

    override fun externalStoragePermissionGranted() {
        savePhotoStrip()
    }

    private fun savePhotoStrip() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            externalStoragePermissionRequestSubject.onNext(Unit)
            return
        }

        // Write to bitmap
        val b = Bitmap.createBitmap(taken_photos.width, taken_photos.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        taken_photos.draw(c)

        // Write to file
        val timestamp = DateTime.now().toString("yyyyMMdd_HHmmss")

        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME)
        folder.mkdirs()

        val file = File(folder, "${FILE_PREFIX}_$timestamp.png")
        file.createNewFile()

        val stream = FileOutputStream(file)
        stream.use {
            b.compress(Bitmap.CompressFormat.PNG, UNUSED_PNG_QUALITY, it)
        }

        Log.i(TAG, "Saved photo to file ${file.absolutePath}")
    }

    companion object {
        private const val TAG = "ReviewView"
        private const val ALBUM_NAME = "DangPhotobooth"
        private const val FILE_PREFIX = "photostrip"
        private const val UNUSED_PNG_QUALITY = 100
    }
}
