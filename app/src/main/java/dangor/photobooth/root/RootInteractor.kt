package dangor.photobooth.root

import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import javax.inject.Inject

/**
 * Coordinates Business Logic for [RootScope].
 */
@RibInteractor
class RootInteractor : Interactor<RootInteractor.RootPresenter, RootRouter>() {

    @Inject lateinit var presenter: RootPresenter

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        router.attachHome()
    }

    override fun handleBackPress(): Boolean {
        return router.home?.handleBackPress() ?: false
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface RootPresenter
}
