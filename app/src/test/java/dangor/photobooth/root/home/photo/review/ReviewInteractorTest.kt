package dangor.photobooth.root.home.photo.review

import com.nhaarman.mockito_kotlin.*
import com.uber.rib.core.Bundle
import com.uber.rib.core.InteractorHelper

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ReviewInteractorTest {

    @Mock private lateinit var presenter: ReviewInteractor.ReviewPresenter
    @Mock private lateinit var listener: ReviewInteractor.Listener
    @Mock private lateinit var router: ReviewRouter

    private lateinit var interactor: ReviewInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        interactor = TestReviewInteractor.create(presenter, listener)
    }

    @Test
    fun `an example test`() {
        attach()
        TODO("not implemented")
    }

    private fun attach(savedInstanceState: Bundle? = null) {
        InteractorHelper.attach(interactor, presenter, router, savedInstanceState)
    }
}