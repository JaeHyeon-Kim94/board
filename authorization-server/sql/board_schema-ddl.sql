CREATE TABLE IF NOT EXISTS tb_oauth2_authorized_client
(
    'client_registration_id'        VARCHAR(100)    NOT NULL,
    'principal_name'                VARCHAR(200)    NOT NULL,
    'access_token_type'             VARCHAR(100)    NOT NULL,
    'access_token_value'            BLOB            NOT NULL,
    'access_token_issued_at'        TIMESTAMP       NOT NULL,
    'access_token_expires_at'       TIMESTAMP       NOT NULL,
    'access_token_scopes'           VARCHAR(1000)   DEFAULT NULL,
    'refresh_token_value'           BLOB            DEFAULT NULL,
    'refresh_token_issued_at'       TIMESTAMP       DEFAULT NULL,
    'created_at'                    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (client_registration_id, principal_name)
);

CREATE TABLE IF NOT EXISTS tb_social_user
(
    'client_registration_id'      VARCHAR(100)    NOT NULL,
    'social_user_id'              VARCHAR(200)    NOT NULL,
    'role_id'                     VARCHAR(100)    NOT NULL,
    'social_user_full_name'       VARCHAR(50)     NULL,
    'social_user_email'           VARCHAR(100)    NULL,
    'id_token_value'              BLOB            NOT NULL,
    'id_token_issued_at'          TIMESTAMP       NOT NULL,
    'id_token_expires_at'         TIMESTAMP       NOT NULL,
    'access_token_value'          BLOB            NOT NULL,
    'access_token_issued_at'      TIMESTAMP       NOT NULL,
    'access_token_expires_at'     TIMESTAMP       NOT NULL,
    'refresh_token_value'         BLOB            DEFAULT NULL,
    'refresh_token_issued_at'     TIMESTAMP       DEFAULT NULL,
    'reg_date'                    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (`client_registration_id`, `social_user_id`),
    INDEX `idx_fk_tb_social_user_tb_role_id` (`role_id` ASC),
    CONSTRAINT `fk_tb_social_user_tb_role_id` FOREIGN KEY (`role_id`)
        REFERENCES `tb_role` (`id`)
        ON DELETE RESTRICT
);


CREATE TABLE IF NOT EXISTS tb_role
(
    `id`                VARCHAR(100) NOT NULL,
    `parent_id`         VARCHAR(100) NULL DEFAULT NULL,
    `role_desc`         VARCHAR(45) NULL DEFAULT NULL,
    `role_name`         VARCHAR(45) NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_fk_tb_role_tb_role_parent_id` (`parent_id` ASC),
    CONSTRAINT `fk_tb_role_tb_role_id`
        FOREIGN KEY (`parent_id`)
            REFERENCES  `tb_role` (`id`)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS  `tb_resource`
(
    `id`                    VARCHAR(100) NOT NULL,
    `tb_role_id`            VARCHAR(100) NOT NULL,
    `resource_type`         VARCHAR(45) NOT NULL,
    `resource_value`        VARCHAR(200) NOT NULL,
    `resource_http_method`  VARCHAR(45) NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_fk_tb_resource_tb_role_id` (`tb_role_id` ASC),
    CONSTRAINT `fk_tb_resource_tb_role_id`
        FOREIGN KEY (`tb_role_id`)
            REFERENCES `tb_role` (`id`)
            ON DELETE RESTRICT
);