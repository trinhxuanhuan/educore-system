package com.stuman.grade_service.service.impl;

import com.stuman.grade_service.dto.request.CreateSubjectRequest;
import com.stuman.grade_service.dto.response.SubjectResponse;
import com.stuman.grade_service.entity.Subject;
import com.stuman.grade_service.exception.BaseException;
import com.stuman.grade_service.exception.ErrorCode;
import com.stuman.grade_service.mapper.SubjectMapper;
import com.stuman.grade_service.repository.SubjectRepository;
import com.stuman.grade_service.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    @Override
    public SubjectResponse createSubject(CreateSubjectRequest request) {

        if (subjectRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                    ErrorCode.SUBJECT_CODE_ALREADY_EXISTS,
                    ErrorCode.SUBJECT_CODE_ALREADY_EXISTS.getMessage()
            );
        }

        Subject subject = subjectMapper.toEntity(request);

        Subject saved = subjectRepository.save(subject);

        return subjectMapper.toResponse(saved);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {

        return subjectRepository.findAll()
                .stream()
                .map(subjectMapper::toResponse)
                .toList();
    }
}