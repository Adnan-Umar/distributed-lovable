package com.adnanumar.distributed_lovable.workspace_service.controller;

import com.adnanumar.distributed_lovable.common_lib.dto.FileTreeDto;
import com.adnanumar.distributed_lovable.workspace_service.service.ProjectFileService;
import com.adnanumar.distributed_lovable.workspace_service.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/internal/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InternalWorkspaceController {

    final ProjectService projectService;
    final ProjectFileService projectFileService;

    @GetMapping("/projects/{projectId}/files/tree")
    public FileTreeDto getFileTree(@PathVariable("projectId") Long projectId) {
        return projectFileService.getFileTree(projectId);
    }

    @GetMapping("/projects/{projectId}/files/content")
    public String getFileContent(@PathVariable("projectId") Long projectId, @RequestParam("path") String path) {
        return projectFileService.getFileContent(projectId, path);
    }

}
