SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `Attendance`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Attendance` (
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
  CONSTRAINT `Attendance_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Attendance_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ),
  CONSTRAINT `Attendance_ibfk_3`
    FOREIGN KEY (`sessionId` )
    REFERENCES `Session` (`id` ),
  CONSTRAINT `Attendance_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `Tutor` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 2000000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Attendance` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `BinaryInfo`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `BinaryInfo` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `referenceNumber` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `byteSize` BIGINT(20) NULL DEFAULT NULL ,
  `mimeType` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `pixelHeight` INT(11) NULL DEFAULT NULL ,
  `pixelWidth` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `BinaryInfo_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `name_idx` ON `BinaryInfo` (`name` ASC) ;
CREATE INDEX `referenceNumber_idx` ON `BinaryInfo` (`referenceNumber` ASC) ;
CREATE INDEX `mimeType_idx` ON `BinaryInfo` (`mimeType` ASC) ;


-- -----------------------------------------------------
-- Table `BinaryInfoRelation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `BinaryInfoRelation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `binaryInfoId` BIGINT(20) NOT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `entityAngelId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `BinaryInfoRelation_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `BinaryInfoRelation_ibfk_2`
    FOREIGN KEY (`binaryInfoId` )
    REFERENCES `BinaryInfo` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `BinaryInfoRelation` (`collegeId` ASC, `angelId` ASC) ;
CREATE INDEX `entityIdentifier_idx` ON `BinaryInfoRelation` (`entityIdentifier` ASC) ;
CREATE INDEX `entityWillowId_idx` ON `BinaryInfoRelation` (`entityWillowId` ASC) ;


-- -----------------------------------------------------
-- Table `Certificate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Certificate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `certificateNumber` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isQualification` BOOLEAN NULL DEFAULT NULL ,
  `fundingSource` INT(11) NULL DEFAULT NULL ,
  `printedWhen` DATETIME NULL DEFAULT NULL ,
  `revokedWhen` DATETIME NULL DEFAULT NULL ,
  `studentFirstName` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `studentLastName` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `privateNotes` VARCHAR(1000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `publicNotes` VARCHAR(1000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Certificate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Certificate_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Certificate` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `CertificateOutcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CertificateOutcome` (
  `certificateId` BIGINT(20) NOT NULL ,
  `outcomeId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`certificateId`, `outcomeId`) ,
  CONSTRAINT `CertificateOutcome_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_2`
    FOREIGN KEY (`certificateId` )
    REFERENCES `Certificate` (`id` ),
  CONSTRAINT `CertificateOutcome_ibfk_3`
    FOREIGN KEY (`outcomeId` )
    REFERENCES `Outcome` (`id` ) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `CertificateOutcome` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `ChangeRequest`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ChangeRequest` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `status` INT(11) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `identifier` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `result` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `ChangeRequest_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `ChangeRequestItem`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ChangeRequestItem` (
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
  CONSTRAINT `ChangeRequestItem_ibfk_1`
    FOREIGN KEY (`changeRequestId` )
    REFERENCES `ChangeRequest` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `College`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `College` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `primaryDomainId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeKey` VARCHAR(25) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isSslEnabled` BOOLEAN NULL DEFAULT NULL ,
  `isWebServicePaymentsEnabled` BOOLEAN NULL DEFAULT NULL ,
  `isWebSitePaymentsEnabled` BOOLEAN NULL DEFAULT NULL ,
  `isTestingWebServicePayments` BOOLEAN NULL DEFAULT NULL ,
  `isTestingWebSitePayments` BOOLEAN NULL DEFAULT NULL ,
  `requiresAvetmiss` BOOLEAN NULL DEFAULT NULL ,
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
  CONSTRAINT `College_ibfk_1`
    FOREIGN KEY (`primaryDomainId` )
    REFERENCES `CollegeDomain` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `webServicesSecurityCode_uniq_idx` ON `College` (`webServicesSecurityCode` ASC) ;
CREATE UNIQUE INDEX `billingCode_uniq_idx` ON `College` (`billingCode` ASC) ;
CREATE INDEX `webServicesLogin_idx` ON `College` (`webServicesLogin` ASC, `webServicesPass` ASC, `isDeleted` ASC) ;


-- -----------------------------------------------------
-- Table `CollegeDomain`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CollegeDomain` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `webSiteId` BIGINT(20) NULL DEFAULT NULL ,
  `aliasedDomainId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `hasSSL` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NOT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `googleAnalyticsAccount` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `googleDirectionsFrom` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subsiteCode` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `CollegeDomain_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `CollegeDomain_ibfk_2`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `WebSite` (`id` ),
  CONSTRAINT `CollegeDomain_ibfk_3`
    FOREIGN KEY (`aliasedDomainId` )
    REFERENCES `CollegeDomain` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeid_name_uniq_idx` ON `CollegeDomain` (`collegeId` ASC, `name` ASC, `subsiteCode` ASC) ;
CREATE INDEX `name_idx` ON `CollegeDomain` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `ConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ConcessionType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `credentialExpiryDays` INT(11) NULL DEFAULT NULL ,
  `hasConcessionNumber` BOOLEAN NULL DEFAULT NULL ,
  `hasExpiryDate` BOOLEAN NULL DEFAULT NULL ,
  `isConcession` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isEnabled` BOOLEAN NULL DEFAULT NULL ,
  `requiresCredentialCheck` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `ConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `ConcessionType` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `Contact`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Contact` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `tutorId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `isCompany` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isMale` BOOLEAN NULL DEFAULT NULL ,
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
  `isMarketingViaEmailAllowed` BOOLEAN NULL DEFAULT NULL ,
  `isMarketingViaPostAllowed` BOOLEAN NULL DEFAULT NULL ,
  `isMarketingViaSMSAllowed` BOOLEAN NULL DEFAULT NULL ,
  `taxFileNumber` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Contact_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Contact_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ),
  CONSTRAINT `Contact_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- CREATE UNIQUE INDEX `studentId_uniq_idx` ON `Contact` (`studentId` ASC) ;
-- CREATE UNIQUE INDEX `tutorId_uniq_idx` ON `Contact` (`tutorId` ASC) ;

CREATE INDEX `countryId_idx` ON `Contact` (`countryID` ASC) ;
CREATE INDEX `isCompany_idx` ON `Contact` (`isCompany` ASC) ;
CREATE INDEX `email_password_idx` ON `Contact` (`emailAddress`(15) ASC, `password` ASC) ;
CREATE INDEX `uniqueCode_idx` ON `Contact` (`uniqueCode` ASC) ;
CREATE INDEX `email_names_idx` ON `Contact` (`emailAddress`(15) ASC, `familyName`(10) ASC, `givenName`(10) ASC) ;


-- -----------------------------------------------------
-- Table `Course`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Course` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `qualificationId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `code` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT NULL ,
  `isVETCourse` BOOLEAN NULL DEFAULT NULL ,
  `isSufficientForQualification` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `allowWaitingList` BOOLEAN NULL DEFAULT NULL ,
  `nominalHours` FLOAT NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `fieldOfEducation` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `searchText` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Course_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Course` (`collegeId` ASC, `angelId` ASC) ;
CREATE INDEX `isWebVisible_idx` ON `Course` (`isWebVisible` ASC) ;
CREATE INDEX `code_idx` ON `Course` (`code` ASC) ;


-- -----------------------------------------------------
-- Table `Site`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Site` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `countryId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT FALSE ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
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
  CONSTRAINT `Site_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))

ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Room`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Room` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `siteId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `capacity` INT(11) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `directions` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `directions_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `facilities` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `facilities_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Room_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Room_ibfk_2`
    FOREIGN KEY (`siteId` )
    REFERENCES `Site` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `CourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CourseClass` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `code` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `courseId` BIGINT(20) NULL DEFAULT NULL ,
  `roomId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT NULL ,
  `isCancelled` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
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
  CONSTRAINT `CourseClass_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `CourseClass_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `Course` (`id` ),
  CONSTRAINT `CourseClass_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `Room` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `code_idx` ON `CourseClass` (`code` ASC) ;
CREATE INDEX `endDate_idx` ON `CourseClass` (`endDate` ASC) ;
CREATE INDEX `startDate_idx` ON `CourseClass` (`startDate` ASC) ;


-- -----------------------------------------------------
-- Table `CourseModule`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CourseModule` (
  `courseId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`courseId`, `moduleId`) ,
  CONSTRAINT `CourseModule_ibfk_1`
    FOREIGN KEY (`courseId` )
    REFERENCES `Course` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Discount` (
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
  `isCodeRequired` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
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
  CONSTRAINT `Discount_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 50000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Discount` (`collegeId` ASC, `angelId` ASC) ;
CREATE INDEX `code_idx` ON `Discount` (`collegeId` ASC, `code` ASC) ;
CREATE INDEX `validTo_validFrom_idx` ON `Discount` (`collegeId` ASC, `validTo` ASC, `validFrom` ASC) ;


-- -----------------------------------------------------
-- Table `DiscountConcessionType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `DiscountConcessionType` (
  `concessionTypeId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `discountId` BIGINT(20) NOT NULL DEFAULT '0' ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`concessionTypeId`, `discountId`) ,
  CONSTRAINT `DiscountConcessionType_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_2`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `ConcessionType` (`id` ),
  CONSTRAINT `DiscountConcessionType_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `DiscountConcessionType` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `DiscountCourseClass`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `DiscountCourseClass` (
  `courseClassId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`courseClassId`, `discountId`) ,
  CONSTRAINT `DiscountCourseClass_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `Collge` (`id` ),
  CONSTRAINT `DiscountCourseClass_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `CourseClass` (`id` ),
  CONSTRAINT `DiscountCourseClass_ibfk_3`
    FOREIGN KEY (`discountId` )
    REFERENCES `Discount` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `DiscountCourseClass` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `Student`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Student` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
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
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isOverseasClient` BOOLEAN NULL DEFAULT NULL ,
  `isStillAtSchool` BOOLEAN NULL DEFAULT NULL ,
  `priorEducationCode` INT(11) NULL DEFAULT NULL ,
  `yearSchoolCompleted` INT(11) NULL DEFAULT NULL ,
  `labourForceType` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Student_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `Collge` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE VIEW StudentView AS
	SELECT 
		c.*,
		s.countryOfBirthId,
		s.languageId,
		s.concessionType,
		s.disabilityType,
		s.englishProficiency,
		s.highestSchoolLevel,
		s.indigenousStatus,
		s.isOverseasClient,
		s.isStillAtSchool,
		s.priorEducationCode,
		s.yearSchoolCompleted,
		s.labourForceType,
		s.isDeleted AS sIsDeleted,
		s.created AS sCreated,
		s.modified AS sModified
	FROM Student s JOIN Contact c ON c.studentId = s.id;


-- -----------------------------------------------------
-- Table `Enrolment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Enrolment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseClassId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `reasonForStudy` INT(11) NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Enrolment_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Enrolment_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `CourseClass` (`id` ),
  CONSTRAINT `Enrolment_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ),
  CONSTRAINT `Enrolment_ibfk_4`
    FOREIGN KEY (`discountId` )
    REFERENCES `Discount` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Enrolment` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `DiscountEnrolment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `DiscountEnrolment` (
  `discountId` BIGINT(20) NOT NULL ,
  `enrolmentId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`discountId`, `enrolmentId`) ,
  CONSTRAINT `DiscountEnrolment_ibfk_1`
    FOREIGN KEY (`discountId` )
    REFERENCES `Discount` (`id` ),
  CONSTRAINT `DiscountEnrolment_ibfk_2`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `Enrolment` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Invoice`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Invoice` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `dateDue` DATE NOT NULL ,
  `invoiceDate` DATE NOT NULL ,
  `invoiceNumber` BIGINT(20) NOT NULL ,
  `amountOwing` DECIMAL(10,2) NOT NULL ,
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
  CONSTRAINT `Invoice_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Invoice_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `Contact` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_invoiceNumber_uniq_idx` ON `Invoice` (`collegeId` ASC, `invoiceNumber` ASC) ;
CREATE INDEX `amoutOwing_idx` ON `Invoice` (`amountOwing` ASC) ;
CREATE INDEX `customerPO_idx` ON `Invoice` (`customerPO`(255) ASC) ;
CREATE INDEX `dateDue_idx` ON `Invoice` (`dateDue` ASC) ;
CREATE INDEX `invoiceDate_idx` ON `Invoice` (`invoiceDate` ASC) ;
CREATE INDEX `angelId_idx` ON `Invoice` (`angelId` ASC) ;


-- -----------------------------------------------------
-- Table `InvoiceLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `InvoiceLine` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `invoiceId` BIGINT(20) NOT NULL ,
  `enrolmentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `priceEachExTax` DECIMAL(10,2) NOT NULL DEFAULT 0.00 ,
  `discountEachExTax` DECIMAL(10,2) NOT NULL DEFAULT 0.00 ,
  `quantity` DECIMAL(10,2) NOT NULL ,
  `taxEach` DECIMAL(10,2) NOT NULL DEFAULT 0.00 ,
  `sortOrder` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `title` VARCHAR(200) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `description` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `unit` VARCHAR(50) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `InvoiceLine_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_2`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `Enrolment` (`id` ),
  CONSTRAINT `InvoiceLine_ibfk_3`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `Invoice` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `sortOrder_idx` ON `InvoiceLine` (`sortOrder` ASC) ;
CREATE INDEX `angelId_idx` ON `InvoiceLine` (`angelId` ASC) ;


-- -----------------------------------------------------
-- Table `InvoiceLine_Discount`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `InvoiceLine_Discount` (
  `invoiceLineId` BIGINT(20) NOT NULL ,
  `discountId` BIGINT(20) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`invoiceLineId`, `discountId`) ,
  CONSTRAINT `InvoiceLine_Discount_ibfk_1`
    FOREIGN KEY (`discountId` )
    REFERENCES `Discount` (`id` ),
  CONSTRAINT `InvoiceLine_Discount_ibfk_2`
    FOREIGN KEY (`invoiceLineId` )
    REFERENCES `InvoiceLine` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `LicenseFee`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `LicenseFee` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `college_id` BIGINT(11) NOT NULL ,
  `key_code` ENUM('sms','cc-office','cc-web','ecommerce','support','hosting') NOT NULL ,
  `fee` DECIMAL(10,3) NOT NULL ,
  `valid_until` DATE NULL DEFAULT NULL ,
  `free_transactions` INT(11) NOT NULL DEFAULT '0' ,
  `plan_name` ENUM('light','professional','enterprise','starter','standard','premium','platinum') NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `LicenseFee_ibfk_1`
    FOREIGN KEY (`college_id` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `college_key_uniq_idx` ON `LicenseFee` (`college_id` ASC, `key_code` ASC) ;
CREATE INDEX `college_id_idx` ON `LicenseFee` (`college_id` ASC) ;


-- -----------------------------------------------------
-- Table `Log`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `userId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `action` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `page` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Log_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Log_ibfk_2`
    FOREIGN KEY (`userId` )
    REFERENCES `WillowUser` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Message`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `emailSubject` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `emailBody` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `smsText` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Message_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 250000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Message` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `MessagePerson`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MessagePerson` (
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
  CONSTRAINT `MessagePerson_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_2`
    FOREIGN KEY (`messageId` )
    REFERENCES `Message` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_3`
    FOREIGN KEY (`contactId` )
    REFERENCES `Contact` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_4`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ),
  CONSTRAINT `MessagePerson_ibfk_5`
    FOREIGN KEY (`tutorId` )
    REFERENCES `Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 400000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `MessagePerson` (`collegeId` ASC, `angelId` ASC) ;
CREATE INDEX `statusType_idx` ON `MessagePerson` (`type` ASC, `status` ASC) ;


-- -----------------------------------------------------
-- Table `MessageTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `MessageTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subject` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `message` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `MessageTemplate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `MessageTemplate` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `NotificationTemplate`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `NotificationTemplate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `allowWebobjectsTags` BOOLEAN NULL DEFAULT NULL ,
  `allowedInTextileTags` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `mimeType` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `subject` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `message` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `NotificationTemplate_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_name_uniq_idx` ON `NotificationTemplate` (`collegeId` ASC, `name` ASC) ;


-- -----------------------------------------------------
-- Table `Outcome`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Outcome` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `moduleId` BIGINT(20) NULL DEFAULT NULL ,
  `enrolmentId` BIGINT(20) NOT NULL ,
  `priorLearningId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `deliveryMode` INT(11) NULL DEFAULT NULL ,
  `fundingSource` INT(11) NULL DEFAULT NULL ,
  `reportableHours` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Outcome_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Outcome_ibfk_2`
    FOREIGN KEY (`moduleId` )
    REFERENCES `Module` (`id` ),
  CONSTRAINT `Outcome_ibfk_3`
    FOREIGN KEY (`enrolmentId` )
    REFERENCES `Enrolment` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Outcome` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `PaymentIn`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PaymentIn` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `totalExGst` DECIMAL(14,2) NULL DEFAULT NULL ,
  `totalGst` DECIMAL(14,2) NULL DEFAULT NULL ,
  `creditCardCVV` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardExpiry` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardName` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardType` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `PaymentIn_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `Contact` (`id` ),
  CONSTRAINT `PaymentIn_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `PaymentInLine`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PaymentInLine` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentInId` BIGINT(20) NOT NULL ,
  `invoiceId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `PaymentInLine_ibfk_1`
    FOREIGN KEY (`invoiceId` )
    REFERENCES `Invoice` (`id` ),
  CONSTRAINT `PaymentInLine_ibfk_2`
    FOREIGN KEY (`paymentInId` )
    REFERENCES `PaymentIn` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `PaymentInLine_isDeleted` ON `PaymentInLine` (`isDeleted` ASC) ;
CREATE INDEX `PaymentInLine_angelId` ON `PaymentInLine` (`angelId` ASC) ;


-- -----------------------------------------------------
-- Table `PaymentOut`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PaymentOut` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `contactId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `paymentInTxnReference` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `source` VARCHAR(1) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `status` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `totalAmount` DECIMAL(14,2) NULL DEFAULT NULL ,
  `creditCardCVV` VARCHAR(4) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `creditCardType` VARCHAR(8) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `PaymentOut_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `PaymentOut_ibfk_2`
    FOREIGN KEY (`contactId` )
    REFERENCES `Contact` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 200000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `PaymentOutTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PaymentOutTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentOutId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isFinalised` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `PaymentOutTransaction_ibfk_1`
    FOREIGN KEY (`paymentOutId` )
    REFERENCES `PaymentOut` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `PaymentTransaction`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PaymentTransaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `paymentId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isFinalised` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `txnReference` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `response` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `PaymentTransaction_ibfk_1`
    FOREIGN KEY (`paymentId` )
    REFERENCES `PaymentIn` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 250000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Preference`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Preference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `sqlType` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `value` BLOB NULL DEFAULT NULL ,
  `explanation` VARCHAR(10000) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Preference_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_name_uniq_idx` ON `Preference` (`collegeId` ASC, `name` ASC) ;
CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Preference` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `QueuedRecord`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `QueuedRecord` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `numberOfAttempts` INT(11) NULL DEFAULT NULL ,
  `lastAttemptTimestamp` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `QueuedRecord_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 2500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `Session`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Session` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `courseClassId` BIGINT(20) NULL DEFAULT NULL ,
  `roomId` BIGINT(20) NULL DEFAULT NULL ,
  `markerId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `endDate` DATETIME NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `timeZone` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Session_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `Collge` (`id` ),
  CONSTRAINT `Session_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `CourseClass` (`id` ),
  CONSTRAINT `Session_ibfk_3`
    FOREIGN KEY (`roomId` )
    REFERENCES `Room` (`id` ),
  CONSTRAINT `Session_ibfk_4`
    FOREIGN KEY (`markerId` )
    REFERENCES `Tutor` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `Session_startDate_idx` ON `Session` (`startDate` DESC) ;
CREATE INDEX `Session_endDate_idx` ON `Session` (`endDate` DESC) ;


-- -----------------------------------------------------
-- Table `SessionTutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SessionTutor` (
  `sessionId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `type` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`sessionId`, `tutorId`) ,
  CONSTRAINT `SessionTutor_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_2`
    FOREIGN KEY (`sessionId` )
    REFERENCES `Session` (`id` ),
  CONSTRAINT `SessionTutor_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `SessionTutor` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `StudentConcession`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `StudentConcession` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `concessionTypeId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `authorisedOn` DATETIME NULL DEFAULT NULL ,
  `authorisationExpiresOn` DATETIME NULL DEFAULT NULL ,
  `expiresOn` DATETIME NULL DEFAULT NULL ,
  `concessionNumber` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `timeZone` VARCHAR(32) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `StudentConcession_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_2`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ),
  CONSTRAINT `StudentConcession_ibfk_3`
    FOREIGN KEY (`concessionTypeId` )
    REFERENCES `ConcessionType` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `StudentConcession` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `Taggable`
