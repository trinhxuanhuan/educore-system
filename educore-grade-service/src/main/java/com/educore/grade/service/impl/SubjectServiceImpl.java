package com.educore.grade.service.impl;

import com.educore.grade.dto.request.CreateSubjectRequest;
import com.educore.grade.dto.response.SubjectResponse;
import com.educore.grade.entity.Subject;
import com.educore.grade.exception.BaseException;
import com.educore.grade.exception.ErrorCode;
import com.educore.grade.mapper.SubjectMapper;
import com.educore.grade.repository.SubjectRepository;
import com.educore.grade.service.SubjectService;
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