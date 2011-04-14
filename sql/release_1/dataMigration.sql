-- set the college you wish to migrate

SET @collegeId = %COLLEGEID%;

INSERT INTO willow_college.College (id, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled,
	isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified,
	name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType,
	webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication)
	SELECT id, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified, name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType, webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication
	FROM oncourse_realdata_willow_college.College
	WHERE id = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.Tutor (angelId, collegeId, created, modified, id, startDate, finishDate, resume, resume_textile)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.modified, t.id, t.dateStarted, t.dateFinished, t.resume, t.resume_textile
	FROM oncourse_realdata_willow_college.Tutor t
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = t.id
	WHERE tag.entityType = 'Tutor' AND t.id IN (SELECT tutorId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId) AND tag.isDeleted <> 1;

INSERT INTO willow_college.Student (angelId, collegeId, created, modified, id, countryOfBirthId, languageId, concessionType, disabilityType, englishProficiency, highestSchoolLevel, indigenousStatus, isOverseasClient, isStillAtSchool, priorEducationCode, yearSchoolCompleted, labourForceType)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.modified, s.id, s.countryOfBirthId, s.languageId, s.concessionType, s.disabilityType, s.englishProficiency, s.highestSchoolLevel, s.indigenousStatus, s.isOverseasClient, s.isStillAtSchool, s.priorEducationCode, s.yearSchoolCompleted, s.labourForceType
	FROM oncourse_realdata_willow_college.Student s
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = s.id
	WHERE tag.entityType = 'Student' AND s.id IN (SELECT studentId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId) AND tag.isDeleted <> 1;

INSERT INTO willow_college.ConcessionType (id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isEnabled, requiresCredentialCheck, name)
	SELECT id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isEnabled, requiresCredentialCheck, name
	FROM oncourse_realdata_willow_college.ConcessionType WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.StudentConcession (angelId, authorisationExpiresOn, authorisedOn, collegeId, concessionNumber, concessionTypeId, created, expiresOn, id, modified, studentId, timeZone)
	SELECT sc.angelId, sc.authorisationExpiresOn, sc.authorisedOn, sc.collegeId, sc.concessionNumber, sc.concessionTypeId, sc.created, sc.expiresOn, sc.id, sc.modified, sc.studentId, sc.timeZone
	FROM oncourse_realdata_willow_college.StudentConcession AS sc
	JOIN oncourse_realdata_willow_college.Taggable AS t ON t.id = sc.studentId
	JOIN willow_college.Student AS s ON sc.studentId=s.id
	WHERE sc.collegeId = @collegeId AND sc.isDeleted <> 1 AND t.entityType like 'Student';

INSERT INTO willow_college.Course (id, collegeId, qualificationId, angelId, code, isWebVisible, isVETCourse, isSufficientForQualification, allowWaitingList, nominalHours, name, detail, detail_textile, fieldOfEducation, searchText, created, modified)
	SELECT c.id, t.collegeId, c.qualificationId, t.angelId, c.code, c.isWebVisible, c.isVETCourse, c.isSufficientForQualification, c.allowWaitingList, c.nominalHours, c.name, c.detail, c.detail_textile, c.fieldOfEducation, c.searchText, t.created, t.modified
	FROM oncourse_realdata_willow_college.Course c
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = c.id
	WHERE t.entityType = 'Course' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_college.Site (countryId, drivingDirections, drivingDirections_textile, id, isWebVisible, latitude, longitude, name, postcode, publicTransportDirections, publicTransportDirections_textile, specialInstructions, specialInstructions_textile, state, street, suburb, timeZone, collegeId, angelId, created, modified)
	SELECT s.countryId, s.drivingDirections, s.drivingDirections_textile, s.id, s.isWebVisible, s.latitude, s.longitude, s.name, s.postcode, s.publicTransportDirections, s.publicTransportDirections_textile, s.specialInstructions, s.specialInstructions_textile, s.state, s.street, s.suburb, s.timeZone, t.collegeId, t.angelId, t.created, t.modified
	FROM oncourse_realdata_willow_college.Site s
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = s.id
	WHERE t.entityType = 'Site' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_college.Room (capacity, directions, directions_textile, facilities, facilities_textile, id, name, siteId, collegeId, angelId, created, modified)
	SELECT r.capacity, r.directions, r.directions_textile, r.facilities, r.facilities_textile, r.id, r.name, r.siteId, t.collegeId, t.angelId, t.created, t.modified
	FROM oncourse_realdata_willow_college.Room r
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = r.id
	WHERE t.entityType = 'Room' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_college.CourseClass (id, collegeId, angelId, code, courseId, roomId, startDate, endDate, isWebVisible, isCancelled, maximumPlaces, minimumPlaces, countOfSessions, deliveryMode, feeExGst, feeGst, minutesPerSession, startingMinutePerSession, detail, detail_textile, materials, materials_textile, sessionDetail, sessionDetail_textile, timeZone, created, modified)
	SELECT cc.id, t.collegeId, t.angelId, cc.code, cc.courseId, cc.roomId, cc.startDate, cc.endDate, cc.isWebVisible, cc.isCancelled, cc.maximumPlaces, cc.minimumPlaces, cc.countOfSessions, cc.deliveryMode, cc.feeExGst, cc.feeGst, cc.minutesPerSession, cc.startingMinutePerSession, cc.detail, cc.detail_textile, cc.materials, cc.materials_textile, cc.sessionDetail, cc.sessionDetail_textile, cc.timeZone, t.created, t.modified
	FROM oncourse_realdata_willow_college.CourseClass cc
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = cc.id
	WHERE t.entityType = 'CourseClass' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_college.Session (angelId, collegeId, courseClassId, created, endDate, id, markerId, modified, roomId, startDate, timeZone)
	SELECT t.angelId, t.collegeId, s.courseClassId, t.created, s.endTimestamp, s.id, s.markerId, t.modified, s.roomId, s.startTimestamp, s.timeZone
	FROM oncourse_realdata_willow_college.Session s
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = s.id
	WHERE t.entityType = 'Session' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_binary.BinaryData (id, angelId,  binaryInfoId, collegeId, content, created, modified)
	SELECT id, angelId, binaryInfoId, collegeId, content, created, modified
	FROM oncourse_realdata_willow_binary.BinaryData WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.BinaryInfo (angelId, collegeId, created, id, modified, name, referenceNumber, bytesize, mimetype, pixelHeight, pixelWidth )
	SELECT t.angelId, t.collegeId, t.created, t.id, t.modified, bio.name, bio.referenceNumber,bio.byteSize, bio.mimeType, bio.pixelHeight,bio.pixelWidth
	FROM oncourse_realdata_willow_college.BinaryInfo as bio
	JOIN oncourse_realdata_willow_college.Taggable as t ON t.entityType = 'BinaryInfo' AND t.collegeId = @collegeId AND t.isDeleted <> 1 AND t.id = bio.id;

