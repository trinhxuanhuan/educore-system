-- Initial schema for student-service.
-- Generated from Hibernate-managed entities so that subsequent changes can be
-- evolved through ordered Flyway migrations instead of relying on
-- spring.jpa.hibernate.ddl-auto=update.

CREATE TABLE students (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    student_code  VARCHAR(20)  NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(255),
    address       VARCHAR(255),
    class_name    VARCHAR(255),
    note          VARCHAR(255),
    date_of_birth DATE,
    gender        ENUM ('FEMALE', 'MALE', 'OTHER'),
    status        ENUM ('ACTIVE', 'DROPPED', 'GRADUATED', 'SUSPENDED'),
    deleted       BIT,
    PRIMARY KEY (id),
    CONSTRAINT uk_students_user_id UNIQUE (user_id),
    CONSTRAINT uk_students_email   UNIQUE (email),
    INDEX idx_students_class_name (class_name),
    INDEX idx_students_status     (status)
) ENGINE = InnoDB;
