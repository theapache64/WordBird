DROP DATABASE IF EXISTS wordbird;
CREATE DATABASE wordbird;
USE wordbird;

CREATE TABLE `users` (
  `id`         INT(11)      NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(100) NOT NULL,
  `imei`       VARCHAR(16)  NOT NULL,
  `api_key`    VARCHAR(10)  NOT NULL,
  `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `imei` (`imei`),
  UNIQUE KEY `api_key` (`api_key`)
);

INSERT INTO users (id, name, imei, api_key) VALUES ('1', 'WordBirdGrabber', '1234567890', 'abcd1234');

CREATE TABLE `results` (
  `id`     INT(11) NOT NULL AUTO_INCREMENT,
  `result` TEXT    NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE url_index (
  id                                 INT        NOT NULL AUTO_INCREMENT,
  url                                TEXT       NOT NULL,
  is_indexed                         TINYINT(4) NOT NULL DEFAULT 0,
  total_words                        INT(11)    NOT NULL DEFAULT 0,
  time_elapsed_to_first_index_in_sec INT(11)             DEFAULT NULL,
  last_indexed_at                    BIGINT              DEFAULT NULL,
  created_at                         TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE `requests` (
  `id`         INT(11)                                                                                                                          NOT NULL AUTO_INCREMENT,
  `user_id`    INT(11)                                                                                                                          NOT NULL,
  `url_id`     INT(11),
  `word`       VARCHAR(50)                                                                                                                      NOT NULL,
  `type`       ENUM ('Synonym', 'Opposite', 'Meaning', 'Rhyme', 'Sentence', 'Plural', 'Singular', 'Past', 'Present', 'Start', 'End', 'Contain') NOT NULL,
  `result_id`  INT(11)                                                                                                                                   DEFAULT NULL,
  `created_at` TIMESTAMP                                                                                                                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (url_id) REFERENCES url_index (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE `preference` (
  `id`     INT(11)      NOT NULL AUTO_INCREMENT,
  `_key`   VARCHAR(100) NOT NULL,
  `_value` TEXT         NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `_key` (`_key`)
);

INSERT INTO preference (_key, _value) VALUES
  ('grabber_user_id', '1'),
  ('gmail_username', 'mymailer64@gmail.com'),
  ('gmail_password', 'mypassword64'),
  ('admin_email', 'theapache64@gmail.com');