INSERT INTO willow_college.BinaryInfoRelation (id, collegeId, binaryInfoId, entityIdentifier, entityWillowId, entityAngelId, angelId, created, modified)
	SELECT bir.id, bir.collegeId, bir.binaryInfoId, bir.entityIdentifier, bir.entityRecordId, bir.entityAngelId, bir.angelId, bir.created, bir.modified
	FROM oncourse_realdata_willow_college.BinaryInfoRelation AS bir
	JOIN willow_college.BinaryInfo AS bi ON bir.binaryInfoId = bi.id
	WHERE bir.collegeId = @collegeId AND bir.isDeleted <> 1;

INSERT INTO willow_college.Certificate (id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes)
	SELECT id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes
	FROM oncourse_realdata_willow_college.Certificate WHERE collegeId = @collegeId AND isDeleted <> 1;


INSERT INTO willow_college.Contact (angelId, collegeId, created, id, modified, businessPhoneNumber, cookieHash, countryID,dateOfBirth,emailAddress,familyName,faxNumber,givenName,homePhoneNumber, isCompany,isMale,isMarketingViaEmailAllowed,isMarketingViaPostAllowed,isMarketingViaSMSAllowed, mobilePhoneNumber,password,postcode,state,street,studentID,suburb,taxFileNumber,tutorID,uniqueCode )
	SELECT t.angelId, t.collegeId, t.created, t.id, t.modified,
	c.businessPhoneNumber,c.cookieHash,c.countryID,c.dateOfBirth,c.emailAddress,c.familyName,
	c.faxNumber,c.givenName,c.homePhoneNumber,c.isCompany,c.isMale,c.isMarketingViaEmailAllowed,
	c.isMarketingViaPostAllowed,c.isMarketingViaSMSAllowed,c.mobilePhoneNumber,c.password,
	c.postcode,c.state,c.street,c.studentID,c.suburb,c.taxFileNumber,c.tutorID,c.uniqueCode
	FROM oncourse_realdata_willow_college.Contact AS c
	JOIN oncourse_realdata_willow_college.Taggable AS t ON c.id = t.id AND t.entityType = 'Contact' AND t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO willow_college.CourseModule (courseId, moduleId, created, modified)
	SELECT courseId, moduleId, created, modified
	FROM oncourse_realdata_willow_college.CourseModule WHERE courseId in (SELECT id FROM willow_college.Course WHERE collegeId = @collegeId);

INSERT INTO willow_college.Discount (id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile)
	SELECT id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile
	FROM oncourse_realdata_willow_college.Discount WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.DiscountConcessionType (concessionTypeId, discountId, collegeId, angelId, created, modified)
	SELECT concessionTypeId, discountId, collegeId, angelId, created, modified
	FROM oncourse_realdata_willow_college.DiscountConcessionType WHERE collegeId = @collegeId AND discountId IN (SELECT id FROM oncourse_realdata_willow_college.Discount);

INSERT INTO willow_college.DiscountCourseClass (courseClassId, discountId, collegeId, angelId, created, modified)
	SELECT dcc.courseClassId, dcc.discountId, dcc.collegeId, dcc.angelId, dcc.created, dcc.modified
	FROM oncourse_realdata_willow_college.DiscountCourseClass AS dcc
	JOIN willow_college.CourseClass AS cc ON dcc.courseClassId = cc.id
	WHERE dcc.collegeId = @collegeId;

