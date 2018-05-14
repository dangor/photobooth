package dangor.photobooth.root.home.photo.review

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import io.reactivex.Observable
import java.io.File
import javax.inject.Inject

/**
 * Coordinates Business Logic for Review.
 *
 * Builder: [ReviewBuilder]
 * Router: [ReviewRouter]
 * Presenter: [ReviewView]
 */
@RibInteractor
class ReviewInteractor : Interactor<ReviewInteractor.ReviewPresenter, ReviewRouter>() {

    @Inject lateinit var presenter: ReviewPresenter
    @Inject lateinit var listener: Listener
    @Inject lateinit var pictures: List<File>

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter.setPictures(pictures)

        presenter.doneClicks
                .subscribe { listener.doneClicked() }
    }

    override fun handleBackPress(): Boolean {
        // do nothing
        return true
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface ReviewPresenter {
        val shareClicks: Observable<Unit>
        val doneClicks: Observable<Unit>

        fun setPictures(pictures: List<File>)
    }

    /**
     * Listener interface implemented by a parent RIB's interactor's inner class.
     */
    interface Listener {
        fun doneClicked()
    }
}
