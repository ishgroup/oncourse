set foreign_key_checks = 0 ;

SET @collegeId = 10;

INSERT INTO willow_college.Attendance (id, collegeId, angelId, studentId, sessionId, attendanceType, markerId, created, modified) 
	SELECT id, collegeId, angelId, studentId, sessionId, attendanceType, markerId, created, modified 
	FROM oncourse_realdata_willow_college.Attendance WHERE collegeId = @collegeId;

INSERT INTO willow_binary.BinaryData (id, angelId,  binaryInfoId, collegeId, content, created, isDeleted, modified) 
       SELECT id, angelId, binaryInfoId, collegeId, content, created, isDeleted, modified
       FROM oncourse_realdata_willow_binary.BinaryData WHERE collegeId = @collegeId;

INSERT INTO willow_college.BinaryInfoRelation (id, collegeId, isDeleted, binaryInfoId, entityIdentifier, entityWillowId, entityAngelId, angelId, created, modified) 
	SELECT id, collegeId, isDeleted, binaryInfoId, entityIdentifier, entityRecordId, entityAngelId, angelId, created, modified 
	FROM oncourse_realdata_willow_college.BinaryInfoRelation WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.BinaryInfo (angelId, collegeId, created, id, isDeleted, modified)
	SELECT angelId, collegeId, created, id, isDeleted, modified
	FROM oncourse_realdata_willow_college.Taggable WHERE entityType = 'BinaryInfo' and collegeId = @collegeId AND isDeleted = 0;

UPDATE willow_college.BinaryInfo bin
	JOIN oncourse_realdata_willow_college.BinaryInfo bio ON bio.id = bin.id 
	SET  bin.isWebVisible = bio.isWebVisible
		, bin.name = bio.name
		, bin.referenceNumber = bio.referenceNumber
		, bin.byteSize = bio.byteSize
		, bin.mimeType = bio.mimeType
		, bin.pixelHeight = bio.pixelHeight
		, bin.pixelWidth = bio.pixelWidth;

INSERT INTO willow_college.Certificate (id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isDeleted, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes)
	SELECT id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isDeleted, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes
	FROM oncourse_realdata_willow_college.Certificate WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.CertificateOutcome (certificateId, outcomeId, collegeId, angelId, created, modified) 
	SELECT certificateId, outcomeId, collegeId, angelId, created, modified 
	FROM oncourse_realdata_willow_college.CertificateOutcome WHERE collegeId = @collegeId;

INSERT INTO willow_college.ChangeRequest (id, collegeId, status, created, modified, identifier, result)
	SELECT id, collegeId, status, created, modified, identifier, result
	FROM oncourse_realdata_willow_college.ChangeRequest WHERE collegeId = @collegeId;

INSERT INTO willow_college.ChangeRequestItem (id, changeRequestId, mainEntityName, mainEntityId, secondEntityId, secondEntityName, sequence, action, keyPath, newValueInteger, newValueString, relationship, result)
	SELECT id, changeRequestId, mainEntityName, mainEntityId, secondEntityId, secondEntityName, sequence, action, keyPath, newValueInteger, newValueString, relationship, result 
	FROM oncourse_realdata_willow_college.ChangeRequestItem WHERE changeRequestId IN (SELECT id FROM oncourse_realdata_willow_college.ChangeRequest WHERE collegeId = @collegeId);

INSERT INTO willow_college.College (id, isDeleted, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified, 
		name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType, webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication) 
	SELECT id,  isDeleted, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified,
	       name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType, webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication 
	FROM oncourse_realdata_willow_college.College WHERE id = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.WebHostName (id, collegeId, webSiteId, created, modified, name) 
	SELECT id, collegeId, webSiteId, created, modified, name
	FROM oncourse_realdata_willow_college.CollegeDomain WHERE collegeId = @collegeId  AND isDeleted = 0;