-- -----------------------------------------------------
CREATE TABLE `Taggable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collegeId` bigint(20) NOT NULL,
  `angelId` bigint(20) DEFAULT NULL,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `entityWillowId` BIGINT(20) NULL DEFAULT NULL ,
  `entityAngelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Taggable_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB 
AUTO_INCREMENT = 3500000
DEFAULT CHARSET = utf8 
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_entityIdentifier_uniq_idx` ON `Taggable` (`collegeId` ASC, `angelId` ASC, `entityIdentifier` ASC) ;
CREATE INDEX `entityIdentifier_isDeleted_idx` ON `Taggable` (`entityIdentifier` ASC, `isDeleted` ASC) ;

-- -----------------------------------------------------
-- Table `Tag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `parentId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isTagGroup` BOOLEAN NULL DEFAULT NULL ,
  `nodeType` INT(11) NULL DEFAULT NULL ,
  `weighting` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `shortName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Tag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `Tag_ibfk_2`
    FOREIGN KEY (`parentId` )
    REFERENCES `Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `Tag` (`collegeId` ASC, `angelId` ASC) ;
CREATE INDEX `isWebVisible_idx` ON `Tag` (`isWebVisible` ASC) ;
CREATE INDEX `isDeleted_idx` ON `Tag` (`isDeleted` ASC) ;
CREATE INDEX `shortName_idx` ON `Tag` (`shortName` ASC) ;


-- -----------------------------------------------------
-- Table `TagGroupRequirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `TagGroupRequirement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `tagId` BIGINT(20) NULL DEFAULT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `allowsMultipleTags` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isRequired` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `entityIdentifier` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `TagGroupRequirement_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `TagGroupRequirement_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `Tag` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1500
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `TagGroupRequirement` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `TaggableTag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `TaggableTag` (
  `tagId` BIGINT(20) NOT NULL ,
  `taggableId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`tagId`, `taggableId`) ,
  CONSTRAINT `TaggableTag_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `Tag` (`id` ),
  CONSTRAINT `TaggableTag_ibfk_3`
    FOREIGN KEY (`taggableId` )
    REFERENCES `Taggable` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `TaggableTag` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `Tutor`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `Tutor` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `startDate` DATETIME NULL DEFAULT NULL ,
  `finishDate` DATETIME NULL DEFAULT NULL ,
  `resume` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `resume_textile` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `Tutor_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `Collge` (`id` ) )
