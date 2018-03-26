package dangor.photobooth

import android.content.Intent
import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.RootBuilder
import dangor.photobooth.services.PermissionHandler

class MainActivity : RibActivity() {

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *, *> {
        val builder = RootBuilder(object : RootBuilder.ParentComponent {})
        return builder.build(parentViewGroup, this)
    }

    var permissionHandler: PermissionHandler? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHandler?.permissionResult(requestCode, resultCode)
    }
}
