package dangor.photobooth

import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import dangor.photobooth.root.RootBuilder

class MainActivity : RibActivity() {

    override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *, *> {
        val builder = RootBuilder(object : RootBuilder.ParentComponent {})
        return builder.build(parentViewGroup)
    }
}
