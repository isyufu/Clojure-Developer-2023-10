create table types
(
    id   integer primary key,
    name varchar(50) -- name eng
);

create table translate_types
(
    id   integer,
    lang      varchar(10),
    translate varchar(50)
);

create table pokemons
(
    id   integer primary key,
    name varchar(50)
);

create table poke_types
(
    id_poke integer,
    id_type integer
);