INSERT INTO willow_college.Enrolment (id, collegeId, courseClassId, studentId, discountId, angelId, created, modified, reasonForStudy, source, status, statusNotes)
	SELECT e.id, e.collegeId, e.courseClassId, e.studentId, e.discountId, e.angelId, e.created, e.modified, e.reasonForStudy, e.source,
  		CASE
			WHEN (e.status = 1) THEN "Pending"
			WHEN (e.status = 2) THEN "In Transaction"
			WHEN (e.status = 3) THEN "Success"
			WHEN (e.status = 4) THEN "Failed"
			WHEN (e.status = 6) THEN "Failed"
			WHEN (e.status = 7) THEN "Failed"
			WHEN (e.status = 8) THEN "Cancelled"
			WHEN (e.status = 9) THEN "Cancelled"
			ELSE "Failed"
		END
		, CASE
			WHEN (e.status = 6) THEN "Card declined"
			WHEN (e.status = 7) THEN "No places"
			WHEN (e.status = 9) THEN "Credited"
		END
	FROM oncourse_realdata_willow_college.Enrolment AS e
	JOIN willow_college.Student AS s ON e.studentid= s.id
	WHERE e.collegeId = @collegeId AND e.isDeleted <> 1;

INSERT INTO willow_college.Invoice (totalExGst, totalGst, amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT p.totalExGst, p.totalGst, 0, p.collegeId, p.contactID, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM oncourse_realdata_willow_college.Payment AS p
	JOIN willow_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

-- deal with legacy data joined on student id instead of contact id
INSERT INTO willow_college.Invoice (totalExGst, totalGst, amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT p.totalExGst, p.totalGst, 0, p.collegeId, c.id, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM oncourse_realdata_willow_college.Payment p
	JOIN willow_college.Contact c ON p.studentID = c.studentID
	JOIN willow_college.Student AS s ON s.id = p.studentID
	WHERE p.collegeId = @collegeid
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

INSERT INTO willow_college.InvoiceLine (id, collegeId, invoiceId, enrolmentId, priceEachExTax, discountEachExTax, quantity, taxEach, created, modified, title)
	SELECT e.id, e.collegeId, e.paymentId, e.id, cc.feeExGst, case when (e.combinedDiscountExTax is null) then 0.00 else e.combinedDiscountExTax end, 1, case when (cc.feeGst is null) then 0.00 else cc.feeGst end, e.created, e.modified, ''
	FROM oncourse_realdata_willow_college.Enrolment e
	JOIN willow_college.Enrolment as newe ON e.id = newe.id
	JOIN willow_college.Invoice as i ON e.paymentid = i.id
	JOIN willow_college.CourseClass cc ON e.courseClassId = cc.id
	WHERE e.collegeId = @collegeId	AND e.isDeleted <> 1 AND e.paymentId is not null;

INSERT INTO willow_college.InvoiceLine_Discount (invoiceLineId, discountId, created, modified)
	SELECT e.id, e.discountId, e.created, e.modified
	FROM oncourse_realdata_willow_college.Enrolment e WHERE e.collegeId = @collegeId AND e.isDeleted <> 1 AND e.discountId is not null;

UPDATE willow_college.Invoice i
   JOIN willow_college.InvoiceLine il ON i.id = il.invoiceId
   JOIN willow_college.Enrolment e ON e.id = il.enrolmentId
SET
  i.status = CASE
      WHEN (e.status = "Failed") THEN "Failed"
      WHEN (e.status = "Success") THEN "Success"
      WHEN (e.status = "Cancelled") THEN "Success"
  END
WHERE i.collegeId = @collegeid;

INSERT INTO willow_college.LicenseFee (id, college_id, key_code, fee, valid_until, free_transactions, plan_name)
	SELECT id, college_id, `key`, fee, valid_until, free_transactions, plan_name
	FROM oncourse_realdata_willow_college.LicenseFee WHERE college_id = @collegeId;

INSERT INTO willow_college.Log (id, collegeId, userId, created, modified, action, page)
	SELECT id, collegeId, userId, created, modified, action, page
	FROM oncourse_realdata_willow_college.Log WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.Message (angelId, collegeId, created, emailBody, emailSubject, id, modified, smsText)
	SELECT angelId, collegeId, created, emailBody, emailSubject, id, modified, smsText
	FROM oncourse_realdata_willow_college.Message WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM oncourse_realdata_willow_college.MessagePerson AS mp
	INNER JOIN willow_college.Contact as c ON mp.contactid = c.id
	WHERE mp.collegeId = @collegeId AND mp.isDeleted <> 1;

-- legacy messagePerson linked to student rather than contact
INSERT INTO willow_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM oncourse_realdata_willow_college.MessagePerson as mp
	JOIN willow_college.Student as s ON mp.studentid = s.id
	WHERE mp.collegeId = @collegeId
		AND mp.contactId IS NULL AND mp.isDeleted <> 1;


INSERT INTO willow_college.MessageTemplate (angelId, collegeId, created, id, message, modified, name, subject)
	SELECT angelId, collegeId, created, id, message, modified, name, subject
	FROM oncourse_realdata_willow_college.MessageTemplate WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.NotificationTemplate (allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, message, mimeType, modified, name, subject)
	SELECT allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, message, mimeType, modified, name, subject
	FROM oncourse_realdata_willow_college.NotificationTemplate WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.Outcome (angelId, collegeId, created, deliveryMode, endDate, enrolmentId, fundingSource, id, modified, moduleID, priorLearningID, reportableHours, startDate, status)
	SELECT o.angelId, o.collegeId, o.created, o.deliveryMode, o.endDate, o.enrolmentId, o.fundingSource, o.id, o.modified, o.moduleID, o.priorLearningID, o.reportableHours, o.startDate, o.status
	FROM oncourse_realdata_willow_college.Outcome AS o
	JOIN willow_college.Enrolment AS e ON e.id = o.EnrolmentId
	WHERE o.collegeId = @collegeId AND o.isDeleted <> 1;

INSERT INTO willow_college.CertificateOutcome (certificateId, outcomeId, collegeId, angelId, created, modified)
	SELECT co.certificateId, co.outcomeId, co.collegeId, co.angelId, co.created, co.modified
	FROM oncourse_realdata_willow_college.CertificateOutcome as co
	JOIN willow_college.Outcome as o ON co.outcomeid = o.id
	WHERE co.collegeId = @collegeId;

INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, modified, source, oldStatus, studentId, amount, status, statusNotes)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.modified, p.source, p.status, NULL, (p.totalExGst + p.totalGst)
  		, CASE
			WHEN (p.status = 1) THEN "Pending"
			WHEN (p.status = 2) THEN "In Transaction"
			WHEN (p.status = 3) THEN "Success"
			WHEN (p.status = 4) THEN "Failed"
			WHEN (p.status = 6) THEN "Failed"
			WHEN (p.status = 7) THEN "Failed"
			WHEN (p.status = 8) THEN "Refunded"
			WHEN (p.status = 9) THEN "Refunded"
			ELSE "Failed"
		END
		, CASE
			WHEN (p.status = 6) THEN "Card declined"
			WHEN (p.status = 7) THEN "No places"
			WHEN (p.status = 9) THEN "Credited"
		END
	FROM oncourse_realdata_willow_college.Payment AS p
	JOIN willow_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1;

-- fixing legacy payments linked to students
INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, modified, source, oldStatus, studentId, amount, status, statusNotes)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.modified, p.source, p.status, p.studentId, (p.totalExGst + p.totalGst)
  		, CASE
			WHEN (p.status = 1) THEN "Pending"
			WHEN (p.status = 2) THEN "In Transaction"
			WHEN (p.status = 3) THEN "Success"
			WHEN (p.status = 4) THEN "Failed"
			WHEN (p.status = 6) THEN "Failed"
			WHEN (p.status = 7) THEN "Failed"
			WHEN (p.status = 8) THEN "Refunded"
			WHEN (p.status = 9) THEN "Refunded"
			ELSE "Failed"
		END
		, CASE
			WHEN (p.status = 6) THEN "Card declined"
			WHEN (p.status = 7) THEN "No places"
			WHEN (p.status = 9) THEN "Credited"
		END
	FROM oncourse_realdata_willow_college.Payment as p
	JOIN willow_college.Student as s on p.studentid = s.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

