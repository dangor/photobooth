package dangor.photobooth.root.home.photo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.uber.autodispose.kotlin.autoDisposable
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibInteractor
import dangor.photobooth.extensions.Bitmaps
import dangor.photobooth.extensions.withLatestFrom
import dangor.photobooth.services.PermissionService
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
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
    @Inject lateinit var activityContext: Context

    override fun didBecomeActive(savedInstanceState: Bundle?) {
        super.didBecomeActive(savedInstanceState)

        presenter.cameraPermissionRequests
                .switchMap { permissionService.request(Permission.CAMERA) }
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(this)
                .subscribe { presenter.cameraPermissionGranted() }

        presenter.startClicks
                .subscribe {
                    presenter.setStartButtonVisible(false)
                    presenter.startTimer(TIMER_LENGTH)
                }

        presenter.timerDone
                .subscribe {
                    presenter.setOverlayVisible(true)
                    presenter.takePhoto()
                }

        val savedFiles: BehaviorSubject<List<File>> = BehaviorSubject.createDefault(emptyList())

        presenter.fileSaved
                .withLatestFrom(savedFiles, { file, saved -> saved + file })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { presenter.setOverlayVisible(false) }
                .autoDisposable(this)
                .subscribe(savedFiles::onNext)

        savedFiles
                .filter { it.size >= PHOTO_MAX }
                .subscribe {
                    listener.photosTaken(it)
                }

        savedFiles
                .filter { it.size in 1 until PHOTO_MAX }
                .map { Unit }
                .subscribe {
                    presenter.startTimer(TIMER_LENGTH)
                }

        savedFiles
                .filter { it.size in 1 until PHOTO_MAX }
                .map { it.last() }
                .observeOn(Schedulers.io())
                .map { Bitmaps.getScaledBitmap(activityContext, Uri.fromFile(it)) }
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(this)
                .subscribe(
                        { presenter.addPhotoPreview(it) },
                        { Log.e(TAG, "Could not add preview", it) }
                )
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
        fun addPhotoPreview(bitmap: Bitmap)
        fun setOverlayVisible(visible: Boolean)
    }

    /**
     * Listener interface implemented by a parent RIB's interactor's inner class.
     */
    interface Listener {
        fun back()
        fun photosTaken(pictures: List<File>)
    }

    companion object {
        private const val TAG = "PhotoInteractor"
        private const val TIMER_LENGTH = 3
        private const val PHOTO_MAX = 4
    }
}
