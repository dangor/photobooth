package dangor.photobooth.root

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RootInteractorTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var presenter: RootInteractor.RootPresenter
    @Mock internal lateinit var listener: RootInteractor.Listener
    @Mock internal lateinit var router: RootRouter

    private lateinit var interactor: RootInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        interactor = TestRootInteractor.create(presenter, listener)
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    fun `an example test`() {
        // Use InteractorHelper to drive your interactor's lifecycle.
        InteractorHelper.attach<RootInteractor.RootPresenter, RootRouter>(interactor, presenter, router, null)
        InteractorHelper.detach(interactor)

        throw RuntimeException("Remove this test and add real tests.")
    }
}