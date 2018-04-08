package dangor.photobooth.root.home.photo

import com.uber.autodispose.kotlin.autoDisposable
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dangor.photobooth.services.PermissionService
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinates Business Logic for [PhotoScope].
 *
 * TODO describe the logic of this scope.
 */
@RibInteractor
class PhotoInteractor : Interactor<PhotoInteractor.PhotoPresenter, PhotoRouter>() {

    @Inject lateinit var presenter: PhotoPresenter
    @Inject lateinit var listener: Listener
    @Inject lateinit var permissionService: PermissionService

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter.cameraPermissionRequests
                .switchMap { permissionService.request(Permission.CAMERA) }
                .autoDisposable(this)
                .subscribe { presenter.cameraPermissionGranted() }
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface PhotoPresenter {
        val cameraPermissionRequests: Observable<Unit>

        fun cameraPermissionGranted()
    }

    /**
     * Listener interface implemented by a parent RIB's interactor's inner class.
     */
    interface Listener {
        fun back()
    }
}
