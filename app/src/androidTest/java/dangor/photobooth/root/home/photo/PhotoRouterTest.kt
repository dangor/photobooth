package dangor.photobooth.root.home.photo

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PhotoRouterTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var component: PhotoBuilder.Component
    @Mock internal lateinit var interactor: PhotoInteractor
    @Mock internal lateinit var view: PhotoView

    private lateinit var router: PhotoRouter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        router = PhotoRouter(view, interactor, component)
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    fun `an example test`() {
        // Use RouterHelper to drive your router's lifecycle.
        RouterHelper.attach(router)
        RouterHelper.detach(router)

        throw RuntimeException("Remove this test and add real tests.")
    }

}

