package dangor.photobooth.root.home.photo

import com.uber.rib.core.RibTestBasePlaceholder
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PhotoInteractorTest : RibTestBasePlaceholder() {

    @Mock internal lateinit var presenter: PhotoInteractor.PhotoPresenter
    @Mock internal lateinit var listener: PhotoInteractor.Listener
    @Mock internal lateinit var router: PhotoRouter

    private lateinit var interactor: PhotoInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        interactor = TestPhotoInteractor.create(presenter, listener)
    }

    /**
     * TODO: Delete this example and add real tests.
     */
    @Test
    fun `an example test`() {
        // Use InteractorHelper to drive your interactor's lifecycle.
        InteractorHelper.attach<PhotoInteractor.PhotoPresenter, PhotoRouter>(interactor, presenter, router, null)
        InteractorHelper.detach(interactor)

        throw RuntimeException("Remove this test and add real tests.")
    }
}