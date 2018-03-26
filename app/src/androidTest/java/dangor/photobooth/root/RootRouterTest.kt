package dangor.photobooth.root

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.RouterHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RootRouterTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var component: RootBuilder.Component
    @Mock internal lateinit var interactor: RootInteractor
    @Mock internal lateinit var view: RootView

    private lateinit var router: RootRouter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        router = RootRouter(view, interactor, component)
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

