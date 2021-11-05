CREATE TABLE gift_certificate
(
    id               BIGINT IDENTITY,
    name             varchar(30)    NOT NULL,
    description      varchar(30)    NOT NULL,
    price            decimal(10, 2) NOT NULL,
    duration         int(30)        NOT NULL,
    create_date      TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    last_update_date TIMESTAMP(3)   NULL ON UPDATE CURRENT_TIMESTAMP(3)
);

CREATE TABLE tag
(
    id   BIGINT IDENTITY,
    name varchar(30) NOT NULL UNIQUE
);

CREATE TABLE gift_certificate_tag
(
    id_gift_certificate BIGINT,
    id_tag              BIGINT,
    PRIMARY KEY (id_gift_certificate, id_tag),
    FOREIGN KEY (id_gift_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_tag) REFERENCES tag (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE users
(
    user_id  BIGINT IDENTITY,
    name     VARCHAR(30) NOT NULL,
    password VARCHAR(80) NOT NULL,
);

CREATE TABLE users_roles
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE roles
(
    role_id BIGINT IDENTITY,
    name    varchar(45) NOT NULL
);

CREATE TABLE orders
(
    id          BIGINT IDENTITY,
    id_user     BIGINT,
    create_date TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    cost        decimal(10, 2) NULL,
    FOREIGN KEY (id_user) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE order_gift_certificate
(
    id_order            BIGINT,
    id_gift_certificate BIGINT,
    PRIMARY KEY (id_order, id_gift_certificate),
    FOREIGN KEY (id_order) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_gift_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gift_certificate_price_history
(
    id                  BIGINT IDENTITY,
    id_gift_certificate BIGINT,
    effective_date_from TIMESTAMP(3)   NOT NULL,
    effective_date_to   TIMESTAMP(3)   NOT NULL,
    price               DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_gift_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tokens
(
    id                  BIGINT IDENTITY,
    access_token        VARCHAR(256) NOT NULL,
    refresh_token       VARCHAR(256) NOT NULL,
    id_user             BIGINT,
    valid_refresh_token BOOLEAN      NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
)