INSERT INTO willow_college.PaymentInLine (amount, angelId, created, id, invoiceId, modified, paymentInId)
	SELECT (il.priceEachExTax + il.taxEach + il.discountEachexTax), il.angelId, il.created, il.id, il.invoiceId, 0, il.modified, il.invoiceId
	FROM willow_college.InvoiceLine il
	JOIN willow_college.Invoice i ON i.id = il.invoiceId
	WHERE i.collegeId = @collegeId;

INSERT INTO willow_college.PaymentOut (id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, totalAmount, creditCardCVV, creditCardType)
	SELECT id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, totalAmount, creditCardCVV, creditCardType
	FROM oncourse_realdata_willow_college.PaymentOut WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.PaymentOutTransaction (created, id, isFinalised, modified, paymentOutId, response, txnReference)
	SELECT created, id, isFinalised, modified, paymentOutId, response, txnReference
	FROM oncourse_realdata_willow_college.PaymentOutTransaction WHERE paymentOutId in (SELECT id FROM willow_college.PaymentOut WHERE collegeId = @collegeId) AND isDeleted <> 1;

INSERT INTO willow_college.PaymentTransaction (created, id, isFinalised, modified, paymentId, response, txnReference)
   SELECT created, id, isFinalised, modified, paymentId, response, txnReference
   FROM oncourse_realdata_willow_college.PaymentTransaction WHERE paymentId IN (SELECT id FROM willow_college.PaymentIn WHERE collegeId = @collegeId) AND isDeleted <> 1;

INSERT INTO willow_college.Preference (angelId, collegeId, created, explanation, id, modified, name, sqlType, value)
	SELECT angelId, collegeId, created, explanation, id, modified, name, sqlType, value
	FROM oncourse_realdata_willow_college.Preference WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.QueuedRecord (collegeId, id, lastAttemptTimestamp, numberOfAttempts, entityIdentifier, entityWillowId)
	SELECT collegeId, id, lastAttemptTimestamp, numberOfAttempts, recordEntity, recordId
	FROM oncourse_realdata_willow_college.QueuedRecord WHERE collegeId = @collegeId;

INSERT INTO willow_college.SessionTutor (angelId, collegeId, created, modified, sessionId, tutorId, type)
	SELECT angelId, collegeId, created, modified, sessionId, tutorId, type
	FROM oncourse_realdata_willow_college.SessionTutor
	WHERE collegeId = @collegeId
		AND sessionId IN (SELECT id FROM willow_college.Session WHERE collegeId = @collegeId)
		AND tutorId IN (SELECT id FROM willow_college.Tutor WHERE collegeId = @collegeId);

INSERT INTO willow_college.Tag (angelId, collegeId, created, detail, detail_textile, id, isTagGroup, isWebVisible, modified, name, nodeType, parentId, shortName, weighting)
	SELECT angelId, collegeId, created, detail, detail_textile, id, isTagGroup, isWebVisible, modified, name, nodeType, NULL, shortName, weighting
	FROM oncourse_realdata_willow_college.Tag
	WHERE collegeId = @collegeId AND isDeleted <> 1;

