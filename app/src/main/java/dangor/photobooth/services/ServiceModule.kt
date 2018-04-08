package dangor.photobooth.services

import dagger.Module
import dagger.Provides
import dangor.photobooth.MainActivity

@Module
class ServiceModule {
    @Provides fun mainActivity(): MainActivity {
        return MainActivity.instance
    }

    @Provides fun permissionService(activity: MainActivity): PermissionService {
        return PermissionService(activity)
    }
}