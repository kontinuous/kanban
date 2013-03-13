# Tasks schema

# --- !Ups

create table Board (id bigint not null AUTO_INCREMENT, name varchar(255), owner varchar(255), primary key (id));
create table Task (id bigint not null AUTO_INCREMENT, name varchar(255), status tinyblob, board bigint, primary key (id));
create table User (name varchar(255) not null, password varchar(255), salt varchar(255), isAdmin boolean, primary key (name));
create table user_board (user_name varchar(255) not null, board_id bigint not null, primary key (board_id, user_name));
alter table Board add index FK3D5FEC64A4623BB (owner), add constraint FK3D5FEC64A4623BB foreign key (owner) references User (name);
alter table Task add index FK27A9A53EDF402F (board), add constraint FK27A9A53EDF402F foreign key (board) references Board (id);
alter table user_board add index FK7260F9729948A25D (board_id), add constraint FK7260F9729948A25D foreign key (board_id) references Board (id);
alter table user_board add index FK7260F972582C1F87 (user_name), add constraint FK7260F972582C1F87 foreign key (user_name) references User (name);

# --- !Downs

 drop table user_board;
 drop table Task;
 drop table board;
 drop table User;