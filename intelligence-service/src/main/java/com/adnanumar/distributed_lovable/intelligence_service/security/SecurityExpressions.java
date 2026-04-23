package com.adnanumar.distributed_lovable.intelligence_service.security;

import com.adnanumar.distributed_lovable.common_lib.enums.ProjectPermission;
import com.adnanumar.distributed_lovable.common_lib.security.AuthUtil;
import com.adnanumar.distributed_lovable.intelligence_service.client.WorkspaceClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component("security")  // defining bean name
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityExpressions {

    final AuthUtil authUtil;
    final WorkspaceClient workspaceClient;

    private boolean hasPermission(Long projectId, ProjectPermission permission) {
        return workspaceClient.checkProjectPermission(projectId, permission);
    }

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
    }

}
