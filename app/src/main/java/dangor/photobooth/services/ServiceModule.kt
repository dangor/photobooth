package dangor.photobooth.services

import dagger.Module
import dagger.Provides
import dangor.photobooth.MainActivity

@Module
class ServiceModule {
    @Module
    companion object {
        @Provides @JvmStatic internal fun permissionService(activity: MainActivity): PermissionService {
            return PermissionService(activity)
        }
    }
}