INSERT INTO willow_college.ConcessionType (id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isDeleted, isEnabled, requiresCredentialCheck, name) 
	SELECT id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isDeleted, isEnabled, requiresCredentialCheck, name
	FROM oncourse_realdata_willow_college.ConcessionType WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Contact (angelId, collegeId, created, id, isDeleted, modified) 
	SELECT angelId, collegeId, created, id, isDeleted, modified
	FROM oncourse_realdata_willow_college.Taggable WHERE entityType = 'Contact' and collegeId = @collegeId AND isDeleted = 0;

UPDATE willow_college.Contact cn
	JOIN oncourse_realdata_willow_college.Contact co ON cn.id = co.id
	SET	  cn.businessPhoneNumber = co.businessPhoneNumber
		, cn.cookieHash = co.cookieHash
		, cn.countryId = co.countryID
		, cn.dateOfBirth = co.dateOfBirth
		, cn.emailAddress = co.emailAddress
		, cn.familyName = co.familyName
		, cn.faxNumber = co.faxNumber
		, cn.givenName = co.givenName
		, cn.homePhoneNumber = co.homePhoneNumber
		, cn.isCompany = co.isCompany
		, cn.isMale = co.isMale
		, cn.isMarketingViaEmailAllowed = co.isMarketingViaEmailAllowed
		, cn.isMarketingViaPostAllowed = co.isMarketingViaPostAllowed
		, cn.isMarketingViaSMSAllowed = co.isMarketingViaSMSAllowed
		, cn.mobilePhoneNumber = co.mobilePhoneNumber
		, cn.password = co.password
		, cn.postcode = co.postcode
		, cn.state = co.state
		, cn.street = co.street
		, cn.studentId = co.studentID
		, cn.suburb = co.suburb
		, cn.taxFileNumber = co.taxFileNumber
		, cn.tutorId = co.tutorID
		, cn.uniqueCode = co.uniqueCode;

INSERT INTO willow_college.Student (angelId, collegeId, created, isDeleted, modified, id, countryOfBirthId, languageId, concessionType, disabilityType, englishProficiency, highestSchoolLevel, indigenousStatus, isOverseasClient, isStillAtSchool, priorEducationCode, yearSchoolCompleted, labourForceType)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.isDeleted, tag.modified, s.id, s.countryOfBirthId, s.languageId, s.concessionType, s.disabilityType, s.englishProficiency, s.highestSchoolLevel, s.indigenousStatus, s.isOverseasClient, s.isStillAtSchool, s.priorEducationCode, s.yearSchoolCompleted, s.labourForceType
	FROM oncourse_realdata_willow_college.Student s 
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = s.id AND tag.isDeleted = 0
	WHERE tag.entityType = 'Student' AND s.id IN (SELECT studentId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId);

INSERT INTO willow_college.Tutor (angelId, collegeId, created, isDeleted, modified, id, startDate, finishDate, resume, resume_textile)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.isDeleted, tag.modified, t.id, t.dateStarted, t.dateFinished, t.resume, t.resume_textile
	FROM oncourse_realdata_willow_college.Tutor t
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = t.id AND tag.isDeleted = 0
	WHERE tag.entityType = 'Tutor' AND t.id IN (SELECT tutorId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId);

