package dangor.photobooth.root.home

import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.home.photo.PhotoBuilder
import dangor.photobooth.root.home.photo.PhotoRouter
import dangor.photobooth.root.home.photo.review.ReviewBuilder
import dangor.photobooth.root.home.photo.review.ReviewRouter
import java.io.File

/**
 * Adds and removes children of {@link HomeBuilder.HomeScope}.
 */
class HomeRouter(
        view: HomeView,
        interactor: HomeInteractor,
        component: HomeBuilder.Component,
        private val photoBuilder: PhotoBuilder,
        private val reviewBuilder: ReviewBuilder
) : ViewRouter<HomeView, HomeInteractor, HomeBuilder.Component>(view, interactor, component) {

    var photo: PhotoRouter? = null
    var review: ReviewRouter? = null

    fun attachPhoto() {
        if (photo == null) {
            photo = photoBuilder.build(view)
            attachChild(photo)
            view.addView(photo?.view)
        }
    }

    fun detachPhoto() {
        if (photo != null) {
            detachChild(photo)
            view.removeView(photo?.view)
            photo = null
        }
    }

    fun attachReview(pictures: List<File>) {
        if (review == null) {
            review = reviewBuilder.build(view, pictures)
            attachChild(review)
            view.addView(review?.view)
        }
    }

    fun detachReview() {
        review ?: return

        detachChild(review)
        view.removeView(review?.view)
        review = null
    }
}