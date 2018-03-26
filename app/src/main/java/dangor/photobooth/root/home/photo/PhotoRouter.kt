package dangor.photobooth.root.home.photo

import android.view.View

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of {@link PhotoBuilder.PhotoScope}.
 *
 * TODO describe the possible child configurations of this scope.
 */
class PhotoRouter(
        view: PhotoView,
        interactor: PhotoInteractor,
        component: PhotoBuilder.Component
) : ViewRouter<PhotoView, PhotoInteractor, PhotoBuilder.Component>(view, interactor, component)