ENGINE = InnoDB
AUTO_INCREMENT = 3500000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE VIEW TutorView AS
	SELECT 
		c.*,
		t.startDate,
		t.finishDate,
		t.resume,
		t.resume_textile,
		t.isDeleted AS tIsDeleted,
		t.created AS tCreated,
		t.modified AS tModified
	FROM Tutor t JOIN Contact c ON c.tutorId = t.id;


-- -----------------------------------------------------
-- Table `TutorRole`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `TutorRole` (
  `courseClassId` BIGINT(20) NOT NULL ,
  `tutorId` BIGINT(20) NOT NULL ,
  `collegeId` BIGINT(20) NOT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `confirmedDate` DATETIME NULL DEFAULT NULL ,
  `isConfirmed` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `detail_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`courseClassId`, `tutorId`) ,
  CONSTRAINT `TutorRole_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `TutorRole_ibfk_2`
    FOREIGN KEY (`courseClassId` )
    REFERENCES `CourseClass` (`id` ),
  CONSTRAINT `TutorRole_ibfk_3`
    FOREIGN KEY (`tutorId` )
    REFERENCES `Tutor` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `TutorRole` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `WaitingList`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WaitingList` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `courseId` BIGINT(20) NOT NULL ,
  `studentId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `potentialStudents` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `detail` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WaitingList_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `WaitingList_ibfk_2`
    FOREIGN KEY (`courseId` )
    REFERENCES `Course` (`id` ),
  CONSTRAINT `WaitingList_ibfk_3`
    FOREIGN KEY (`studentId` )
    REFERENCES `Student` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_angelId_uniq_idx` ON `WaitingList` (`collegeId` ASC, `angelId` ASC) ;


-- -----------------------------------------------------
-- Table `WaitingListSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WaitingListSite` (
  `siteId` BIGINT(20) NOT NULL ,
  `waitingListId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`siteId`, `waitingListId`) ,
  CONSTRAINT `WaitingListSite_ibfk_1`
    FOREIGN KEY (`siteId` )
    REFERENCES `Site` (`id` ),
  CONSTRAINT `WaitingListSite_ibfk_2`
    FOREIGN KEY (`waitingListId` )
    REFERENCES `WaitingList` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WebBlockType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebBlockType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `name_uniq_idx` ON `WebBlockType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `WebSite`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebSite` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NOT NULL ,
  `parentWebSiteId` BIGINT(20) NULL DEFAULT NULL ,
  `homePageId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `siteKey` VARCHAR(25) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `defaultUrlPath` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `code` VARCHAR(100) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `sslHostName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebSite_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ),
  CONSTRAINT `WebSite_ibfk_2`
    FOREIGN KEY (`parentWebSiteId` )
    REFERENCES `WebSite` (`id` ),
  CONSTRAINT `WebSite_ibfk_3`
    FOREIGN KEY (`homePageId` )
    REFERENCES `WebNode` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WebBlock`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebBlock` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `webBlockTypeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `regionKey` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `weighting` INT(11) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `content` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `content_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebBlock_ibfk_1`
    FOREIGN KEY (`webBlockTypeId` )
    REFERENCES `WebBlockType` (`id` ),
  CONSTRAINT `WebBlock_ibfk_2`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `name_idx` ON `WebBlock` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `WebTheme`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebTheme` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `themeKey` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `description` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `description_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebTheme_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_themeKey_uniq_idx` ON `WebTheme` (`collegeId` ASC, `themeKey` ASC) ;
