package dangor.photobooth.root

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link RootBuilder.RootScope}.
 */
class RootRouter(
        view: RootView,
        interactor: RootInteractor,
        component: RootBuilder.Component
) : ViewRouter<RootView, RootInteractor, RootBuilder.Component>(view, interactor, component)
