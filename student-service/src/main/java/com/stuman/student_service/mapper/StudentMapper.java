package com.stuman.student_service.mapper;

import com.stuman.student_service.dto.request.StudentCreateRequest;
import com.stuman.student_service.dto.request.StudentSelfUpdateRequest;
import com.stuman.student_service.dto.request.StudentUpdateRequest;
import com.stuman.student_service.dto.response.StudentPageResponse;
import com.stuman.student_service.dto.response.StudentResponse;
import com.stuman.student_service.dto.response.StudentSummaryResponse;
import com.stuman.student_service.entity.Student;
import com.stuman.student_service.entity.StudentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentMapper {

    // ===================== CREATE =====================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Student toEntity(StudentCreateRequest request);

    // ===================== ADMIN UPDATE =====================
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Student student, StudentUpdateRequest request);

    // ===================== STUDENT SELF UPDATE =====================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateSelf(@MappingTarget Student student, StudentSelfUpdateRequest request);

    // ===================== RESPONSE =====================
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "status", source = "status")
    StudentResponse toResponse(Student student);

    // ===================== SUMMARY =====================
    @Mapping(target = "status", source = "status")
    StudentSummaryResponse toSummary(Student student);

    // ===================== PAGE =====================
    default StudentPageResponse toPageResponse(Page<Student> page) {
        return StudentPageResponse.builder()
                .students(
                        page.getContent()
                                .stream()
                                .map(this::toSummary)
                                .toList()
                )
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
