-- Initial schema for analytics-service.
-- Generated from Hibernate-managed entities so that subsequent changes can be
-- evolved through ordered Flyway migrations instead of relying on
-- spring.jpa.hibernate.ddl-auto=update.
--
-- Note: this service stores read-model projections that are populated from
-- Kafka events emitted by student-service and grade-service. There are no
-- foreign keys back to those services - the database-per-service pattern
-- makes those tables logically owned, not relationally enforced, references.

CREATE TABLE student_analytics (
    student_id     BIGINT NOT NULL,
    student_code   VARCHAR(255),
    full_name      VARCHAR(255),
    email          VARCHAR(255),
    class_name     VARCHAR(255),
    gpa            FLOAT(53),
    average_score  FLOAT(53),
    total_subjects INTEGER,
    total_credits  INTEGER,
    classification ENUM ('AVERAGE', 'EXCELLENT', 'GOOD', 'POOR', 'UNKNOWN', 'VERY_GOOD'),
    PRIMARY KEY (student_id),
    INDEX idx_student_analytics_class_name (class_name),
    INDEX idx_student_analytics_classification (classification)
) ENGINE = InnoDB;

CREATE TABLE student_course (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    student_id  BIGINT,
    subject_id  BIGINT,
    final_score FLOAT(53),
    credit      INTEGER,
    PRIMARY KEY (id),
    INDEX idx_student_course_student (student_id),
    INDEX idx_student_course_subject (subject_id)
) ENGINE = InnoDB;

CREATE TABLE grade_cache (
    grade_id   BIGINT NOT NULL,
    student_id BIGINT,
    subject_id BIGINT,
    score      FLOAT(53),
    weight     FLOAT(53),
    PRIMARY KEY (grade_id),
    INDEX idx_grade_cache_student (student_id),
    INDEX idx_grade_cache_subject (subject_id)
) ENGINE = InnoDB;
