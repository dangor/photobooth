package dangor.photobooth.root.home

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link HomeBuilder.HomeScope}.
 */
class HomeRouter(
        view: HomeView,
        interactor: HomeInteractor,
        component: HomeBuilder.Component
) : ViewRouter<HomeView, HomeInteractor, HomeBuilder.Component>(view, interactor, component)
