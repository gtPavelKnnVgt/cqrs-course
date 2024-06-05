CREATE SEQUENCE department_seq start with 100000 increment by 50;

CREATE TABLE department
(
    id          bigint  not null default nextval('department_seq') primary key,
    name        varchar not null,
    description varchar not null
);

CREATE SEQUENCE timeslot_seq start with 100000 increment by 50;

CREATE TABLE timeslot
(
    id         bigint    not null default nextval('timeslot_seq') primary key,
    start_time      timestamp not null,
    end_time        timestamp not null
);

CREATE SEQUENCE doctor_seq start with 100000 increment by 50;

CREATE TABLE doctor
(
    id             bigint  not null default nextval('doctor_seq') primary key,
    first_name     varchar not null,
    last_name      varchar not null,
    specialization varchar not null,
    license_number varchar not null,
    department_id  bigint  not null references department (id),
    timeslot_id  bigint  not null references timeslot (id)
);

CREATE SEQUENCE patient_seq start with 100000 increment by 50;

CREATE TABLE patient
(
    id                    bigint    not null default nextval('patient_seq') primary key,
    first_name            varchar   not null,
    last_name             varchar   not null,
    birthday              date not null,
    medical_record_number varchar   not null
);

CREATE SEQUENCE appointment_seq start with 100000 increment by 50;

CREATE TABLE appointment
(
    id         bigint    not null default nextval('appointment_seq') primary key,
    patient_id  bigint  not null references patient (id),
    doctor_id  bigint  not null references doctor (id),
    timeslot_id  bigint  not null references timeslot (id),
    day_of_week   varchar not null
);