CREATE INDEX `name_idx` ON `WebTheme` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `WebNodeType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebNodeType` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `defaultWebThemeId` BIGINT(20) NOT NULL ,
  `webSiteId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `templateKey` VARCHAR(50) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `numberOfRegions` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebNodeType_ibfk_1`
    FOREIGN KEY (`defaultWebThemeId` )
    REFERENCES `WebTheme` (`id` ),
  CONSTRAINT `WebNodeType_ibfk_2`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `WebSite` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `webSiteId_templateKey_uniq_idx` ON `WebNodeType` (`webSiteId` ASC, `templateKey` ASC) ;
CREATE INDEX `name_idx` ON `WebNodeType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `WebNode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebNode` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `parentNodeID` BIGINT(20) NULL DEFAULT NULL ,
  `webNodeTypeId` BIGINT(20) NOT NULL ,
  `menuAliasId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isWebVisible` BOOLEAN NULL DEFAULT NULL ,
  `isWebNavigable` BOOLEAN NULL DEFAULT NULL ,
  `isPublished` BOOLEAN NULL DEFAULT NULL ,
  `isRssFeed` BOOLEAN NULL DEFAULT NULL ,
  `name` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `shortName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `weighting` INT(11) NOT NULL ,
  `nodeNumber` INT(11) NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `savedByUserId` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebNode_ibfk_1`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `WebSite` (`id` ),
  CONSTRAINT `WebNode_ibfk_2`
    FOREIGN KEY (`parentNodeId` )
    REFERENCES `WebNode` (`id` ),
  CONSTRAINT `WebNode_ibfk_3`
    FOREIGN KEY (`webNodeTypeId` )
    REFERENCES `WebNodeType` (`id` ),
  CONSTRAINT `WebNode_ibfk_4`
    FOREIGN KEY (`savedByUserId` )
    REFERENCES `WillowUser` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX `isPublished_isWebNavigable_idx` ON `WebNode` (`isPublished` ASC, `isWebNavigable` ASC) ;
CREATE INDEX `isDeleted_idx` ON `WebNode` (`isDeleted` ASC) ;


-- -----------------------------------------------------
-- Table `WebNodeContent`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebNodeContent` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webNodeId` BIGINT(20) NULL DEFAULT NULL ,
  `regionKey` VARCHAR(25) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `content` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `content_textile` MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebNodeContent_ibfk_1`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `WebNode` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `webNodeId_regionKey_uniq_idx` ON `WebNodeContent` (`webNodeId` ASC, `regionKey` ASC) ;


