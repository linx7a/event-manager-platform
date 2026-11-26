--liquibase formatted sql
--changeset linx7a:0002-create-location-table
create table locations (
    id bigserial primary key,
    name varchar not null,
    address varchar not null,
    capacity int not null check (capacity >= 5),
    description varchar
);