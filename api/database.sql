  DROP DATABASE IF EXISTS wordbird;
  CREATE DATABASE wordbird;
  USE  wordbird;

  CREATE TABLE  `users` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `imei` varchar(16) NOT NULL,
    `api_key` varchar(10) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `imei` (`imei`),
    UNIQUE KEY `api_key` (`api_key`)
  );

  INSERT INTO users (id,name,imei,api_key) VALUES ('1','WordBirdGrabber','1234567890','abcd1234');

  CREATE TABLE `results` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `result` text NOT NULL,
    PRIMARY KEY (`id`)
  );

CREATE TABLE  url_index(
  id INT NOT NULL AUTO_INCREMENT,
  url TEXT NOT NULL,
  is_indexed TINYINT(4) NOT NULL DEFAULT 0,
  total_words INT(11) NOT NULL DEFAULT 0,
  time_elapsed_to_first_index_in_sec INT(11) DEFAULT NULL,
  last_indexed_at INT(11) DEFAULT NULL ,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

  CREATE TABLE `requests` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `url_id` INT(11),
    `word` varchar(50) NOT NULL,
    `type` enum('Synonym','Opposite','Meaning','Rhyme','Sentence','Plural','Singular','Past','Present','Start','End','Contain') NOT NULL,
    `result_id` int(11) DEFAULT NULL,
    `is_success` tinyint(4) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (url_id) REFERENCES url_index(id) ON UPDATE CASCADE ON DELETE CASCADE
  );



CREATE TABLE  `preference` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`_key` varchar(100) NOT NULL,
`_value` text NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `_key` (`_key`)
);

INSERT INTO preference (_key,_value) VALUES ('grabber_user_id','1');



SELECT
ui.id,ui.url,ui.is_indexed,ui.time_elapsed_to_first_index_in_sec, ui.last_indexed_at , GROUP_CONCAT(r.word)
FROM url_index ui
LEFT JOIN requests r ON r.url_id = ui.id
WHERE %s = ? GROUP BY ui.id LIMIT 1;

