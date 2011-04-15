-- set the college you wish to migrate

SET @collegeId = %COLLEGEID%;

INSERT INTO %DESTINATIONDB%_college.College (id, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled,
	isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified,
	name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType,
	webServicesLogin, webServicesPass, firstRemoteAuthentication, lastRemoteAuthentication)
	SELECT id, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified, name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType, webServicesLogin, webServicesPass, firstRemoteAuthentication, lastRemoteAuthentication
	FROM %SOURCEDB%_college.College
	WHERE id = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Tutor (angelId, collegeId, created, modified, id, startDate, finishDate, resume, resume_textile)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.modified, t.id, t.dateStarted, t.dateFinished, t.resume, t.resume_textile
	FROM %SOURCEDB%_college.Tutor t
	JOIN %SOURCEDB%_college.Taggable tag ON tag.id = t.id
	WHERE tag.entityType = 'Tutor' AND t.id IN (SELECT tutorId FROM %SOURCEDB%_college.Contact WHERE collegeId = @collegeId) AND tag.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Student (angelId, collegeId, created, modified, id, countryOfBirthId, languageId, concessionType, disabilityType, englishProficiency, highestSchoolLevel, indigenousStatus, isOverseasClient, isStillAtSchool, priorEducationCode, yearSchoolCompleted, labourForceType)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.modified, s.id, s.countryOfBirthId, s.languageId, s.concessionType, s.disabilityType, s.englishProficiency, s.highestSchoolLevel, s.indigenousStatus, s.isOverseasClient, s.isStillAtSchool, s.priorEducationCode, s.yearSchoolCompleted, s.labourForceType
	FROM %SOURCEDB%_college.Student s
	JOIN %SOURCEDB%_college.Taggable tag ON tag.id = s.id
	WHERE tag.entityType = 'Student' AND s.id IN (SELECT studentId FROM %SOURCEDB%_college.Contact WHERE collegeId = @collegeId) AND tag.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.ConcessionType (id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isEnabled, requiresCredentialCheck, name)
	SELECT id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isEnabled, requiresCredentialCheck, name
	FROM %SOURCEDB%_college.ConcessionType WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.StudentConcession (angelId, authorisationExpiresOn, authorisedOn, collegeId, concessionNumber, concessionTypeId, created, expiresOn, id, modified, studentId, timeZone)
	SELECT sc.angelId, sc.authorisationExpiresOn, sc.authorisedOn, sc.collegeId, sc.concessionNumber, sc.concessionTypeId, sc.created, sc.expiresOn, sc.id, sc.modified, sc.studentId, sc.timeZone
	FROM %SOURCEDB%_college.StudentConcession AS sc
	JOIN %SOURCEDB%_college.Taggable AS t ON t.id = sc.studentId
	JOIN %DESTINATIONDB%_college.Student AS s ON sc.studentId=s.id
	WHERE sc.collegeId = @collegeId AND sc.isDeleted <> 1 AND t.entityType like 'Student';

INSERT INTO %DESTINATIONDB%_college.Course (id, collegeId, qualificationId, angelId, code, isWebVisible, isVETCourse, isSufficientForQualification, allowWaitingList, nominalHours, name, detail, detail_textile, fieldOfEducation, searchText, created, modified)
	SELECT c.id, t.collegeId, c.qualificationId, t.angelId, c.code, c.isWebVisible, c.isVETCourse, c.isSufficientForQualification, c.allowWaitingList, c.nominalHours, c.name, c.detail, c.detail_textile, c.fieldOfEducation, c.searchText, t.created, t.modified
	FROM %SOURCEDB%_college.Course c
	JOIN %SOURCEDB%_college.Taggable t ON t.id = c.id
	WHERE t.entityType = 'Course' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Site (countryId, drivingDirections, drivingDirections_textile, id, isWebVisible, latitude, longitude, name, postcode, publicTransportDirections, publicTransportDirections_textile, specialInstructions, specialInstructions_textile, state, street, suburb, timeZone, collegeId, angelId, created, modified)
	SELECT s.countryId, s.drivingDirections, s.drivingDirections_textile, s.id, s.isWebVisible, s.latitude, s.longitude, s.name, s.postcode, s.publicTransportDirections, s.publicTransportDirections_textile, s.specialInstructions, s.specialInstructions_textile, s.state, s.street, s.suburb, s.timeZone, t.collegeId, t.angelId, t.created, t.modified
	FROM %SOURCEDB%_college.Site s
	JOIN %SOURCEDB%_college.Taggable t ON t.id = s.id
	WHERE t.entityType = 'Site' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Room (capacity, directions, directions_textile, facilities, facilities_textile, id, name, siteId, collegeId, angelId, created, modified)
	SELECT r.capacity, r.directions, r.directions_textile, r.facilities, r.facilities_textile, r.id, r.name, r.siteId, t.collegeId, t.angelId, t.created, t.modified
	FROM %SOURCEDB%_college.Room r
	JOIN %SOURCEDB%_college.Taggable t ON t.id = r.id
	WHERE t.entityType = 'Room' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.CourseClass (id, collegeId, angelId, code, courseId, roomId, startDate, endDate, isWebVisible, isCancelled, maximumPlaces, minimumPlaces, countOfSessions, deliveryMode, feeExGst, feeGst, minutesPerSession, startingMinutePerSession, detail, detail_textile, materials, materials_textile, sessionDetail, sessionDetail_textile, timeZone, created, modified)
	SELECT cc.id, t.collegeId, t.angelId, cc.code, cc.courseId, cc.roomId, cc.startDate, cc.endDate, cc.isWebVisible, cc.isCancelled, cc.maximumPlaces, cc.minimumPlaces, cc.countOfSessions, cc.deliveryMode, cc.feeExGst, cc.feeGst, cc.minutesPerSession, cc.startingMinutePerSession, cc.detail, cc.detail_textile, cc.materials, cc.materials_textile, cc.sessionDetail, cc.sessionDetail_textile, cc.timeZone, t.created, t.modified
	FROM %SOURCEDB%_college.CourseClass cc
	JOIN %SOURCEDB%_college.Taggable t ON t.id = cc.id
	WHERE t.entityType = 'CourseClass' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Session (angelId, collegeId, courseClassId, created, endDate, id, markerId, modified, roomId, startDate, timeZone)
	SELECT t.angelId, t.collegeId, s.courseClassId, t.created, s.endTimestamp, s.id, s.markerId, t.modified, s.roomId, s.startTimestamp, s.timeZone
	FROM %SOURCEDB%_college.Session s
	JOIN %SOURCEDB%_college.Taggable t ON t.id = s.id
	WHERE t.entityType = 'Session' and t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_binary.BinaryData (id, angelId,  binaryInfoId, collegeId, content, created, modified)
	SELECT id, angelId, binaryInfoId, collegeId, content, created, modified
	FROM %SOURCEDB%_binary.BinaryData WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.BinaryInfo (angelId, collegeId, created, id, modified, name, referenceNumber, bytesize, mimetype, pixelHeight, pixelWidth )
	SELECT t.angelId, t.collegeId, t.created, t.id, t.modified, bio.name, bio.referenceNumber,bio.byteSize, bio.mimeType, bio.pixelHeight,bio.pixelWidth
	FROM %SOURCEDB%_college.BinaryInfo as bio
	JOIN %SOURCEDB%_college.Taggable as t ON t.entityType = 'BinaryInfo' AND t.collegeId = @collegeId AND t.isDeleted <> 1 AND t.id = bio.id;

