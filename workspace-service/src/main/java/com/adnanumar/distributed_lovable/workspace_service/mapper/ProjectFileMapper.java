package com.adnanumar.distributed_lovable.workspace_service.mapper;

import com.adnanumar.distributed_lovable.workspace_service.dto.project.FileNode;
import com.adnanumar.distributed_lovable.workspace_service.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNodes(List<ProjectFile> projectFileList);

}
