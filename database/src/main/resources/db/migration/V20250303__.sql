CREATE TABLE contact_types
(
    id         UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(20),
    image_url  VARCHAR(255),
    CONSTRAINT pk_contact_types PRIMARY KEY (id)
);

CREATE TABLE contacts
(
    id              UUID NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    user_id         UUID,
    contact_type_id UUID,
    display_name    VARCHAR(255),
    url             VARCHAR(255),
    CONSTRAINT pk_contacts PRIMARY KEY (id)
);

CREATE TABLE email_verifications
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    email             VARCHAR(255)                NOT NULL,
    code              VARCHAR(6)                  NOT NULL,
    attempts          SMALLINT                    NOT NULL,
    verification_type VARCHAR(20)                 NOT NULL,
    verified_at       TIMESTAMP WITHOUT TIME ZONE,
    expired_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_email_verifications PRIMARY KEY (id)
);

CREATE TABLE media_files
(
    id           UUID         NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    user_id      UUID         NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    CONSTRAINT pk_media_files PRIMARY KEY (id)
);

CREATE TABLE post_tags
(
    id         UUID NOT NULL,
    post_id    UUID NOT NULL,
    tag_id     UUID NOT NULL,
    user_id    UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_post_tags PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id           UUID         NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    user_id      UUID         NOT NULL,
    title        VARCHAR(100) NOT NULL,
    content      TEXT         NOT NULL,
    summary      VARCHAR(200),
    thumbnail    TEXT,
    thumbnail_id UUID,
    status       VARCHAR(20)  NOT NULL,
    published_at TIMESTAMP WITHOUT TIME ZONE,
    view_count   BIGINT DEFAULT 0,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE TABLE tags
(
    id         UUID              NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    UUID              NOT NULL,
    title      VARCHAR(50)       NOT NULL,
    tag_count  INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                UUID         NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    nickname          VARCHAR(20),
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255),
    profile_image_id  UUID,
    profile_image_url VARCHAR(255),
    job_role          VARCHAR(30),
    bio               VARCHAR(100),
    role              VARCHAR(20)  NOT NULL,
    refresh_token     TEXT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_nickname UNIQUE (nickname);

CREATE INDEX idx_contact_user_id ON contacts (user_id);

CREATE INDEX idx_email_verification_email ON email_verifications (email);

CREATE INDEX idx_media_file_user_id ON media_files (user_id);

CREATE INDEX idx_post_status ON posts (status);

CREATE INDEX idx_post_tag_post_id ON post_tags (post_id);

CREATE INDEX idx_post_tag_tag_id ON post_tags (tag_id);

CREATE INDEX idx_post_tag_user_id ON post_tags (user_id);

CREATE INDEX idx_post_user_id ON posts (user_id);

CREATE UNIQUE INDEX idx_tag_title ON tags (title);

CREATE UNIQUE INDEX idx_user_email ON users (email);

CREATE UNIQUE INDEX idx_user_nickname ON users (nickname);