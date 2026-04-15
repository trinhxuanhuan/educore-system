package com.educore.student.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPageResponse {

    private List<StudentSummaryResponse> students;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}

