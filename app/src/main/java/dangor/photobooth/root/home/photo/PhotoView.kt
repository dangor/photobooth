package dangor.photobooth.root.home.photo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.photo_view.view.camera_preview

/**
 * Top level view for {@link PhotoBuilder.PhotoScope}.
 */
class PhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), PhotoInteractor.PhotoPresenter {

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

            override fun onDisconnected(camera: CameraDevice?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(camera: CameraDevice?, error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, null)
    }
}