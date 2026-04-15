package com.educore.grade.mapper;

import com.educore.grade.dto.request.CreateSubjectRequest;
import com.educore.grade.dto.response.SubjectResponse;
import com.educore.grade.entity.Subject;
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