INSERT INTO willow_college.Course (id, collegeId, qualificationId, angelId, code, isWebVisible, isVETCourse, isSufficientForQualification, allowWaitingList, nominalHours, name, detail, detail_textile, fieldOfEducation, searchText, isDeleted, created, modified)
	SELECT c.id, t.collegeId, c.qualificationId, t.angelId, c.code, c.isWebVisible, c.isVETCourse, c.isSufficientForQualification, c.allowWaitingList, c.nominalHours, c.name, c.detail, c.detail_textile, c.fieldOfEducation, c.searchText, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.Course c
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = c.id AND t.isDeleted = 0
	WHERE t.entityType = 'Course' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.CourseClass (id, collegeId, angelId, code, courseId, roomId, startDate, endDate, isWebVisible, isCancelled, maximumPlaces, minimumPlaces, countOfSessions, deliveryMode, feeExGst, feeGst, minutesPerSession, startingMinutePerSession, detail, detail_textile, materials, materials_textile, sessionDetail, sessionDetail_textile, timeZone, isDeleted, created, modified)
	SELECT cc.id, t.collegeId, t.angelId, cc.code, cc.courseId, cc.roomId, cc.startDate, cc.endDate, cc.isWebVisible, cc.isCancelled, cc.maximumPlaces, cc.minimumPlaces, cc.countOfSessions, cc.deliveryMode, cc.feeExGst, cc.feeGst, cc.minutesPerSession, cc.startingMinutePerSession, cc.detail, cc.detail_textile, cc.materials, cc.materials_textile, cc.sessionDetail, cc.sessionDetail_textile, cc.timeZone, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.CourseClass cc
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = cc.id AND t.isDeleted = 0
	WHERE t.entityType = 'CourseClass' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.CourseModule (courseId, moduleId, created, modified)
	SELECT courseId, moduleId, created, modified
	FROM oncourse_realdata_willow_college.CourseModule WHERE courseId in (SELECT id FROM Course WHERE collegeId = @collegeId);

INSERT INTO willow_college.Discount (id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, isDeleted, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile)
	SELECT id, collegeId, angelId, code, validFrom, validTo, combinationType, created, modified, discountAmount, discountRate, isCodeRequired, isDeleted, maximumDiscount, minimumDiscount, name, roundingMode, studentAge, studentAgeOperator, studentEnrolledWithinDays, studentPostcodes, studentsQualifier, timeZone, detail, detail_textile
	FROM oncourse_realdata_willow_college.Discount WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.DiscountConcessionType (concessionTypeId, discountId, collegeId, angelId, created, modified)
	SELECT concessionTypeId, discountId, collegeId, angelId, created, modified
	FROM oncourse_realdata_willow_college.DiscountConcessionType WHERE collegeId = @collegeId AND discountId IN (SELECT id FROM Discount);

INSERT INTO willow_college.DiscountCourseClass (courseClassId, discountId, collegeId, angelId, created, modified)
	SELECT courseClassId, discountId, collegeId, angelId, created, modified
	FROM oncourse_realdata_willow_college.DiscountCourseClass WHERE collegeId = @collegeId AND discountId IN (SELECT id FROM Discount);

INSERT INTO willow_college.Enrolment (id, collegeId, courseClassId, studentId, discountId, angelId, isDeleted, created, modified, reasonForStudy, source, status)
	SELECT id, collegeId, courseClassId, studentId, discountId, angelId, isDeleted, created, modified, reasonForStudy, source, status
	FROM oncourse_realdata_willow_college.Enrolment WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.DiscountEnrolment (discountId, enrolmentId)
	SELECT discountId, enrolmentId
	FROM oncourse_realdata_willow_college.DiscountEnrolment WHERE enrolmentId IN (SELECT id FROM Enrolment WHERE collegeId = @collegeId);

