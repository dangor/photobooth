package dangor.photobooth.root

import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dangor.photobooth.R
import dangor.photobooth.root.home.HomeBuilder
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Builder for the {@link RootScope}.
 */
class RootBuilder(dependency: ParentComponent) : ViewBuilder<RootView, RootRouter, RootBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [RootRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [RootRouter].
     */
    fun build(parentViewGroup: ViewGroup): RootRouter {
        val view = createView(parentViewGroup)
        val interactor = RootInteractor()
        val component = DaggerRootBuilder_Component.builder()
                .parentComponent(dependency)
                .view(view)
                .interactor(interactor)
                .build()
        return component.rootRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): RootView? {
        return RootView(parentViewGroup.context)
    }

    interface ParentComponent

    @dagger.Module
    abstract class Module {

        @RootScope @Binds
        internal abstract fun presenter(view: RootView): RootInteractor.RootPresenter

        @dagger.Module
        companion object {

            @RootScope @Provides @JvmStatic
            internal fun router(
                    component: Component,
                    view: RootView,
                    interactor: RootInteractor): RootRouter {
                return RootRouter(view, interactor, component,
                        HomeBuilder(component))
            }

            @RootScope @Provides @JvmStatic @Named("loggerTag")
            internal fun loggerTag(): String = RootInteractor::class.java.simpleName
        }
    }

    @RootScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<RootInteractor>, BuilderComponent,
            HomeBuilder.ParentComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: RootInteractor): Builder

            @BindsInstance
            fun view(view: RootView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun rootRouter(): RootRouter
    }

    @Scope
    @Retention(BINARY)
    internal annotation class RootScope

    @Qualifier
    @Retention(BINARY)
    internal annotation class RootInternal
}
