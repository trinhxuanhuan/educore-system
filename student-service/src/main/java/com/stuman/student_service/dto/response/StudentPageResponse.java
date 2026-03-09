package com.stuman.student_service.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPageResponse {

    private List<StudentSummaryResponse> students;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}

