package dangor.photobooth.root.home

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HomeInteractorTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var presenter: HomeInteractor.HomePresenter
    @Mock internal lateinit var listener: HomeInteractor.Listener
    @Mock internal lateinit var router: HomeRouter

    private lateinit var interactor: HomeInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        interactor = TestHomeInteractor.create(presenter, listener)
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    fun `an example test`() {
        // Use InteractorHelper to drive your interactor's lifecycle.
        InteractorHelper.attach<HomeInteractor.HomePresenter, HomeRouter>(interactor, presenter, router, null)
        InteractorHelper.detach(interactor)

        throw RuntimeException("Remove this test and add real tests.")
    }
}