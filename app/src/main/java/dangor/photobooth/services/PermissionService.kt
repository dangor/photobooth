package dangor.photobooth.services

import android.content.pm.PackageManager
import android.util.Log
import dangor.photobooth.MainActivity
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PermissionService(private val activity: MainActivity) {

    init {
        activity.permissionHandler = Handler()
    }

    private val permissionsRequests: MutableMap<Int, PublishSubject<Unit>> = mutableMapOf()

    fun request(permission: Permission): Observable<Unit> {
        val subject = PublishSubject.create<Unit>()
        permissionsRequests[permission.requestCode] = subject
        activity.requestPermissions(permission.permissions, permission.requestCode)
        return subject.hide()
    }

    inner class Handler : PermissionHandler {
        override fun permissionResult(requestCode: Int, grantResults: List<Int>) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                permissionsRequests[requestCode]?.onNext(Unit)
            } else {
                Log.e(TAG, "Well, they denied us.")
            }
        }
    }

    companion object {
        private const val TAG = "PermissionService"
    }
}

interface PermissionHandler {
    fun permissionResult(requestCode: Int, grantResults: List<Int>)
}