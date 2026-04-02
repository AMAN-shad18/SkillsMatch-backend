DataBase Schema 


-- Create database
CREATE DATABASE IF NOT EXISTS skills_bridge;
USE skills_bridge;

-- Users table
CREATE TABLE users (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
full_name VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
roll_number VARCHAR(50) NOT NULL UNIQUE,
department VARCHAR(100),
semester INT,
bio VARCHAR(500),
profile_pic_url VARCHAR(255),
role ENUM('STUDENT', 'TEACHER', 'ALUMNI', 'ADMIN') DEFAULT 'STUDENT',
is_verified BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
INDEX idx_department (department),
INDEX idx_email (email),
INDEX idx_roll_number (roll_number)
);

-- Skills table
CREATE TABLE skills (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL UNIQUE,
category VARCHAR(100),
description TEXT,
popularity_score INT DEFAULT 0,
INDEX idx_name (name),
INDEX idx_category (category)
);

-- User Skills junction table
CREATE TABLE user_skills (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT NOT NULL,
skill_id BIGINT NOT NULL,
skill_type ENUM('OFFERING', 'SEEKING') NOT NULL,
proficiency_level INT DEFAULT 1 CHECK (proficiency_level BETWEEN 1 AND 5),
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
UNIQUE KEY unique_user_skill_type (user_id, skill_id, skill_type),
INDEX idx_user_id (user_id),
INDEX idx_skill_id (skill_id)
);

-- Exchanges table
CREATE TABLE exchanges (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
requester_id BIGINT NOT NULL,
provider_id BIGINT NOT NULL,
skill_offered_id BIGINT NOT NULL,
skill_requested_id BIGINT NOT NULL,
status ENUM('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'REJECTED') DEFAULT 'PENDING',
session_date TIMESTAMP NULL,
session_link VARCHAR(255),
rating INT CHECK (rating BETWEEN 1 AND 5),
feedback TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
completed_at TIMESTAMP NULL,
FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (skill_offered_id) REFERENCES skills(id) ON DELETE CASCADE,
FOREIGN KEY (skill_requested_id) REFERENCES skills(id) ON DELETE CASCADE,
INDEX idx_status (status),
INDEX idx_requester (requester_id),
INDEX idx_provider (provider_id)
);

-- Notifications table
CREATE TABLE notifications (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT NOT NULL,
type ENUM('EXCHANGE_REQUEST', 'EXCHANGE_ACCEPTED', 'EXCHANGE_REJECTED', 'EXCHANGE_CANCELLED', 'EXCHANGE_COMPLETED', 'NEW_MESSAGE', 'REVIEW_RECEIVED', 'SYSTEM_ALERT') NOT NULL,
message VARCHAR(500) NOT NULL,
is_read BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
INDEX idx_user_read (user_id, is_read),
INDEX idx_created_at (created_at)
);

-- Messages table
CREATE TABLE messages (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
sender_id BIGINT NOT NULL,
receiver_id BIGINT NOT NULL,
exchange_id BIGINT,
content TEXT NOT NULL,
is_read BOOLEAN DEFAULT FALSE,
timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (exchange_id) REFERENCES exchanges(id) ON DELETE SET NULL,
INDEX idx_exchange (exchange_id),
INDEX idx_sender_receiver (sender_id, receiver_id),
INDEX idx_timestamp (timestamp)
);

-- Reviews table
CREATE TABLE reviews (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
reviewer_id BIGINT NOT NULL,
reviewee_id BIGINT NOT NULL,
exchange_id BIGINT NOT NULL UNIQUE,
rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
comment VARCHAR(500),
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (reviewee_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (exchange_id) REFERENCES exchanges(id) ON DELETE CASCADE,
INDEX idx_reviewee (reviewee_id),
INDEX idx_reviewer (reviewer_id)
);


API Design 


Design ALL endpoints before coding:
├── Authentication API
│   ├── POST /api/auth/register
│   ├── POST /api/auth/login
│   ├── POST /api/auth/refresh-token
│   ├── POST /api/auth/verify-email
│   └── POST /api/auth/forgot-password
│
├── Users API
│   ├── GET /api/users/profile
│   ├── PUT /api/users/profile
│   ├── GET /api/users/{id}
│   ├── GET /api/users/search
│   └── GET /api/users/matches
│
├── Skills API
│   ├── GET /api/skills
│   ├── POST /api/skills
│   ├── GET /api/skills/categories
│   └── GET /api/skills/popular
│
├── Exchanges API
│   ├── POST /api/exchanges/request
│   ├── GET /api/exchanges/pending
│   ├── PUT /api/exchanges/{id}/respond
│   ├── GET /api/exchanges/history
│   └── POST /api/exchanges/{id}/rate
│
└── Analytics API
├── GET /api/analytics/skills-demand
├── GET /api/analytics/user-stats
└── GET /api/analytics/exchanges-trend