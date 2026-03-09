package com.stuman.grade_service.mapper;

import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Grade;
import com.stuman.grade_service.entity.Subject;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GradeMapper {

    // CREATE ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Grade toEntity(CreateGradeRequest request);

    // RESPONSE (MULTI SOURCE)
    @Mapping(target = "id", source = "grade.id")
    @Mapping(target = "studentId", source = "grade.studentId")
    @Mapping(target = "subjectId", source = "grade.subjectId")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "type", source = "grade.type")
    @Mapping(target = "score", source = "grade.score")
    @Mapping(target = "weight", source = "grade.weight")
    @Mapping(target = "semester", source = "grade.semester")
    @Mapping(target = "academicYear", source = "grade.academicYear")
    GradeResponse toResponse(Grade grade, Subject subject);
}
