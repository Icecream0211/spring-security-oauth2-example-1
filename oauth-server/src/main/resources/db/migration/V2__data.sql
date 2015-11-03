INSERT INTO authority VALUES (0, 'role_oauth_admin');
INSERT INTO authority VALUES (1, 'role_admin');
INSERT INTO authority VALUES (2, 'role_user');
INSERT INTO credentials VALUES (0, 1, 'oauth_admin', 'admin', 0);
INSERT INTO credentials VALUES (1, 1, 'resource_admin', 'admin', 0);
INSERT INTO credentials VALUES (2, 1, 'user', 'user', 0);
INSERT INTO credentials_authorities VALUES (0, 0);
INSERT INTO credentials_authorities VALUES (1, 1);
INSERT INTO credentials_authorities VALUES (2, 2);

INSERT INTO oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
  ('read-only-client', 'todo-services', NULL, 'read', 'implicit', 'http://localhost', NULL, 7200, 0, NULL, 'false');

INSERT INTO oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
  ('curl-client', 'todo-services', 'client-secret', 'read,write', 'client_credentials', '', 'role_admin', 7200, 0, NULL, 'false');

INSERT INTO oauth_client_details
(client_id, client_secret)
VALUES
  ('resource-server', 'resource-server');