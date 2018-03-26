package dangor.photobooth.root

import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.home.HomeBuilder

/**
 * Adds and removes children of {@link RootBuilder.RootScope}.
 */
class RootRouter(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component,
        private val homeBuilder: HomeBuilder
) : ViewRouter<RootView, RootInteractor, RootBuilder.Component>(view, interactor, component) {

    fun attachHome() {
        val router = homeBuilder.build(view)
        attachChild(router)
        view.addView(router.view)
    }
}