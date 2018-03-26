package dangor.photobooth.root

import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.home.HomeBuilder
import dangor.photobooth.root.home.HomeRouter

/**
 * Adds and removes children of {@link RootBuilder.RootScope}.
 */
class RootRouter(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component,
        private val homeBuilder: HomeBuilder
) : ViewRouter<RootView, RootInteractor, RootBuilder.Component>(view, interactor, component) {

    var home: HomeRouter? = null

    fun attachHome() {
        home = homeBuilder.build(view)
        attachChild(home)
        view.addView(home?.view)
    }
}