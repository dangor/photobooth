package dangor.photobooth.root.home.photo

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link PhotoBuilder.PhotoScope}.
 *
 * No children
 */
class PhotoRouter(
        view: PhotoView,
        interactor: PhotoInteractor,
        component: PhotoBuilder.Component
) : ViewRouter<PhotoView, PhotoInteractor, PhotoBuilder.Component>(view, interactor, component)
