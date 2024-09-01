CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    id_role BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT FK_userRole FOREIGN KEY (id_role) REFERENCES tb_roles (id)
);
