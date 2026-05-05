-- Initial schema for auth-service.
-- Generated from Hibernate-managed entities so that subsequent changes can be
-- evolved through ordered Flyway migrations instead of relying on
-- spring.jpa.hibernate.ddl-auto=update.

CREATE TABLE roles (
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name ENUM ('ADMIN', 'STUDENT', 'TEACHER') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE users (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    username                 VARCHAR(255) NOT NULL,
    password                 VARCHAR(255) NOT NULL,
    email                    VARCHAR(255) NOT NULL,
    enabled                  BIT          NOT NULL,
    account_non_locked       BIT          NOT NULL,
    account_non_expired      BIT          NOT NULL,
    credentials_non_expired  BIT          NOT NULL,
    password_change_required BIT          NOT NULL,
    created_at               DATETIME(6),
    updated_at               DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email    UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE user_roles (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_user_roles_user (user_id)
) ENGINE = InnoDB;
