-- Initial schema for grade-service.
-- Generated from Hibernate-managed entities so that subsequent changes can be
-- evolved through ordered Flyway migrations instead of relying on
-- spring.jpa.hibernate.ddl-auto=update.

CREATE TABLE subjects (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    code   VARCHAR(255) NOT NULL,
    name   VARCHAR(255) NOT NULL,
    credit INTEGER,
    status ENUM ('ACTIVE', 'INACTIVE'),
    PRIMARY KEY (id),
    CONSTRAINT uk_subjects_code UNIQUE (code)
) ENGINE = InnoDB;

CREATE TABLE grades (
    id            BIGINT     NOT NULL AUTO_INCREMENT,
    student_id    BIGINT     NOT NULL,
    subject_id    BIGINT     NOT NULL,
    score         FLOAT(53)  NOT NULL,
    weight        FLOAT(53)  NOT NULL,
    academic_year INTEGER    NOT NULL,
    semester      ENUM ('FALL', 'SPRING', 'SUMMER'),
    type          ENUM ('FINAL', 'MIDTERM', 'QUIZ'),
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    PRIMARY KEY (id),
    INDEX idx_grades_student (student_id),
    INDEX idx_grades_subject (subject_id),
    INDEX idx_grades_year_semester (academic_year, semester)
) ENGINE = InnoDB;

CREATE TABLE teaching_assignment (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_teaching_assignment_teacher_subject UNIQUE (teacher_id, subject_id),
    INDEX idx_teaching_assignment_teacher (teacher_id),
    INDEX idx_teaching_assignment_subject (subject_id)
) ENGINE = InnoDB;
