package dangor.photobooth.root.home

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HomeRouterTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var component: HomeBuilder.Component
    @Mock internal lateinit var interactor: HomeInteractor
    @Mock internal lateinit var view: HomeView

    private lateinit var router: HomeRouter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        router = HomeRouter(view, interactor, component)
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

