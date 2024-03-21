DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS item_requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT unique_user_email UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1000) NOT NULL,
    requestor BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)

);


CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    available BOOLEAN NOT NULL,
    owner BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)

);

CREATE TYPE IF NOT EXISTS booking_status AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS bookings (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     start_time TIMESTAMP NOT NULL,
     end_time TIMESTAMP NOT NULL,
     item BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
     booker BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
     status booking_status NOT NULL,
     CONSTRAINT pk_booking PRIMARY KEY (id)

);

CREATE TABLE IF NOT EXISTS comments (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     text VARCHAR (1024) NOT NULL,
     author BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
     item BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
     created TIMESTAMP NOT NULL,
     CONSTRAINT pk_comments PRIMARY KEY (id)

);