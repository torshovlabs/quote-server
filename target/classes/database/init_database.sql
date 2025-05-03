# CREATE DATABASE `quote-db`;

-- Create app_user table with UUID as primary key
CREATE TABLE app_user (
                          id VARCHAR(36) PRIMARY KEY,  -- UUID stored as string
                          name VARCHAR(100) NOT NULL UNIQUE,  -- Username is unique and indexed
                          INDEX idx_username (name)  -- Index for fast username lookups
);

-- Create group table
CREATE TABLE `group` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         created_by VARCHAR(36) NOT NULL,
                         FOREIGN KEY (created_by) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE TABLE group_membership (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id VARCHAR(36) NOT NULL,  -- References UUID
                                  group_id BIGINT NOT NULL,
                                  queue_number INT NOT NULL,
                                  FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                  FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE,
                                  UNIQUE KEY unique_membership (user_id, group_id)
);

-- Create quote table - joins on UUID
CREATE TABLE quote (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       quote TEXT NOT NULL,
                       author VARCHAR(100) NOT NULL,
                       user_id VARCHAR(36) NOT NULL,  -- References UUID
                       group_id BIGINT NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                       FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE
);