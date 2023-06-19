-- liquibase formatted sql

-- changeset akuznetsov:1
CREATE TABLE users
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255),
    chat_id             bigint,
    contact             VARCHAR(255),
    shelter_type_choice VARCHAR(255),
    failed              BOOLEAN
);

CREATE TABLE shelters
(
    id                           SERIAL PRIMARY KEY,
    volunteer_chat_id            bigint,
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
    list_of_cynologists          VARCHAR(255),
    disability_recommendations   VARCHAR(255),
    why_we_can_deny              VARCHAR(255),
    CONSTRAINT chk_cynologist_recommendations CHECK (shelter_type = 'dogs' OR cynologist_recommendations IS NULL),
    CONSTRAINT chk_list_of_cynologists CHECK (shelter_type = 'dogs' OR list_of_cynologists IS NULL)
);

CREATE TABLE pets
(
    id                         SERIAL PRIMARY KEY,
    name                       VARCHAR(255),
    age                        INTEGER,
    short_info                 VARCHAR(255),
    rejections                 INTEGER,
    user_id                    INTEGER,
    shelters_id                INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (shelters_id) REFERENCES shelters (id)
);


CREATE TABLE adopted_cats
(
    id               SERIAL PRIMARY KEY,
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
    id               SERIAL PRIMARY KEY,
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
    user_id    INTEGER REFERENCES users (id),
    shelter_id INTEGER REFERENCES shelters (id)
);

