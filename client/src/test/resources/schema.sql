DROP TABLE IF EXISTS tb_user;
DROP TABLE IF EXISTS tb_role;
DROP TABLE IF EXISTS tb_resource;
DROP TABLE IF EXISTS tb_board;

CREATE TABLE IF NOT EXISTS tb_user
(
    `user_id`         VARCHAR(100) NOT NULL,
    `provider_id`     VARCHAR(45)  NULL,
    `password`        VARCHAR(300) NOT NULL,
    `fullname`       VARCHAR(100)  NULL,
    `nickname`        VARCHAR(45)  NOT NULL,
    `phone`           VARCHAR(100) NULL,
    `email`           VARCHAR(100) NULL,
    `birth`           DATE         NULL,
    `refresh_token`   VARCHAR(300) NULL,
    `reg_date`        TIMESTAMP    NULL    DEFAULT CURRENT_TIMESTAMP,
    `mod_date`        TIMESTAMP    NULL    DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX `tb_user_nickname_unique` (`nickname` ASC)
);

CREATE TABLE IF NOT EXISTS tb_role
(
    `id`                        VARCHAR(100)    NOT NULL,
    `parent_id`                 VARCHAR(100)    NULL DEFAULT NULL,
    `role_desc`                 VARCHAR(45)     NULL DEFAULT NULL,
    `role_name`                 VARCHAR(45)     NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_fk_tb_role_tb_role_parent_id` (`parent_id` ASC),
    INDEX `idx_role_name` (`role_name` ASC),
    CONSTRAINT `fk_tb_role_tb_role_id`
        FOREIGN KEY (`parent_id`)
            REFERENCES  `tb_role` (`id`)
            ON DELETE SET NULL
);



CREATE TABLE IF NOT EXISTS tb_user_role
(
    `id`                                        BIGINT(20) UNSIGNED AUTO_INCREMENT,
    `tb_role_id`                                VARCHAR(100) NOT NULL,
    `tb_user_user_id`                           VARCHAR(100) NOT NULL,
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
            ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS  `tb_resource`
(
    `id`                    BIGINT(20)      UNSIGNED AUTO_INCREMENT,
    `resource_type`         VARCHAR(45)     NOT NULL,
    `resource_level`        BIGINT(20)      UNSIGNED NOT NULL,
    `tb_role_id`            VARCHAR(100)    NULL,
    `resource_value`        VARCHAR(200)    NOT NULL,
    `resource_http_method`  VARCHAR(45)     NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_resource_level` (`resource_level` DESC),
    INDEX `idx_fk_tb_resource_tb_role_id` (`tb_role_id` ASC),
    UNIQUE INDEX `tb_resource_level` (`resource_level` DESC),
    CONSTRAINT `fk_tb_resource_tb_role_id`
        FOREIGN KEY (`tb_role_id`)
            REFERENCES `tb_role` (`id`)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS `tb_board`
(
    `id`                    BIGINT(20)      UNSIGNED AUTO_INCREMENT,
    `category`              VARCHAR(45)     NOT NULL,
    `subject`               VARCHAR(45)     NOT NULL,
    `tb_resource_id`        BIGINT(20)      UNSIGNED NULL,
    `reg_date`              TIMESTAMP       NULL DEFAULT CURRENT_TIMESTAMP,
    `mod_date`              TIMESTAMP       NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `tb_board_subject_unique` (`subject` ASC),
    CONSTRAINT `fk_tb_board_tb_resource_id`
        FOREIGN KEY (`tb_resource_id`)
            REFERENCES `tb_resource` (`id`)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS oauth2_authorized_client
(
    `client_registration_id`        VARCHAR(100)    NOT NULL,
    `principal_name`                VARCHAR(200)    NOT NULL,
    `access_token_type`             VARCHAR(100)    NOT NULL,
    `access_token_value`            BLOB            NOT NULL,
    `access_token_issued_at`        TIMESTAMP       NOT NULL,
    `access_token_expires_at`       TIMESTAMP       NOT NULL,
    `access_token_scopes`           VARCHAR(1000)   DEFAULT NULL,
    `refresh_token_value`           BLOB            DEFAULT NULL,
    `refresh_token_issued_at`       TIMESTAMP       DEFAULT NULL,
    `created_at`                    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (client_registration_id, principal_name)
);

CREATE TABLE IF NOT EXISTS oauth2_registered_client (
                                                        `id` VARCHAR(100) NOT NULL,
                                                        `client_id` varchar(100) NOT NULL
    , `client_id_issued_at` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
    , `client_secret` varchar(200) DEFAULT NULL
    , `client_secret_expires_at` timestamp DEFAULT NULL
    , `client_name` varchar(200) NOT NULL
    , `client_authentication_methods` varchar(1000) NOT NULL
    , `authorization_grant_types` varchar(1000) NOT NULL
    , `redirect_uris` varchar(1000) DEFAULT NULL
    , `scopes` varchar(1000) NOT NULL
    , `client_settings` varchar(2000) NOT NULL
    , `token_settings` varchar(2000) NOT NULL, PRIMARY KEY (id)
);


-- TODO
-- CREATE TABLE IF NOT EXISTS `tb_post`
-- (
--     `id`                    BIGINT(20)      UNSIGNED AUTO_INCREMENT,
--     `tb_user_user_id`       VARCHAR(100)    NULL,
--     `tb_board_id`           BIGINT(20)      UNSIGNED NOT NULL,
--     `title`                 VARCHAR(300)    NOT NULL,
--     `content`               LONGTEXT        NOT NULL,
--     `reg_date`              TIMESTAMP       NULL DEFAULT CURRENT_TIMESTAMP,
--     `mod_date`              TIMESTAMP       NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
--     PRIMARY KEY (`id`),
--     CONSTRAINT `fk_tb_post_tb_user_user_id`
--         FOREIGN KEY (`tb_user_user_id`)
--             REFERENCES `tb_user` (`user_id`)
--             ON DELETE SET NULL,
--     CONSTRAINT `fk_tb_post_tb_board_id`
--         FOREIGN KEY (`tb_board_id`)
--             REFERENCES `tb_board` (`id`)
--             ON DELETE CASCADE
-- );
--
-- # TODO
-- CREATE TABLE IF NOT EXISTS `tb_board_registration`
-- (
--     `id`                    BIGINT(20)      UNSIGNED AUTO_INCREMENT,
--     `tb_user_user_id`       VARCHAR(100)    NULL,
--     `status`                VARCHAR(45)     NOT NULL,
--     `reg_date`              TIMESTAMP       NULL DEFAULT CURRENT_TIMESTAMP,
--     `processed_date`        TIMESTAMP       NULL,
--     PRIMARY KEY (`id`),
--     CONSTRAINT `fk_tb_board_registration_tb_user_user_id`
--         FOREIGN KEY (`tb_user_user_id`)
--             REFERENCES `tb_user` (`user_id`)
--             ON DELETE SET NULL
-- );