package dangor.photobooth

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.RootBuilder
import dangor.photobooth.services.PermissionHandler

class MainActivity : RibActivity() {

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *, *> {
        instance = this
        val builder = RootBuilder(object : RootBuilder.ParentComponent {})
        return builder.build(parentViewGroup)
    }

    var permissionHandler: PermissionHandler? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHandler?.permissionResult(requestCode, resultCode)
    }

    companion object {
        lateinit var instance: MainActivity
            private set
    }
}
