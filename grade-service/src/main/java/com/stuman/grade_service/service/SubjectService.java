package com.stuman.grade_service.service;

import com.stuman.grade_service.dto.request.CreateSubjectRequest;
import com.stuman.grade_service.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(CreateSubjectRequest request);

    List<SubjectResponse> getAllSubjects();
}
