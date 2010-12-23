SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `willow_binary` DEFAULT CHARACTER SET latin1 ;
CREATE SCHEMA IF NOT EXISTS `willow_college` DEFAULT CHARACTER SET latin1 ;
CREATE SCHEMA IF NOT EXISTS `willow_reference` DEFAULT CHARACTER SET latin1 ;
USE `willow_binary` ;

-- -----------------------------------------------------
-- Table `willow_binary`.`BinaryData`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_binary`.`BinaryData` (
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `binaryInfoId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `content` LONGBLOB NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `id` BIGINT(20) NOT NULL ,
  `isDeleted` SMALLINT(6) NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `angelId_collegeId_uniq_idx` (`angelId` ASC, `collegeId` ASC) ,
  INDEX `binaryInfoId` (`binaryInfoId` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

USE `willow_college` ;

-- -----------------------------------------------------
-- Table `willow_college`.`College`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`College` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isWebServicePaymentsEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `isWebSitePaymentsEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `isTestingWebServicePayments` TINYINT(1) NULL DEFAULT NULL ,
  `isTestingWebSitePayments` TINYINT(1) NULL DEFAULT NULL ,
  `communication_key` BIGINT(20) NULL DEFAULT NULL ,
  `requiresAvetmiss` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `nationalProviderCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `billingCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `paymentGatewayAccount` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `paymentGatewayPass` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `paymentGatewayType` INT(11) NULL DEFAULT NULL ,
  `webServicesLogin` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `webServicesPass` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `webServicesSecurityCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `firstRemoteAuthentication` DATETIME NULL DEFAULT NULL ,
  `lastRemoteAuthentication` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `webServicesSecurityCode_uniq_idx` (`webServicesSecurityCode` ASC) ,
  UNIQUE INDEX `billingCode_uniq_idx` (`billingCode` ASC) ,
  INDEX `webServicesLogin_idx` (`webServicesLogin` ASC, `webServicesPass` ASC, `isDeleted` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 300
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Student`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Student` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `countryOfBirthId` BIGINT(20) NULL DEFAULT NULL ,
  `languageId` BIGINT(20) NULL DEFAULT NULL ,
  `concessionType` INT(11) NULL DEFAULT NULL ,
  `disabilityType` INT(11) NULL DEFAULT NULL ,
  `englishProficiency` INT(11) NULL DEFAULT NULL ,
  `highestSchoolLevel` INT(11) NULL DEFAULT NULL ,
  `indigenousStatus` INT(11) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isOverseasClient` TINYINT(1) NULL DEFAULT NULL ,
  `isStillAtSchool` TINYINT(1) NULL DEFAULT NULL ,
  `priorEducationCode` INT(11) NULL DEFAULT NULL ,
  `yearSchoolCompleted` INT(11) NULL DEFAULT NULL ,
  `labourForceType` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Student_ibfk_1` (`collegeId` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 1189147
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Course`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Course` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `code` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
  `isVETCourse` TINYINT(1) NULL DEFAULT NULL ,
  `isSufficientForQualification` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `allowWaitingList` TINYINT(1) NULL DEFAULT NULL ,
  `nominalHours` FLOAT NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `fieldOfEducation` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `searchText` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `isWebVisible_idx` (`isWebVisible` ASC) ,
  INDEX `code_idx` (`code` ASC) ,
  CONSTRAINT `Course_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186952
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Site`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Site` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT '0' ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `drivingDirections` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `drivingDirections_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `latitude` DECIMAL(12,8) NULL DEFAULT NULL ,
  `longitude` DECIMAL(12,8) NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `postcode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `publicTransportDirections` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `publicTransportDirections_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `specialInstructions` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `specialInstructions_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `state` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `street` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `suburb` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `timeZone` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Site_ibfk_1` (`collegeId` ASC) ,
  CONSTRAINT `Site_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181610
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Room`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Room` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `siteId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `capacity` INT(11) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `directions` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `directions_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `facilities` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `facilities_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Room_ibfk_1` (`collegeId` ASC) ,
  INDEX `Room_ibfk_2` (`siteId` ASC) ,
  CONSTRAINT `Room_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Room_ibfk_2`
    FOREIGN KEY (`siteId` )
    REFERENCES `willow_college`.`Site` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181746
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`CourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`CourseClass` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `code` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `courseId` BIGINT(20) NULL DEFAULT NULL ,
  `roomId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
  `isCancelled` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `maximumPlaces` INT(11) NULL DEFAULT NULL ,
  `minimumPlaces` INT(11) NULL DEFAULT NULL ,
  `countOfSessions` INT(11) NULL DEFAULT NULL ,
  `deliveryMode` INT(11) NULL DEFAULT NULL ,
  `feeExGst` DECIMAL(14,2) NULL DEFAULT NULL ,
  `feeGst` DECIMAL(14,2) NULL DEFAULT NULL ,
  `minutesPerSession` INT(11) NULL DEFAULT NULL ,
  `startingMinutePerSession` INT(11) NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `materials` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `materials_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `sessionDetail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `sessionDetail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `timeZone` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `CourseClass_ibfk_1` (`collegeId` ASC) ,
  INDEX `CourseClass_ibfk_2` (`courseId` ASC) ,
  INDEX `CourseClass_ibfk_3` (`roomId` ASC) ,
  INDEX `code_idx` (`code` ASC) ,
  INDEX `endDate_idx` (`endDate` ASC) ,
  INDEX `startDate_idx` (`startDate` ASC) ,
  CONSTRAINT `CourseClass_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `CourseClass_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `willow_college`.`Course` (`id` ),
  CONSTRAINT `CourseClass_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `willow_college`.`Room` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186958
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Tutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Tutor` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `finishDate` DATETIME NULL DEFAULT NULL ,
  `resume` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `resume_textile` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Tutor_ibfk_1` (`collegeId` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 1185782
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Session`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Session` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `courseClassId` BIGINT(20) NULL DEFAULT NULL ,
  `roomId` BIGINT(20) NULL DEFAULT NULL ,
  `markerId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `timeZone` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Session_ibfk_1` (`collegeId` ASC) ,
  INDEX `Session_ibfk_2` (`courseClassId` ASC) ,
  INDEX `Session_ibfk_3` (`roomId` ASC) ,
  INDEX `Session_ibfk_4` (`markerId` ASC) ,
  INDEX `Session_startDate_idx` (`startDate` ASC) ,
  INDEX `Session_endDate_idx` (`endDate` ASC) ,
  CONSTRAINT `Session_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `willow_college`.`CourseClass` (`id` ),
  CONSTRAINT `Session_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `willow_college`.`Room` (`id` ),
  CONSTRAINT `Session_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186969
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Attendance`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Attendance` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `sessionId` BIGINT(20) NULL DEFAULT NULL ,
  `attendanceType` INT(11) NULL DEFAULT NULL ,
  `markerId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `Attendance_ibfk_2` (`studentId` ASC) ,
  INDEX `Attendance_ibfk_3` (`sessionId` ASC) ,
  INDEX `Attendance_ibfk_4` (`markerId` ASC) ,
  CONSTRAINT `Attendance_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Attendance_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ),
  CONSTRAINT `Attendance_ibfk_3`
    FOREIGN KEY (`sessionId` )
    REFERENCES `willow_college`.`Session` (`id` ),
  CONSTRAINT `Attendance_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 657187
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`BinaryInfo`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`BinaryInfo` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `name` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `referenceNumber` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `byteSize` BIGINT(20) NULL DEFAULT NULL ,
  `mimeType` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `pixelHeight` INT(11) NULL DEFAULT NULL ,
  `pixelWidth` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `BinaryInfo_ibfk_1` (`collegeId` ASC) ,
  INDEX `name_idx` (`name`(255) ASC) ,
  INDEX `referenceNumber_idx` (`referenceNumber` ASC) ,
  INDEX `mimeType_idx` (`mimeType`(255) ASC) ,
  CONSTRAINT `BinaryInfo_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181948
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`BinaryInfoRelation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`BinaryInfoRelation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `binaryInfoId` BIGINT(20) NOT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `entityAngelId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `BinaryInfoRelation_ibfk_2` (`binaryInfoId` ASC) ,
  INDEX `entityIdentifier_idx` (`entityIdentifier` ASC) ,
  INDEX `entityWillowId_idx` (`entityWillowId` ASC) ,
  CONSTRAINT `BinaryInfoRelation_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `BinaryInfoRelation_ibfk_2`
    FOREIGN KEY (`binaryInfoId` )
    REFERENCES `willow_college`.`BinaryInfo` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 679
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Certificate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Certificate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `certificateNumber` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isQualification` TINYINT(1) NULL DEFAULT NULL ,
  `fundingSource` INT(11) NULL DEFAULT NULL ,
  `printedWhen` DATETIME NULL DEFAULT NULL ,
  `revokedWhen` DATETIME NULL DEFAULT NULL ,
  `studentFirstName` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `studentLastName` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `privateNotes` VARCHAR(1000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `publicNotes` VARCHAR(1000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `Certificate_ibfk_2` (`studentId` ASC) ,
  CONSTRAINT `Certificate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Certificate_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 930
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_reference`.`Module`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`Module` (
  `ishVersion` BIGINT(11) NOT NULL ,
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `nationalCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `title` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `disciplineCode` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `fieldOfEducation` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isModule` TINYINT(4) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `trainingPackageId` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `nationalcode` (`nationalCode` ASC) ,
  INDEX `ishVersion` (`ishVersion` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 1110427
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------
-- Table `willow_college`.`Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Discount` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `code` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `validFrom` DATETIME NULL DEFAULT NULL ,
  `validTo` DATETIME NULL DEFAULT NULL ,
  `combinationType` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `discountAmount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `discountRate` DECIMAL(4,2) NULL DEFAULT NULL ,
  `isCodeRequired` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `maximumDiscount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `minimumDiscount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `name` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `roundingMode` INT(11) NULL DEFAULT NULL ,
  `studentAge` INT(11) NULL DEFAULT NULL ,
  `studentAgeOperator` VARCHAR(2) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `studentEnrolledWithinDays` INT(11) NULL DEFAULT NULL ,
  `studentPostcodes` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `studentsQualifier` MEDIUMBLOB NULL DEFAULT NULL ,
  `timeZone` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `code_idx` (`collegeId` ASC, `code` ASC) ,
  INDEX `validTo_validFrom_idx` (`collegeId` ASC, `validTo` ASC, `validFrom` ASC) ,
  CONSTRAINT `Discount_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 12416
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Enrolment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Enrolment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseClassId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `reasonForStudy` INT(11) NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` ENUM('Pending', 'In Transaction', 'Success', 'Failed', 'Cancelled') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT 'Pending' ,
  `statusNotes` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `Enrolment_ibfk_2` (`courseClassId` ASC) ,
  INDEX `Enrolment_ibfk_3` (`studentId` ASC) ,
  INDEX `Enrolment_ibfk_4` (`discountId` ASC) ,
  CONSTRAINT `Enrolment_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Enrolment_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `willow_college`.`CourseClass` (`id` ),
  CONSTRAINT `Enrolment_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ),
  CONSTRAINT `Enrolment_ibfk_4`
    FOREIGN KEY (`discountId` )
    REFERENCES `willow_college`.`Discount` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140104
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Outcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Outcome` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NULL DEFAULT NULL ,
  `enrolmentId` BIGINT(20) NOT NULL ,
  `priorLearningId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `deliveryMode` INT(11) NULL DEFAULT NULL ,
  `fundingSource` INT(11) NULL DEFAULT NULL ,
  `reportableHours` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `Outcome_ibfk_2` (`moduleId` ASC) ,
  INDEX `Outcome_ibfk_3` (`enrolmentId` ASC) ,
  CONSTRAINT `Outcome_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Outcome_ibfk_2`
    FOREIGN KEY (`moduleId` )
    REFERENCES `willow_reference`.`Module` (`id` ),
  CONSTRAINT `Outcome_ibfk_3`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `willow_college`.`Enrolment` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88041
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`CertificateOutcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`CertificateOutcome` (
  `certificateId` BIGINT(20) NOT NULL ,
  `outcomeId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`certificateId`, `outcomeId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `CertificateOutcome_ibfk_3` (`outcomeId` ASC) ,
  CONSTRAINT `CertificateOutcome_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_2`
    FOREIGN KEY (`certificateId` )
    REFERENCES `willow_college`.`Certificate` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_3`
    FOREIGN KEY (`outcomeId` )
    REFERENCES `willow_college`.`Outcome` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`ChangeRequest`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`ChangeRequest` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `status` INT(11) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `identifier` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `result` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ChangeRequest_ibfk_1` (`collegeId` ASC) ,
  CONSTRAINT `ChangeRequest_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 29393
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`ChangeRequestItem`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`ChangeRequestItem` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `changeRequestId` BIGINT(20) NOT NULL ,
  `mainEntityName` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `mainEntityId` INT(11) NULL DEFAULT NULL ,
  `secondEntityId` INT(11) NULL DEFAULT NULL ,
  `secondEntityName` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `sequence` INT(11) NOT NULL ,
  `action` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `keyPath` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `newValueInteger` INT(11) NULL DEFAULT NULL ,
  `newValueString` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `relationship` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `result` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ChangeRequestItem_ibfk_1` (`changeRequestId` ASC) ,
  CONSTRAINT `ChangeRequestItem_ibfk_1`
    FOREIGN KEY (`changeRequestId` )
    REFERENCES `willow_college`.`ChangeRequest` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 29458
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebSite` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `siteKey` VARCHAR(25) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `SSLhostNameId` BIGINT(20) NULL DEFAULT NULL ,
  `googleAnalyticsAccount` VARCHAR(16) NULL DEFAULT NULL ,
  `googleDirectionsFrom` VARCHAR(256) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Website_college` (`collegeId` ASC) ,
  UNIQUE INDEX `Website_SSLhostname` (`SSLhostNameId` ASC) ,
  CONSTRAINT `Website_college`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Website_SSLhostname`
    FOREIGN KEY (`SSLhostNameId` )
    REFERENCES `willow_college`.`WebHostName` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 301
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebHostName`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebHostName` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `webSiteId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `WebHostName_college` (`collegeId` ASC, `name` ASC) ,
  INDEX `WebHostNama_website` (`webSiteId` ASC) ,
  INDEX `WebHostName_name` (`name` ASC) ,
  CONSTRAINT `WebHostName_college`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `WebHostName_website`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1004
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`ConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`ConcessionType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `credentialExpiryDays` INT(11) NULL DEFAULT NULL ,
  `hasConcessionNumber` TINYINT(1) NULL DEFAULT NULL ,
  `hasExpiryDate` TINYINT(1) NULL DEFAULT NULL ,
  `isConcession` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `requiresCredentialCheck` TINYINT(1) NULL DEFAULT NULL ,
  `name` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `ConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 82
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Contact`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Contact` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `tutorId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `isCompany` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isMale` TINYINT(1) NULL DEFAULT NULL ,
  `uniqueCode` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `cookieHash` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `familyName` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `givenName` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `emailAddress` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `password` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `passwordHash` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `dateOfBirth` DATE NULL DEFAULT NULL ,
  `homePhoneNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `businessPhoneNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `faxNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `mobilePhoneNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `street` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `suburb` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `state` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `postcode` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isMarketingViaEmailAllowed` TINYINT(1) NULL DEFAULT NULL ,
  `isMarketingViaPostAllowed` TINYINT(1) NULL DEFAULT NULL ,
  `isMarketingViaSMSAllowed` TINYINT(1) NULL DEFAULT NULL ,
  `taxFileNumber` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Contact_ibfk_1` (`collegeId` ASC) ,
  INDEX `Contact_ibfk_2` (`studentId` ASC) ,
  INDEX `Contact_ibfk_3` (`tutorId` ASC) ,
  INDEX `countryId_idx` (`countryId` ASC) ,
  INDEX `isCompany_idx` (`isCompany` ASC) ,
  INDEX `email_password_idx` (`emailAddress`(15) ASC, `password` ASC) ,
  INDEX `uniqueCode_idx` (`uniqueCode` ASC) ,
  INDEX `email_names_idx` (`emailAddress`(15) ASC, `familyName`(10) ASC, `givenName`(10) ASC) ,
  CONSTRAINT `Contact_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Contact_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ),
  CONSTRAINT `Contact_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1189156
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`CourseModule`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`CourseModule` (
  `courseId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`courseId`, `moduleId`) ,
  CONSTRAINT `CourseModule_ibfk_1`
    FOREIGN KEY (`courseId` )
    REFERENCES `willow_college`.`Course` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`DiscountConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`DiscountConcessionType` (
  `concessionTypeId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `discountId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`concessionTypeId`, `discountId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `DiscountConcessionType_ibfk_3` (`discountId` ASC) ,
  CONSTRAINT `DiscountConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_2`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `willow_college`.`ConcessionType` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `willow_college`.`Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`DiscountCourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`DiscountCourseClass` (
  `courseClassId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`courseClassId`, `discountId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `DiscountCourseClass_ibfk_3` (`discountId` ASC) ,
  CONSTRAINT `DiscountCourseClass_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `willow_college`.`CourseClass` (`id` ),
  CONSTRAINT `DiscountCourseClass_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `willow_college`.`Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Invoice`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Invoice` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `dateDue` DATE NOT NULL ,
  `invoiceDate` DATE NOT NULL ,
  `invoiceNumber` BIGINT(20) NOT NULL ,
  `status` ENUM('Pending', 'In Transaction', 'Success', 'Failed') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT 'Pending' ,
  `totalExGst` DECIMAL(14,2) NOT NULL DEFAULT '0.00' ,
  `totalGst` DECIMAL(14,2) NOT NULL DEFAULT '0.00' ,
  `amountOwing` DECIMAL(14,2) NOT NULL DEFAULT '0.00' ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `customerPO` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `customerReference` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `description` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `shippingAddress` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `billToAddress` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `publicNotes` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `source` CHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_invoiceNumber_uniq_idx` (`collegeId` ASC, `invoiceNumber` ASC) ,
  INDEX `Invoice_ibfk_2` (`contactId` ASC) ,
  INDEX `amoutOwing_idx` (`amountOwing` ASC) ,
  INDEX `customerPO_idx` (`customerPO`(255) ASC) ,
  INDEX `dateDue_idx` (`dateDue` ASC) ,
  INDEX `invoiceDate_idx` (`invoiceDate` ASC) ,
  INDEX `angelId_idx` (`angelId` ASC) ,
  CONSTRAINT `Invoice_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Invoice_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `willow_college`.`Contact` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 93539
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`InvoiceLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`InvoiceLine` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `invoiceId` BIGINT(20) NOT NULL ,
  `enrolmentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `priceEachExTax` DECIMAL(10,2) NOT NULL DEFAULT '0.00' ,
  `discountEachExTax` DECIMAL(10,2) NOT NULL DEFAULT '0.00' ,
  `quantity` DECIMAL(10,2) NOT NULL ,
  `taxEach` DECIMAL(10,2) NOT NULL DEFAULT '0.00' ,
  `sortOrder` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `title` VARCHAR(200) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `description` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `unit` VARCHAR(50) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `InvoiceLine_ibfk_1` (`collegeId` ASC) ,
  INDEX `InvoiceLine_ibfk_2` (`enrolmentId` ASC) ,
  INDEX `InvoiceLine_ibfk_3` (`invoiceId` ASC) ,
  INDEX `sortOrder_idx` (`sortOrder` ASC) ,
  INDEX `angelId_idx` (`angelId` ASC) ,
  CONSTRAINT `InvoiceLine_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_2`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `willow_college`.`Enrolment` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_3`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `willow_college`.`Invoice` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140103
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`InvoiceLine_Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`InvoiceLine_Discount` (
  `invoiceLineId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`invoiceLineId`, `discountId`) ,
  INDEX `InvoiceLine_Discount_ibfk_1` (`discountId` ASC) ,
  CONSTRAINT `InvoiceLine_Discount_ibfk_1`
    FOREIGN KEY (`discountId` )
    REFERENCES `willow_college`.`Discount` (`id` ),
  CONSTRAINT `InvoiceLine_Discount_ibfk_2`
    FOREIGN KEY (`invoiceLineId` )
    REFERENCES `willow_college`.`InvoiceLine` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`LicenseFee`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`LicenseFee` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `college_id` BIGINT(11) NOT NULL ,
  `key_code` ENUM('sms','cc-office','cc-web','ecommerce','support','hosting') NOT NULL ,
  `fee` DECIMAL(10,3) NOT NULL ,
  `valid_until` DATE NULL DEFAULT NULL ,
  `free_transactions` INT(11) NOT NULL DEFAULT '0' ,
  `plan_name` ENUM('light','professional','enterprise','starter','standard','premium','platinum') NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `college_key_uniq_idx` (`college_id` ASC, `key_code` ASC) ,
  INDEX `college_id_idx` (`college_id` ASC) ,
  CONSTRAINT `LicenseFee_ibfk_1`
    FOREIGN KEY (`college_id` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 389
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `willow_college`.`WillowUser`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WillowUser` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `email` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `firstName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `lastName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `password` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `passwordHash` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isActive` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isSuperUser` TINYINT(1) NULL DEFAULT NULL ,
  `lastLogin` DATETIME NULL DEFAULT NULL ,
  `lastFailedLogin` DATETIME NULL DEFAULT NULL ,
  `failedLoginCount` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `flag1` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_email_uniq_idx` (`collegeId` ASC, `email` ASC) ,
  CONSTRAINT `WillowUser_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 57
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Log`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `userId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `action` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `page` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Log_ibfk_1` (`collegeId` ASC) ,
  INDEX `Log_ibfk_2` (`userId` ASC) ,
  CONSTRAINT `Log_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Log_ibfk_2`
    FOREIGN KEY (`userId` )
    REFERENCES `willow_college`.`WillowUser` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Message`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `emailSubject` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `emailBody` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `smsText` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `Message_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88324
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`MessagePerson`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`MessagePerson` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `messageId` BIGINT(20) NULL DEFAULT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `tutorId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` SMALLINT(6) NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `numberOfAttempts` INT(11) NULL DEFAULT NULL ,
  `timeOfDelivery` DATETIME NULL DEFAULT NULL ,
  `type` INT(11) NULL DEFAULT NULL ,
  `destinationAddress` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `MessagePerson_ibfk_2` (`messageId` ASC) ,
  INDEX `MessagePerson_ibfk_3` (`contactId` ASC) ,
  INDEX `MessagePerson_ibfk_4` (`studentId` ASC) ,
  INDEX `MessagePerson_ibfk_5` (`tutorId` ASC) ,
  INDEX `statusType_idx` (`type` ASC, `status` ASC) ,
  CONSTRAINT `MessagePerson_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_2`
    FOREIGN KEY (`messageId` )
    REFERENCES `willow_college`.`Message` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_3`
    FOREIGN KEY (`contactId` )
    REFERENCES `willow_college`.`Contact` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_4`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_5`
    FOREIGN KEY (`tutorId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 147621
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`MessageTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`MessageTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subject` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `message` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `MessageTemplate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 131
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`NotificationTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`NotificationTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `allowWebobjectsTags` TINYINT(1) NULL DEFAULT NULL ,
  `allowedInTextileTags` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `mimeType` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subject` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `message` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_name_uniq_idx` (`collegeId` ASC, `name` ASC) ,
  CONSTRAINT `NotificationTemplate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`PaymentIn`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`PaymentIn` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` ENUM('Pending', 'In Transaction', 'Success', 'Failed', 'Refunded') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT 'Pending' ,
  `statusNotes` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `amount` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  `creditCardCVV` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardExpiry` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardName` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardType` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentIn_ibfk_1` (`collegeId` ASC) ,
  INDEX `PaymentIn_ibfk_2` (`contactId` ASC) ,
  INDEX `PaymentIn_ibfk_3` (`studentId` ASC) ,
  CONSTRAINT `PaymentIn_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `willow_college`.`Contact` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 93539
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`PaymentInLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`PaymentInLine` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentInId` BIGINT(20) NOT NULL ,
  `invoiceId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `amount` DECIMAL(10,2) NOT NULL DEFAULT '0.00' ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentInLine_ibfk_1` (`invoiceId` ASC) ,
  INDEX `PaymentInLine_ibfk_2` (`paymentInId` ASC) ,
  INDEX `PaymentInLine_isDeleted` (`isDeleted` ASC) ,
  INDEX `PaymentInLine_angelId` (`angelId` ASC) ,
  CONSTRAINT `PaymentInLine_ibfk_1`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `willow_college`.`Invoice` (`id` ),
  CONSTRAINT `PaymentInLine_ibfk_2`
    FOREIGN KEY (`paymentInId` )
    REFERENCES `willow_college`.`PaymentIn` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140103
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`PaymentOut`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`PaymentOut` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `paymentInTxnReference` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `totalAmount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `creditCardCVV` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardType` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentOut_ibfk_1` (`collegeId` ASC) ,
  INDEX `PaymentOut_ibfk_2` (`contactId` ASC) ,
  CONSTRAINT `PaymentOut_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `PaymentOut_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `willow_college`.`Contact` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 53806
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`PaymentOutTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`PaymentOutTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentOutId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isFinalised` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentOutTransaction_ibfk_1` (`paymentOutId` ASC) ,
  CONSTRAINT `PaymentOutTransaction_ibfk_1`
    FOREIGN KEY (`paymentOutId` )
    REFERENCES `willow_college`.`PaymentOut` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 2743
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`PaymentTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`PaymentTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isFinalised` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentTransaction_ibfk_1` (`paymentId` ASC) ,
  CONSTRAINT `PaymentTransaction_ibfk_1`
    FOREIGN KEY (`paymentId` )
    REFERENCES `willow_college`.`PaymentIn` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88593
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Preference`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Preference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `sqlType` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `value` BLOB NULL DEFAULT NULL ,
  `value_string` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `explanation` VARCHAR(10000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_name_uniq_idx` (`collegeId` ASC, `name` ASC) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `Preference_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 2418
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`QueuedRecord`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`QueuedRecord` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `numberOfAttempts` INT(11) NULL DEFAULT NULL ,
  `lastAttemptTimestamp` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `QueuedRecord_ibfk_1` (`collegeId` ASC) ,
  CONSTRAINT `QueuedRecord_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 804270
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`SessionTutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`SessionTutor` (
  `sessionId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `type` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`sessionId`, `tutorId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `SessionTutor_ibfk_3` (`tutorId` ASC) ,
  CONSTRAINT `SessionTutor_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_2`
    FOREIGN KEY (`sessionId` )
    REFERENCES `willow_college`.`Session` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`StudentConcession`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`StudentConcession` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `concessionTypeId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `authorisedOn` DATETIME NULL DEFAULT NULL ,
  `authorisationExpiresOn` DATETIME NULL DEFAULT NULL ,
  `expiresOn` DATETIME NULL DEFAULT NULL ,
  `concessionNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `timeZone` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `StudentConcession_ibfk_2` (`studentId` ASC) ,
  INDEX `StudentConcession_ibfk_3` (`concessionTypeId` ASC) ,
  CONSTRAINT `StudentConcession_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_3`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `willow_college`.`ConcessionType` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10969
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Tag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `parentId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isTagGroup` TINYINT(1) NULL DEFAULT NULL ,
  `nodeType` INT(11) NULL DEFAULT NULL ,
  `weighting` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `shortName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `Tag_ibfk_2` (`parentId` ASC) ,
  INDEX `isWebVisible_idx` (`isWebVisible` ASC) ,
  INDEX `isDeleted_idx` (`isDeleted` ASC) ,
  INDEX `shortName_idx` (`shortName` ASC) ,
  CONSTRAINT `Tag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `Tag_ibfk_2`
    FOREIGN KEY (`parentId` )
    REFERENCES `willow_college`.`Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3471
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`TagGroupRequirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`TagGroupRequirement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `tagId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `allowsMultipleTags` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `isRequired` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TagGroupRequirement_ibfk_2` (`tagId` ASC) ,
  CONSTRAINT `TagGroupRequirement_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `TagGroupRequirement_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `willow_college`.`Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 477
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`Taggable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`Taggable` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `entityAngelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_entityIdentifier_uniq_idx` (`collegeId` ASC, `angelId` ASC, `entityIdentifier` ASC) ,
  INDEX `entityIdentifier_isDeleted_idx` (`entityIdentifier` ASC, `isDeleted` ASC) ,
  CONSTRAINT `Taggable_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1189156
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`TaggableTag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`TaggableTag` (
  `tagId` BIGINT(20) NOT NULL ,
  `taggableId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`tagId`, `taggableId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TaggableTag_ibfk_3` (`taggableId` ASC) ,
  CONSTRAINT `TaggableTag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `willow_college`.`Tag` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_3`
    FOREIGN KEY (`taggableId` )
    REFERENCES `willow_college`.`Taggable` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`TutorRole`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`TutorRole` (
  `courseClassId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `confirmedDate` DATETIME NULL DEFAULT NULL ,
  `isConfirmed` TINYINT(1) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`courseClassId`, `tutorId`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TutorRole_ibfk_3` (`tutorId` ASC) ,
  CONSTRAINT `TutorRole_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `TutorRole_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `willow_college`.`CourseClass` (`id` ),
  CONSTRAINT `TutorRole_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `willow_college`.`Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WaitingList`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WaitingList` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` TINYINT(1) NULL DEFAULT NULL ,
  `potentialStudents` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `WaitingList_ibfk_2` (`courseId` ASC) ,
  INDEX `WaitingList_ibfk_3` (`studentId` ASC) ,
  CONSTRAINT `WaitingList_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `willow_college`.`College` (`id` ),
  CONSTRAINT `WaitingList_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `willow_college`.`Course` (`id` ),
  CONSTRAINT `WaitingList_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `willow_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 24483
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WaitingListSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WaitingListSite` (
  `siteId` BIGINT(20) NOT NULL ,
  `waitingListId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`siteId`, `waitingListId`) ,
  INDEX `WaitingListSite_ibfk_2` (`waitingListId` ASC) ,
  CONSTRAINT `WaitingListSite_ibfk_1`
    FOREIGN KEY (`siteId` )
    REFERENCES `willow_college`.`Site` (`id` ),
  CONSTRAINT `WaitingListSite_ibfk_2`
    FOREIGN KEY (`waitingListId` )
    REFERENCES `willow_college`.`WaitingList` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebNodeType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebNodeType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `layoutKey` VARCHAR(50) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `webSiteId_templateKey_uniq_idx` (`webSiteId` ASC, `layoutKey` ASC) ,
  INDEX `name_idx` (`name` ASC) ,
  CONSTRAINT `WebNodeType_website`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebURLAlias`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebURLAlias` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `webNodeId` BIGINT(20) NOT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  `urlPath` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `webSite` (`webSiteId` ASC) ,
  INDEX `webNode` (`webNodeId` ASC) ,
--  UNIQUE INDEX `urlPath` (`urlPath` ASC, `webSiteId` ASC) ,
  CONSTRAINT `WebURLAlias_website`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` ),
  CONSTRAINT `WebURLAlias_webNode`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `willow_college`.`WebNode` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 552
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebNode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebNode` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `webNodeTypeId` BIGINT(20) NOT NULL ,
  `isPublished` TINYINT(1) NOT NULL DEFAULT false ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `nodeNumber` INT(11) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `defaultURLalias` BIGINT(20) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `WebNode_webSite` (`webSiteId` ASC) ,
  INDEX `WebNode_webNodeType` (`webNodeTypeId` ASC) ,
  INDEX `isPublished_isWebNavigable_idx` (`isPublished` ASC) ,
  UNIQUE INDEX `college_nodeNumber` (`nodeNumber` ASC, `webSiteId` ASC) ,
  UNIQUE INDEX `WebNode_defaultWebURLalias` (`defaultURLalias` ASC) ,
  CONSTRAINT `WebNode_webSite`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` ),
  CONSTRAINT `WebNode_webNodeType`
    FOREIGN KEY (`webNodeTypeId` )
    REFERENCES `willow_college`.`WebNodeType` (`id` ),
  CONSTRAINT `WebNode_defaultWebURLalias`
    FOREIGN KEY (`defaultURLalias` )
    REFERENCES `willow_college`.`WebURLAlias` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 5494
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebContent`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebContent` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `content` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `content_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(128) NULL DEFAULT NULL ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `webContent_webSite` (`webSiteId` ASC) ,
  INDEX `webContent_name` (`name` ASC) ,
  CONSTRAINT `webContent_webSite`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 374
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebMenu`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebMenu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webNodeId` BIGINT(20) NULL ,
  `URL` TEXT NULL ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `webMenuParentId` BIGINT(20) NULL ,
  `weight` INT NOT NULL DEFAULT 0 ,
  `name` VARCHAR(64) NOT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `WebMenu_webNode` (`webNodeId` ASC) ,
  INDEX `WebMenu_webSite` (`webSiteId` ASC) ,
  INDEX `WebMenu_menuParent` (`webMenuParentId` ASC) ,
  CONSTRAINT `WebMenu_webNode`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `willow_college`.`WebNode` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebMenu_webSite`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `willow_college`.`WebSite` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebMenu_menuParent`
    FOREIGN KEY (`webMenuParentId` )
    REFERENCES `willow_college`.`WebMenu` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `willow_college`.`WebContentVisibility`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_college`.`WebContentVisibility` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `WebNodeTypeId` BIGINT(20) NULL ,
  `WebNodeId` BIGINT(20) NULL ,
  `WebContentId` BIGINT(20) NOT NULL ,
  `weight` INT NOT NULL DEFAULT 0 ,
  `regionKey` VARCHAR(24) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `WebContentVisibility_WebContent` (`WebContentId` ASC) ,
  INDEX `WebContentVisibility_WebNodeType` (`WebNodeTypeId` ASC) ,
  INDEX `WebContentVisibility_WebNode` (`WebNodeId` ASC) ,
  CONSTRAINT `WebContentVisibility_WebContent`
    FOREIGN KEY (`WebContentId` )
    REFERENCES `willow_college`.`WebContent` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebContentVisibility_WebNodeType`
    FOREIGN KEY (`WebNodeTypeId` )
    REFERENCES `willow_college`.`WebNodeType` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebContentVisibility_WebNode`
    FOREIGN KEY (`WebNodeId` )
    REFERENCES `willow_college`.`WebNode` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Placeholder table for view `willow_college`.`StudentView`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `willow_college`.`StudentView` (`id` INT, `collegeId` INT, `studentId` INT, `tutorId` INT, `angelId` INT, `countryId` INT, `created` INT, `modified` INT, `isCompany` INT, `isDeleted` INT, `isMale` INT, `uniqueCode` INT, `cookieHash` INT, `familyName` INT, `givenName` INT, `emailAddress` INT, `password` INT, `passwordHash` INT, `dateOfBirth` INT, `homePhoneNumber` INT, `businessPhoneNumber` INT, `faxNumber` INT, `mobilePhoneNumber` INT, `street` INT, `suburb` INT, `state` INT, `postcode` INT, `isMarketingViaEmailAllowed` INT, `isMarketingViaPostAllowed` INT, `isMarketingViaSMSAllowed` INT, `taxFileNumber` INT, `countryOfBirthId` INT, `languageId` INT, `concessionType` INT, `disabilityType` INT, `englishProficiency` INT, `highestSchoolLevel` INT, `indigenousStatus` INT, `isOverseasClient` INT, `isStillAtSchool` INT, `priorEducationCode` INT, `yearSchoolCompleted` INT, `labourForceType` INT, `sIsDeleted` INT, `sCreated` INT, `sModified` INT);

-- -----------------------------------------------------
-- Placeholder table for view `willow_college`.`TutorView`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `willow_college`.`TutorView` (`id` INT, `collegeId` INT, `studentId` INT, `tutorId` INT, `angelId` INT, `countryId` INT, `created` INT, `modified` INT, `isCompany` INT, `isDeleted` INT, `isMale` INT, `uniqueCode` INT, `cookieHash` INT, `familyName` INT, `givenName` INT, `emailAddress` INT, `password` INT, `passwordHash` INT, `dateOfBirth` INT, `homePhoneNumber` INT, `businessPhoneNumber` INT, `faxNumber` INT, `mobilePhoneNumber` INT, `street` INT, `suburb` INT, `state` INT, `postcode` INT, `isMarketingViaEmailAllowed` INT, `isMarketingViaPostAllowed` INT, `isMarketingViaSMSAllowed` INT, `taxFileNumber` INT, `startDate` INT, `finishDate` INT, `resume` INT, `resume_textile` INT, `tIsDeleted` INT, `tCreated` INT, `tModified` INT);

-- -----------------------------------------------------
-- View `willow_college`.`StudentView`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `willow_college`.`StudentView`;
USE `willow_college`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`203.29.62.%` SQL SECURITY DEFINER VIEW `willow_college`.`StudentView` AS select `c`.`id` AS `id`,`c`.`collegeId` AS `collegeId`,`c`.`studentId` AS `studentId`,`c`.`tutorId` AS `tutorId`,`c`.`angelId` AS `angelId`,`c`.`countryId` AS `countryId`,`c`.`created` AS `created`,`c`.`modified` AS `modified`,`c`.`isCompany` AS `isCompany`,`c`.`isDeleted` AS `isDeleted`,`c`.`isMale` AS `isMale`,`c`.`uniqueCode` AS `uniqueCode`,`c`.`cookieHash` AS `cookieHash`,`c`.`familyName` AS `familyName`,`c`.`givenName` AS `givenName`,`c`.`emailAddress` AS `emailAddress`,`c`.`password` AS `password`,`c`.`passwordHash` AS `passwordHash`,`c`.`dateOfBirth` AS `dateOfBirth`,`c`.`homePhoneNumber` AS `homePhoneNumber`,`c`.`businessPhoneNumber` AS `businessPhoneNumber`,`c`.`faxNumber` AS `faxNumber`,`c`.`mobilePhoneNumber` AS `mobilePhoneNumber`,`c`.`street` AS `street`,`c`.`suburb` AS `suburb`,`c`.`state` AS `state`,`c`.`postcode` AS `postcode`,`c`.`isMarketingViaEmailAllowed` AS `isMarketingViaEmailAllowed`,`c`.`isMarketingViaPostAllowed` AS `isMarketingViaPostAllowed`,`c`.`isMarketingViaSMSAllowed` AS `isMarketingViaSMSAllowed`,`c`.`taxFileNumber` AS `taxFileNumber`,`s`.`countryOfBirthId` AS `countryOfBirthId`,`s`.`languageId` AS `languageId`,`s`.`concessionType` AS `concessionType`,`s`.`disabilityType` AS `disabilityType`,`s`.`englishProficiency` AS `englishProficiency`,`s`.`highestSchoolLevel` AS `highestSchoolLevel`,`s`.`indigenousStatus` AS `indigenousStatus`,`s`.`isOverseasClient` AS `isOverseasClient`,`s`.`isStillAtSchool` AS `isStillAtSchool`,`s`.`priorEducationCode` AS `priorEducationCode`,`s`.`yearSchoolCompleted` AS `yearSchoolCompleted`,`s`.`labourForceType` AS `labourForceType`,`s`.`isDeleted` AS `sIsDeleted`,`s`.`created` AS `sCreated`,`s`.`modified` AS `sModified` from (`willow_college`.`Student` `s` join `willow_college`.`Contact` `c` on((`c`.`studentId` = `s`.`id`)));

-- -----------------------------------------------------
-- View `willow_college`.`TutorView`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `willow_college`.`TutorView`;
USE `willow_college`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`203.29.62.%` SQL SECURITY DEFINER VIEW `willow_college`.`TutorView` AS select `c`.`id` AS `id`,`c`.`collegeId` AS `collegeId`,`c`.`studentId` AS `studentId`,`c`.`tutorId` AS `tutorId`,`c`.`angelId` AS `angelId`,`c`.`countryId` AS `countryId`,`c`.`created` AS `created`,`c`.`modified` AS `modified`,`c`.`isCompany` AS `isCompany`,`c`.`isDeleted` AS `isDeleted`,`c`.`isMale` AS `isMale`,`c`.`uniqueCode` AS `uniqueCode`,`c`.`cookieHash` AS `cookieHash`,`c`.`familyName` AS `familyName`,`c`.`givenName` AS `givenName`,`c`.`emailAddress` AS `emailAddress`,`c`.`password` AS `password`,`c`.`passwordHash` AS `passwordHash`,`c`.`dateOfBirth` AS `dateOfBirth`,`c`.`homePhoneNumber` AS `homePhoneNumber`,`c`.`businessPhoneNumber` AS `businessPhoneNumber`,`c`.`faxNumber` AS `faxNumber`,`c`.`mobilePhoneNumber` AS `mobilePhoneNumber`,`c`.`street` AS `street`,`c`.`suburb` AS `suburb`,`c`.`state` AS `state`,`c`.`postcode` AS `postcode`,`c`.`isMarketingViaEmailAllowed` AS `isMarketingViaEmailAllowed`,`c`.`isMarketingViaPostAllowed` AS `isMarketingViaPostAllowed`,`c`.`isMarketingViaSMSAllowed` AS `isMarketingViaSMSAllowed`,`c`.`taxFileNumber` AS `taxFileNumber`,`t`.`startDate` AS `startDate`,`t`.`finishDate` AS `finishDate`,`t`.`resume` AS `resume`,`t`.`resume_textile` AS `resume_textile`,`t`.`isDeleted` AS `tIsDeleted`,`t`.`created` AS `tCreated`,`t`.`modified` AS `tModified` from (`willow_college`.`Tutor` `t` join `willow_college`.`Contact` `c` on((`c`.`tutorId` = `t`.`id`)));
USE `willow_reference` ;

-- -----------------------------------------------------
-- Table `willow_reference`.`Country`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`Country` (
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
-- Table `willow_reference`.`Language`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`Language` (
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
-- Table `willow_reference`.`Qualification`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`Qualification` (
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
-- Table `willow_reference`.`TrainingPackage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`TrainingPackage` (
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
-- Table `willow_reference`.`postcode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`postcode` (
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
-- Table `willow_reference`.`postcode_db`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `willow_reference`.`postcode_db` (
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
