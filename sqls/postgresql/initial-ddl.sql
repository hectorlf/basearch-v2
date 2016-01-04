-- Sequences
create sequence jpa_seq start with 100;

-- Table: languages
CREATE TABLE languages
(
  id integer NOT NULL,
  language character varying(3) NOT NULL,
  region character varying(3),
  variant character varying(8),
  CONSTRAINT languages_pk PRIMARY KEY (id),
  CONSTRAINT locale_unique UNIQUE (language, region, variant)
)
WITH (
  OIDS=FALSE
);

-- Table: users
CREATE TABLE users
(
  id integer NOT NULL,
  username character varying(50) NOT NULL,
  password character varying(60) NOT NULL,
  enabled boolean NOT NULL,
  language_id integer NOT NULL,
  CONSTRAINT users_pk PRIMARY KEY (id),
  CONSTRAINT users_languages_fk FOREIGN KEY (language_id)
      REFERENCES languages (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT username_unique UNIQUE (username)
)
WITH (
  OIDS=FALSE
);

-- Table: authorities
CREATE TABLE authorities
(
  id integer NOT NULL,
  username character varying(50) NOT NULL,
  authority character varying(50) NOT NULL,
  CONSTRAINT authorities_pk PRIMARY KEY (id),
  CONSTRAINT authorities_users_fk FOREIGN KEY (username)
      REFERENCES users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT username_authority_unique UNIQUE (username, authority)
)
WITH (
  OIDS=FALSE
);

