CREATE TABLE tb_products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    link VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    image VARCHAR(255) NOT NULL
);