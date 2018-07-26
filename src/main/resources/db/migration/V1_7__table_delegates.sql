CREATE TABLE delegates (
  id         INTEGER PRIMARY KEY,
  public_key VARCHAR NULL UNIQUE,
  address    VARCHAR NULL
);

CREATE TABLE delegate2genesis (
  delegate_id INTEGER NOT NULL REFERENCES delegates,
  genesis_id  INTEGER NOT NULL REFERENCES genesis_blocks,
  PRIMARY KEY (delegate_id, genesis_id)
);