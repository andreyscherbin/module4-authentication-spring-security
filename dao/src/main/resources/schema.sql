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
    id   BIGINT IDENTITY ,
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
    id BIGINT IDENTITY,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE orders
(
    id BIGINT IDENTITY,
    id_user              BIGINT,
    create_date      TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    cost             decimal(20, 2) NULL,
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE order_gift_certificate
(
    id_order BIGINT,
    id_gift_certificate              BIGINT,
    PRIMARY KEY (id_order, id_gift_certificate),
    FOREIGN KEY (id_order) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_gift_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE gift_certificate_price_history
(
    id BIGINT                AUTO_INCREMENT PRIMARY KEY,
    id_gift_certificate      BIGINT,
    effective_date_from      TIMESTAMP(3)   NOT NULL,
    effective_date_to        TIMESTAMP(3)   NOT NULL,
    price                    DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_gift_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO users VALUES(1,'andrey');
INSERT INTO users VALUES(2,'petya');
INSERT INTO users VALUES(3,'nikolay');
INSERT INTO users VALUES(4,'kristina');





