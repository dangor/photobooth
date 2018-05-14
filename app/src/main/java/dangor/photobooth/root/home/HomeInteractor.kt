package dangor.photobooth.root.home

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dangor.photobooth.root.home.photo.PhotoInteractor
import dangor.photobooth.root.home.photo.review.ReviewInteractor
import io.reactivex.Observable
import java.io.File
import javax.inject.Inject

/**
 * Coordinates Business Logic for [HomeScope].
 */
@RibInteractor
class HomeInteractor : Interactor<HomeInteractor.HomePresenter, HomeRouter>() {

    @Inject lateinit var presenter: HomePresenter

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter.photoClicks
                .subscribe { router.attachPhoto() }

        presenter.videoClicks
                .subscribe {  }
    }

    override fun handleBackPress(): Boolean {
        return (router.photo?.handleBackPress() ?: false) ||
                (router.review?.handleBackPress() ?: false)
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface HomePresenter {
        val photoClicks: Observable<Unit>
        val videoClicks: Observable<Unit>
    }

    inner class PhotoListener : PhotoInteractor.Listener {
        override fun photosTaken(pictures: List<File>) {
            router.detachPhoto()
            router.attachReview(pictures)
        }

        override fun back() {
            router.detachPhoto()
        }
    }

    inner class ReviewListener : ReviewInteractor.Listener {
        override fun doneClicked() {
            router.detachReview()
        }
    }
}
