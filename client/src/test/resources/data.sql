INSERT INTO tb_role (id, parent_id, role_desc, role_name)
VALUES('A_0000', null, '관리자 계정', 'ROLE_ADMIN');

INSERT INTO tb_role (id, parent_id, role_desc, role_name)
VALUES('M_0000', 'A_0000', '매니저 계정', 'ROLE_MANAGER');

INSERT INTO tb_role (id, parent_id, role_desc, role_name)
VALUES('U_0000', 'M_0000', '사용자 계정', 'ROLE_USER');

INSERT INTO tb_resource (resource_type, resource_level, tb_role_id, resource_value, resource_http_method)
VALUES ('url', 1000, 'A_0000', '/api/resources*/**', null);

INSERT INTO tb_resource (resource_type, resource_level, tb_role_id, resource_value, resource_http_method)
VALUES ('url', 2000, 'A_0000', '/api/roles*/**', null);

INSERT INTO tb_resource (resource_type, resource_level, tb_role_id, resource_value, resource_http_method)
VALUES ('url', 3000, 'A_0000', '/api/boards*/**', null);

INSERT INTO tb_resource (resource_type, resource_level, tb_role_id, resource_value, resource_http_method)
VALUES ('url', 4000, 'A_0000', '/api/users*/**', null);

INSERT INTO tb_user (user_id, provider_id, password, fullname, nickname, phone, email, birth)
VALUES('user', 'myOAuth', '{noop}1234', 'USER 성명', 'USER 닉네임', '010-0000-0000', 'user@email.com', '1999-09-09');

INSERT INTO tb_user_role(tb_role_id, tb_user_user_id)
VALUES ('U_0000', 'user');

INSERT INTO tb_user (user_id, provider_id, password, fullname, nickname, phone, email, birth)
VALUES('test_manager', 'myOAuth', '{noop}1234', 'TEST_MANAGER 성명', 'TEST_MANAGER 닉네임', '010-0000-0000', 'testmanager@email.com', '1999-09-09');

INSERT INTO tb_user_role (tb_role_id, tb_user_user_id)
VALUES('U_0000', 'test_manager')