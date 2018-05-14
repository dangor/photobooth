package dangor.photobooth.root.home.photo

import com.uber.autodispose.kotlin.autoDisposable
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dangor.photobooth.services.PermissionService
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import javax.inject.Inject

/**
 * Coordinates Business Logic for [PhotoScope].
 */
@RibInteractor
class PhotoInteractor : Interactor<PhotoInteractor.PhotoPresenter, PhotoRouter>() {

    @Inject lateinit var presenter: PhotoPresenter
    @Inject lateinit var listener: Listener
    @Inject lateinit var permissionService: PermissionService

    var savedFiles = emptyList<File>()

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter.cameraPermissionRequests
                .switchMap { permissionService.request(Permission.CAMERA) }
                .autoDisposable(this)
                .subscribe { presenter.cameraPermissionGranted() }

        presenter.startClicks
                .subscribe {
                    presenter.setStartButtonVisible(false)
                    presenter.startTimer(TIMER_LENGTH)
                }

        presenter.timerDone
                .subscribe {
                    presenter.takePhoto()
                }

        presenter.fileSaved
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    savedFiles += it
                    presenter.addPhotoPreview(it)

                    if (savedFiles.size >= PHOTO_MAX) {
                        listener.photosTaken(savedFiles)
                    } else {
                        presenter.startTimer(TIMER_LENGTH)
                    }
                }
    }

    override fun handleBackPress(): Boolean {
        listener.back()
        return true
    }

    /**
     * Presenter interface implemented by this RIB's view.
     */
    interface PhotoPresenter {
        val cameraPermissionRequests: Observable<Unit>
        val startClicks: Observable<Unit>
        val timerDone: Observable<Unit>
        val fileSaved: Observable<File>

        fun cameraPermissionGranted()
        fun setStartButtonVisible(visible: Boolean)
        fun startTimer(seconds: Int)
        fun hideTimer()
        fun takePhoto()
        fun addPhotoPreview(file: File)
    }

    /**
     * Listener interface implemented by a parent RIB's interactor's inner class.
     */
    interface Listener {
        fun back()
        fun photosTaken(pictures: List<File>)
    }

    companion object {
        private const val TIMER_LENGTH = 3
        private const val PHOTO_MAX = 4
    }
}
