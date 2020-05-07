-- DROP TABLE IF EXISTS filial;
CREATE TABLE IF NOT EXISTS filial (
    id          SERIAL NOT NULL PRIMARY KEY,
    dealer_id   BIGINT NOT NULL,
    address     VARCHAR(250),
    code        VARCHAR(100),
    FOREIGN KEY (dealer_id) REFERENCES dealer(id)
);