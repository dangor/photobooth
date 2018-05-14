package dangor.photobooth.root.home.photo.review

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import dangor.photobooth.R
import dangor.photobooth.extensions.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.review_view.view.done_button
import kotlinx.android.synthetic.main.review_view.view.share_button
import kotlinx.android.synthetic.main.review_view.view.taken_photos
import java.io.File

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

    override fun setPictures(pictures: List<File>) {
        pictures.forEachIndexed { index, file ->
            val imageView = LayoutInflater.from(context).inflate(R.layout.image_view_large, taken_photos, false) as ImageView
            imageView.setImageURI(Uri.fromFile(file))
            taken_photos.addView(imageView, index) // always keep footer at bottom
        }
    }
}
