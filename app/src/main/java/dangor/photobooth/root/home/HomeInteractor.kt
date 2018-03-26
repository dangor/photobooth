package dangor.photobooth.root.home

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dangor.photobooth.root.home.photo.PhotoInteractor
import io.reactivex.Observable
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
        return router.photo?.handleBackPress() ?: false
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface HomePresenter {
        val photoClicks: Observable<Unit>
        val videoClicks: Observable<Unit>
    }

    inner class PhotoListener : PhotoInteractor.Listener {
        override fun back() {
            router.detachPhoto()
        }
    }
}