UPDATE willow_college.Tag AS t
	JOIN oncourse_realdata_willow_college.Tag AS tOld ON tOld.id = t.id
	SET t.parentId = tOld.parentId
	WHERE t.collegeId = @collegeId;

INSERT INTO willow_college.Taggable (angelId, collegeId, created, id, modified, entityIdentifier, entityWillowId)
	SELECT angelId, collegeId, created, id, modified, entityType, id
	FROM oncourse_realdata_willow_college.Taggable WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO willow_college.TaggableTag (angelId, collegeId, created, modified, taggableId, tagId)
	SELECT tt.angelId, tt.collegeId, tt.created, tt.modified, tt.taggableId, tt.tagId
	FROM oncourse_realdata_willow_college.TaggableTag AS tt
	JOIN willow_college.Taggable AS t ON tt.taggableid = t.id
	JOIN willow_college.Tag as tag ON tt.tagId = tag.id
	WHERE tt.collegeId = @collegeId AND tt.isDeleted <> 1;

INSERT INTO willow_college.TagGroupRequirement (allowsMultipleTags, angelId, collegeId, created, entityIdentifier, id, isRequired, modified, tagId)
	SELECT tgr.allowsMultipleTags, tgr.angelId, tgr.collegeId, tgr.created, tgr.entityIdentifier, tgr.id, tgr.isRequired, tgr.modified, tgr.tagId
	FROM oncourse_realdata_willow_college.TagGroupRequirement as tgr
	JOIN willow_college.Tag as t ON tgr.tagId = t.id
	 WHERE tgr.collegeId = @collegeId AND tgr.isDeleted <> 1;

INSERT INTO willow_college.TutorRole (angelId, collegeId, courseClassId, created, confirmedDate, detail, detail_textile, isConfirmed, modified, tutorId)
	SELECT tr.angelId, tr.collegeId, tr.courseClassId, tr.created, tr.dateConfirmed, tr.detail,  tr.detail_textile, tr.isConfirmed, tr.modified, tr.tutorId
	FROM oncourse_realdata_willow_college.TutorRole AS tr
	JOIN willow_college.Tutor AS t ON tr.tutorid = t.id
	JOIN willow_college.CourseClass AS cc ON tr.courseclassid=cc.id
	WHERE tr.collegeId = @collegeId tr.isDeleted <> 1;

INSERT INTO willow_college.WaitingList (angelId, collegeId, courseId, created, detail, id, modified, potentialStudents, studentId)
	SELECT wl.angelId, wl.collegeId, wl.courseId, wl.created, wl.detail, wl.id, wl.modified, wl.potentialStudents, wl.studentId
	FROM oncourse_realdata_willow_college.WaitingList as wl
	JOIN willow_college.Student as s on wl.studentid = s.id
	WHERE wl.collegeId = @collegeId AND wl.isDeleted <> 1;

INSERT INTO willow_college.WaitingListSite (siteId, waitingListId)
	SELECT siteId, waitingListId
	FROM oncourse_realdata_willow_college.WaitingListSite
	WHERE siteId IN (SELECT id FROM willow_college.Site WHERE collegeId = @collegeId)
		AND waitingListId IN (SELECT id FROM willow_college.WaitingList WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebSite (  collegeId, created,  id, modified, name, siteKey, googleAnalyticsAccount, googleDirectionsFrom )
	SELECT	ws.collegeId, ws.created, ws.id, ws.modified, ws.name, ws.code, cd.googleAnalyticsAccount, cd.googleDirectionsFrom
	FROM oncourse_realdata_willow_college.WebSite AS ws
	JOIN oncourse_realdata_willow_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND ws.sslHostName = cd.name
	WHERE ws.isDeleted <> 1 AND ws.collegeId = @collegeId AND ws.code IS NOT NULL;

INSERT INTO willow_college.WebHostName (id, collegeId, webSiteId, created, modified, name)
	SELECT id, collegeId, webSiteId, created, modified, name
	FROM oncourse_realdata_willow_college.CollegeDomain WHERE collegeId = @collegeId  AND isDeleted <> 1;

UPDATE willow_college.WebSite AS ws
	JOIN oncourse_realdata_willow_college.WebSite AS wsOld ON wsOld.id = ws.id
	JOIN oncourse_realdata_willow_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND wsOld.sslHostName = cd.name
	SET ws.SSLhostNameId = cd.id
	WHERE ws.collegeId = @collegeId AND ws.isDeleted <> 1;

INSERT INTO willow_college.WebNodeType ( created,  modified, name, layoutKey, webSiteId)
		SELECT	NOW(), NOW(), 'page', 'default', ws.id
	FROM oncourse_realdata_willow_college.WebSite AS ws
	WHERE ws.isDeleted <> 1 AND ws.collegeId = @collegeId ;

-- web blocks go into web content now
INSERT INTO willow_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT	id, content, content_textile, name, webSiteID, created, modified
	FROM oncourse_realdata_willow_college.WebBlock WHERE isDeleted <> 1 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);



