package stroom.auth.clients;

import stroom.auth.config.AppPermissionServiceConfig;
import stroom.auth.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Singleton
public class AppPermissionsService {
    private final AppPermissionServiceConfig config;
    private AppPermissionsClient appPermissionsClient;

    @Inject
    public AppPermissionsService( @NotNull AppPermissionsClient appPermissionsClient, @NotNull Config config ) {
        super();
        this.appPermissionsClient = appPermissionsClient;
        this.config = config.getAppPermissionServiceConfig();
    }

    public boolean isAuthorisedToManageUsers(String userName, String idToken){
        Set<String> permissions = appPermissionsClient.getPermissionNamesForUserName(userName, idToken);
        Optional<String> optionalPermission = permissions.stream().filter(permission ->
                permission.equals(config.getCanManageUsersPermission())
                || permission.equals("Administrator"))
                .findFirst();
        return optionalPermission.isPresent();
    }
}
