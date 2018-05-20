package dangor.photobooth.root.home.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dangor.photobooth.MainActivity
import dangor.photobooth.R
import dangor.photobooth.services.ServiceModule
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Builder for the {@link PhotoScope}.
 */
class PhotoBuilder(dependency: ParentComponent) : ViewBuilder<PhotoView, PhotoRouter, PhotoBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [PhotoRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [PhotoRouter].
     */
    fun build(parentViewGroup: ViewGroup): PhotoRouter {
        val view = createView(parentViewGroup)
        val interactor = PhotoInteractor()
        val component = DaggerPhotoBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.photoRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): PhotoView? {
        return inflater.inflate(R.layout.photo_view, parentViewGroup, false) as PhotoView
    }

    interface ParentComponent {
        val photoListener: PhotoInteractor.Listener
    }

    @dagger.Module
    abstract class Module {

        @PhotoScope @Binds
        internal abstract fun presenter(view: PhotoView): PhotoInteractor.PhotoPresenter

        @dagger.Module
        companion object {

            @PhotoScope @Provides @JvmStatic
            internal fun router(
                    component: Component,
                    view: PhotoView,
                    interactor: PhotoInteractor): PhotoRouter {
                return PhotoRouter(view, interactor, component)
            }

            @PhotoScope @Provides @JvmStatic @Named("loggerTag")
            internal fun loggerTag(): String = PhotoInteractor::class.java.simpleName

            @PhotoScope @Provides @JvmStatic
            internal fun activityContext(): Context {
                return MainActivity.instance
            }
        }
    }

    @PhotoScope
    @dagger.Component(modules = arrayOf(Module::class, ServiceModule::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<PhotoInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: PhotoInteractor): Builder

            @BindsInstance
            fun view(view: PhotoView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun photoRouter(): PhotoRouter
    }

    @Scope
    @Retention(BINARY)
    internal annotation class PhotoScope

    @Qualifier
    @Retention(BINARY)
    internal annotation class PhotoInternal
}
