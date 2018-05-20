package dangor.photobooth.root.home.photo.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dangor.photobooth.R
import dangor.photobooth.services.ServiceModule
import java.io.File
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Review the photos you've just taken
 *
 * Interactor: [ReviewInteractor]
 * Presenter: [ReviewView]
 * Router: [ReviewRouter]
 */
class ReviewBuilder(dependency: ParentComponent) : ViewBuilder<ReviewView, ReviewRouter, ReviewBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [ReviewRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [ReviewRouter].
     */
    fun build(parentViewGroup: ViewGroup, pictures: List<File>): ReviewRouter {
        val view = createView(parentViewGroup)
        val interactor = ReviewInteractor()
        val component = DaggerReviewBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .pictures(pictures)
                .build()
        return component.reviewRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): ReviewView? {
        return inflater.inflate(R.layout.review_view, parentViewGroup, false) as ReviewView
    }

    interface ParentComponent {
        val reviewListener: ReviewInteractor.Listener
    }

    @dagger.Module
    abstract class Module {

        @ReviewScope @Binds
        internal abstract fun presenter(view: ReviewView): ReviewInteractor.ReviewPresenter

        @dagger.Module
        companion object {

            @ReviewScope @Provides @JvmStatic
            internal fun router(
                    component: Component,
                    view: ReviewView,
                    interactor: ReviewInteractor): ReviewRouter {
                return ReviewRouter(view, interactor, component)
            }

            @ReviewScope @Provides @JvmStatic @Named("loggerTag")
            internal fun loggerTag(): String = ReviewInteractor::class.java.simpleName

            @ReviewScope @Provides @JvmStatic
            internal fun appContext(view: ReviewView): Context {
                return view.context.applicationContext
            }
        }
    }

    @ReviewScope
    @dagger.Component(modules = arrayOf(Module::class, ServiceModule::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<ReviewInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: ReviewInteractor): Builder

            @BindsInstance
            fun view(view: ReviewView): Builder

            @BindsInstance
            fun pictures(pictures: List<File>): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun reviewRouter(): ReviewRouter
    }

    @Scope
    @Retention(BINARY)
    internal annotation class ReviewScope

    @Qualifier
    @Retention(BINARY)
    internal annotation class ReviewInternal
}
