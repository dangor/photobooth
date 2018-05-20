package dangor.photobooth.services

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import dangor.photobooth.MainActivity
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PermissionService(private val activity: MainActivity) {

    init {
        activity.permissionHandler = Handler()
        activity.resultHandler = ResultHandler()
    }

    private val permissionsRequests: MutableMap<Int, PublishSubject<Unit>> = mutableMapOf()
    private val activityResultsRequests: MutableMap<Int, PublishSubject<Intent>> = mutableMapOf()

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

    fun startActivityForResult(intent: Intent, requestCode: Int): Observable<Intent> {
        val subject = PublishSubject.create<Intent>()
        activityResultsRequests[requestCode] = subject
        activity.startActivityForResult(intent, requestCode)
        return subject.hide()
    }

    inner class ResultHandler : IntentResultHandler {
        override fun intentResult(requestCode: Int, result: Int, data: Intent) {
            if (result == RESULT_OK) {
                activityResultsRequests[requestCode]?.onNext(data)
            } else {
                Log.e(TAG, "Well, activity result isn't good.")
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

interface IntentResultHandler {
    fun intentResult(requestCode: Int, result: Int, data: Intent)
}