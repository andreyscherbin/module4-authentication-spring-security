INSERT INTO gift_certificate (id, name, description, price, duration, create_date)
VALUES (1, 'name1', 'description1', '10.99', 10, CURRENT_TIMESTAMP(3));
INSERT INTO tag (id, name)
VALUES (1, 'tag1');
INSERT INTO tag (id, name)
VALUES (2, 'tag2');
INSERT INTO tag (id, name)
VALUES (3, 'tag3');
INSERT INTO tag (id, name)
VALUES (4, 'tag4');
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (1, 1);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (1, 2);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (1, 3);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (1, 4);

INSERT INTO gift_certificate (id, name, description, price, duration, create_date)
VALUES (2, 'name2', 'description2', '10.99', 100, CURRENT_TIMESTAMP(3));
INSERT INTO tag (id, name)
VALUES (5, 'tag5');
INSERT INTO tag (id, name)
VALUES (6, 'tag6');
INSERT INTO tag (id, name)
VALUES (7, 'tag7');
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (2, 5);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (2, 1);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (2, 2);

INSERT INTO gift_certificate (id, name, description, price, duration, create_date)
VALUES (3, 'name3', 'description3', '10.99', 5, CURRENT_TIMESTAMP(3));
INSERT INTO tag (id, name)
VALUES (8, 'tag8');
INSERT INTO tag (id, name)
VALUES (9, 'tag9');
INSERT INTO tag (id, name)
VALUES (10, 'tag10');
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (3, 8);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (3, 6);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (3, 7);

INSERT INTO gift_certificate (id, name, description, price, duration, create_date)
VALUES (4, 'name4', 'description4', '10.99', 1, CURRENT_TIMESTAMP(3));
INSERT INTO tag (id, name)
VALUES (11, 'tag11');
INSERT INTO tag (id, name)
VALUES (12, 'tag12');
INSERT INTO tag (id, name)
VALUES (13, 'tag13');
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (4, 11);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (4, 12);
INSERT INTO gift_certificate_tag (id_gift_certificate, id_tag)
VALUES (4, 1);

INSERT INTO users
VALUES (1, 'andrey','12345');
INSERT INTO users
VALUES (2, 'petya','12345');
INSERT INTO users
VALUES (3, 'nikolay','12345');
INSERT INTO users
VALUES (4, 'kristina','123456');

INSERT INTO roles (role_id,name) VALUES (1,'ROLE_GUEST');
INSERT INTO roles (role_id,name) VALUES (2,'ROLE_USER');
INSERT INTO roles (role_id,name) VALUES (3,'ROLE_ADMIN');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 3);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (4, 2);


INSERT INTO orders (id, id_user)
VALUES (1, 1);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (1, 1);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (1, 2);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (1, 3);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (1, 4);

INSERT INTO orders (id, id_user)
VALUES (2, 2);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (2, 3);
INSERT INTO order_gift_certificate (id_order, id_gift_certificate)
VALUES (2, 4);

UPDATE
    orders
SET cost =
        (SELECT SUM(price)
         FROM
             order_gift_certificate ogc
                 INNER JOIN gift_certificate gc on ogc.id_gift_certificate= gc.id
         WHERE ogc.id_order = 1)
WHERE orders.id = 1