INSERT INTO willow_college.WebNode ( created, id, isPublished,	modified, name, nodeNumber,	 webNodeTypeId, webSiteId )
  SELECT wn.created, wn.id, case when (wn.isPublished is null) then 0 else wn.isPublished end,	wn.modified, case when (wn.name is null) then '' else wn.name end, wn.nodeNumber, wnt.id, wn.webSiteId
  FROM oncourse_realdata_willow_college.WebNode as wn
    JOIN willow_college.WebNodeType as wnt ON wnt.webSiteId = wn.webSiteId
  WHERE wn.isDeleted <> 1 AND wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT created, id, modified, urlPath, webNodeId, webSiteId
	FROM oncourse_realdata_willow_college.WebURLAlias WHERE isDeleted <> 1 AND webSiteID in (SELECT id from willow_college.WebSite WHERE collegeId = @collegeId) AND urlPath is not NULL and webNodeId is not NULL;

UPDATE willow_college.WebNode AS wn
	JOIN willow_college.WebNodeType as wnt ON wnt.webSiteId= wn.webSiteId
	JOIN oncourse_realdata_willow_college.WebNode as wnold ON wnold.id=wn.id
	JOIN willow_college.WebURLAlias as wa ON wa.id=wnold.menuAliasID
	SET defaultURLAlias = wnold.menuAliasID
	WHERE wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId) AND wnold.isDeleted <> 1;

INSERT INTO willow_college.WillowUser (angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, passwordHash)
	SELECT angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, null
	FROM oncourse_realdata_willow_college.WillowUser WHERE collegeId = @collegeId AND  isDeleted <> 1;

INSERT INTO willow_college.WebContentVisibility ( WebNodeTypeId, WebContentId, weight,regionKey)
	SELECT wnt.id, wb.id, wb.weighting, wb.regionKey
	FROM oncourse_realdata_willow_college.WebBlock AS wb
	JOIN willow_college.WebNodeType AS wnt ON  wnt.webSiteId = wb.webSiteId
	WHERE wb.isDeleted <> 1 AND wb.regionKey is not NULL AND wb.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

-- web node content goes into WebContent
INSERT INTO willow_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT id + 1000, content, content_textile, NULL, webSiteId, NOW(), NOW()
	FROM oncourse_realdata_willow_college.WebNode WHERE isDeleted <> 1 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebContentVisibility ( WebNodeId, WebContentId, weight,regionKey)
	SELECT wn.id, wn.id + 1000, 0, 'content'
	FROM willow_college.WebNode AS wn
	JOIN willow_college.WebSite AS ws ON  ws.id = wn.webSiteId
	WHERE ws.collegeId = @collegeId;


CREATE TABLE willow_college.WebMenuTEMP (
  `webNodeId` BIGINT(20) ,
  `webSiteId` BIGINT(20),
  `webMenuParentId` BIGINT(20),
  `weight` INT,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci'
);

INSERT INTO willow_college.WebMenuTEMP (webNodeId, webSiteId, webMenuParentId, weight, name)
	SELECT wn.id, wn.webSiteID, wn.parentNodeID, wn.weighting, case when (wn.shortName is null) then wn.name else wn.shortName end
	FROM oncourse_realdata_willow_college.WebNode AS wn
	JOIN willow_college.WebSite AS ws ON ws.id = wn.webSiteId
	WHERE wn.isDeleted <> 1 AND wn.isWebVisible = 1 AND wn.isPublished AND wn.isWebNavigable = 1 AND ws.collegeId = @collegeId;

use willow_college;
-- do this many times to clean up the whole menu tree
DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM willow_college.WebMenuTEMP AS w
	LEFT OUTER JOIN willow_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;


INSERT INTO willow_college.WebMenu (id, webNodeId, URL, webSiteId, webMenuParentId, weight, name, created, modified)
	SELECT webNodeId, webNodeId, '', webSiteId, NULL, weight, name, NOW(), NOW()
	FROM willow_college.WebMenuTEMP;

-- we need to do the parent references as a separate step since we have constraints
UPDATE willow_college.WebMenu AS wm
	JOIN willow_college.WebMenuTEMP AS temp ON temp.webNodeId = wm.webNodeId
	SET wm.webMenuParentId = temp.webMenuParentId;

DROP TABLE willow_college.WebMenuTEMP;


UPDATE willow_college.WebHostName AS wh
	JOIN willow_college.WebSite AS ws ON ws.id = wh.webSiteId
	SET wh.name=CONCAT(ws.siteKey, '.dev.oncourse.net.au')
	WHERE wh.name LIKE '%.test.oncourse.net.au';

-- add special aliases for the home page
INSERT INTO willow_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT NOW(), id + 10000, NOW(), '/', id, webSiteId
	FROM oncourse_realdata_willow_college.WebNode
	WHERE isDeleted <> 1 AND isWebVisible = 1
	AND parentNodeID IS NULL AND name like 'Home page'
	AND  webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.Attendance (id, collegeId, angelId, studentId, sessionId, attendanceType, markerId, created, modified)
	SELECT a.id, a.collegeId, a.angelId, a.studentId, a.sessionId, a.attendanceType, a.markerId, a.created, a.modified
	FROM oncourse_realdata_willow_college.Attendance AS a
	JOIN willow_college.Session AS s ON s.id = a.sessionId
	JOIN willow_college.Student AS st ON st.id = a.studentId
	WHERE a.collegeId = @collegeId;
	
	
-- change compound primary keys to auto_increment

