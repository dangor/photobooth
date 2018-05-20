package dangor.photobooth

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ViewGroup
import android.view.WindowManager
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.RootBuilder
import dangor.photobooth.services.IntentResultHandler
import dangor.photobooth.services.PermissionHandler

class MainActivity : RibActivity() {

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *, *> {
        instance = this
        val builder = RootBuilder(object : RootBuilder.ParentComponent {})
        return builder.build(parentViewGroup)
    }

    var permissionHandler: PermissionHandler? = null
    var resultHandler: IntentResultHandler? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler?.permissionResult(requestCode, grantResults.toList())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultHandler?.intentResult(requestCode, resultCode, data!!)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        lateinit var instance: MainActivity
            private set
    }
}
