package dangor.photobooth.root.home

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Top level view for {@link HomeBuilder.HomeScope}.
 */
class HomeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), HomeInteractor.HomePresenter
