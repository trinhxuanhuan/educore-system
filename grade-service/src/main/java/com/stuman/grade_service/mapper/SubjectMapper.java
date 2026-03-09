package com.stuman.grade_service.mapper;

import com.stuman.grade_service.dto.request.CreateSubjectRequest;
import com.stuman.grade_service.dto.response.SubjectResponse;
import com.stuman.grade_service.entity.Subject;
import com.stuman.grade_service.entity.SubjectStatus;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SubjectMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Subject toEntity(CreateSubjectRequest request);

    // RESPONSE
    @Mapping(
            target = "status",
            expression = "java(subject.getStatus() != null ? subject.getStatus().name() : null)"
    )
    SubjectResponse toResponse(Subject subject);
}