INSERT INTO willow_college.Invoice (amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT 0, collegeId, contactID, created, created, id, created, id, modified, source
	FROM oncourse_realdata_willow_college.Payment 
	WHERE collegeId = @collegeId 
		AND (contactId IS NOT NULL)
		AND (contactId IN (SELECT id FROM oncourse_realdata_willow_college.Contact));

INSERT INTO willow_college.Invoice (amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT 0, p.collegeId, c.id, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM oncourse_realdata_willow_college.Payment p
	JOIN Contact c ON p.studentID = c.studentID
	WHERE p.collegeId = @collegeId 
		AND p.isDeleted = 0
		AND p.contactId IS NULL 
		AND p.studentId IS NOT NULL
		AND (p.studentId IN (SELECT id FROM oncourse_realdata_willow_college.Student));

INSERT INTO willow_college.InvoiceLine (id, collegeId, invoiceId, enrolmentId, priceEachExTax, discountEachExTax, quantity, taxEach, created, modified, title)
	SELECT e.id, e.collegeId, e.paymentId, e.id, cc.feeExGst, case when (e.combinedDiscountExTax is null) then 0.00 else e.combinedDiscountExTax end, 1, case when (cc.feeGst is null) then 0.00 else cc.feeGst end, e.created, e.modified, ''
	FROM oncourse_realdata_willow_college.Enrolment e
	JOIN CourseClass cc ON e.courseClassId = cc.id
	WHERE e.collegeId = @collegeId  AND e.isDeleted = 0 AND e.paymentId is not null;

INSERT INTO willow_college.InvoiceLine_Discount (invoiceLineId, discountId, created, modified)
	SELECT e.id, e.discountId, e.created, e.modified
	FROM oncourse_realdata_willow_college.Enrolment e WHERE e.collegeId = @collegeId AND e.isDeleted = 0 AND e.discountId is not null;

INSERT INTO willow_college.LicenseFee (id, college_id, key_code, fee, valid_until, free_transactions, plan_name)
	SELECT id, college_id, `key`, fee, valid_until, free_transactions, plan_name
	FROM oncourse_realdata_willow_college.LicenseFee WHERE college_id = @collegeId;

INSERT INTO willow_college.Log (id, collegeId, userId, isDeleted, created, modified, action, page)
	SELECT id, collegeId, userId, isDeleted, created, modified, action, page
	FROM oncourse_realdata_willow_college.Log WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Message (angelId, collegeId, created, emailBody, emailSubject, id, isDeleted, modified, smsText)
	SELECT angelId, collegeId, created, emailBody, emailSubject, id, isDeleted, modified, smsText
	FROM oncourse_realdata_willow_college.Message WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, isDeleted, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT angelId, collegeId, contactID, created, destinationAddress, id, isDeleted, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type
	FROM oncourse_realdata_willow_college.MessagePerson 
	WHERE collegeId = @collegeId 
		AND (contactId IS NOT NULL)
		AND (contactId IN (SELECT id FROM oncourse_realdata_willow_college.Contact));

INSERT INTO willow_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, isDeleted, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT angelId, collegeId, contactID, created, destinationAddress, id, isDeleted, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type
	FROM oncourse_realdata_willow_college.MessagePerson
	WHERE collegeId = @collegeId 
		AND contactId IS NULL 
		AND studentId IS NOT NULL
		AND (studentId IN (SELECT id FROM oncourse_realdata_willow_college.Student));

INSERT INTO willow_college.MessageTemplate (angelId, collegeId, created, id, isDeleted, message, modified, name, subject)
	SELECT angelId, collegeId, created, id, isDeleted, message, modified, name, subject
	FROM oncourse_realdata_willow_college.MessageTemplate WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.NotificationTemplate (allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, isDeleted, message, mimeType, modified, name, subject)
	SELECT allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, isDeleted, message, mimeType, modified, name, subject
	FROM oncourse_realdata_willow_college.NotificationTemplate WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Outcome (angelId, collegeId, created, deliveryMode, endDate, enrolmentId, fundingSource, id, isDeleted, modified, moduleID, priorLearningID, reportableHours, startDate, status)
	SELECT angelId, collegeId, created, deliveryMode, endDate, enrolmentId, fundingSource, id, isDeleted, modified, moduleID, priorLearningID, reportableHours, startDate, status
	FROM oncourse_realdata_willow_college.Outcome WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst)
	SELECT angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst
	FROM oncourse_realdata_willow_college.Payment
	WHERE collegeId = @collegeId 
		AND isDeleted = 0
		AND (contactId IS NOT NULL)
		AND (contactId IN (SELECT id FROM oncourse_realdata_willow_college.Contact));

INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst)
	SELECT angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst
	FROM oncourse_realdata_willow_college.Payment
	WHERE collegeId = @collegeId 
		AND isDeleted = 0
		AND contactId IS NULL 
		AND studentId IS NOT NULL
		AND (studentId IN (SELECT id FROM oncourse_realdata_willow_college.Student));

