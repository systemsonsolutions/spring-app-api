CREATE TABLE tb_banners (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    height INT NOT NULL,
    width INT NOT NULL
);