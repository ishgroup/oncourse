SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `w2_binary`;
DROP SCHEMA IF EXISTS `w2_college`;
DROP SCHEMA IF EXISTS `w2_reference`;

CREATE SCHEMA IF NOT EXISTS `w2_binary` DEFAULT CHARACTER SET utf8 ;
CREATE SCHEMA IF NOT EXISTS `w2_college` DEFAULT CHARACTER SET utf8 ;
CREATE SCHEMA IF NOT EXISTS `w2_reference` DEFAULT CHARACTER SET utf8 ;
USE `w2_binary` ;

-- -----------------------------------------------------
-- Table `w2_binary`.`BinaryData`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_binary`.`BinaryData` (
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `binaryInfoId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `content` LONGBLOB NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `id` BIGINT(20) NOT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `angelId_collegeId_uniq_idx` (`angelId` ASC, `collegeId` ASC) ,
  INDEX `binaryInfoId` (`binaryInfoId` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

USE `w2_college` ;

-- -----------------------------------------------------
-- Table `w2_college`.`College`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`College` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `isWebServicePaymentsEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `isWebSitePaymentsEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `isTestingWebServicePayments` TINYINT(1) NULL DEFAULT NULL ,
  `isTestingWebSitePayments` TINYINT(1) NULL DEFAULT NULL ,
  `communication_key` BIGINT(20) NULL DEFAULT NULL ,
  `communication_key_status` VARCHAR(20) NOT NULL DEFAULT 'VALID' ,
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
  `ipAddress` VARCHAR(39) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `angelVersion` VARCHAR(40) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `webServicesSecurityCode_uniq_idx` (`webServicesSecurityCode` ASC) ,
  UNIQUE INDEX `billingCode_uniq_idx` (`billingCode` ASC) ,
  INDEX `webServicesLogin_idx` (`webServicesLogin` ASC, `webServicesPass` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 300
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Student`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Student` (
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
-- Table `w2_college`.`Course`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Course` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `code` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
  `isVETCourse` TINYINT(1) NULL DEFAULT NULL ,
  `isSufficientForQualification` TINYINT(1) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186952
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Site`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Site` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT '0' ,
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181610
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Room`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Room` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `siteId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `capacity` INT(11) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Room_ibfk_2`
    FOREIGN KEY (`siteId` )
    REFERENCES `w2_college`.`Site` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181746
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`CourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`CourseClass` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `CourseClass_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `w2_college`.`Course` (`id` ),
  CONSTRAINT `CourseClass_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `w2_college`.`Room` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186958
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Tutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Tutor` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
-- Table `w2_college`.`Session`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Session` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `courseClassId` BIGINT(20) NULL DEFAULT NULL ,
  `roomId` BIGINT(20) NULL DEFAULT NULL ,
  `markerId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`CourseClass` (`id` ),
  CONSTRAINT `Session_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `w2_college`.`Room` (`id` ),
  CONSTRAINT `Session_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1186969
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Attendance`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Attendance` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Attendance_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ),
  CONSTRAINT `Attendance_ibfk_3`
    FOREIGN KEY (`sessionId` )
    REFERENCES `w2_college`.`Session` (`id` ),
  CONSTRAINT `Attendance_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 657187
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`BinaryInfo`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`BinaryInfo` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1181948
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`BinaryInfoRelation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`BinaryInfoRelation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `BinaryInfoRelation_ibfk_2`
    FOREIGN KEY (`binaryInfoId` )
    REFERENCES `w2_college`.`BinaryInfo` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 679
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Certificate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Certificate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `certificateNumber` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Certificate_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 930
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_reference`.`Module`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_reference`.`Module` (
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
-- Table `w2_college`.`Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Discount` (
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 12416
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Enrolment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Enrolment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseClassId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
  CONSTRAINT `Enrolment_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Enrolment_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `w2_college`.`CourseClass` (`id` ),
  CONSTRAINT `Enrolment_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140104
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Outcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Outcome` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NULL DEFAULT NULL ,
  `enrolmentId` BIGINT(20) NOT NULL ,
  `priorLearningId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Outcome_ibfk_2`
    FOREIGN KEY (`moduleId` )
    REFERENCES `w2_reference`.`Module` (`id` ),
  CONSTRAINT `Outcome_ibfk_3`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `w2_college`.`Enrolment` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88041
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`CertificateOutcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`CertificateOutcome` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `certificateId` BIGINT(20) NOT NULL ,
  `outcomeId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `CertificateOutcome_ibfk_3` (`outcomeId` ASC) ,
  CONSTRAINT `CertificateOutcome_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_2`
    FOREIGN KEY (`certificateId` )
    REFERENCES `w2_college`.`Certificate` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_3`
    FOREIGN KEY (`outcomeId` )
    REFERENCES `w2_college`.`Outcome` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`WebSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebSite` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Website_SSLhostname`
    FOREIGN KEY (`SSLhostNameId` )
    REFERENCES `w2_college`.`WebHostName` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 301
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebHostName`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebHostName` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `WebHostName_website`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `w2_college`.`WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1004
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`ConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`ConcessionType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `credentialExpiryDays` INT(11) NULL DEFAULT NULL ,
  `hasConcessionNumber` TINYINT(1) NULL DEFAULT NULL ,
  `hasExpiryDate` TINYINT(1) NULL DEFAULT NULL ,
  `isConcession` TINYINT(1) NULL DEFAULT NULL ,
  `isEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `requiresCredentialCheck` TINYINT(1) NULL DEFAULT NULL ,
  `name` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `ConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 82
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Contact`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Contact` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `tutorId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `isCompany` TINYINT(1) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Contact_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ),
  CONSTRAINT `Contact_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1189156
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`CourseModule`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`CourseModule` (
   `id` BIGINT not null primary key AUTO_INCREMENT,
  `courseId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
   `angelId` BIGINT,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  CONSTRAINT `CourseModule_ibfk_1`
    FOREIGN KEY (`courseId` )
    REFERENCES `w2_college`.`Course` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`DiscountConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`DiscountConcessionType` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `concessionTypeId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `discountId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `DiscountConcessionType_ibfk_3` (`discountId` ASC) ,
  CONSTRAINT `DiscountConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_2`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `w2_college`.`ConcessionType` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `w2_college`.`Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`DiscountCourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`DiscountCourseClass` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `courseClassId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `DiscountCourseClass_ibfk_3` (`discountId` ASC) ,
  CONSTRAINT `DiscountCourseClass_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `w2_college`.`CourseClass` (`id` ),
  CONSTRAINT `DiscountCourseClass_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `w2_college`.`Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`Invoice`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Invoice` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Invoice_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `w2_college`.`Contact` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 93539
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`InvoiceLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`InvoiceLine` (
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_2`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `w2_college`.`Enrolment` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_3`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `w2_college`.`Invoice` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140103
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`InvoiceLine_Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`InvoiceLine_Discount` (
  `id` BIGINT not null primary key AUTO_INCREMENT,	
  `invoiceLineId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT not null,
   `angelId` BIGINT,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  INDEX `InvoiceLine_Discount_ibfk_1` (`discountId` ASC) ,
  CONSTRAINT `InvoiceLine_Discount_ibfk_1`
    FOREIGN KEY (`discountId` )
    REFERENCES `w2_college`.`Discount` (`id` ),
  CONSTRAINT `InvoiceLine_Discount_ibfk_2`
    FOREIGN KEY (`invoiceLineId` )
    REFERENCES `w2_college`.`InvoiceLine` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`LicenseFee`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`LicenseFee` (
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 389
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `w2_college`.`WillowUser`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WillowUser` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `email` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `firstName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `lastName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `password` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `passwordHash` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isActive` TINYINT(1) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 57
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Log`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `userId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `action` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `page` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `Log_ibfk_1` (`collegeId` ASC) ,
  INDEX `Log_ibfk_2` (`userId` ASC) ,
  CONSTRAINT `Log_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Log_ibfk_2`
    FOREIGN KEY (`userId` )
    REFERENCES `w2_college`.`WillowUser` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Message`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `emailSubject` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `emailBody` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `smsText` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `Message_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88324
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`MessagePerson`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`MessagePerson` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `messageId` BIGINT(20) NULL DEFAULT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `tutorId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_2`
    FOREIGN KEY (`messageId` )
    REFERENCES `w2_college`.`Message` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_3`
    FOREIGN KEY (`contactId` )
    REFERENCES `w2_college`.`Contact` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_4`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_5`
    FOREIGN KEY (`tutorId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 147621
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`MessageTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`MessageTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subject` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `message` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `MessageTemplate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 131
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`NotificationTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`NotificationTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`PaymentIn`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`PaymentIn` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `w2_college`.`Contact` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 93539
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`PaymentInLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`PaymentInLine` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `paymentInId` BIGINT(20) NOT NULL ,
  `invoiceId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `amount` DECIMAL(10,2) NOT NULL DEFAULT '0.00' ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentInLine_ibfk_1` (`invoiceId` ASC) ,
  INDEX `PaymentInLine_ibfk_2` (`paymentInId` ASC) ,
  INDEX `PaymentInLine_angelId` (`angelId` ASC) ,
  CONSTRAINT `PaymentInLine_ibfk_1`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `w2_college`.`Invoice` (`id` ),
  CONSTRAINT `PaymentInLine_ibfk_2`
    FOREIGN KEY (`paymentInId` )
    REFERENCES `w2_college`.`PaymentIn` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 140103
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`PaymentOut`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`PaymentOut` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `paymentInTxnReference` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `totalAmount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `creditCardCVV` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardType` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentOut_ibfk_1` (`collegeId` ASC) ,
  INDEX `PaymentOut_ibfk_2` (`contactId` ASC) ,
  CONSTRAINT `PaymentOut_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `PaymentOut_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `w2_college`.`Contact` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 53806
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`PaymentOutTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`PaymentOutTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentOutId` BIGINT(20) NULL DEFAULT NULL ,
  `isFinalised` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentOutTransaction_ibfk_1` (`paymentOutId` ASC) ,
  CONSTRAINT `PaymentOutTransaction_ibfk_1`
    FOREIGN KEY (`paymentOutId` )
    REFERENCES `w2_college`.`PaymentOut` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 2743
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`PaymentTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`PaymentTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentId` BIGINT(20) NULL DEFAULT NULL ,
  `isFinalised` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `PaymentTransaction_ibfk_1` (`paymentId` ASC) ,
  CONSTRAINT `PaymentTransaction_ibfk_1`
    FOREIGN KEY (`paymentId` )
    REFERENCES `w2_college`.`PaymentIn` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 88593
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Preference`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Preference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `sqlType` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `value` BLOB NULL DEFAULT NULL ,
  `value_string` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `explanation` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_name_uniq_idx` (`collegeId` ASC, `name` ASC) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  CONSTRAINT `Preference_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 2418
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`QueuedRecord`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`QueuedRecord` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `action` ENUM('New', 'Update', 'Delete') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL ,
  `numberOfAttempts` INT(11) NULL DEFAULT NULL ,
  `lastAttemptTimestamp` DATETIME NULL DEFAULT NULL ,
  `transactionKey` VARCHAR(255),	
  PRIMARY KEY (`id`) ,
  INDEX `QueuedRecord_ibfk_1` (`collegeId` ASC) ,
  CONSTRAINT `QueuedRecord_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 804270
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`SessionTutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`SessionTutor` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `sessionId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `type` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `SessionTutor_ibfk_3` (`tutorId` ASC) ,
  CONSTRAINT `SessionTutor_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_2`
    FOREIGN KEY (`sessionId` )
    REFERENCES `w2_college`.`Session` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`StudentConcession`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`StudentConcession` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `concessionTypeId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `authorisedOn` DATETIME NULL DEFAULT NULL ,
  `authorisationExpiresOn` DATETIME NULL DEFAULT NULL ,
  `expiresOn` DATETIME NULL DEFAULT NULL ,
  `concessionNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `timeZone` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `StudentConcession_ibfk_2` (`studentId` ASC) ,
  INDEX `StudentConcession_ibfk_3` (`concessionTypeId` ASC) ,
  CONSTRAINT `StudentConcession_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_3`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `w2_college`.`ConcessionType` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10969
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Tag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `parentId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` TINYINT(1) NULL DEFAULT NULL ,
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
  INDEX `shortName_idx` (`shortName` ASC) ,
  CONSTRAINT `Tag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `Tag_ibfk_2`
    FOREIGN KEY (`parentId` )
    REFERENCES `w2_college`.`Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3471
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`TagGroupRequirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`TagGroupRequirement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `tagId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `allowsMultipleTags` TINYINT(1) NULL DEFAULT NULL ,
  `isRequired` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TagGroupRequirement_ibfk_2` (`tagId` ASC) ,
  CONSTRAINT `TagGroupRequirement_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `TagGroupRequirement_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `w2_college`.`Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 477
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`Taggable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`Taggable` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `entityAngelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `collegeId_angelId_entityIdentifier_uniq_idx` (`collegeId` ASC, `angelId` ASC, `entityIdentifier` ASC) ,
  CONSTRAINT `Taggable_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1189156
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`TaggableTag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`TaggableTag` (
   `id` BIGINT not null primary key AUTO_INCREMENT,
  `tagId` BIGINT(20) NOT NULL ,
  `taggableId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TaggableTag_ibfk_3` (`taggableId` ASC) ,
  CONSTRAINT `TaggableTag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `w2_college`.`Tag` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_3`
    FOREIGN KEY (`taggableId` )
    REFERENCES `w2_college`.`Taggable` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`TutorRole`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`TutorRole` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `courseClassId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `confirmedDate` DATETIME NULL DEFAULT NULL ,
  `isConfirmed` TINYINT(1) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  UNIQUE INDEX `collegeId_angelId_uniq_idx` (`collegeId` ASC, `angelId` ASC) ,
  INDEX `TutorRole_ibfk_3` (`tutorId` ASC) ,
  CONSTRAINT `TutorRole_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `TutorRole_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `w2_college`.`CourseClass` (`id` ),
  CONSTRAINT `TutorRole_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `w2_college`.`Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WaitingList`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WaitingList` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
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
    REFERENCES `w2_college`.`College` (`id` ),
  CONSTRAINT `WaitingList_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `w2_college`.`Course` (`id` ),
  CONSTRAINT `WaitingList_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `w2_college`.`Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 24483
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WaitingListSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WaitingListSite` (
  `id` BIGINT not null primary key AUTO_INCREMENT,
  `siteId` BIGINT(20) NOT NULL ,
  `waitingListId` BIGINT(20) NOT NULL ,
   `angelId` BIGINT,
   `collegeId` BIGINT not null,
  INDEX `WaitingListSite_ibfk_2` (`waitingListId` ASC) ,
  CONSTRAINT `WaitingListSite_ibfk_1`
    FOREIGN KEY (`siteId` )
    REFERENCES `w2_college`.`Site` (`id` ),
  CONSTRAINT `WaitingListSite_ibfk_2`
    FOREIGN KEY (`waitingListId` )
    REFERENCES `w2_college`.`WaitingList` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `w2_college`.`WebNodeType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebNodeType` (
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
    REFERENCES `w2_college`.`WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebURLAlias`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebURLAlias` (
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
    REFERENCES `w2_college`.`WebSite` (`id` ),
  CONSTRAINT `WebURLAlias_webNode`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `w2_college`.`WebNode` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 552
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebNode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebNode` (
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
    REFERENCES `w2_college`.`WebSite` (`id` ),
  CONSTRAINT `WebNode_webNodeType`
    FOREIGN KEY (`webNodeTypeId` )
    REFERENCES `w2_college`.`WebNodeType` (`id` ),
  CONSTRAINT `WebNode_defaultWebURLalias`
    FOREIGN KEY (`defaultURLalias` )
    REFERENCES `w2_college`.`WebURLAlias` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 5494
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebContent`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebContent` (
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
    REFERENCES `w2_college`.`WebSite` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 374
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebMenu`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebMenu` (
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
    REFERENCES `w2_college`.`WebNode` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebMenu_webSite`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `w2_college`.`WebSite` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebMenu_menuParent`
    FOREIGN KEY (`webMenuParentId` )
    REFERENCES `w2_college`.`WebMenu` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `w2_college`.`WebContentVisibility`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `w2_college`.`WebContentVisibility` (
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
    REFERENCES `w2_college`.`WebContent` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebContentVisibility_WebNodeType`
    FOREIGN KEY (`WebNodeTypeId` )
    REFERENCES `w2_college`.`WebNodeType` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `WebContentVisibility_WebNode`
    FOREIGN KEY (`WebNodeId` )
    REFERENCES `w2_college`.`WebNode` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

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