INSERT INTO %DESTINATIONDB%_college.BinaryInfoRelation (id, collegeId, binaryInfoId, entityIdentifier, entityWillowId, entityAngelId, angelId, created, modified)
	SELECT bir.id, bir.collegeId, bir.binaryInfoId, bir.entityIdentifier, bir.entityRecordId, bir.entityAngelId, bir.angelId, bir.created, bir.modified
	FROM %SOURCEDB%_college.BinaryInfoRelation AS bir
	JOIN %DESTINATIONDB%_college.BinaryInfo AS bi ON bir.binaryInfoId = bi.id
	WHERE bir.collegeId = @collegeId AND bir.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Certificate (id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes)
	SELECT id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes
	FROM %SOURCEDB%_college.Certificate WHERE collegeId = @collegeId AND isDeleted <> 1;


INSERT INTO %DESTINATIONDB%_college.Contact (angelId, collegeId, created, id, modified, businessPhoneNumber, cookieHash, countryID,dateOfBirth,emailAddress,familyName,faxNumber,givenName,homePhoneNumber, isCompany,isMale,isMarketingViaEmailAllowed,isMarketingViaPostAllowed,isMarketingViaSMSAllowed, mobilePhoneNumber,password,postcode,state,street,studentID,suburb,taxFileNumber,tutorID,uniqueCode )
	SELECT t.angelId, t.collegeId, t.created, t.id, t.modified,
	c.businessPhoneNumber,c.cookieHash,c.countryID,c.dateOfBirth,c.emailAddress,c.familyName,
	c.faxNumber,c.givenName,c.homePhoneNumber,c.isCompany,c.isMale,c.isMarketingViaEmailAllowed,
	c.isMarketingViaPostAllowed,c.isMarketingViaSMSAllowed,c.mobilePhoneNumber,c.password,
	c.postcode,c.state,c.street,c.studentID,c.suburb,c.taxFileNumber,c.tutorID,c.uniqueCode
	FROM %SOURCEDB%_college.Contact AS c
	JOIN %SOURCEDB%_college.Taggable AS t ON c.id = t.id AND t.entityType = 'Contact' AND t.collegeId = @collegeId AND t.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.CourseModule (courseId, moduleId, created, modified)
	SELECT courseId, moduleId, created, modified
	FROM %SOURCEDB%_college.CourseModule WHERE courseId in (SELECT id FROM %DESTINATIONDB%_college.Course WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.Discount (id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile)
	SELECT id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile
	FROM %SOURCEDB%_college.Discount WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.DiscountConcessionType (concessionTypeId, discountId, collegeId, angelId, created, modified)
	SELECT concessionTypeId, discountId, collegeId, angelId, created, modified
	FROM %SOURCEDB%_college.DiscountConcessionType WHERE collegeId = @collegeId AND discountId IN (SELECT id FROM %SOURCEDB%_college.Discount);

INSERT INTO %DESTINATIONDB%_college.DiscountCourseClass (courseClassId, discountId, collegeId, angelId, created, modified)
	SELECT dcc.courseClassId, dcc.discountId, dcc.collegeId, dcc.angelId, dcc.created, dcc.modified
	FROM %SOURCEDB%_college.DiscountCourseClass AS dcc
	JOIN %DESTINATIONDB%_college.CourseClass AS cc ON dcc.courseClassId = cc.id
	WHERE dcc.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.Enrolment (id, collegeId, courseClassId, studentId, angelId, created, modified, reasonForStudy, source, status, statusNotes)
	SELECT e.id, e.collegeId, e.courseClassId, e.studentId, e.angelId, e.created, e.modified, e.reasonForStudy, e.source,
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
	FROM %SOURCEDB%_college.Enrolment AS e
	JOIN %DESTINATIONDB%_college.Student AS s ON e.studentid= s.id
	WHERE e.collegeId = @collegeId AND e.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Invoice (totalExGst, totalGst, amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT p.totalExGst, p.totalGst, 0, p.collegeId, p.contactID, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM %SOURCEDB%_college.Payment AS p
	JOIN %DESTINATIONDB%_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

-- deal with legacy data joined on student id instead of contact id
INSERT INTO %DESTINATIONDB%_college.Invoice (totalExGst, totalGst, amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT p.totalExGst, p.totalGst, 0, p.collegeId, c.id, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM %SOURCEDB%_college.Payment p
	JOIN %DESTINATIONDB%_college.Contact c ON p.studentID = c.studentID
	JOIN %DESTINATIONDB%_college.Student AS s ON s.id = p.studentID
	WHERE p.collegeId = @collegeid
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

INSERT INTO %DESTINATIONDB%_college.InvoiceLine (id, collegeId, invoiceId, enrolmentId, priceEachExTax, discountEachExTax, quantity, taxEach, created, modified, title)
	SELECT e.id, e.collegeId, e.paymentId, e.id, cc.feeExGst, case when (e.combinedDiscountExTax is null) then 0.00 else e.combinedDiscountExTax end, 1, case when (cc.feeGst is null) then 0.00 else cc.feeGst end, e.created, e.modified, ''
	FROM %SOURCEDB%_college.Enrolment e
	JOIN %DESTINATIONDB%_college.Enrolment as newe ON e.id = newe.id
	JOIN %DESTINATIONDB%_college.Invoice as i ON e.paymentid = i.id
	JOIN %DESTINATIONDB%_college.CourseClass cc ON e.courseClassId = cc.id
	WHERE e.collegeId = @collegeId	AND e.isDeleted <> 1 AND e.paymentId is not null;

INSERT INTO %DESTINATIONDB%_college.InvoiceLine_Discount (invoiceLineId, discountId, created, modified)
	SELECT e.id, e.discountId, e.created, e.modified
	FROM %SOURCEDB%_college.Enrolment e WHERE e.collegeId = @collegeId AND e.isDeleted <> 1 AND e.discountId is not null;

UPDATE %DESTINATIONDB%_college.Invoice i
   JOIN %DESTINATIONDB%_college.InvoiceLine il ON i.id = il.invoiceId
   JOIN %DESTINATIONDB%_college.Enrolment e ON e.id = il.enrolmentId
SET
  i.status = CASE
      WHEN (e.status = "Failed") THEN "Failed"
      WHEN (e.status = "Success") THEN "Success"
      WHEN (e.status = "Cancelled") THEN "Success"
  END
WHERE i.collegeId = @collegeid;

INSERT INTO %DESTINATIONDB%_college.LicenseFee (id, college_id, key_code, fee, valid_until, free_transactions, plan_name)
	SELECT id, college_id, `key`, fee, valid_until, free_transactions, plan_name
	FROM %SOURCEDB%_college.LicenseFee WHERE college_id = @collegeId;

INSERT INTO %DESTINATIONDB%_college.Log (id, collegeId, userId, created, modified, action, page)
	SELECT id, collegeId, userId, created, modified, action, page
	FROM %SOURCEDB%_college.Log WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Message (angelId, collegeId, created, emailBody, emailSubject, id, modified, smsText)
	SELECT angelId, collegeId, created, emailBody, emailSubject, id, modified, smsText
	FROM %SOURCEDB%_college.Message WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM %SOURCEDB%_college.MessagePerson AS mp
	INNER JOIN %DESTINATIONDB%_college.Contact as c ON mp.contactid = c.id
	WHERE mp.collegeId = @collegeId AND mp.isDeleted <> 1;

-- legacy messagePerson linked to student rather than contact
INSERT INTO %DESTINATIONDB%_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM %SOURCEDB%_college.MessagePerson as mp
	JOIN %DESTINATIONDB%_college.Student as s ON mp.studentid = s.id
	WHERE mp.collegeId = @collegeId
		AND mp.contactId IS NULL AND mp.isDeleted <> 1;


INSERT INTO %DESTINATIONDB%_college.MessageTemplate (angelId, collegeId, created, id, message, modified, name, subject)
	SELECT angelId, collegeId, created, id, message, modified, name, subject
	FROM %SOURCEDB%_college.MessageTemplate WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.NotificationTemplate (allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, message, mimeType, modified, name, subject)
	SELECT allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, message, mimeType, modified, name, subject
	FROM %SOURCEDB%_college.NotificationTemplate WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Outcome (angelId, collegeId, created, deliveryMode, endDate, enrolmentId, fundingSource, id, modified, moduleID, priorLearningID, reportableHours, startDate, status)
	SELECT o.angelId, o.collegeId, o.created, o.deliveryMode, o.endDate, o.enrolmentId, o.fundingSource, o.id, o.modified, o.moduleID, o.priorLearningID, o.reportableHours, o.startDate, o.status
	FROM %SOURCEDB%_college.Outcome AS o
	JOIN %DESTINATIONDB%_college.Enrolment AS e ON e.id = o.EnrolmentId
	WHERE o.collegeId = @collegeId AND o.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.CertificateOutcome (certificateId, outcomeId, collegeId, angelId, created, modified)
	SELECT co.certificateId, co.outcomeId, co.collegeId, co.angelId, co.created, co.modified
	FROM %SOURCEDB%_college.CertificateOutcome as co
	JOIN %DESTINATIONDB%_college.Outcome as o ON co.outcomeid = o.id
	WHERE co.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, modified, source, studentId, amount, status, statusNotes)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.modified, p.source, NULL, (p.totalExGst + p.totalGst)
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
	FROM %SOURCEDB%_college.Payment AS p
	JOIN %DESTINATIONDB%_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1;

-- fixing legacy payments linked to students
INSERT INTO %DESTINATIONDB%_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, modified, source, studentId, amount, status, statusNotes)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.modified, p.source, p.studentId, (p.totalExGst + p.totalGst)
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
	FROM %SOURCEDB%_college.Payment as p
	JOIN %DESTINATIONDB%_college.Student as s on p.studentid = s.id
	WHERE p.collegeId = @collegeId
		AND p.isDeleted <> 1
		AND p.contactId IS NULL
		AND p.studentId IS NOT NULL;

INSERT INTO %DESTINATIONDB%_college.PaymentInLine (amount, angelId, created, id, invoiceId, modified, paymentInId)
	SELECT (il.priceEachExTax + il.taxEach + il.discountEachexTax), il.angelId, il.created, il.id, il.invoiceId, il.modified, il.invoiceId
	FROM %DESTINATIONDB%_college.InvoiceLine il
	JOIN %DESTINATIONDB%_college.Invoice i ON i.id = il.invoiceId
	WHERE i.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.PaymentOut (id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, totalAmount, creditCardCVV, creditCardType)
	SELECT id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, totalAmount, creditCardCVV, creditCardType
	FROM %SOURCEDB%_college.PaymentOut WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.PaymentOutTransaction (created, id, isFinalised, modified, paymentOutId, response, txnReference)
	SELECT created, id, isFinalised, modified, paymentOutId, response, txnReference
	FROM %SOURCEDB%_college.PaymentOutTransaction WHERE paymentOutId in (SELECT id FROM %DESTINATIONDB%_college.PaymentOut WHERE collegeId = @collegeId) AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.PaymentTransaction (created, id, isFinalised, modified, paymentId, response, txnReference)
   SELECT created, id, isFinalised, modified, paymentId, response, txnReference
   FROM %SOURCEDB%_college.PaymentTransaction WHERE paymentId IN (SELECT id FROM %DESTINATIONDB%_college.PaymentIn WHERE collegeId = @collegeId) AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.Preference (angelId, collegeId, created, explanation, id, modified, name, sqlType, value)
	SELECT angelId, collegeId, created, explanation, id, modified, name, sqlType, value
	FROM %SOURCEDB%_college.Preference WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.QueuedRecord (collegeId, id, lastAttemptTimestamp, numberOfAttempts, entityIdentifier, entityWillowId)
	SELECT collegeId, id, lastAttemptTimestamp, numberOfAttempts, recordEntity, recordId
	FROM %SOURCEDB%_college.QueuedRecord WHERE collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.SessionTutor (angelId, collegeId, created, modified, sessionId, tutorId, type)
	SELECT angelId, collegeId, created, modified, sessionId, tutorId, type
	FROM %SOURCEDB%_college.SessionTutor
	WHERE collegeId = @collegeId
		AND sessionId IN (SELECT id FROM %DESTINATIONDB%_college.Session WHERE collegeId = @collegeId)
		AND tutorId IN (SELECT id FROM %DESTINATIONDB%_college.Tutor WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.Tag (angelId, collegeId, created, detail, detail_textile, id, isTagGroup, isWebVisible, modified, name, nodeType, parentId, shortName, weighting)
	SELECT angelId, collegeId, created, detail, detail_textile, id, isTagGroup, isWebVisible, modified, name, nodeType, NULL, shortName, weighting
	FROM %SOURCEDB%_college.Tag
	WHERE collegeId = @collegeId AND isDeleted <> 1;

UPDATE %DESTINATIONDB%_college.Tag AS t
	JOIN %SOURCEDB%_college.Tag AS tOld ON tOld.id = t.id
	SET t.parentId = tOld.parentId
	WHERE t.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.Taggable (angelId, collegeId, created, id, modified, entityIdentifier, entityWillowId)
	SELECT angelId, collegeId, created, id, modified, entityType, id
	FROM %SOURCEDB%_college.Taggable WHERE collegeId = @collegeId AND isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.TaggableTag (angelId, collegeId, created, modified, taggableId, tagId)
	SELECT tt.angelId, tt.collegeId, tt.created, tt.modified, tt.taggableId, tt.tagId
	FROM %SOURCEDB%_college.TaggableTag AS tt
	JOIN %DESTINATIONDB%_college.Taggable AS t ON tt.taggableid = t.id
	JOIN %DESTINATIONDB%_college.Tag as tag ON tt.tagId = tag.id
	WHERE tt.collegeId = @collegeId AND tt.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.TagGroupRequirement (allowsMultipleTags, angelId, collegeId, created, entityIdentifier, id, isRequired, modified, tagId)
	SELECT tgr.allowsMultipleTags, tgr.angelId, tgr.collegeId, tgr.created, tgr.entityIdentifier, tgr.id, tgr.isRequired, tgr.modified, tgr.tagId
	FROM %SOURCEDB%_college.TagGroupRequirement as tgr
	JOIN %DESTINATIONDB%_college.Tag as t ON tgr.tagId = t.id
	 WHERE tgr.collegeId = @collegeId AND tgr.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.TutorRole (angelId, collegeId, courseClassId, created, confirmedDate, detail, detail_textile, isConfirmed, modified, tutorId)
	SELECT tr.angelId, tr.collegeId, tr.courseClassId, tr.created, tr.dateConfirmed, tr.detail,  tr.detail_textile, tr.isConfirmed, tr.modified, tr.tutorId
	FROM %SOURCEDB%_college.TutorRole AS tr
	JOIN %DESTINATIONDB%_college.Tutor AS t ON tr.tutorid = t.id
	JOIN %DESTINATIONDB%_college.CourseClass AS cc ON tr.courseclassid=cc.id
	WHERE tr.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.WaitingList (angelId, collegeId, courseId, created, detail, id, modified, potentialStudents, studentId)
	SELECT wl.angelId, wl.collegeId, wl.courseId, wl.created, wl.detail, wl.id, wl.modified, wl.potentialStudents, wl.studentId
	FROM %SOURCEDB%_college.WaitingList as wl
	JOIN %DESTINATIONDB%_college.Student as s on wl.studentid = s.id
	WHERE wl.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.WaitingListSite (siteId, waitingListId)
	SELECT siteId, waitingListId
	FROM %SOURCEDB%_college.WaitingListSite
	WHERE siteId IN (SELECT id FROM %DESTINATIONDB%_college.Site WHERE collegeId = @collegeId)
		AND waitingListId IN (SELECT id FROM %DESTINATIONDB%_college.WaitingList WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.WebSite (  collegeId, created,  id, modified, name, siteKey, googleAnalyticsAccount, googleDirectionsFrom )
	SELECT	ws.collegeId, ws.created, ws.id, ws.modified, ws.name, ws.code, cd.googleAnalyticsAccount, cd.googleDirectionsFrom
	FROM %SOURCEDB%_college.WebSite AS ws
	JOIN %SOURCEDB%_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND ws.sslHostName = cd.name
	WHERE ws.isDeleted <> 1 AND ws.collegeId = @collegeId AND ws.code IS NOT NULL;

INSERT INTO %DESTINATIONDB%_college.WebHostName (id, collegeId, webSiteId, created, modified, name)
	SELECT id, collegeId, webSiteId, created, modified, name
	FROM %SOURCEDB%_college.CollegeDomain WHERE collegeId = @collegeId  AND isDeleted <> 1;

UPDATE %DESTINATIONDB%_college.WebSite AS ws
	JOIN %SOURCEDB%_college.WebSite AS wsOld ON wsOld.id = ws.id
	JOIN %SOURCEDB%_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND wsOld.sslHostName = cd.name
	SET ws.SSLhostNameId = cd.id
	WHERE ws.collegeId = @collegeId;

INSERT INTO %DESTINATIONDB%_college.WebNodeType ( created,  modified, name, layoutKey, webSiteId)
		SELECT	NOW(), NOW(), 'page', 'default', ws.id
	FROM %SOURCEDB%_college.WebSite AS ws
	WHERE ws.isDeleted <> 1 AND ws.collegeId = @collegeId ;

-- web blocks go into web content now
INSERT INTO %DESTINATIONDB%_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT	id, content, content_textile, name, webSiteID, created, modified
	FROM %SOURCEDB%_college.WebBlock WHERE isDeleted <> 1 AND webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.WebNode ( created, id, isPublished,	modified, name, nodeNumber,	 webNodeTypeId, webSiteId )
  SELECT wn.created, wn.id, case when (wn.isPublished is null) then 0 else wn.isPublished end,	wn.modified, case when (wn.name is null) then '' else wn.name end, wn.nodeNumber, wnt.id, wn.webSiteId
  FROM %SOURCEDB%_college.WebNode as wn
    JOIN %DESTINATIONDB%_college.WebNodeType as wnt ON wnt.webSiteId = wn.webSiteId
  WHERE wn.isDeleted <> 1 AND wn.webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT created, id, modified, urlPath, webNodeId, webSiteId
	FROM %SOURCEDB%_college.WebURLAlias WHERE isDeleted <> 1 AND webSiteID in (SELECT id from %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId) AND urlPath is not NULL and webNodeId is not NULL;

UPDATE %DESTINATIONDB%_college.WebNode AS wn
	JOIN %DESTINATIONDB%_college.WebNodeType as wnt ON wnt.webSiteId= wn.webSiteId
	JOIN %SOURCEDB%_college.WebNode as wnold ON wnold.id=wn.id
	JOIN %DESTINATIONDB%_college.WebURLAlias as wa ON wa.id=wnold.menuAliasID
	SET defaultURLAlias = wnold.menuAliasID
	WHERE wn.webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId) AND wnold.isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.WillowUser (angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, passwordHash)
	SELECT angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, null
	FROM %SOURCEDB%_college.WillowUser WHERE collegeId = @collegeId AND  isDeleted <> 1;

INSERT INTO %DESTINATIONDB%_college.WebContentVisibility ( WebNodeTypeId, WebContentId, weight,regionKey)
	SELECT wnt.id, wb.id, wb.weighting, wb.regionKey
	FROM %SOURCEDB%_college.WebBlock AS wb
	JOIN %DESTINATIONDB%_college.WebNodeType AS wnt ON  wnt.webSiteId = wb.webSiteId
	WHERE wb.isDeleted <> 1 AND wb.regionKey is not NULL AND wb.webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId);

-- web node content goes into WebContent
INSERT INTO %DESTINATIONDB%_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT id + 1000, content, content_textile, NULL, webSiteId, NOW(), NOW()
	FROM %SOURCEDB%_college.WebNode WHERE isDeleted <> 1 AND webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.WebContentVisibility ( WebNodeId, WebContentId, weight,regionKey)
	SELECT wn.id, wn.id + 1000, 0, 'content'
	FROM %DESTINATIONDB%_college.WebNode AS wn
	JOIN %DESTINATIONDB%_college.WebSite AS ws ON  ws.id = wn.webSiteId
	WHERE ws.collegeId = @collegeId;


CREATE TABLE %DESTINATIONDB%_college.WebMenuTEMP (
  `webNodeId` BIGINT(20) ,
  `webSiteId` BIGINT(20),
  `webMenuParentId` BIGINT(20),
  `weight` INT,
  `name` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci'
);

INSERT INTO %DESTINATIONDB%_college.WebMenuTEMP (webNodeId, webSiteId, webMenuParentId, weight, name)
	SELECT wn.id, wn.webSiteID, wn.parentNodeID, wn.weighting, case when (wn.shortName is null) then wn.name else wn.shortName end
	FROM %SOURCEDB%_college.WebNode AS wn
	JOIN %DESTINATIONDB%_college.WebSite AS ws ON ws.id = wn.webSiteId
	WHERE wn.isDeleted <> 1 AND wn.isWebVisible = 1 AND wn.isPublished AND wn.isWebNavigable = 1 AND ws.collegeId = @collegeId;

use %DESTINATIONDB%_college;
-- do this many times to clean up the whole menu tree
DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;

DELETE w FROM %DESTINATIONDB%_college.WebMenuTEMP AS w
	LEFT OUTER JOIN %DESTINATIONDB%_college.WebMenuTEMP AS parent ON parent.webNodeId = w.webMenuParentId
	WHERE parent.webNodeId IS NULL AND w.webMenuParentId IS NOT NULL;


INSERT INTO %DESTINATIONDB%_college.WebMenu (id, webNodeId, URL, webSiteId, webMenuParentId, weight, name, created, modified)
	SELECT webNodeId, webNodeId, '', webSiteId, NULL, weight, name, NOW(), NOW()
	FROM %DESTINATIONDB%_college.WebMenuTEMP;

-- we need to do the parent references as a separate step since we have constraints
UPDATE %DESTINATIONDB%_college.WebMenu AS wm
	JOIN %DESTINATIONDB%_college.WebMenuTEMP AS temp ON temp.webNodeId = wm.webNodeId
	SET wm.webMenuParentId = temp.webMenuParentId;

DROP TABLE %DESTINATIONDB%_college.WebMenuTEMP;


UPDATE %DESTINATIONDB%_college.WebHostName AS wh
	JOIN %DESTINATIONDB%_college.WebSite AS ws ON ws.id = wh.webSiteId
	SET wh.name=CONCAT(ws.siteKey, '.dev.oncourse.net.au')
	WHERE wh.name LIKE '%.test.oncourse.net.au';

-- add special aliases for the home page
INSERT INTO %DESTINATIONDB%_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT NOW(), id + 10000, NOW(), '/', id, webSiteId
	FROM %SOURCEDB%_college.WebNode
	WHERE isDeleted <> 1 AND isWebVisible = 1
	AND parentNodeID IS NULL AND name like 'Home page'
	AND  webSiteId IN (SELECT id FROM %DESTINATIONDB%_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO %DESTINATIONDB%_college.Attendance (id, collegeId, angelId, studentId, sessionId, attendanceType, markerId, created, modified)
	SELECT a.id, a.collegeId, a.angelId, a.studentId, a.sessionId, a.attendanceType, a.markerId, a.created, a.modified
	FROM %SOURCEDB%_college.Attendance AS a
	JOIN %DESTINATIONDB%_college.Session AS s ON s.id = a.sessionId
	JOIN %DESTINATIONDB%_college.Student AS st ON st.id = a.studentId
	WHERE a.collegeId = @collegeId;
	
	
-- change compound primary keys to auto_increment

-- CertificateOutcome
alter table %DESTINATIONDB%_college.CertificateOutcome drop foreign key CertificateOutcome_ibfk_2;
alter table %DESTINATIONDB%_college.CertificateOutcome drop foreign key CertificateOutcome_ibfk_3;
alter table %DESTINATIONDB%_college.CertificateOutcome drop primary key;
alter table %DESTINATIONDB%_college.CertificateOutcome add column id BIGINT;
update %DESTINATIONDB%_college.CertificateOutcome set id=certificateId + (outcomeId << 32);
update %DESTINATIONDB%_college.CertificateOutcome co set co.angelId=(select c.angelId from %DESTINATIONDB%_college.Certificate c where c.id=co.certificateId) + ((select o.angelId from %DESTINATIONDB%_college.Outcome o where o.id=co.outcomeId) << 32);
alter table %DESTINATIONDB%_college.CertificateOutcome change column id id BIGINT not null primary key auto_increment;

alter table %DESTINATIONDB%_college.CertificateOutcome add CONSTRAINT co_certificateIdFk FOREIGN KEY (certificateId) references %DESTINATIONDB%_college.Certificate(id);
alter table %DESTINATIONDB%_college.CertificateOutcome add CONSTRAINT co_outcomeIdFk FOREIGN KEY  (outcomeId) references %DESTINATIONDB%_college.Outcome(id);
alter table %DESTINATIONDB%_college.CertificateOutcome add CONSTRAINT co_cert_outcome_uq UNIQUE index  (certificateId, outcomeId);

-- end CertificateOutcome 

-- CourseModule

alter table %DESTINATIONDB%_college.CourseModule drop foreign key CourseModule_ibfk_1;
alter table %DESTINATIONDB%_college.CourseModule drop primary key;
alter table %DESTINATIONDB%_college.CourseModule add column id BIGINT;
alter table %DESTINATIONDB%_college.CourseModule add column angelId BIGINT;

update %DESTINATIONDB%_college.CourseModule set id=courseId + (moduleId << 32);
update %DESTINATIONDB%_college.CourseModule cm set cm.angelId=(select c.angelId from %DESTINATIONDB%_college.Course c where c.id=cm.courseId) + (cm.moduleId << 32);

alter table %DESTINATIONDB%_college.CourseModule change column id id BIGINT not null primary key auto_increment;
alter table %DESTINATIONDB%_college.CourseModule add CONSTRAINT cm_courseIdFk FOREIGN KEY (courseId) references %DESTINATIONDB%_college.Course(id);
-- Note! add foreign key to %DESTINATIONDB%_reference.module (id)
alter table %DESTINATIONDB%_college.CourseModule add CONSTRAINT cm_course_mod_uq UNIQUE index (courseId, moduleId);

-- end CourseModule

-- DiscountConcessionType

alter table %DESTINATIONDB%_college.DiscountConcessionType drop foreign key DiscountConcessionType_ibfk_2;
alter table %DESTINATIONDB%_college.DiscountConcessionType drop foreign key DiscountConcessionType_ibfk_3;

alter table %DESTINATIONDB%_college.DiscountConcessionType drop primary key;

alter table %DESTINATIONDB%_college.DiscountConcessionType add column id BIGINT;

update %DESTINATIONDB%_college.DiscountConcessionType set id=concessionTypeId + (discountId << 32);
update %DESTINATIONDB%_college.DiscountConcessionType dct set dct.angelId=(select ct.angelId from %DESTINATIONDB%_college.ConcessionType ct where ct.id=dct.concessionTypeId) + ((select dc.angelId from %DESTINATIONDB%_college.Discount dc where dc.id=dct.discountId) << 32);

alter table %DESTINATIONDB%_college.DiscountConcessionType change column id id BIGINT not null primary key auto_increment;
alter table %DESTINATIONDB%_college.DiscountConcessionType add CONSTRAINT dct_concessionTypeIdfk FOREIGN KEY(concessionTypeId) references %DESTINATIONDB%_college.ConcessionType(id);
alter table %DESTINATIONDB%_college.DiscountConcessionType add CONSTRAINT dct_discountIdfk FOREIGN KEY (discountId) references %DESTINATIONDB%_college.Discount(id);
alter table %DESTINATIONDB%_college.DiscountConcessionType add CONSTRAINT dct_discCon_uq UNIQUE index (concessionTypeId, discountId);

-- end DiscountConcessionType

--DiscountCourseClass
alter table %DESTINATIONDB%_college.DiscountCourseClass drop foreign key DiscountCourseClass_ibfk_2;
alter table %DESTINATIONDB%_college.DiscountCourseClass drop foreign key DiscountCourseClass_ibfk_3;

alter table %DESTINATIONDB%_college.DiscountCourseClass drop primary key;

alter table %DESTINATIONDB%_college.DiscountCourseClass add column id BIGINT;

update %DESTINATIONDB%_college.DiscountCourseClass set id=courseClassId + (discountId << 32);
update %DESTINATIONDB%_college.DiscountCourseClass dcc set dcc.angelId=(select cc.angelId from %DESTINATIONDB%_college.CourseClass cc where cc.id=dcc.courseClassId) + ((select dc.angelId from %DESTINATIONDB%_college.Discount dc where dc.id=dcc.discountId) << 32);
	
alter table %DESTINATIONDB%_college.DiscountCourseClass change column id id BIGINT not null primary key auto_increment;
alter table %DESTINATIONDB%_college.DiscountCourseClass add CONSTRAINT dcc_courseClassIdfk FOREIGN KEY(courseClassId) references %DESTINATIONDB%_college.CourseClass(id);
alter table %DESTINATIONDB%_college.DiscountCourseClass add CONSTRAINT dcc_discountIdfk FOREIGN KEY (discountId) references %DESTINATIONDB%_college.Discount(id);
alter table %DESTINATIONDB%_college.DiscountCourseClass add CONSTRAINT dcc_discCC_uq UNIQUE index (courseClassId, discountId);

--end DiscountCourseClass

-- InvoiceLine_Discount

alter table %DESTINATIONDB%_college.InvoiceLine_Discount drop foreign key InvoiceLine_Discount_ibfk_1;
alter table %DESTINATIONDB%_college.InvoiceLine_Discount drop foreign key InvoiceLine_Discount_ibfk_2;

alter table %DESTINATIONDB%_college.InvoiceLine_Discount drop primary key;
alter table %DESTINATIONDB%_college.InvoiceLine_Discount add column id BIGINT;

update %DESTINATIONDB%_college.InvoiceLine_Discount set id=discountId + (invoiceLineId << 32);

alter table %DESTINATIONDB%_college.InvoiceLine_Discount add column angelId BIGINT;	
	
update %DESTINATIONDB%_college.InvoiceLine_Discount inld set inld.angelId=(select dc.angelId from %DESTINATIONDB%_college.Discount dc where dc.id=inld.discountId) + ((select inl.angelId from %DESTINATIONDB%_college.InvoiceLine inl where inl.id=inld.invoiceLineId) << 32);
	
alter table %DESTINATIONDB%_college.InvoiceLine_Discount change column id id BIGINT not null primary key auto_increment;

alter table %DESTINATIONDB%_college.InvoiceLine_Discount add column collegeId BIGINT not null;

update %DESTINATIONDB%_college.InvoiceLine_Discount inld set inld.collegeId=(select d.collegeId from %DESTINATIONDB%_college.Discount d where d.id=inld.discountId);

alter table %DESTINATIONDB%_college.InvoiceLine_Discount add CONSTRAINT inld_InvLineIdfk FOREIGN KEY(invoiceLineId) references %DESTINATIONDB%_college.InvoiceLine(id);
alter table %DESTINATIONDB%_college.InvoiceLine_Discount add CONSTRAINT inld_discountIdfk FOREIGN KEY (discountId) references %DESTINATIONDB%_college.Discount(id);
alter table %DESTINATIONDB%_college.InvoiceLine_Discount add CONSTRAINT inld_disInvLin_uq UNIQUE index (discountId, invoiceLineId);

-- end InvoiceLine_Discount

-- SessionTutor

alter table %DESTINATIONDB%_college.SessionTutor drop foreign key SessionTutor_ibfk_2;
alter table %DESTINATIONDB%_college.SessionTutor drop foreign key SessionTutor_ibfk_3;

alter table %DESTINATIONDB%_college.SessionTutor drop primary key;

alter table %DESTINATIONDB%_college.SessionTutor add column id BIGINT;

update %DESTINATIONDB%_college.SessionTutor set id=sessionId + (tutorId << 32);
update %DESTINATIONDB%_college.SessionTutor st set st.angelId=(select s.angelId from %DESTINATIONDB%_college.Session s where s.id=st.sessionId) + ((select t.angelId from %DESTINATIONDB%_college.Tutor t where t.id=st.tutorId) << 32);
	
alter table %DESTINATIONDB%_college.SessionTutor change column id id BIGINT not null primary key auto_increment;
alter table %DESTINATIONDB%_college.SessionTutor add CONSTRAINT st_sessionIdfk FOREIGN KEY(sessionId) references %DESTINATIONDB%_college.Session(id);
alter table %DESTINATIONDB%_college.SessionTutor add CONSTRAINT st_tutorIdfk FOREIGN KEY (tutorId) references %DESTINATIONDB%_college.Tutor(id);
alter table %DESTINATIONDB%_college.SessionTutor add CONSTRAINT st_sTut_uq UNIQUE index (sessionId, tutorId);

--end SessionTutor

-- TutorRole
alter table %DESTINATIONDB%_college.TutorRole drop foreign key TutorRole_ibfk_2;
alter table %DESTINATIONDB%_college.TutorRole drop foreign key TutorRole_ibfk_3;

alter table %DESTINATIONDB%_college.TutorRole drop primary key;

alter table %DESTINATIONDB%_college.TutorRole add column id BIGINT;

update %DESTINATIONDB%_college.TutorRole set id=courseClassId + (tutorId << 32);
update %DESTINATIONDB%_college.TutorRole tr set tr.angelId=(select cc.angelId from %DESTINATIONDB%_college.CourseClass cc where cc.id=tr.courseClassId) + ((select t.angelId from %DESTINATIONDB%_college.Tutor t where t.id=tr.tutorId) << 32);

alter table %DESTINATIONDB%_college.TutorRole change column id id BIGINT not null primary key auto_increment;

alter table %DESTINATIONDB%_college.TutorRole add CONSTRAINT tur_courseClassIdfk FOREIGN KEY(courseClassId) references %DESTINATIONDB%_college.CourseClass(id);
alter table %DESTINATIONDB%_college.TutorRole add CONSTRAINT tur_tutorIdfk FOREIGN KEY (tutorId) references %DESTINATIONDB%_college.Tutor(id);
alter table %DESTINATIONDB%_college.TutorRole add CONSTRAINT tur_ccTr_uq UNIQUE index (courseClassId, tutorId);

-- end TutorRole

-- WaitingListSite

alter table %DESTINATIONDB%_college.WaitingListSite drop foreign key WaitingListSite_ibfk_1;
alter table %DESTINATIONDB%_college.WaitingListSite drop foreign key WaitingListSite_ibfk_2;

alter table %DESTINATIONDB%_college.WaitingListSite drop primary key;

alter table %DESTINATIONDB%_college.WaitingListSite add column id BIGINT;
alter table %DESTINATIONDB%_college.WaitingListSite add column angelId BIGINT;

update %DESTINATIONDB%_college.WaitingListSite set id=siteId + (waitingListId << 32);
update %DESTINATIONDB%_college.WaitingListSite wls set wls.angelId=(select s.angelId from %DESTINATIONDB%_college.Site s where s.id=wls.siteId) + ((select w.angelId from %DESTINATIONDB%_college.WaitingList w where w.id=wls.waitingListId) << 32);

alter table %DESTINATIONDB%_college.WaitingListSite change column id id BIGINT not null primary key auto_increment;

alter table %DESTINATIONDB%_college.WaitingListSite add CONSTRAINT wls_wlIdfk FOREIGN KEY (waitingListId) references %DESTINATIONDB%_college.WaitingList(id); 
alter table %DESTINATIONDB%_college.WaitingListSite add CONSTRAINT wls_siteIdfk FOREIGN KEY (siteId) references %DESTINATIONDB%_college.Site(id);
alter table %DESTINATIONDB%_college.WaitingListSite add CONSTRAINT wls_wlStuq UNIQUE index (siteId, waitingListId);

-- end WaitingListSite

-- TaggableTag

alter table %DESTINATIONDB%_college.TaggableTag drop foreign key TaggableTag_ibfk_2;
alter table %DESTINATIONDB%_college.TaggableTag drop foreign key TaggableTag_ibfk_3;

alter table %DESTINATIONDB%_college.TaggableTag drop primary key;

alter table %DESTINATIONDB%_college.TaggableTag add column id BIGINT;

update %DESTINATIONDB%_college.TaggableTag set id=tagId + (taggableId << 32);
update %DESTINATIONDB%_college.TaggableTag tt set tt.angelId=(select t.angelId from %DESTINATIONDB%_college.Tag t where t.id=tt.tagId) + ((select ta.angelId from %DESTINATIONDB%_college.Taggable ta where ta.id=tt.taggableId) << 32);
	
alter table %DESTINATIONDB%_college.TaggableTag change column id id BIGINT not null primary key auto_increment;

alter table %DESTINATIONDB%_college.TaggableTag add CONSTRAINT tt_tagIdfk FOREIGN KEY (tagId) references %DESTINATIONDB%_college.Tag(id); 
alter table %DESTINATIONDB%_college.TaggableTag add CONSTRAINT tt_taggabeIdfk FOREIGN KEY (taggableId) references %DESTINATIONDB%_college.Taggable(id);
alter table %DESTINATIONDB%_college.TaggableTag add CONSTRAINT tt_tTagable_uq UNIQUE index (tagId, taggableId);

-- end TaggableTag

-- Set the services key

UPDATE %DESTINATIONDB%_college.College SET webServicesSecurityCode =
	(SELECT webServicesSecurityCode
	FROM %SOURCEDB%_college.College
	WHERE id = @collegeId AND isDeleted <> 1)
	WHERE id = @collegeId;
	
-- end Set the services key