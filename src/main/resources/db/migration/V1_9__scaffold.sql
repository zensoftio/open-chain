CREATE TABLE open_scaffolds
(
    id                BIGSERIAL PRIMARY KEY,
    recipient_address VARCHAR NOT NULL UNIQUE,
    web_hook VARCHAR NOT NULL
);

CREATE INDEX idx_open_scaffolds_recipient_address
    ON open_scaffolds (recipient_address);

ALTER TABLE open_transfer_transactions
    ALTER web_hook DROP NOT NULL;