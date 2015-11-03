DROP TABLE IF EXISTS authority;
CREATE TABLE authority (
  id        INTEGER,
  authority VARCHAR(255),
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS credentials;
CREATE TABLE credentials (
  id       INTEGER,
  enabled  BOOLEAN      NOT NULL,
  name     VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  version  INTEGER,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS credentials_authorities;
CREATE TABLE credentials_authorities (
  credentials_id BIGINT NOT NULL,
  authorities_id BIGINT NOT NULL
);

DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE oauth_client_details (
  client_id               VARCHAR(256) PRIMARY KEY,
  resource_ids            VARCHAR(256),
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types  VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities             VARCHAR(256),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(256)
);

DROP TABLE IF EXISTS oauth_client_token;
CREATE TABLE oauth_client_token (
  token_id          VARCHAR(256),
  token             LONGVARBINARY,
  authentication_id VARCHAR(256),
  user_name         VARCHAR(256),
  client_id         VARCHAR(256)
);

DROP TABLE IF EXISTS oauth_access_token;
CREATE TABLE oauth_access_token (
  token_id          VARCHAR(256),
  token             LONGVARBINARY,
  authentication_id VARCHAR(256),
  user_name         VARCHAR(256),
  client_id         VARCHAR(256),
  authentication    LONGVARBINARY,
  refresh_token     VARCHAR(256)
);

DROP TABLE IF EXISTS oauth_refresh_token;
CREATE TABLE oauth_refresh_token (
  token_id       VARCHAR(256),
  token          LONGVARBINARY,
  authentication LONGVARBINARY
);

DROP TABLE IF EXISTS oauth_code;
CREATE TABLE oauth_code (
  code           VARCHAR(256),
  authentication LONGVARBINARY
);

DROP TABLE IF EXISTS oauth_approvals;
CREATE TABLE oauth_approvals (
  userid         VARCHAR(256),
  clientid       VARCHAR(256),
  scope          VARCHAR(256),
  status         VARCHAR(10),
  expiresat      TIMESTAMP,
  lastmodifiedat TIMESTAMP
);