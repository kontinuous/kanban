# Tasks schema

# --- !Ups
CREATE TABLE board (
  id mediumint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

# --- !Downs

DROP TABLE board;