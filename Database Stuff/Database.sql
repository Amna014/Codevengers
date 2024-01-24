CREATE DATABASE job_application;

USE job_application;

CREATE TABLE `job_seekers`(
  `job_seeker_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `field_of_interest` VARCHAR(255) DEFAULT NULL,
  `phone_no` VARCHAR(20) DEFAULT NULL
);

CREATE TABLE `employers` (
  `employer_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `company_name` VARCHAR(255) DEFAULT NULL
); 

CREATE TABLE `jobs` (
  `job_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
  `employer_id` INT(11) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `designation` VARCHAR(255) DEFAULT NULL,
  `experience_required` INT(11) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `salary` DECIMAL(10,2) DEFAULT NULL,
  `posted_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  FOREIGN KEY (employer_id) REFERENCES employers(employer_id)
);

CREATE TABLE `applications` (
  `application_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
  `job_id` INT(11) NOT NULL,
  `job_seeker_id` INT(11) NOT NULL,
  `application_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `status` ENUM('pending','accepted','rejected') DEFAULT 'pending',
  `email` VARCHAR(255) NOT NULL,
  `phone_no` VARCHAR(20) NOT NULL,
  `experience` INT(11) DEFAULT NULL,
  `expected_salary` DECIMAL(10,2) DEFAULT NULL,
  `cv` VARCHAR(255) DEFAULT NULL,
  FOREIGN KEY (job_id) REFERENCES jobs(job_id),
  FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(job_seeker_id)
);

CREATE TABLE `connections` (
  `connection_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
  `sender_id` INT(11) NOT NULL,
  `receiver_id` INT(11) NOT NULL,
  `status` ENUM('pending','accepted','rejected') DEFAULT 'pending',
  `date_connected` DATETIME DEFAULT NULL,
  FOREIGN KEY (sender_id) REFERENCES job_seekers(job_seeker_id),
  FOREIGN KEY (receiver_id) REFERENCES job_seekers(job_seeker_id)
);
