DROP VIEW fin_dd_view ;
DROP TABLE fin_dd ; 
DROP TABLE fin_sym ; 

CREATE TABLE fin_sym 
(
  sym_id INT  PRIMARY KEY, 
  name   TEXT UNIQUE NOT NULL, 
  alt    TEXT
) ; 

CREATE INDEX fin_sym_name_idx on fin_sym(name) ; 

-- daily data
CREATE TABLE fin_dd
(
  sym_id INT  REFERENCES fin_sym, 
  ts     date NOT NULL,
  open   REAL, 
  close  REAL, 
  high   REAL, 
  low    REAL, 
  vol    REAL, 
  adj    REAL, 
  UNIQUE (ts,sym_id)
) ; 

CREATE VIEW fin_dd_view AS
SELECT name as sym, ts, open, close, high, low, vol, adj
  FROM fin_sym s, fin_dd d
 WHERE s.sym_id = d.sym_id ; 



