CREATE SEQUENCE user_id_seq INCREMENT BY 1;
CREATE TABLE users
(
    id           BIGINT(255)  NOT NULL DEFAULT NEXT VALUE FOR user_id_seq PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE, -- assume email is unique
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    birth_date   DATE         NOT NULL,
    address      VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL
);