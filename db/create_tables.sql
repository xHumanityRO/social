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
  insta_user_id VARCHAR(50) DEFAULT NULL,
  insta_access_token VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS campaign_video;
CREATE TABLE campaign_video (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL DEFAULT 0,
  campaign_id INT NOT NULL DEFAULT 0,
  source VARCHAR(50) DEFAULT '',
  link VARCHAR(500) DEFAULT '',
  entity_id VARCHAR(50) DEFAULT '',
  post_url VARCHAR(500) DEFAULT '',
  creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS instagram_insight;
CREATE TABLE instagram_insight (
  id INT NOT NULL AUTO_INCREMENT,
  media_url VARCHAR(500) DEFAULT '',
  likes INT DEFAULT 0,
  dislikes INT DEFAULT 0,
  creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS instagram_insight_comment;
CREATE TABLE instagram_insight_comment (
  id INT NOT NULL AUTO_INCREMENT,
  insight_id INT NOT NULL DEFAULT 0,
  comment_id BIGINT DEFAULT 0,
  message VARCHAR(5000) DEFAULT '',
  creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB;
