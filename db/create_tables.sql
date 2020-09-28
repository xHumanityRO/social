DROP TABLE IF EXISTS telegram_users;
CREATE TABLE telegram_users (
  id INT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 0,
  chat_id BIGINT NOT NULL DEFAULT 0,
  username VARCHAR(50) DEFAULT '',
  user_password VARCHAR(128) DEFAULT '',
  first_name VARCHAR(50) DEFAULT '',
  last_name VARCHAR(50) DEFAULT '',
  phone_number VARCHAR(20) DEFAULT '',
  forum_user_id INT DEFAULT 0,
  forum_username VARCHAR(50) DEFAULT '',
  forum_email VARCHAR(50) DEFAULT '',
  user_lastvisit DATETIME DEFAULT NULL,
  user_regdate DATETIME DEFAULT NULL,
  fb_access_token VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;
