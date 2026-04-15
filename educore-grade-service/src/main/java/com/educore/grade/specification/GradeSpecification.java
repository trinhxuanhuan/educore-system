package com.educore.grade.specification;

import com.educore.grade.entity.Grade;
import com.educore.grade.entity.Semester;
import org.springframework.data.jpa.domain.Specification;

public class GradeSpecification {

    public static Specification<Grade> filter(
            Long studentId,
            Semester semester,
            Integer year,
            Long subjectId
    ) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (studentId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("studentId"), studentId));
            }

            if (semester != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("semester"), semester));
            }

            if (year != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("academicYear"), year));
            }

            if (subjectId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("subjectId"), subjectId));
            }

            return predicate;
        };
    }
}
