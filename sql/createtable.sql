CREATE TABLE mem (
    time       timestamp,
    machine    varchar(255),
    query      varchar(255),
    match      int,
    free       real,
    swap       real
);

CREATE TABLE disk (
    time          timestamp,
    machine       varchar(255),
    query         varchar(255),
    match         int,
    tps           real,
    bulk_read     real,
    bulk_written  real
);

CREATE TABLE cpu (
    time         timestamp,
    machine      varchar(255),
    query        varchar(255),
    match        int,
    cpu_user     real,
    cpu_system   real
);

CREATE TABLE net (
    time              timestamp,
    machine           varchar(255),
    query             varchar(255),
    match             int,
    pkt_received      real,
    pkt_transmitted   real,
    kb_received       real,
    kb_transmitted    real,
    cpkt_received     real,
    cpkt_transmitted  real,
    mpkt_received     real
);