--CertificateOutcome
alter table willow_college.CertificateOutcome drop foreign key CertificateOutcome_ibfk_2;
alter table willow_college.CertificateOutcome drop foreign key CertificateOutcome_ibfk_3;
alter table willow_college.CertificateOutcome drop primary key;
alter table willow_college.CertificateOutcome add column id BIGINT;
update willow_college.CertificateOutcome set id=certificateId + (outcomeId << 32);
update willow_college.CertificateOutcome co set co.angelId=(select c.angelId from willow_college.Certificate c where c.id=co.certificateId) + ((select o.angelId from willow_college.Outcome o where o.id=co.outcomeId) << 32);
alter table willow_college.CertificateOutcome change column id id BIGINT not null primary key auto_increment;

alter table willow_college.CertificateOutcome add CONSTRAINT co_certificateIdFk FOREIGN KEY (certificateId) references willow_college.Certificate(id);
alter table willow_college.CertificateOutcome add CONSTRAINT co_outcomeIdFk FOREIGN KEY  (outcomeId) references willow_college.Outcome(id);
alter table willow_college.CertificateOutcome add CONSTRAINT co_cert_outcome_uq UNIQUE index  (certificateId, outcomeId);

--end CertificateOutcome 

-- CourseModule

alter table willow_college.CourseModule drop foreign key CourseModule_ibfk_1;
alter table willow_college.CourseModule drop primary key;
alter table willow_college.CourseModule add column id BIGINT;
alter table willow_college.CourseModule add column angelId BIGINT;

update willow_college.CourseModule set id=courseId + (moduleId << 32);
update willow_college.CourseModule cm set cm.angelId=(select c.angelId from willow_college.Course c where c.id=cm.courseId) + (cm.moduleId << 32);

alter table willow_college.CourseModule change column id id BIGINT not null primary key auto_increment;
alter table willow_college.CourseModule add CONSTRAINT cm_courseIdFk FOREIGN KEY (courseId) references willow_college.Course(id);
-- Note! add foreign key to willow_reference.module (id)
alter table willow_college.CourseModule add CONSTRAINT cm_course_mod_uq UNIQUE index (courseId, moduleId);

-- end CourseModule

-- DiscountConcessionType

alter table willow_college.DiscountConcessionType drop foreign key DiscountConcessionType_ibfk_2;
alter table willow_college.DiscountConcessionType drop foreign key DiscountConcessionType_ibfk_3;

alter table willow_college.DiscountConcessionType drop primary key;

alter table willow_college.DiscountConcessionType add column id BIGINT;

update willow_college.DiscountConcessionType set id=concessionTypeId + (discountId << 32);
update willow_college.DiscountConcessionType dct set dct.angelId=(select ct.angelId from willow_college.ConcessionType ct where ct.id=dct.concessionTypeId) + ((select dc.angelId from willow_college.Discount dc where dc.id=dct.discountId) << 32);

alter table willow_college.DiscountConcessionType change column id id BIGINT not null primary key auto_increment;
alter table willow_college.DiscountConcessionType add CONSTRAINT dct_concessionTypeIdfk FOREIGN KEY(concessionTypeId) references willow_college.ConcessionType(id);
alter table willow_college.DiscountConcessionType add CONSTRAINT dct_discountIdfk FOREIGN KEY (discountId) references willow_college.Discount(id);
alter table willow_college.DiscountConcessionType add CONSTRAINT dct_discCon_uq UNIQUE index (concessionTypeId, discountId);

-- end DiscountConcessionType

--DiscountCourseClass
alter table willow_college.DiscountCourseClass drop foreign key DiscountCourseClass_ibfk_2;
alter table willow_college.DiscountCourseClass drop foreign key DiscountCourseClass_ibfk_3;

alter table willow_college.DiscountCourseClass drop primary key;

alter table willow_college.DiscountCourseClass add column id BIGINT;

update willow_college.DiscountCourseClass set id=courseClassId + (discountId << 32);
update willow_college.DiscountCourseClass dcc set dcc.angelId=(select cc.angelId from willow_college.CourseClass cc where cc.id=dcc.courseClassId) + ((select dc.angelId from willow_college.Discount dc where dc.id=dcc.discountId) << 32);
	
alter table willow_college.DiscountCourseClass change column id id BIGINT not null primary key auto_increment;
alter table willow_college.DiscountCourseClass add CONSTRAINT dcc_courseClassIdfk FOREIGN KEY(courseClassId) references willow_college.CourseClass(id);
alter table willow_college.DiscountCourseClass add CONSTRAINT dcc_discountIdfk FOREIGN KEY (discountId) references willow_college.Discount(id);
alter table willow_college.DiscountCourseClass add CONSTRAINT dcc_discCC_uq UNIQUE index (courseClassId, discountId);

--end DiscountCourseClass

-- InvoiceLine_Discount

alter table willow_college.InvoiceLine_Discount drop foreign key InvoiceLine_Discount_ibfk_1;
alter table willow_college.InvoiceLine_Discount drop foreign key InvoiceLine_Discount_ibfk_2;

alter table willow_college.InvoiceLine_Discount drop primary key;
alter table willow_college.InvoiceLine_Discount add column id BIGINT;

update willow_college.InvoiceLine_Discount set id=discountId + (invoiceLineId << 32);

alter table willow_college.InvoiceLine_Discount add column angelId BIGINT;	
	
update willow_college.InvoiceLine_Discount inld set inld.angelId=(select dc.angelId from willow_college.Discount dc where dc.id=inld.discountId) + ((select inl.angelId from willow_college.InvoiceLine inl where inl.id=inld.invoiceLineId) << 32);
	
alter table willow_college.InvoiceLine_Discount change column id id BIGINT not null primary key auto_increment;

alter table willow_college.InvoiceLine_Discount add column collegeId BIGINT not null;

