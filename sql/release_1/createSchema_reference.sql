SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `w2_reference`;
CREATE SCHEMA IF NOT EXISTS `w2_reference` DEFAULT CHARACTER SET utf8 ;
USE `w2_reference` ;


-- -----------------------------------------------------
-- Table `w2_reference`.`Country`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`Country` (
  `asccssCode` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `id` BIGINT(20) NOT NULL DEFAULT '0' ,
  `ishVersion` BIGINT(11) NULL DEFAULT NULL ,
  `isoCodeAlpha2` CHAR(3) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isoCodeAlpha3` CHAR(3) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isoCodeNumeric` INT(11) NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `saccCode` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_uniq_idx` (`name` ASC) ,
  INDEX `ishVersion` (`ishVersion` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `w2_reference`.`Language`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`Language` (
  `absCode` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `id` BIGINT(20) NOT NULL DEFAULT '0' ,
  `isActive` TINYINT(4) NOT NULL DEFAULT '0' ,
  `ishVersion` BIGINT(11) NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ishVersion` (`ishVersion` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `w2_reference`.`Qualification`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`Qualification` (
  `ishVersion` BIGINT(11) NULL DEFAULT '0' ,
  `id` BIGINT(20) NOT NULL DEFAULT '0' ,
  `nationalCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `level` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `title` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `anzsco` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `anzsic` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `asco` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `fieldOfStudy` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `fieldOfEducation` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `nominalHours` FLOAT NULL DEFAULT NULL ,
  `reviewDate` DATE NULL DEFAULT NULL ,
  `isAccreditedCourse` TINYINT(4) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `newApprenticeship` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `trainingPackageId` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ishVersion` (`ishVersion` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `w2_reference`.`TrainingPackage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`TrainingPackage` (
  `ishVersion` BIGINT(11) NULL DEFAULT '0' ,
  `id` BIGINT(20) NOT NULL DEFAULT '0' ,
  `nationalISC` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `title` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `copyrightCategory` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `copyrightContact` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `developer` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `endorsementFrom` DATETIME NULL DEFAULT NULL ,
  `endorsementTo` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `purchaseFrom` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `type` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ishVersion` (`ishVersion` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `w2_reference`.`postcode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`postcode` (
  `ishVersion` BIGINT(11) NULL DEFAULT NULL ,
  `postcode` SMALLINT(6) NOT NULL DEFAULT '0' ,
  `locality` VARCHAR(60) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `state` CHAR(3) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `comments` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `deliveryOffice` VARCHAR(60) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `presortIndicator` SMALLINT(6) NOT NULL DEFAULT '0' ,
  `parcelZone` CHAR(3) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `BSPnumber` SMALLINT(6) NOT NULL DEFAULT '0' ,
  `BSPname` VARCHAR(60) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  `category` VARCHAR(60) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT '' ,
  INDEX `postcode` (`postcode` ASC, `locality` ASC, `state` ASC, `comments` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `w2_reference`.`postcode_db`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`postcode_db` (
  `postcode` INT(4) UNSIGNED NOT NULL ,
  `suburb` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `state` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `dc` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `lat` DOUBLE NOT NULL DEFAULT '0' ,
  `lon` DOUBLE NOT NULL DEFAULT '0' ,
  PRIMARY KEY (`postcode`, `suburb`) ,
  INDEX `idx_lon` (`lon` ASC) ,
  INDEX `idx_lat` (`lat` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
