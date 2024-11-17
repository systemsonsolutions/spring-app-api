CREATE TABLE tb_images (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    banner_id BIGINT NOT NULL,
    CONSTRAINT fk_tb_images_tb_banners
        FOREIGN KEY (banner_id)
        REFERENCES tb_banners (id)
        ON DELETE CASCADE
);