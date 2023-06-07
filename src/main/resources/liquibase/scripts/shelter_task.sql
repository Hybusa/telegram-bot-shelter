-- liquibase formatted sql

-- changeset akuznetsov:1
CREATE TABLE users
(
    id      INTEGER PRIMARY KEY,
    name    VARCHAR(255),
    chat_id VARCHAR(255),
    contact VARCHAR(255),
    failed  BOOLEAN
);

CREATE TABLE shelters
(
    id                           INTEGER PRIMARY KEY,
    volunteer_chat_id            VARCHAR(255),
    shelter_type                 VARCHAR(255),
    meeting_recommendations      VARCHAR(255),
    how_to_get_pet               VARCHAR(255),
    documents_list               VARCHAR(255),
    general_info                 VARCHAR(255),
    phone_number                 VARCHAR(255),
    schedule                     VARCHAR(255),
    address                      VARCHAR(255),
    how_to_get                   VARCHAR(255),
    security_and_pass            VARCHAR(255),
    safety                       VARCHAR(255),
    transporting_recommendations VARCHAR(255),
    home_recommendations_young   VARCHAR(255),
    home_recommendations_old     VARCHAR(255),
    cynologist_recommendations   VARCHAR(255),
    why_we_can_deny              VARCHAR(255),
    CONSTRAINT chk_cynologist_recommendations CHECK (shelter_type = 'dogs' OR cynologist_recommendations IS NULL)
);

CREATE TABLE pets
(
    id                         INTEGER PRIMARY KEY,
    name                       VARCHAR(255),
    age                        INTEGER,
    short_info                 VARCHAR(255),
    rejections                 INTEGER,
    disability_recommendations VARCHAR(255),
    user_id                    INTEGER,
    shelters_id                INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (shelters_id) REFERENCES shelters (id)
);


CREATE TABLE adopted_cats
(
    id               INTEGER PRIMARY KEY,
    id_pet           INTEGER,
    id_user          INTEGER,
    period_start     timestamptz,
    period_end       timestamptz,
    last_report_date timestamptz,
    FOREIGN KEY (id_pet) REFERENCES pets (id),
    FOREIGN KEY (id_user) REFERENCES users (id)
);

CREATE TABLE adopted_dogs
(
    id               INTEGER PRIMARY KEY,
    id_pet           INTEGER,
    id_user          INTEGER,
    period_start     timestamptz,
    period_end       timestamptz,
    last_report_date timestamptz,
    FOREIGN KEY (id_pet) REFERENCES pets (id),
    FOREIGN KEY (id_user) REFERENCES users (id)
);
-- changeset akuznetsov:2
SELECT *
FROM users
         JOIN pets ON users.id = pets.user_id;


SELECT *
FROM users
         JOIN pets ON users.id = pets.user_id
         JOIN adopted_cats ON pets.id = adopted_cats.id_pet;


SELECT *
FROM users
         JOIN pets ON users.id = pets.user_id
         JOIN adopted_dogs ON pets.id = adopted_dogs.id_pet;

SELECT *
FROM users
         JOIN pets ON users.id = pets.user_id
         LEFT JOIN adopted_cats ON pets.id = adopted_cats.id_pet
         LEFT JOIN adopted_dogs ON pets.id = adopted_dogs.id_pet;

SELECT users.name AS user_name, shelters.address, pets.name AS pet_name
FROM users
         JOIN pets ON users.id = pets.user_id
         JOIN shelters ON pets.shelters_id = shelters.id;

-- changeset akuznetsov:3
CREATE TABLE user_shelter_join
(
    user_id                       INTEGER REFERENCES users(id),
    shelter_id                    INTEGER REFERENCES shelters(id)
);
