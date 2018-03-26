package dangor.photobooth.root.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dangor.photobooth.R
import dangor.photobooth.root.home.photo.PhotoBuilder
import dangor.photobooth.root.home.photo.PhotoInteractor
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Builder for the {@link HomeScope}.
 */
class HomeBuilder(dependency: ParentComponent) : ViewBuilder<HomeView, HomeRouter, HomeBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [HomeRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [HomeRouter].
     */
    fun build(parentViewGroup: ViewGroup): HomeRouter {
        val view = createView(parentViewGroup)
        val interactor = HomeInteractor()
        val component = DaggerHomeBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.homeRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): HomeView? {
        return inflater.inflate(R.layout.home_view, parentViewGroup, false) as HomeView
    }

    interface ParentComponent

    @dagger.Module
    abstract class Module {

        @HomeScope @Binds
        internal abstract fun presenter(view: HomeView): HomeInteractor.HomePresenter

        @dagger.Module
        companion object {

            @HomeScope @Provides @JvmStatic
            internal fun router(
                    component: Component,
                    view: HomeView,
                    interactor: HomeInteractor): HomeRouter {
                return HomeRouter(view, interactor, component,
                        PhotoBuilder(component))
            }

            @HomeScope @Provides @JvmStatic @Named("loggerTag")
            internal fun loggerTag(): String = HomeInteractor::class.java.simpleName

            @HomeScope @Provides @JvmStatic
            internal fun photoListener(interactor: HomeInteractor): PhotoInteractor.Listener {
                return interactor.PhotoListener()
            }
        }
    }

    @HomeScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<HomeInteractor>, BuilderComponent,
            PhotoBuilder.ParentComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: HomeInteractor): Builder

            @BindsInstance
            fun view(view: HomeView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun homeRouter(): HomeRouter
    }

    @Scope
    @Retention(BINARY)
    internal annotation class HomeScope

    @Qualifier
    @Retention(BINARY)
    internal annotation class HomeInternal
}
