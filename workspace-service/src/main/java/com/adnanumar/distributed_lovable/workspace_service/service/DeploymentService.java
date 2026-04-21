package com.adnanumar.distributed_lovable.workspace_service.service;

import com.adnanumar.distributed_lovable.workspace_service.dto.project.DeployResponse;
import org.jspecify.annotations.Nullable;

public interface DeploymentService {

    @Nullable DeployResponse deploy(Long projectId);

}