-- -----------------------------------------------------
-- Table `WebNodeContentWebBlock`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebNodeContentWebBlock` (
  `webNodeContentId` BIGINT(20) NOT NULL ,
  `webBlockId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`webNodeContentId`, `webBlockId`) ,
  CONSTRAINT `WebNodeContentWebBlock_ibfk_1`
    FOREIGN KEY (`webNodeContentId` )
    REFERENCES `WebNodeContent` (`id` ),
  CONSTRAINT `WebNodeContentWebBlock_ibfk_2`
    FOREIGN KEY (`webBlockId` )
    REFERENCES `WebBlock` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WebNodeTag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebNodeTag` (
  `webNodeId` BIGINT(20) NOT NULL ,
  `tagId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`webNodeId`, `tagId`) ,
  CONSTRAINT `WebNodeTag_ibfk_1`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `WebNode` (`id` ),
  CONSTRAINT `WebNodeTag_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `Tag` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WebBlockTag`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebBlockTag` (
  `webBlockId` BIGINT(20) NOT NULL ,
  `tagId` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`webBlockId`, `tagId`) ,
  CONSTRAINT `WebBlockTag_ibfk_1`
    FOREIGN KEY (`webBlockId` )
    REFERENCES `Site` (`id` ),
  CONSTRAINT `WebBlockTag_ibfk_2`
    FOREIGN KEY (`tagId` )
    REFERENCES `Tag` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WebURLAlias`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WebURLAlias` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `webSiteId` BIGINT(20) NOT NULL ,
  `webNodeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `urlPath` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WebURLAlias_ibfk_1`
    FOREIGN KEY (`webSiteId` )
    REFERENCES `WebSite` (`id` ),
  CONSTRAINT `WebURLAlias_ibfk_2`
    FOREIGN KEY (`webNodeId` )
    REFERENCES `WebNode` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 10000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `WillowUser`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `WillowUser` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `collegeId` BIGINT(20) NULL DEFAULT NULL ,
  `angelId` BIGINT(20) NULL DEFAULT NULL ,
  `email` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `firstName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `lastName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `password` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `passwordHash` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `isActive` BOOLEAN NULL DEFAULT NULL ,
  `isDeleted` BOOLEAN NULL DEFAULT NULL ,
  `isSuperUser` BOOLEAN NULL DEFAULT NULL ,
  `lastLogin` DATETIME NULL DEFAULT NULL ,
  `lastFailedLogin` DATETIME NULL DEFAULT NULL ,
  `failedLoginCount` INT(11) NULL DEFAULT NULL ,
  `created` DATETIME NULL DEFAULT NULL ,
  `modified` DATETIME NULL DEFAULT NULL ,
  `flag1` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `WillowUser_ibfk_1`
    FOREIGN KEY (`collegeId` )
    REFERENCES `College` (`id` ))
ENGINE = InnoDB
AUTO_INCREMENT = 1000
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE UNIQUE INDEX `collegeId_email_uniq_idx` ON `WillowUser` (`collegeId` ASC, `email` ASC) ;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
