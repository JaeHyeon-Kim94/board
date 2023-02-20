CREATE TABLE IF NOT EXISTS tb_user
(
    `user_id`         VARCHAR(100) NOT NULL,
    `password`        VARCHAR(300) NOT NULL,
    `full_name`       VARCHAR(100) NOT NULL,
    `nickname`        VARCHAR(45)  NOT NULL,
    `phone`           VARCHAR(100) NULL,
    `email`           VARCHAR(100) NULL,
    `birth`           DATE         NULL,
    `reg_date`        TIMESTAMP    NULL    DEFAULT CURRENT_TIMESTAMP,
    `mod_date`        TIMESTAMP    NULL    DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX `tb_user_nickname_unique` (`nickname` ASC)
);

CREATE TABLE IF NOT EXISTS tb_user_role
(
    `id`                                        BIGINT(20)   AUTO_INCREMENT,
    `tb_role_id`                                VARCHAR(100) NOT NULL,
    `tb_user_user_id`                           VARCHAR(100) NULL,
    `tb_social_user_social_user_id`             VARCHAR(200) NULL,
    `tb_social_user_client_registration_id`     VARCHAR(100) NULL,
    PRIMARY KEY (`id`),

    INDEX `idx_fk_tb_user_role_id` (`tb_role_id` ASC),
    CONSTRAINT `fk_tb_user_role_id`
        FOREIGN KEY (`tb_role_id`)
            REFERENCES  `tb_role` (`id`)
            ON DELETE CASCADE,

    INDEX `idx_fk_tb_user_role_user_id` (`tb_user_user_id` ASC),
    CONSTRAINT `fk_tb_user_role_user_id`
        FOREIGN KEY (`tb_user_user_id`)
            REFERENCES  `tb_user` (`user_id`)
            ON DELETE CASCADE,

    INDEX `idx_fk_tb_user_role_social_user_id_client_registration_id` (`tb_social_user_social_user_id`, `tb_social_user_client_registration_id` ASC),
    CONSTRAINT `fk_tb_user_role_social_user_id_client_registration_id`
        FOREIGN KEY (`tb_social_user_social_user_id`, `tb_social_user_client_registration_id`)
            REFERENCES  `tb_social_user` (`social_user_id`, `client_registration_id`)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_social_user
(
    `social_user_id`              VARCHAR(200)    NOT NULL,
    `client_registration_id`      VARCHAR(100)    NOT NULL,
    `full_name`                   VARCHAR(50)     NULL,
    `nickname`                   VARCHAR(50)     NOT NULL,
    `phone`                       VARCHAR(50)     NULL,
    `email`                       VARCHAR(100)    NULL,
    `birth`                       TIMESTAMP       NULL,
    `reg_date`                    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `mod_date`                    TIMESTAMP       DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`social_user_id`, `client_registration_id`),
    UNIQUE INDEX `tb_social_user_nickname_unique` (`nickname` ASC)
);


CREATE TABLE IF NOT EXISTS tb_role
(
    `id`                        VARCHAR(100)    NOT NULL,
    `parent_id`                 VARCHAR(100)    NULL DEFAULT NULL,
    `role_desc`                 VARCHAR(45)     NULL DEFAULT NULL,
    `role_name`                 VARCHAR(45)     NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_fk_tb_role_tb_role_parent_id` (`parent_id` ASC),
    CONSTRAINT `fk_tb_role_tb_role_id`
        FOREIGN KEY (`parent_id`)
            REFERENCES  `tb_role` (`id`)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tb_role_resource
(
    `id`                    BIGINT(20)      AUTO_INCREMENT,
    `tb_role_id`            VARCHAR(100)    NOT NULL,
    `tb_resource_id`        BIGINT(20)      NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_fk_tb_role_resource_tb_role_id` (`tb_role_id` ASC),
    CONSTRAINT `fk_tb_role_resource_tb_role_id`
        FOREIGN KEY (`tb_role_id`)
            REFERENCES `tb_role` (`id`)
            ON DELETE CASCADE,
    INDEX `idx_fk_tb_role_resource_tb_resource_id` (`tb_resource_id` ASC),
    CONSTRAINT `fk_tb_role_resource_tb_resource_id`
        FOREIGN KEY (`tb_resource_id`)
        REFERENCES `tb_resource` (`id`)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS  `tb_resource`
(
    `id`                    BIGINT(20)      AUTO_INCREMENT,
    `resource_type`         VARCHAR(45)     NOT NULL,
    `resource_level`        VARCHAR(50)     NOT NULL,
    `resource_value`        VARCHAR(200)    NOT NULL,
    `resource_http_method`  VARCHAR(45)     NOT NULL,
    PRIMARY KEY (`id`)
);