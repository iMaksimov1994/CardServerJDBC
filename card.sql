create table user
(
    id       integer primary key auto_increment,
    login    varchar(30) not null unique,
    password varchar(30) not null,
    name     varchar(30) not null,
    regDate  datetime
);

create table category
(
    id   integer primary key auto_increment,
    name varchar(30) not null unique ,
    id_u integer(30) not null,
    foreign key (id_u) references user (id) on update cascade on delete cascade
);

create table card
(
    id           integer(30) primary key auto_increment,
    question     varchar(30) not null unique ,
    answer       varchar(30) not null,
    id_c         integer(30) not null,
    creationDate datetime,
    foreign key (id_c) references category (id) on update cascade on delete cascade
);