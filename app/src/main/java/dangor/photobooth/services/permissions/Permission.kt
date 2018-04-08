package dangor.photobooth.services.permissions

import android.Manifest

enum class Permission(val permissions: Array<String>, val requestCode: Int) {
    CAMERA(arrayOf(Manifest.permission.CAMERA), 1)
}