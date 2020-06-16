CREATE TABLE open_scaffolds
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY HASH,
    recipient_address VARCHAR NOT NULL,
    web_hook          VARCHAR
);

CREATE INDEX idx_open_scaffolds_recipient_address
    ON open_scaffolds (recipient_address);