INSERT INTO willow_college.PaymentInLine (amount, angelId, created, id, invoiceId, isDeleted, modified, paymentInId)
	SELECT (il.priceEachExTax + il.taxEach + il.discountEachexTax), il.angelId, il.created, il.id, il.invoiceId, 0, il.modified, il.invoiceId
	FROM InvoiceLine il
	JOIN Invoice i ON i.id = il.invoiceId
	WHERE i.collegeId = @collegeId;
 
INSERT INTO willow_college.PaymentOut (id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, isDeleted, totalAmount, creditCardCVV, creditCardType)
	SELECT id, collegeId, contactId, angelId, paymentInTxnReference, source, status, created, modified, isDeleted, totalAmount, creditCardCVV, creditCardType
	FROM oncourse_realdata_willow_college.PaymentOut WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.PaymentOutTransaction (created, id, isDeleted, isFinalised, modified, paymentOutId, response, txnReference)
	SELECT created, id, isDeleted, isFinalised, modified, paymentOutId, response, txnReference
	FROM oncourse_realdata_willow_college.PaymentOutTransaction WHERE paymentOutId in (SELECT id FROM PaymentOut WHERE collegeId = @collegeId) AND isDeleted = 0;

INSERT INTO willow_college.PaymentTransaction (created, id, isDeleted, isFinalised, modified, paymentId, response, txnReference)
   SELECT created, id, isDeleted, isFinalised, modified, paymentId, response, txnReference
   FROM oncourse_realdata_willow_college.PaymentTransaction WHERE paymentId IN (SELECT id FROM PaymentIn WHERE collegeId = @collegeId) AND isDeleted = 0;

INSERT INTO willow_college.Preference (angelId, collegeId, created, explanation, id, isDeleted, modified, name, sqlType, value)
	SELECT angelId, collegeId, created, explanation, id, isDeleted, modified, name, sqlType, value
	FROM oncourse_realdata_willow_college.Preference WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.QueuedRecord (collegeId, id, lastAttemptTimestamp, numberOfAttempts, entityIdentifier, entityWillowId)
	SELECT collegeId, id, lastAttemptTimestamp, numberOfAttempts, recordEntity, recordId
	FROM oncourse_realdata_willow_college.QueuedRecord WHERE collegeId = @collegeId;

INSERT INTO willow_college.Site (countryId, drivingDirections, drivingDirections_textile, id, isWebVisible, latitude, longitude, name, postcode, publicTransportDirections, publicTransportDirections_textile, specialInstructions, specialInstructions_textile, state, street, suburb, timeZone, collegeId, angelId, isDeleted, created, modified)
	SELECT s.countryId, s.drivingDirections, s.drivingDirections_textile, s.id, s.isWebVisible, s.latitude, s.longitude, s.name, s.postcode, s.publicTransportDirections, s.publicTransportDirections_textile, s.specialInstructions, s.specialInstructions_textile, s.state, s.street, s.suburb, s.timeZone, t.collegeId, t.angelId, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.Site s
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = s.id AND t.isDeleted = 0
	WHERE t.entityType = 'Site' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.Room (capacity, directions, directions_textile, facilities, facilities_textile, id, name, siteId, collegeId, angelId, isDeleted, created, modified)
	SELECT r.capacity, r.directions, r.directions_textile, r.facilities, r.facilities_textile, r.id, r.name, r.siteId, t.collegeId, t.angelId, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.Room r
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = r.id AND t.isDeleted = 0
	WHERE t.entityType = 'Room' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.Session (angelId, collegeId, courseClassId, created, endDate, id, isDeleted, markerId, modified, roomId, startDate, timeZone)
	SELECT t.angelId, t.collegeId, s.courseClassId, t.created, s.endTimestamp, s.id, t.isDeleted, s.markerId, t.modified, s.roomId, s.startTimestamp, s.timeZone
	FROM oncourse_realdata_willow_college.Session s
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = s.id AND t.isDeleted = 0
	WHERE t.entityType = 'Session' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.SessionTutor (angelId, collegeId, created, modified, sessionId, tutorId, type)
	SELECT angelId, collegeId, created, modified, sessionId, tutorId, type
	FROM oncourse_realdata_willow_college.SessionTutor 
	WHERE collegeId = @collegeId 
		AND sessionId IN (SELECT id FROM Session WHERE collegeId = @collegeId)
		AND tutorId IN (SELECT id FROM Tutor WHERE collegeId = @collegeId);

