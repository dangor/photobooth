package dangor.photobooth.root.home.photo.review

import com.uber.rib.core.ViewRouter

/**
 * Adds and removes children of Review.
 *
 * Builder: [ReviewBuilder]
 * Interactor: [ReviewInteractor]
 * Presenter: [ReviewView]
 */
class ReviewRouter(
        view: ReviewView,
        interactor: ReviewInteractor,
        component: ReviewBuilder.Component
) : ViewRouter<ReviewView, ReviewInteractor, ReviewBuilder.Component>(view, interactor, component)
