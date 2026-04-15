package com.educore.grade.mapper;

import com.educore.grade.dto.request.CreateGradeRequest;
import com.educore.grade.dto.response.GradeResponse;
import com.educore.grade.entity.Grade;
import com.educore.grade.entity.Subject;
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