INSERT INTO willow_college.StudentConcession (angelId, authorisationExpiresOn, authorisedOn, collegeId, concessionNumber, concessionTypeId, created, expiresOn, id, isDeleted, modified, studentId, timeZone)
	SELECT angelId, authorisationExpiresOn, authorisedOn, collegeId, concessionNumber, concessionTypeId, created, expiresOn, id, isDeleted, modified, studentId, timeZone
	FROM oncourse_realdata_willow_college.StudentConcession 
	WHERE collegeId = @collegeId AND isDeleted = 0
		AND concessionTypeId IN (SELECT id FROM oncourse_realdata_willow_college.ConcessionType WHERE collegeId = @collegeId)
		AND studentId IN (SELECT id FROM oncourse_realdata_willow_college.Student WHERE collegeId = @collegeId);

INSERT INTO willow_college.Tag (angelId, collegeId, created, detail, detail_textile, id, isDeleted, isTagGroup, isWebVisible, modified, name, nodeType, parentId, shortName, weighting)
	SELECT angelId, collegeId, created, detail, detail_textile, id, isDeleted, isTagGroup, isWebVisible, modified, name, nodeType, parentId, shortName, weighting
	FROM oncourse_realdata_willow_college.Tag WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Taggable (angelId, collegeId, created, id, isDeleted, modified, entityIdentifier, entityWillowId)
	SELECT angelId, collegeId, created, id, isDeleted, modified, entityType, id
	FROM oncourse_realdata_willow_college.Taggable WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.TaggableTag (angelId, collegeId, created, isDeleted, modified, taggableId, tagId)
	SELECT angelId, collegeId, created, isDeleted, modified, taggableId, tagId
	FROM oncourse_realdata_willow_college.TaggableTag WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.TagGroupRequirement (allowsMultipleTags, angelId, collegeId, created, entityIdentifier, id, isDeleted, isRequired, modified, tagId)
	SELECT allowsMultipleTags, angelId, collegeId, created, entityIdentifier, id, isDeleted, isRequired, modified, tagId
	FROM oncourse_realdata_willow_college.TagGroupRequirement WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.TutorRole (angelId, collegeId, courseClassId, created, confirmedDate, detail, detail_textile, isConfirmed, isDeleted, modified, tutorId)
	SELECT angelId, collegeId, courseClassId, created, dateConfirmed, detail, detail_textile, isConfirmed, isDeleted, modified, tutorId
	FROM oncourse_realdata_willow_college.TutorRole WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.WaitingList (angelId, collegeId, courseId, created, detail, id, isDeleted, modified, potentialStudents, studentId)
	SELECT angelId, collegeId, courseId, created, detail, id, isDeleted, modified, potentialStudents, studentId
	FROM oncourse_realdata_willow_college.WaitingList WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.WaitingListSite (siteId, waitingListId)
	SELECT siteId, waitingListId
	FROM oncourse_realdata_willow_college.WaitingListSite
	WHERE siteId IN (SELECT id FROM Site WHERE collegeId = @collegeId)
		AND waitingListId IN (SELECT id FROM WaitingList WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebSite (  collegeId, created,   id,  modified, name,  siteKey, googleAnalyticsAccount, googleDirectionsFrom, SSLhostNameId )
	SELECT  ws.collegeId, ws.created, ws.id, ws.modified, ws.name,  ws.code, cd.googleAnalyticsAccount, cd.googleDirectionsFrom, cd.id
	FROM oncourse_realdata_willow_college.WebSite AS ws
	JOIN oncourse_realdata_willow_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND ws.sslHostName = cd.name
	WHERE ws.isDeleted = 0 AND ws.collegeId = @collegeId AND ws.code IS NOT NULL;

INSERT INTO willow_college.WebBlock ( content, content_textile, created, id,  modified, name,   webSiteId)
	SELECT  content, content_textile, created, id,  modified, name, webSiteID
	FROM oncourse_realdata_willow_college.WebBlock WHERE isDeleted = 0 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebNodeType ( created,  modified, name, layoutKey, webSiteId)
        SELECT  NOW(), NOW(), 'page', 'default', ws.id
	FROM oncourse_realdata_willow_college.WebSite AS ws
	WHERE ws.isDeleted = 0 AND ws.collegeId = @collegeId ;

INSERT INTO willow_college.WebNode ( created, id, isPublished,  modified, name, nodeNumber,  webNodeTypeId, webSiteId )
	SELECT  wn.created, wn.id, wn.isPublished, wn.modified, wn.name, wn.nodeNumber, wnt.id, wn.webSiteId
	FROM oncourse_realdata_willow_college.WebNode as wn
	JOIN willow_college.WebNodeType AS wnt ON wnt.webSiteId = wn.webSiteId
	WHERE wn.isDeleted = 0 AND wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebNodeContent (content, content_textile, webNodeId)
	SELECT content, content_textile, id
	FROM oncourse_realdata_willow_college.WebNode WHERE isDeleted = 0 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT created, id, modified, urlPath, webNodeId, webSiteId
	FROM oncourse_realdata_willow_college.WebURLAlias WHERE isDeleted = 0 AND webSiteID in (SELECT id from willow_college.WebSite WHERE collegeId = @collegeId) AND urlPath is not NULL and webNodeId is not NULL;

INSERT INTO willow_college.WillowUser (angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isDeleted, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, passwordHash)
	SELECT angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isDeleted, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, null
	FROM oncourse_realdata_willow_college.WillowUser WHERE collegeId = @collegeId;

INSERT INTO willow_college.WebBlockVisibility ( WebNodeTypeId, WebBlockId, weight,regionKey) 
	SELECT wnt.id, wb.id, wb.weighting, wb.regionKey
	FROM oncourse_realdata_willow_college.WebBlock AS wb
	JOIN willow_college.WebNodeType AS wnt ON  wnt.webSiteId= wb.webSiteId
	WHERE wb.isDeleted = 0 AND wb.regionKey is not NULL AND wb.webSiteId IN (SELECT id FROM WebSite WHERE collegeId = @collegeId);

 
INSERT INTO willow_college.WebMenu (id , webNodeId, URL, webSiteId, webMenuParentId, weight, name, created, modified)
       SELECT wn.id, wn.id, '', wn.webSiteID, wn.parentNodeID, wn.weighting, wn.shortName, NOW(), NOW()
       FROM oncourse_realdata_willow_college.WebNode AS wn
       WHERE wn.isDeleted = 0 AND wn.isWebNavigable =1 AND wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

UPDATE willow_college.WebHostName AS wh 
       JOIN willow_college.WebSite AS ws ON ws.id = wh.webSiteId
       SET wh.name=CONCAT(ws.siteKey, '.test1.oncourse.net.au') 
       WHERE wh.name LIKE '%.test.oncourse.net.au'; 
       
set foreign_key_checks = 1 ;
