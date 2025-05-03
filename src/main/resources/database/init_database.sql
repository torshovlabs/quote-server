# CREATE DATABASE `quote-db`;

-- Create app_user table
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL
);

-- Create group table (using backticks because 'group' is a reserved word)
CREATE TABLE `group` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL
);

-- Create groupmembership table
CREATE TABLE groupmembership (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 group_id BIGINT NOT NULL,
                                 queue_number INT NOT NULL,
                                 FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                 FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE,
                                 UNIQUE KEY unique_membership (user_id, group_id)
);

-- Create quote table
CREATE TABLE quote (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       quote TEXT NOT NULL,
                       author VARCHAR(100) NOT NULL,
                       user_id BIGINT NOT NULL,
                       group_id BIGINT NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                       FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE
);