package dangor.photobooth.root.home

import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.home.photo.PhotoBuilder
import dangor.photobooth.root.home.photo.PhotoRouter

/**
 * Adds and removes children of {@link HomeBuilder.HomeScope}.
 */
class HomeRouter(
        view: HomeView,
        interactor: HomeInteractor,
        component: HomeBuilder.Component,
        private val photoBuilder: PhotoBuilder
) : ViewRouter<HomeView, HomeInteractor, HomeBuilder.Component>(view, interactor, component) {

    var photo: PhotoRouter? = null

    fun attachPhoto() {
        if (photo == null) {
            photo = photoBuilder.build(view)
            attachChild(photo)
            view.addView(photo?.view)
        }
    }

    fun detachPhoto () {
        if (photo != null) {
            detachChild(photo)
            view.removeView(photo?.view)
            photo = null
        }
    }
}