# Tasks schema

# --- !Ups

ALTER TABLE task MODIFY status VARCHAR(255) NOT NULL;

# --- !Downs