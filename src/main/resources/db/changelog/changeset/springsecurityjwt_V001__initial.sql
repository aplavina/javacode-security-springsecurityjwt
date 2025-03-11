create TABLE users (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   email VARCHAR NOT NULL,
   password VARCHAR,
   role SMALLINT,
   log_in_tries BIGINT NOT NULL,
   is_account_non_locked BOOLEAN NOT NULL,
   CONSTRAINT pk_users PRIMARY KEY (id)
);

alter table users add CONSTRAINT uc_users_email UNIQUE (email);