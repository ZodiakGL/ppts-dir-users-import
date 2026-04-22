CREATE SCHEMA IF NOT EXISTS sys1;
SET search_path TO sys1;


CREATE TABLE palette_colors (
    id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    code varchar NOT NULL,
    "name" varchar NOT NULL,
    value int4 NOT NULL,
    entity varchar NOT NULL,
    CONSTRAINT palette_colors_code_key UNIQUE (code),
    CONSTRAINT palette_colors_pkey PRIMARY KEY (id)
);
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-magenta-user', 'Special/Static Light Magenta', 16691157, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-purple-user', 'Special/Static Light Purple', 15642344, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-violet-user', 'Special/Static Light Violet', 12497146, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-turquoise-user', 'Special/Static Light Turquoise', 10083812, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-green-user', 'Special/Static Light Green', 10608552, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-lime-user', 'Special/Static Light Lime', 14084500, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-yellow-user', 'Special/Static Light Yellow', 16767913, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-orange-user', 'Special/Static Light Orange', 16630698, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-red-user', 'Special/Static Light Red', 15969195, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-magenta-user', 'Special/Static Magenta', 15621504, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-purple-user', 'Special/Static Purple', 13524450, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-cyan-user', 'Special/Static Cyan', 4628991, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-turquoise-user', 'Special/Static Turquoise', 1557969, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-green-user', 'Special/Static Green', 4047955, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-lime-user', 'Special/Static Lime', 12507667, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-yellow-user', 'Special/Static Yellow', 16760896, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-orange-user', 'Special/Static Orange', 16551238, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-magenta-user', 'Special/Static Dark Magenta', 11551838, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-violet-user', 'Special/Static Dark Violet', 6439613, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-blue-user', 'Special/Static Dark Blue', 3952825, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-cyan-user', 'Special/Static Dark Cyan', 3438783, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-turquoise-user', 'Special/Static Dark Turquoise', 1016458, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-green-user', 'Special/Static Dark Green', 2787129, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-red-user', 'Special/Static Dark Red', 13187640, 'USER');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-magenta-area', 'Special/Static Light Magenta', 16691157, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-purple-area', 'Special/Static Light Purple', 15642344, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-violet-area', 'Special/Static Light Violet', 12497146, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-turquoise-area', 'Special/Static Light Turquoise', 10083812, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-green-area', 'Special/Static Light Green', 10608552, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-lime-area', 'Special/Static Light Lime', 14084500, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-yellow-area', 'Special/Static Light Yellow', 16767913, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-orange-area', 'Special/Static Light Orange', 16630698, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-light-red-area', 'Special/Static Light Red', 15969195, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-magenta-area', 'Special/Static Magenta', 15621504, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-purple-area', 'Special/Static Purple', 13524450, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-cyan-area', 'Special/Static Cyan', 4628991, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-turquoise-area', 'Special/Static Turquoise', 1557969, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-green-area', 'Special/Static Green', 4047955, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-lime-area', 'Special/Static Lime', 12507667, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-yellow-area', 'Special/Static Yellow', 16760896, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-orange-area', 'Special/Static Orange', 16551238, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-magenta-area', 'Special/Static Dark Magenta', 11551838, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-violet-area', 'Special/Static Dark Violet', 6439613, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-blue-area', 'Special/Static Dark Blue', 3952825, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-cyan-area', 'Special/Static Dark Cyan', 3438783, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-turquoise-area', 'Special/Static Dark Turquoise', 1016458, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-green-area', 'Special/Static Dark Green', 2787129, 'AREA');
INSERT INTO palette_colors
(code, "name", value, entity)
VALUES('static-dark-red-area', 'Special/Static Dark Red', 13187640, 'AREA');


CREATE TABLE users (
    id int8 NOT NULL GENERATED ALWAYS AS IDENTITY,
    login varchar NOT NULL,
    first_name varchar NULL,
    patronymic varchar NULL,
    last_name varchar NULL,
    email varchar NULL,
    icon_id int4 NULL,
    color varchar NULL,
    locale varchar(10) NULL,
    timezone varchar(50) NULL,
    activity_status varchar(1) NOT NULL DEFAULT 'A'::character varying,
    CONSTRAINT users_login_key UNIQUE (login),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);
CREATE INDEX users_full_name_idx ON users USING btree (first_name);
CREATE INDEX users_last_name_idx ON users USING btree (last_name);

INSERT INTO users(login, first_name, last_name, activity_status)
values('sfera_tech_user', 'Пользователь', 'Системный', 'A')