update willow_college.InvoiceLine_Discount inld set inld.collegeId=(select d.collegeId from willow_college.Discount d where d.id=inld.discountId);

alter table willow_college.InvoiceLine_Discount add CONSTRAINT inld_InvLineIdfk FOREIGN KEY(invoiceLineId) references willow_college.InvoiceLine(id);
alter table willow_college.InvoiceLine_Discount add CONSTRAINT inld_discountIdfk FOREIGN KEY (discountId) references willow_college.Discount(id);
alter table willow_college.InvoiceLine_Discount add CONSTRAINT inld_disInvLin_uq UNIQUE index (discountId, invoiceLineId);

-- end InvoiceLine_Discount

-- SessionTutor

alter table willow_college.SessionTutor drop foreign key SessionTutor_ibfk_2;
alter table willow_college.SessionTutor drop foreign key SessionTutor_ibfk_3;

alter table willow_college.SessionTutor drop primary key;

alter table willow_college.SessionTutor add column id BIGINT;

update willow_college.SessionTutor set id=sessionId + (tutorId << 32);
update willow_college.SessionTutor st set st.angelId=(select s.angelId from willow_college.Session s where s.id=st.sessionId) + ((select t.angelId from willow_college.Tutor t where t.id=st.tutorId) << 32);
	
alter table willow_college.SessionTutor change column id id BIGINT not null primary key auto_increment;
alter table willow_college.SessionTutor add CONSTRAINT st_sessionIdfk FOREIGN KEY(sessionId) references willow_college.Session(id);
alter table willow_college.SessionTutor add CONSTRAINT st_tutorIdfk FOREIGN KEY (tutorId) references willow_college.Tutor(id);
alter table willow_college.SessionTutor add CONSTRAINT st_sTut_uq UNIQUE index (sessionId, tutorId);

--end SessionTutor

-- TutorRole
alter table willow_college.TutorRole drop foreign key TutorRole_ibfk_2;
alter table willow_college.TutorRole drop foreign key TutorRole_ibfk_3;

alter table willow_college.TutorRole drop primary key;

alter table willow_college.TutorRole add column id BIGINT;

update willow_college.TutorRole set id=courseClassId + (tutorId << 32);
update willow_college.TutorRole tr set tr.angelId=(select cc.angelId from willow_college.CourseClass cc where cc.id=tr.courseClassId) + ((select t.angelId from willow_college.Tutor t where t.id=tr.tutorId) << 32);

alter table willow_college.TutorRole change column id id BIGINT not null primary key auto_increment;

alter table willow_college.TutorRole add CONSTRAINT tur_courseClassIdfk FOREIGN KEY(courseClassId) references willow_college.CourseClass(id);
alter table willow_college.TutorRole add CONSTRAINT tur_tutorIdfk FOREIGN KEY (tutorId) references willow_college.Tutor(id);
alter table willow_college.TutorRole add CONSTRAINT tur_ccTr_uq UNIQUE index (courseClassId, tutorId);

-- end TutorRole

-- WaitingListSite

alter table willow_college.WaitingListSite drop foreign key WaitingListSite_ibfk_1;
alter table willow_college.WaitingListSite drop foreign key WaitingListSite_ibfk_2;

alter table willow_college.WaitingListSite drop primary key;

alter table willow_college.WaitingListSite add column id BIGINT;
alter table willow_college.WaitingListSite add column angelId BIGINT;

update willow_college.WaitingListSite set id=siteId + (waitingListId << 32);
update willow_college.WaitingListSite wls set wls.angelId=(select s.angelId from willow_college.Site s where s.id=wls.siteId) + ((select w.angelId from willow_college.WaitingList w where w.id=wls.waitingListId) << 32);

alter table willow_college.WaitingListSite change column id id BIGINT not null primary key auto_increment;

alter table willow_college.WaitingListSite add CONSTRAINT wls_wlIdfk FOREIGN KEY (waitingListId) references willow_college.WaitingList(id); 
alter table willow_college.WaitingListSite add CONSTRAINT wls_siteIdfk FOREIGN KEY (siteId) references willow_college.Site(id);
alter table willow_college.WaitingListSite add CONSTRAINT wls_wlStuq UNIQUE index (siteId, waitingListId);

-- end WaitingListSite

-- TaggableTag

alter table willow_college.TaggableTag drop foreign key TaggableTag_ibfk_2;
alter table willow_college.TaggableTag drop foreign key TaggableTag_ibfk_3;

alter table willow_college.TaggableTag drop primary key;

alter table willow_college.TaggableTag add column id BIGINT;

update willow_college.TaggableTag set id=tagId + (taggableId << 32);
update willow_college.TaggableTag tt set tt.angelId=(select t.angelId from willow_college.Tag t where t.id=tt.tagId) + ((select ta.angelId from willow_college.Taggable ta where ta.id=tt.taggableId) << 32);
	
alter table willow_college.TaggableTag change column id id BIGINT not null primary key auto_increment;

alter table willow_college.TaggableTag add CONSTRAINT tt_tagIdfk FOREIGN KEY (tagId) references willow_college.Tag(id); 
alter table willow_college.TaggableTag add CONSTRAINT tt_taggabeIdfk FOREIGN KEY (taggableId) references willow_college.Taggable(id);
alter table willow_college.TaggableTag add CONSTRAINT tt_tTagable_uq UNIQUE index (tagId, taggableId);

-- end TaggableTag
