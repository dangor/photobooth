package dangor.photobooth.root.home.photo

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import dangor.photobooth.extensions.clicks
import dangor.photobooth.extensions.isVisible
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.photo_view.view.camera_preview
import kotlinx.android.synthetic.main.photo_view.view.start_button
import kotlinx.android.synthetic.main.photo_view.view.timer_progress
import kotlinx.android.synthetic.main.photo_view.view.timer_text

/**
 * Top level view for {@link PhotoBuilder.PhotoScope}.
 */
class PhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), PhotoInteractor.PhotoPresenter {

    override val startClicks: Observable<Unit> get() = start_button.clicks

    private val timerDoneSubject = PublishSubject.create<Unit>()
    override val timerDone: Observable<Unit> get() = timerDoneSubject.hide()

    override fun setStartButtonVisible(visible: Boolean) {
        start_button.isVisible = visible
    }

    override fun startTimer(seconds: Int) {
        CountDown(seconds) { secondsLeft ->
            timer_text.text = secondsLeft.toString()
        }.start()

        val animation = ObjectAnimator.ofInt(timer_progress, "progress", 0, 500)
        animation.duration = seconds * 1000L
        animation.interpolator = LinearInterpolator()
        animation.start()
    }

    private inner class CountDown(
            seconds: Int,
            private val onTick: (Int) -> Unit
    ) : CountDownTimer(seconds * 1000L, 1000L) {

        override fun onFinish() {
            timerDoneSubject.onNext(Unit)
        }

        override fun onTick(millisUntilFinished: Long) {
            this.onTick.invoke(Math.ceil(millisUntilFinished / 1000.0).toInt())
        }
    }

    private val cameraPermissionRequestSubject = PublishSubject.create<Unit>()
    override val cameraPermissionRequests: Observable<Unit> get() = cameraPermissionRequestSubject.hide()

    override fun cameraPermissionGranted() {
        openCamera()
    }

    lateinit var previewSurface: Surface

    override fun onFinishInflate() {
        super.onFinishInflate()

        camera_preview.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) = Unit
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                previewSurface = Surface(surface)
                openCamera()
            }
        }
    }

    private fun openCamera() {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionRequestSubject.onNext(Unit)
            return
        }

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]

        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice?) {
                camera?.createCaptureSession(listOf(previewSurface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(session: CameraCaptureSession?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onConfigured(session: CameraCaptureSession) {
                        val request = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        request.addTarget(previewSurface)

                        session.setRepeatingRequest(request.build(), null, null)
                    }
                }, null)
            }

            override fun onDisconnected(camera: CameraDevice?) = Unit

            override fun onError(camera: CameraDevice?, error: Int) {
                Toast.makeText(context, "Camera Device error $error", Toast.LENGTH_SHORT).show()
            }

        }, null)
    }
}