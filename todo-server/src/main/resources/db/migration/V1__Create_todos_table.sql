CREATE TABLE todos(
    ID          UUID         not null,
    title       varchar(128) not null,
    description varchar(128) not null,
    "user"      varchar(128) not null
);