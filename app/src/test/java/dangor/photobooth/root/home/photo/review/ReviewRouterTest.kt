package dangor.photobooth.root.home.photo.review

import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ReviewRouterTest {

    @Mock private lateinit var component: ReviewBuilder.Component
    @Mock private lateinit var interactor: ReviewInteractor
    @Mock private lateinit var view: ReviewView

    private lateinit var router: ReviewRouter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        router = ReviewRouter(view, interactor, component)
        RouterHelper.attach(router)
    }

    @Test
    fun `an example test`() {
        TODO("not implemented")
    }
}

