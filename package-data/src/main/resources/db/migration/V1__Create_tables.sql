-- Create stops
create table stops
(
    id                  varchar(10)      not null
        constraint stops_pk
            primary key,
    code                varchar(10),
    name                text             not null,
    description         text,
    latitude            double precision not null,
    longitude           double precision not null,
    zone_id             integer,
    stop_url            text,
    location_type       integer,
    parent_station      varchar(10),
    time_zone            varchar(10),
    wheelchair_boarding integer
);

create unique index stops_id_uindex
    on stops (id);

-- Create routes
create table routes
(
    id          varchar(10) not null
        constraint routes_pk
            primary key,
    agency_id   text,
    short_name  text        not null,
    long_name   text        not null,
    description text,
    type        integer     not null,
    url         text,
    color       text,
    text_color  text,
    sort_order  integer
);

create unique index routes_id_uindex
    on routes (id);

-- Create agencies
create table agencies
(
    id       text not null
        constraint agencies_pk
            primary key,
    name     text not null,
    url      text not null,
    time_zone text not null,
    language text,
    phone    text,
    fare_url text,
    email    text
);

-- Create calendar_Dates
create table calendar_dates
(
    service_id     text    not null,
    date           text    not null,
    exception_type integer not null
);

-- Create calendars
create table calendars
(
    service_id text    not null
        constraint calendars_pk
            primary key,
    monday     integer not null,
    tuesday    integer not null,
    wednesday  integer not null,
    thursday   integer not null,
    friday     integer not null,
    saturday   integer not null,
    sunday     integer not null,
    start_date text    not null,
    end_date   text    not null
);

-- Create stop_times
create table stop_times
(
    trip_id                 text        not null,
    arrival_time            varchar(15) not null,
    departure_time          varchar(15) not null,
    stop_id                 varchar(10) not null,
    stop_sequence           integer     not null,
    stop_headsign           text,
    pickup_type             integer,
    drop_off_type           integer,
    shape_distance_traveled double precision,
    timepoint               integer
);

-- Create trips
create table trips
(
    route_id              text not null,
    service_id            text not null,
    trip_id               text not null
        constraint trips_pk
            primary key,
    headsign              text,
    short_name            text,
    direction_id          integer,
    block_id              text,
    shape_id              text,
    wheelchair_accessible integer,
    bikes_allowed         integer
);

-- Create shapes
create table shapes
(
    id                text             not null,
    latitude          double precision not null,
    longitude         double precision not null,
    sequence          integer          not null,
    distance_traveled double precision
);


-- Create data versions history table
create table data_versions
(
    version        text                    not null,
    schema_version int                     not null,
    date           timestamp default now() not null
);

create unique index data_versions_version_uindex
    on data_versions (version);

alter table data_versions
    add constraint data_versions_pk
        primary key (version);

-- Create app metadata table
create table metadata
(
    branch           text                    not null
        constraint metadata_pk
            primary key,
    data_version     text                    not null,
    schema_version   integer                 not null,
    updated          timestamp default now() not null,
    app_version_code integer                 not null
);

comment on column metadata.branch is 'Indicates which branch of the app this metadata is for (live, dev, beta)';

comment on column metadata.data_version is 'The version string of the latest copy of the data for this branch';

comment on column metadata.schema_version is 'The schema version of the data specified in data_version';

comment on column metadata.updated is 'The time this data was last updated';

comment on column metadata.app_version_code is 'The latest published version code of this branch of the app';