CREATE TABLE access_token
(
    id           BIGINT NOT NULL,
    access_token VARCHAR(255),
    token_type   VARCHAR(255),
    expire_in    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_accesstoken PRIMARY KEY (id)
);