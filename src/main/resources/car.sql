DROP TABLE IF EXISTS car;
CREATE TABLE IF NOT EXISTS car (
    id           SERIAL NOT NULL PRIMARY KEY,
    filial_id    BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    color        VARCHAR(100),
    FOREIGN KEY (filial_id) REFERENCES filial(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

CREATE TABLE IF NOT EXISTS model (
    id           SERIAL NOT NULL PRIMARY KEY,
    link_part    VARCHAR(250),
    name         VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS equipment (
    id           SERIAL NOT NULL PRIMARY KEY,
    model_id     BIGINT NOT NULL,
    name         VARCHAR(250),
    code         VARCHAR(50),
    FOREIGN KEY (model_id) REFERENCES model(id)
);
