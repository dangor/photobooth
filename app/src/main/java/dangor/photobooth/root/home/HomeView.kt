package dangor.photobooth.root.home

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import dangor.photobooth.extensions.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.home_view.view.take_photos
import kotlinx.android.synthetic.main.home_view.view.take_video

/**
 * Top level view for {@link HomeBuilder.HomeScope}.
 */
class HomeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), HomeInteractor.HomePresenter {

    override val photoClicks: Observable<Unit> get() = take_photos.clicks
    override val videoClicks: Observable<Unit> get() = take_video.clicks
}
