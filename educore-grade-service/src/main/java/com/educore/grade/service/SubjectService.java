package com.educore.grade.service;

import com.educore.grade.dto.request.CreateSubjectRequest;
import com.educore.grade.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(CreateSubjectRequest request);

    List<SubjectResponse> getAllSubjects();
}
