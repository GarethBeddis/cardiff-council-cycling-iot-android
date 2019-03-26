DROP SCHEMA IF EXISTS cycling_web;

CREATE SCHEMA IF NOT EXISTS cycling_web;

USE cycling_web;

-- MySQL Workbench Forward Engineering

-- -----------------------------------------------------
-- Table user
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS user (
	`user_id` INT NOT NULL AUTO_INCREMENT,
	`email` VARCHAR(50) NOT NULL,
	`password` VARCHAR(20) NOT NULL,
    `share_Readings` BOOLEAN,
	PRIMARY KEY (`user_id`),
	UNIQUE INDEX `id_UNIQUE` (`user_id` ASC))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table journey user takes 
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS journey (
	`journey_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
		PRIMARY KEY (`journey_id`),
		FOREIGN KEY (`user_id`)
        REFERENCES user (`user_id`)
			ON DELETE NO ACTION
			ON UPDATE NO ACTION )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table user readings
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS measurements (
	`user_id` INT NOT NULL,
	`journey_id` INT NOT NULL,
    `dB_reading` FLOAT, 
    `NO2_reading` FLOAT, 
	`PM10_reading` FLOAT,
    `PM2.5_reading` FLOAT,
    `time_taken` DATETIME NOT NULL,
    `longitude` FLOAT NOT NULL,
    `latitude` FLOAT NOT NULL,
		FOREIGN KEY (`user_id`)
        REFERENCES user (`user_id`)
			ON DELETE NO ACTION
			ON UPDATE NO ACTION,
        FOREIGN KEY (`journey_id`)
        REFERENCES journey (`journey_id`))
ENGINE = InnoDB;
    
    
	
	