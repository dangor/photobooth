package dangor.photobooth.services

import android.content.pm.PackageManager
import dangor.photobooth.MainActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PermissionService(private val activity: MainActivity) {

    init {
        activity.permissionHandler = Handler()
    }

    private val permissionsRequests: MutableMap<Int, PublishSubject<Unit>> = mutableMapOf()

    fun request(permissions: Array<String>, requestCode: Int): Observable<Unit> {
        val subject = PublishSubject.create<Unit>()
        permissionsRequests[requestCode] = subject
        activity.requestPermissions(permissions, requestCode)
        return subject.hide()
    }

    inner class Handler : PermissionHandler {
        override fun permissionResult(requestCode: Int, resultCode: Int) {
            if (resultCode == PackageManager.PERMISSION_GRANTED) {
                permissionsRequests[requestCode]?.onNext(Unit)
            }
        }
    }
}

interface PermissionHandler {
    fun permissionResult(requestCode: Int, resultCode: Int)
}