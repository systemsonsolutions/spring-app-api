CREATE TABLE tb_founders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    linkedin VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL
);