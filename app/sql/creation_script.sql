DROP SCHEMA cycling;

CREATE SCHEMA IF NOT EXISTS cycling;

USE cycling;

-- MySQL Workbench Forward Engineering

-- -----------------------------------------------------
-- Table user
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS user (
	`user_id` INT NOT NULL AUTO_INCREMENT,
	`email` VARCHAR(50) NOT NULL,
	`password` VARCHAR(20) NOT NULL,
	PRIMARY KEY (`user_id`),
	UNIQUE INDEX `id_UNIQUE` (`user_id` ASC))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table user settings
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS user_settings (
  `user_id` INT NOT NULL,
  `BLUETOOTH` BOOLEAN NOT NULL,
  `SHARE_SYNC` BOOLEAN NOT NULL,
  `GPS` BOOLEAN NOT NULL,
    FOREIGN KEY (`user_id`)
    REFERENCES user (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table start location of user
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS start_location (
	`start_id` INT NOT NULL AUTO_INCREMENT,
    `longitude` INT NOT NULL,
    `latitude` INT NOT NULL,
    PRIMARY KEY (`start_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS end_location (
	`end_id` INT NOT NULL AUTO_INCREMENT,
    `longitude` INT NOT NULL,
    `latitude` INT NOT NULL,
    PRIMARY KEY (`end_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS location (
	`location_id` INT NOT NULL AUTO_INCREMENT,
    `start_id` INT NOT NULL,
    `end_id` INT NOT NULL,
    `location_coordinates` INT NOT NULL,
		PRIMARY KEY (`location_id`),
		FOREIGN KEY(`start_id`)
		REFERENCES start_location (`start_id`),
		FOREIGN KEY (`end_id`)
		REFERENCES end_location (`end_id`)
			ON DELETE NO ACTION
			ON UPDATE NO ACTION )
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS journey (
	`journey_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
    `location_id` INT NOT NULL,
		PRIMARY KEY (`journey_id`),
		FOREIGN KEY (`user_id`)
        REFERENCES user (`user_id`),
        FOREIGN KEY (`location_id`)
        REFERENCES location (`location_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION )
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS air_measurements (
	`air_id` INT NOT NULL AUTO_INCREMENT,
    `NO2` FLOAT,
    `PM10` FLOAT,
    `PM2.5` FLOAT,
		PRIMARY KEY (`air_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS noise_measurements (
	`noise_id` INT NOT NULL AUTO_INCREMENT,
    `dB` FLOAT,
		PRIMARY KEY (`noise_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS readings (
	`user_id` INT NOT NULL,
	`air_id` INT NOT NULL,
    `noise_id` INT NOT NULL, 
    `length` FLOAT NOT NULL, 
	`location_id` INT NOT NULL,
		FOREIGN KEY (`user_id`)
        REFERENCES user (`user_id`),
        FOREIGN KEY (`air_id`)
        REFERENCES air_measurements (`air_id`),
        FOREIGN KEY (`noise_id`)
        REFERENCES noise_measurements (`noise_id`),
        FOREIGN KEY (`location_id`)
        REFERENCES location (`location_id`)
	        ON DELETE NO ACTION
			ON UPDATE NO ACTION )
	ENGINE = InnoDB;
    
    
	
	