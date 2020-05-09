-- DROP TABLE IF EXISTS filial;
CREATE TABLE IF NOT EXISTS car (
    id          SERIAL NOT NULL PRIMARY KEY,
    filial_id   BIGINT NOT NULL,
    equipment   VARCHAR(250),
    color       VARCHAR(100),
    FOREIGN KEY (filial_id) REFERENCES filial(id)
);

ALTER TABLE car ADD COLUMN status VARCHAR(100) default 'NEW';