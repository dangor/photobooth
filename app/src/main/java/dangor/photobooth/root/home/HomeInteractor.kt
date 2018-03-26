package dangor.photobooth.root.home

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import javax.inject.Inject

/**
 * Coordinates Business Logic for [HomeScope].
 */
@RibInteractor
class HomeInteractor : Interactor<HomeInteractor.HomePresenter, HomeRouter>() {

    @Inject lateinit var presenter: HomePresenter

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface HomePresenter
}