-- changeset a.sychkova:1
CREATE TABLE contacts_for_cats_shelter
(
    user_id SERIAL PRIMARY KEY,
    name    VARCHAR,
    contact VARCHAR,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

SELECT *
FROM users
         JOIN contacts_for_cats_shelter cFCS on users.id = cFCS.user_id;

-- changeset a.sychkova:2
CREATE TABLE contacts_for_dogs_shelter
(
    user_id SERIAL PRIMARY KEY,
    name    VARCHAR,
    contact VARCHAR,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

SELECT *
FROM users
         JOIN contacts_for_dogs_shelter cFDS on users.id = cFDS.user_id;



-- changeset m_yatsushko:4
ALTER TABLE shelters ALTER COLUMN meeting_recommendations TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN how_to_get_pet TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN safety TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN home_recommendations_young TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN home_recommendations_old TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN transporting_recommendations TYPE varchar(1000);
ALTER TABLE shelters ALTER COLUMN cynologist_recommendations TYPE varchar(4000);

INSERT INTO shelters (id, volunteer_chat_id, shelter_type, meeting_recommendations, how_to_get_pet,
                      documents_list, general_info, phone_number, schedule, address, how_to_get,
                      security_and_pass, safety, transporting_recommendations, home_recommendations_young,
                      home_recommendations_old, cynologist_recommendations,list_of_cynologists,disability_recommendations, why_we_can_deny)
VALUES (0, 0, 'dogs',
        'Важно, чтобы между вами и выбранным вами пушистым другом была связь.
        Вы должны убедиться, что новый член вашей семьи будет успешно адаптироваться к вашему образу жизни.
        Чтобы привести в дом подходящую собаку и убедиться, что ваш образ жизни идеален для вашего нового питомца, необходимы тщательные исследования и планирование.
        Большинство спасенных собак, которые живут в приютах ждут, когда люди с большим сердцем подарят им новый дом, легко становятся послушными и любящими домашними животными,
        готовыми на все ради ласки и немного внимания. Но есть собаки, которым нужно больше времени, чтобы приспособиться, научиться доверять людям, и вам нужно убедиться, что у вас есть ресурсы,
        чтобы принять участие в ответственном усыновлении. Любая собака, независимо от перенесенной травмы, может узнать, что такое любовь, если живет в окружении, полном любви.',
        'Мы отдаем животное строго по договору.
        Только в Москву и Московскую область
        Мы не отдаем животных на цепь, только в квартиру, в загородный дом или вольер
        Мы проверяем условия содержания у будущих владельцев',
        'Список необходимых документов',
        'Муниципальный Московский приют Щербинка ЮЗАО', '8(905)552-31-95',
        '6 дней в неделю с 11 00 до 17 00 ч. Санитарные дни 1-е и 15-е число месяца',
        'ул. Брусилова, 32Б, Москва, Россия, 117148',
        'Выезжаем из Москвы в сторону Бутово. Мы находим недалеко от ледового комплекса "Южный лёд"',
        'Проезд свободный. Главное без кошек',
        'Самостоятельно открывать выгулы и вольеры без разрешения работника приюта.
        Кормить животных. Этим Вы можете спровоцировать драку.
        Угощения разрешены только постоянным опекунам и волонтерам, во время прогулок с животными на поводке.
        Оставлять после себя мусор на территории приюта и прилегающей территории.
        Подходить близко к вольерам и гладить собак через сетку на выгулах. Животные могут быть агрессивны!
        Кричать, размахивать руками, бегать между будками или вольерами, пугать и дразнить животных.
        Посещение приюта для детей дошкольного и младшего школьного возраста без сопровождения взрослых.
        Нахождение на территории приюта детей среднего и старшего школьного возраста без  сопровождения взрослых или письменной справки-разрешения от родителей или законных представителей.
        Посещение приюта в состоянии алкогольного, наркотического опьянения.',
        'Перед началом поездки водителю необходимо разместить и закрепить животное так, чтобы избежать его падения или создания помех для движения.
        Питомец не должен загораживать обзор водителю, препятствовать управлению транспортного средства, нарушать его устойчивость.',
        'Постарайтесь проводить с животным больше времени в первые дни и даже месяцы вашей совместной жизни.
        На начальном этапе полезно взять отпуск или договориться со знакомыми или профессионалами, чтобы они навещали питомца.
        Это поможет ему проще адаптироваться к новому месту и режиму.
        Обеспечьте животному активную жизнь: приучите себя к качественным прогулкам и играм с собакой. Если вы живете в городе, постарайтесь включить в свои планы поездки на природу вместе с животным.',
        'Корми собаку и не обижай, она и так старая',
        '1.Последовательность. Нельзя запрещать собаке, например, есть со стола, но время от времени бросать ей какую-нибудь вкусность. Если на что-то стоит запрет, то этот запрет должен быть окончательным и бесповоротным, иначе животное просто будет ждать, пока вы дадите слабину, и постоянно клянчить в надежде, что вот сейчас-то точно! И такая последовательность должна быть во всём, иначе собака не воспримет команды всерьёз.
        2.Если дали команду, то ждите её выполнения. Даёте команду — дождитесь, пока собака её выполнит, а потом вознаградите за выполнение. Этот простой алгоритм должен быть отточен до автоматизма. Приказы, которые постоянно отменяются, обесцениваются в собачьих глазах.
        3.Сразу продумывайте все нюансы выполнения команды. Как вы научите собаку выполнять команду, так она и будет её выполнять. К примеру, если вы сквозь пальцы смотрите на неровную «посадку» при выполнении «Сидеть», то впоследствии животное будет не переучить — с собачьей точки зрения неважным будет, как именно сидеть, если основной принцип соблюдён. Тем более, что вас же всё устраивало.
        4.Будьте понятным собаке. Это — главное в дрессировке: научиться говорить с животным на его языке, а не требовать невозможного. Как только вы выучите «собачий», всё пойдёт как по маслу.
        5.Не обманывайте собаку. Языков приводит тлаакой пример: вам нужно сделать собаке укол, животное в ужасе забивается под диван, откуда его никак не вытащить, и тогда вы идёте на хитрость — произносите любимую питомцем команду «Гулять!». Собака радостно выскакивает из-под дивана, но вместо прогулки получает иголкой в холку. В следующий раз на команду «Гулять», даже произнесённую искренне, пёс будет реагировать с подозрением — и с полным на то правом.
        6.Собака не ошибается, ошибается хозяин. Если не получается научить любимца команде, значит, что-то делается неправильно. Животное не может ошибаться само по себе — это вы можете научить его неправильно. К примеру, вместо того, чтобы ходить у левой ноги, вы научили его бежать вприпрыжку (пытаясь дотянуться до лакомства для собак) или слегка опережая.
        7.Может, проблема в здоровье. Вспышки агрессии и непослушание могут быть признаками более комплексных проблем. Вместо того, чтобы наказывать животное, посетите с ним ветеринара.
        8.Будьте разумны в требованиях. Не нужно требовать, чтобы собака делала то, что явно за гранью её возможностей. Чем больше будет таких требований, тем сильнее упадёт доверие.
        9.Не доверяйте собаке на 100%. Помните, что она — животное, насколько бы идеально выдрессированной она ни была, и вы не в состоянии предсказать абсолютно всё. Даже если пёс научен ходить у ноги и мгновенно выполнять команды, берите его на поводок у шоссе и в потенциально опасных местах. Вдруг она увидит знакомого человека и бросится под колёса? Нельзя быть уверенным, что этого не случится, а одна ошибка может стоить животному жизни.
        10.Самое страшное — потерять уважение. Непоследовательность, обманы, жестокость, необоснованные наказания, завышенные требования — всё это рано или поздно приведёт к тому, что собака окончательно потеряет к вам доверие. Этого допустить нельзя никак.',
        'LIST OF CYNOLOGISTS PLACEHOLDER',
        'DISABILITY RECO PLACEHOLDER',
        'Не достаточно финансов для содержания животного, нет времени на собаку');

-- changeset m_yatsushko:5
INSERT INTO shelters (id, volunteer_chat_id, shelter_type, meeting_recommendations, how_to_get_pet,
                      documents_list, general_info, phone_number, schedule, address, how_to_get,
                      security_and_pass, safety, transporting_recommendations, home_recommendations_young,
                      home_recommendations_old,disability_recommendations, why_we_can_deny)
VALUES (1, 1, 'cats',
        'Важно, чтобы между вами и выбранным вами пушистым другом была связь.
        Вы должны убедиться, что новый член вашей семьи будет успешно адаптироваться к вашему образу жизни.
        Чтобы привести в дом подходящую кошку и убедиться, что ваш образ жизни идеален для вашего нового питомца, необходимы тщательные исследования и планирование.
        Большинство спасенных кошек, которые живут в приютах ждут, когда люди с большим сердцем подарят им новый дом, легко становятся любящими домашними животными,
        готовыми на все ради ласки и немного внимания. Но есть кошки, которым нужно больше времени, чтобы приспособиться, научиться доверять людям, и вам нужно убедиться, что у вас есть ресурсы,
        чтобы принять участие в ответственном усыновлении. Любая кошка, независимо от перенесенной травмы, может узнать, что такое любовь, если живет в окружении, полном любви.',
        'Мы отдаем животное строго по договору.
        Только в Санкт-Петербурге
        Мы отдаем животных только в квартиру,
        Мы проверяем условия содержания у будущих владельцев',
        'Список необходимых документов',
        'МИНИ-ПРИЮТ ДЛЯ КОШЕК "КОТ ВАСЯ"', '+79522339858',
        'У нас нет четкого графика работ',
        'Россия, Санкт-Петербург. Для уточнения адреса звоните',
        'Звоните нам и мы расскажем как добраться',
        'Проезд свободный',
        'Кормить животных. Этим Вы можете спровоцировать драку.
        Оставлять после себя мусор.
        Кричать, размахивать руками, бегать, пугать и дразнить животных.
        Посещение приюта для детей дошкольного и младшего школьного возраста без сопровождения взрослых.
        Посещение приюта в состоянии алкогольного, наркотического опьянения.',
        'Перед началом поездки водителю необходимо разместить и закрепить животное так, чтобы избежать его падения или создания помех для движения.
        Питомец не должен загораживать обзор водителю, препятствовать управлению транспортного средства, нарушать его устойчивость.',
        'Постарайтесь проводить с животным больше времени в первые дни и даже месяцы вашей совместной жизни.
        На начальном этапе полезно взять отпуск или договориться со знакомыми или профессионалами, чтобы они навещали питомца.
        Это поможет ему проще адаптироваться к новому месту и режиму.
        Обеспечьте животному активную жизнь: предоставьте котику необходимые игрушки. Играйте вместе',
        'Корми кошку и не обижай, и подчиняйся',
        'DISABILITY RECO PLACEHOLDER',
        'Не достаточно финансов для содержания животного, нет времени на кошку');
