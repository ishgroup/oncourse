-- set the college you wish to migrate

SET @collegeId = 10;

INSERT INTO willow_college.College (id, isDeleted, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, 
	isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified, 
	name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType,
	webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication) 
	SELECT id,  isDeleted, isWebServicePaymentsEnabled, isWebSitePaymentsEnabled, isTestingWebServicePayments, isTestingWebSitePayments, requiresAvetmiss, created, modified, name, nationalProviderCode, billingCode, paymentGatewayAccount, paymentGatewayPass, paymentGatewayType, webServicesLogin, webServicesPass, webServicesSecurityCode, firstRemoteAuthentication, lastRemoteAuthentication 
	FROM oncourse_realdata_willow_college.College
	WHERE id = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Tutor (angelId, collegeId, created, isDeleted, modified, id, startDate, finishDate, resume, resume_textile)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.isDeleted, tag.modified, t.id, t.dateStarted, t.dateFinished, t.resume, t.resume_textile
	FROM oncourse_realdata_willow_college.Tutor t
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = t.id AND tag.isDeleted = 0
	WHERE tag.entityType = 'Tutor' AND t.id IN (SELECT tutorId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId);

INSERT INTO willow_college.Student (angelId, collegeId, created, isDeleted, modified, id, countryOfBirthId, languageId, concessionType, disabilityType, englishProficiency, highestSchoolLevel, indigenousStatus, isOverseasClient, isStillAtSchool, priorEducationCode, yearSchoolCompleted, labourForceType)
	SELECT tag.angelId, tag.collegeId, tag.created, tag.isDeleted, tag.modified, s.id, s.countryOfBirthId, s.languageId, s.concessionType, s.disabilityType, s.englishProficiency, s.highestSchoolLevel, s.indigenousStatus, s.isOverseasClient, s.isStillAtSchool, s.priorEducationCode, s.yearSchoolCompleted, s.labourForceType
	FROM oncourse_realdata_willow_college.Student s 
	JOIN oncourse_realdata_willow_college.Taggable tag ON tag.id = s.id AND tag.isDeleted = 0
	WHERE tag.entityType = 'Student' AND s.id IN (SELECT studentId FROM oncourse_realdata_willow_college.Contact WHERE collegeId = @collegeId);

INSERT INTO willow_college.ConcessionType (id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isDeleted, isEnabled, requiresCredentialCheck, name) 
	SELECT id, collegeId, angelId, created, modified, credentialExpiryDays, hasConcessionNumber, hasExpiryDate, isConcession, isDeleted, isEnabled, requiresCredentialCheck, name
	FROM oncourse_realdata_willow_college.ConcessionType WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.StudentConcession (angelId, authorisationExpiresOn, authorisedOn, collegeId, concessionNumber, concessionTypeId, created, expiresOn, id, isDeleted, modified, studentId, timeZone)
	SELECT sc.angelId, sc.authorisationExpiresOn, sc.authorisedOn, sc.collegeId, sc.concessionNumber, sc.concessionTypeId, sc.created, sc.expiresOn, sc.id, sc.isDeleted, sc.modified, sc.studentId, sc.timeZone
	FROM oncourse_realdata_willow_college.StudentConcession AS sc
	JOIN oncourse_realdata_willow_college.Taggable AS t ON t.isDeleted=0 AND t.id = sc.studentId AND t.entityType like 'Student'
	WHERE sc.collegeId = @collegeId AND sc.isDeleted = 0;

INSERT INTO willow_college.Course (id, collegeId, qualificationId, angelId, code, isWebVisible, isVETCourse, isSufficientForQualification, allowWaitingList, nominalHours, name, detail, detail_textile, fieldOfEducation, searchText, isDeleted, created, modified)
	SELECT c.id, t.collegeId, c.qualificationId, t.angelId, c.code, c.isWebVisible, c.isVETCourse, c.isSufficientForQualification, c.allowWaitingList, c.nominalHours, c.name, c.detail, c.detail_textile, c.fieldOfEducation, c.searchText, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.Course c
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = c.id AND t.isDeleted = 0
	WHERE t.entityType = 'Course' and t.collegeId = @collegeId AND t.isDeleted = 0;

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

INSERT INTO willow_college.CourseClass (id, collegeId, angelId, code, courseId, roomId, startDate, endDate, isWebVisible, isCancelled, maximumPlaces, minimumPlaces, countOfSessions, deliveryMode, feeExGst, feeGst, minutesPerSession, startingMinutePerSession, detail, detail_textile, materials, materials_textile, sessionDetail, sessionDetail_textile, timeZone, isDeleted, created, modified)
	SELECT cc.id, t.collegeId, t.angelId, cc.code, cc.courseId, cc.roomId, cc.startDate, cc.endDate, cc.isWebVisible, cc.isCancelled, cc.maximumPlaces, cc.minimumPlaces, cc.countOfSessions, cc.deliveryMode, cc.feeExGst, cc.feeGst, cc.minutesPerSession, cc.startingMinutePerSession, cc.detail, cc.detail_textile, cc.materials, cc.materials_textile, cc.sessionDetail, cc.sessionDetail_textile, cc.timeZone, t.isDeleted, t.created, t.modified
	FROM oncourse_realdata_willow_college.CourseClass cc
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = cc.id AND t.isDeleted = 0
	WHERE t.entityType = 'CourseClass' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_college.Session (angelId, collegeId, courseClassId, created, endDate, id, isDeleted, markerId, modified, roomId, startDate, timeZone)
	SELECT t.angelId, t.collegeId, s.courseClassId, t.created, s.endTimestamp, s.id, t.isDeleted, s.markerId, t.modified, s.roomId, s.startTimestamp, s.timeZone
	FROM oncourse_realdata_willow_college.Session s
	JOIN oncourse_realdata_willow_college.Taggable t ON t.id = s.id AND t.isDeleted = 0
	WHERE t.entityType = 'Session' and t.collegeId = @collegeId AND t.isDeleted = 0;

INSERT INTO willow_binary.BinaryData (id, angelId,  binaryInfoId, collegeId, content, created, isDeleted, modified) 
	SELECT id, angelId, binaryInfoId, collegeId, content, created, isDeleted, modified
	FROM oncourse_realdata_willow_binary.BinaryData WHERE collegeId = @collegeId;

INSERT INTO willow_college.BinaryInfo (angelId, collegeId, created, id, isDeleted, modified, name, referenceNumber, bytesize, mimetype, pixelHeight, pixelWidth )
	SELECT t.angelId, t.collegeId, t.created, t.id, t.isDeleted, t.modified, bio.name, bio.referenceNumber,bio.byteSize, bio.mimeType, bio.pixelHeight,bio.pixelWidth
	FROM oncourse_realdata_willow_college.BinaryInfo as bio
	JOIN oncourse_realdata_willow_college.Taggable as t ON t.entityType = 'BinaryInfo' AND t.collegeId = @collegeId AND t.isDeleted = 0 AND t.id = bio.id;

INSERT INTO willow_college.BinaryInfoRelation (id, collegeId, isDeleted, binaryInfoId, entityIdentifier, entityWillowId, entityAngelId, angelId, created, modified) 
	SELECT bir.id, bir.collegeId, bir.isDeleted, bir.binaryInfoId, bir.entityIdentifier, bir.entityRecordId, bir.entityAngelId, bir.angelId, bir.created, bir.modified 
	FROM oncourse_realdata_willow_college.BinaryInfoRelation AS bir
	JOIN willow_college.BinaryInfo AS bi ON bir.binaryInfoId = bi.id
	WHERE bir.collegeId = @collegeId AND bir.isDeleted = 0;

INSERT INTO willow_college.Certificate (id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isDeleted, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes)
	SELECT id, collegeId, studentId, qualificationId, angelId, created, modified, endDate, certificateNumber, isDeleted, isQualification, fundingSource, printedWhen, revokedWhen, studentFirstName, studentLastName, privateNotes, publicNotes
	FROM oncourse_realdata_willow_college.Certificate WHERE collegeId = @collegeId AND isDeleted = 0;


INSERT INTO willow_college.ChangeRequest (id, collegeId, status, created, modified, identifier, result)
	SELECT id, collegeId, status, created, modified, identifier, result
	FROM oncourse_realdata_willow_college.ChangeRequest WHERE collegeId = @collegeId;

INSERT INTO willow_college.ChangeRequestItem (id, changeRequestId, mainEntityName, mainEntityId, secondEntityId, secondEntityName, sequence, action, keyPath, newValueInteger, newValueString, relationship, result)
	SELECT id, changeRequestId, mainEntityName, mainEntityId, secondEntityId, secondEntityName, sequence, action, keyPath, newValueInteger, newValueString, relationship, result 
	FROM oncourse_realdata_willow_college.ChangeRequestItem WHERE changeRequestId IN (SELECT id FROM oncourse_realdata_willow_college.ChangeRequest WHERE collegeId = @collegeId);

INSERT INTO willow_college.Contact (angelId, collegeId, created, id, isDeleted, modified, businessPhoneNumber, cookieHash, countryID,dateOfBirth,emailAddress,familyName,faxNumber,givenName,homePhoneNumber, isCompany,isMale,isMarketingViaEmailAllowed,isMarketingViaPostAllowed,isMarketingViaSMSAllowed, mobilePhoneNumber,password,postcode,state,street,studentID,suburb,taxFileNumber,tutorID,uniqueCode ) 
	SELECT t.angelId, t.collegeId, t.created, t.id, t.isDeleted, t.modified, 
	c.businessPhoneNumber,c.cookieHash,c.countryID,c.dateOfBirth,c.emailAddress,c.familyName,
	c.faxNumber,c.givenName,c.homePhoneNumber,c.isCompany,c.isMale,c.isMarketingViaEmailAllowed,
	c.isMarketingViaPostAllowed,c.isMarketingViaSMSAllowed,c.mobilePhoneNumber,c.password,
	c.postcode,c.state,c.street,c.studentID,c.suburb,c.taxFileNumber,c.tutorID,c.uniqueCode
	FROM oncourse_realdata_willow_college.Contact AS c
	JOIN oncourse_realdata_willow_college.Taggable AS t ON c.id = t.id AND t.entityType = 'Contact' AND t.collegeId = @collegeId AND t.isDeleted = 0;

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
	SELECT dcc.courseClassId, dcc.discountId, dcc.collegeId, dcc.angelId, dcc.created, dcc.modified
	FROM oncourse_realdata_willow_college.DiscountCourseClass AS dcc
	JOIN willow_college.CourseClass AS cc ON dcc.courseClassId = cc.id
	WHERE dcc.collegeId = @collegeId; 

INSERT INTO willow_college.Enrolment (id, collegeId, courseClassId, studentId, discountId, angelId, isDeleted, created, modified, reasonForStudy, source, status)
	SELECT e.id, e.collegeId, e.courseClassId, e.studentId, e.discountId, e.angelId, e.isDeleted, e.created, e.modified, e.reasonForStudy, e.source, e.status
	FROM oncourse_realdata_willow_college.Enrolment AS e
	JOIN willow_college.Student AS s ON e.studentid= s.id
	WHERE e.collegeId = @collegeId AND e.isDeleted = 0;

INSERT INTO willow_college.DiscountEnrolment (discountId, enrolmentId)
	SELECT de.discountId, de.enrolmentId
	FROM oncourse_realdata_willow_college.DiscountEnrolment as de 
	JOIN willow_college.Enrolment AS e ON de.enrolmentid = e.id
	JOIN willow_college.Discount AS d ON de.discountid = d.id
	WhERE e.collegeid = @collegeid;

INSERT INTO willow_college.Invoice (amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT 0, p.collegeId, p.contactID, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM oncourse_realdata_willow_college.Payment AS p 
	JOIN willow_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId;

-- deal with legacy data joined on student id instead of contact id
INSERT INTO willow_college.Invoice (amountOwing, collegeId, contactId, created, dateDue, id, invoiceDate, invoiceNumber, modified, source)
	SELECT 0, p.collegeId, c.id, p.created, p.created, p.id, p.created, p.id, p.modified, p.source
	FROM oncourse_realdata_willow_college.Payment p
	JOIN willow_college.Contact c ON p.studentID = c.studentID
	JOIN willow_college.Student AS s ON s.id = p.studentID
	WHERE p.collegeId = @collegeid
		AND p.isDeleted = 0
		AND p.contactId IS NULL 
		AND p.studentId IS NOT NULL;
		
INSERT INTO willow_college.InvoiceLine (id, collegeId, invoiceId, enrolmentId, priceEachExTax, discountEachExTax, quantity, taxEach, created, modified, title)
	SELECT e.id, e.collegeId, e.paymentId, e.id, cc.feeExGst, case when (e.combinedDiscountExTax is null) then 0.00 else e.combinedDiscountExTax end, 1, case when (cc.feeGst is null) then 0.00 else cc.feeGst end, e.created, e.modified, ''
	FROM oncourse_realdata_willow_college.Enrolment e
	JOIN willow_college.Enrolment as newe ON e.id = newe.id
	JOIN willow_college.Invoice as i ON e.paymentid = i.id
	JOIN willow_college.CourseClass cc ON e.courseClassId = cc.id
	WHERE e.collegeId = @collegeId	AND e.isDeleted = 0 AND e.paymentId is not null;

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
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.isDeleted, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM oncourse_realdata_willow_college.MessagePerson AS mp
	INNER JOIN willow_college.Contact as c ON mp.contactid = c.id
	WHERE mp.collegeId = @collegeId;
	
-- legacy messagePerson linked to student rather than contact
INSERT INTO willow_college.MessagePerson (angelId, collegeId, contactId, created, destinationAddress, id, isDeleted, messageId, modified, numberOfAttempts, response, status, studentId, timeOfDelivery, tutorId, type)
	SELECT mp.angelId, mp.collegeId, mp.contactID, mp.created, mp.destinationAddress, mp.id, mp.isDeleted, mp.messageId, mp.modified, mp.numberOfAttempts, mp.response, mp.status, NULL, mp.timeOfDelivery, mp.tutorId, mp.type
	FROM oncourse_realdata_willow_college.MessagePerson as mp
	JOIN willow_college.Student as s ON mp.studentid = s.id
	WHERE mp.collegeId = @collegeId 
		AND mp.contactId IS NULL ;
		

INSERT INTO willow_college.MessageTemplate (angelId, collegeId, created, id, isDeleted, message, modified, name, subject)
	SELECT angelId, collegeId, created, id, isDeleted, message, modified, name, subject
	FROM oncourse_realdata_willow_college.MessageTemplate WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.NotificationTemplate (allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, isDeleted, message, mimeType, modified, name, subject)
	SELECT allowedInTextileTags, allowWebobjectsTags, angelId, collegeId, created, id, isDeleted, message, mimeType, modified, name, subject
	FROM oncourse_realdata_willow_college.NotificationTemplate WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.Outcome (angelId, collegeId, created, deliveryMode, endDate, enrolmentId, fundingSource, id, isDeleted, modified, moduleID, priorLearningID, reportableHours, startDate, status)
	SELECT o.angelId, o.collegeId, o.created, o.deliveryMode, o.endDate, o.enrolmentId, o.fundingSource, o.id, o.isDeleted, o.modified, o.moduleID, o.priorLearningID, o.reportableHours, o.startDate, o.status
	FROM oncourse_realdata_willow_college.Outcome AS o
	JOIN willow_college.Enrolment AS e ON e.id = o.EnrolmentId
	WHERE o.collegeId = @collegeId AND o.isDeleted = 0;

INSERT INTO willow_college.CertificateOutcome (certificateId, outcomeId, collegeId, angelId, created, modified) 
	SELECT co.certificateId, co.outcomeId, co.collegeId, co.angelId, co.created, co.modified 
	FROM oncourse_realdata_willow_college.CertificateOutcome as co
	JOIN willow_college.Outcome as o ON co.outcomeid = o.id
	WHERE co.collegeId = @collegeId;

INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.isDeleted, p.modified, p.source, p.status, NULL, p.totalExGst, p.totalGst
	FROM oncourse_realdata_willow_college.Payment AS p
	JOIN willow_college.Contact AS c ON p.contactid = c.id
	WHERE p.collegeId = @collegeId 
		AND p.isDeleted = 0;
		
-- fixing legacy payments linked to students
INSERT INTO willow_college.PaymentIn (angelId, collegeId, contactID, created, creditCardCVV, creditCardExpiry, creditCardName, creditCardNumber, creditCardType, id, isDeleted, modified, source, status, studentId, totalExGst, totalGst)
	SELECT p.angelId, p.collegeId, p.contactID, p.created, p.creditCardCVV, p.creditCardExpiry, p.creditCardName, p.creditCardNumber, p.creditCardType, p.id, p.isDeleted, p.modified, p.source, p.status, p.studentId, p.totalExGst, p.totalGst
	FROM oncourse_realdata_willow_college.Payment as p 
	JOIN willow_college.Student as s on p.studentid = s.id
	WHERE p.collegeId = @collegeId 
		AND p.isDeleted = 0
		AND p.contactId IS NULL 
		AND p.studentId IS NOT NULL;

INSERT INTO willow_college.PaymentInLine (amount, angelId, created, id, invoiceId, isDeleted, modified, paymentInId)
	SELECT (il.priceEachExTax + il.taxEach + il.discountEachexTax), il.angelId, il.created, il.id, il.invoiceId, 0, il.modified, il.invoiceId
	FROM willow_college.InvoiceLine il
	JOIN willow_college.Invoice i ON i.id = il.invoiceId
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

INSERT INTO willow_college.SessionTutor (angelId, collegeId, created, modified, sessionId, tutorId, type)
	SELECT angelId, collegeId, created, modified, sessionId, tutorId, type
	FROM oncourse_realdata_willow_college.SessionTutor 
	WHERE collegeId = @collegeId 
		AND sessionId IN (SELECT id FROM Session WHERE collegeId = @collegeId)
		AND tutorId IN (SELECT id FROM Tutor WHERE collegeId = @collegeId);

INSERT INTO willow_college.Tag (angelId, collegeId, created, detail, detail_textile, id, isDeleted, isTagGroup, isWebVisible, modified, name, nodeType, parentId, shortName, weighting)
	SELECT angelId, collegeId, created, detail, detail_textile, id, isDeleted, isTagGroup, isWebVisible, modified, name, nodeType, NULL, shortName, weighting
	FROM oncourse_realdata_willow_college.Tag
	WHERE collegeId = @collegeId AND isDeleted = 0;
	
UPDATE willow_college.Tag AS t
	JOIN oncourse_realdata_willow_college.Tag AS tOld ON tOld.id = t.id
	SET t.parentId = tOld.parentId 
	WHERE t.collegeId = @collegeId;

INSERT INTO willow_college.Taggable (angelId, collegeId, created, id, isDeleted, modified, entityIdentifier, entityWillowId)
	SELECT angelId, collegeId, created, id, isDeleted, modified, entityType, id
	FROM oncourse_realdata_willow_college.Taggable WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.TaggableTag (angelId, collegeId, created, isDeleted, modified, taggableId, tagId)
	SELECT tt.angelId, tt.collegeId, tt.created, tt.isDeleted, tt.modified, tt.taggableId, tt.tagId
	FROM oncourse_realdata_willow_college.TaggableTag AS tt
	JOIN willow_college.Taggable AS t ON tt.taggableid = t.id
	JOIN willow_college.Tag as tag ON tt.tagId = tag.id
	WHERE tt.collegeId = @collegeId AND tt.isDeleted = 0;

INSERT INTO willow_college.TagGroupRequirement (allowsMultipleTags, angelId, collegeId, created, entityIdentifier, id, isDeleted, isRequired, modified, tagId)
	SELECT tgr.allowsMultipleTags, tgr.angelId, tgr.collegeId, tgr.created, tgr.entityIdentifier, tgr.id, tgr.isDeleted, tgr.isRequired, tgr.modified, tgr.tagId
	FROM oncourse_realdata_willow_college.TagGroupRequirement as tgr
	JOIN willow_college.Tag as t ON tgr.tagId = t.id
	 WHERE tgr.collegeId = @collegeId AND tgr.isDeleted = 0;

INSERT INTO willow_college.TutorRole (angelId, collegeId, courseClassId, created, confirmedDate, detail, detail_textile, isConfirmed, isDeleted, modified, tutorId)
	SELECT angelId, collegeId, courseClassId, created, dateConfirmed, detail, detail_textile, isConfirmed, isDeleted, modified, tutorId
	FROM oncourse_realdata_willow_college.TutorRole WHERE collegeId = @collegeId AND isDeleted = 0;

INSERT INTO willow_college.WaitingList (angelId, collegeId, courseId, created, detail, id, isDeleted, modified, potentialStudents, studentId)
	SELECT wl.angelId, wl.collegeId, wl.courseId, wl.created, wl.detail, wl.id, wl.isDeleted, wl.modified, wl.potentialStudents, wl.studentId
	FROM oncourse_realdata_willow_college.WaitingList as wl
	JOIN willow_college.Student as s on wl.studentid = s.id
	WHERE wl.collegeId = @collegeId AND wl.isDeleted = 0;

INSERT INTO willow_college.WaitingListSite (siteId, waitingListId)
	SELECT siteId, waitingListId
	FROM oncourse_realdata_willow_college.WaitingListSite
	WHERE siteId IN (SELECT id FROM Site WHERE collegeId = @collegeId)
		AND waitingListId IN (SELECT id FROM WaitingList WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebSite (  collegeId, created,  id, modified, name, siteKey, googleAnalyticsAccount, googleDirectionsFrom )
	SELECT	ws.collegeId, ws.created, ws.id, ws.modified, ws.name, ws.code, cd.googleAnalyticsAccount, cd.googleDirectionsFrom
	FROM oncourse_realdata_willow_college.WebSite AS ws
	JOIN oncourse_realdata_willow_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND ws.sslHostName = cd.name
	WHERE ws.isDeleted = 0 AND ws.collegeId = @collegeId AND ws.code IS NOT NULL;

INSERT INTO willow_college.WebHostName (id, collegeId, webSiteId, created, modified, name) 
	SELECT id, collegeId, webSiteId, created, modified, name
	FROM oncourse_realdata_willow_college.CollegeDomain WHERE collegeId = @collegeId  AND isDeleted = 0;

UPDATE willow_college.WebSite AS ws
	JOIN oncourse_realdata_willow_college.WebSite AS wsOld ON wsOld.id = ws.id
	JOIN oncourse_realdata_willow_college.CollegeDomain AS cd ON ws.id = cd.WebSiteId AND wsOld.sslHostName = cd.name
	SET ws.SSLhostNameId = cd.id
	WHERE ws.collegeId = @collegeId;

INSERT INTO willow_college.WebNodeType ( created,  modified, name, layoutKey, webSiteId)
		SELECT	NOW(), NOW(), 'page', 'default', ws.id
	FROM oncourse_realdata_willow_college.WebSite AS ws
	WHERE ws.isDeleted = 0 AND ws.collegeId = @collegeId ;

-- web blocks go into web content now
INSERT INTO willow_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT	id, content, content_textile, name, webSiteID, created, modified
	FROM oncourse_realdata_willow_college.WebBlock WHERE isDeleted = 0 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);



INSERT INTO willow_college.WebNode ( created, id, isPublished,	modified, name, nodeNumber,	 webNodeTypeId, webSiteId )
	SELECT	wn.created, wn.id, wn.isPublished,	wn.modified, wn.name, wn.nodeNumber, wnt.id, wn.webSiteId
	FROM oncourse_realdata_willow_college.WebNode as wn
	JOIN willow_college.WebNodeType as wnt ON wnt.webSiteId= wn.webSiteId
	WHERE wn.isDeleted = 0 AND wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT created, id, modified, urlPath, webNodeId, webSiteId
	FROM oncourse_realdata_willow_college.WebURLAlias WHERE isDeleted = 0 AND webSiteID in (SELECT id from willow_college.WebSite WHERE collegeId = @collegeId) AND urlPath is not NULL and webNodeId is not NULL;

UPDATE willow_college.WebNode AS wn
	JOIN willow_college.WebNodeType as wnt ON wnt.webSiteId= wn.webSiteId
	JOIN oncourse_realdata_willow_college.WebNode as wnold ON wnold.id=wn.id
	JOIN willow_college.WebURLAlias as wa ON wa.id=wnold.menuAliasID
	SET defaultURLAlias = wnold.menuAliasID
	WHERE wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WillowUser (angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isDeleted, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, passwordHash)
	SELECT angelId, collegeId, created, email, failedLoginCount, firstName, flag1, id, isActive, isDeleted, isSuperUser, lastFailedLogin, lastLogin, lastName, modified, password, null
	FROM oncourse_realdata_willow_college.WillowUser WHERE collegeId = @collegeId;

INSERT INTO willow_college.WebContentVisibility ( WebNodeTypeId, WebContentId, weight,regionKey) 
	SELECT wnt.id, wb.id, wb.weighting, wb.regionKey
	FROM oncourse_realdata_willow_college.WebBlock AS wb
	JOIN willow_college.WebNodeType AS wnt ON  wnt.webSiteId = wb.webSiteId
	WHERE wb.isDeleted = 0 AND wb.regionKey is not NULL AND wb.webSiteId IN (SELECT id FROM WebSite WHERE collegeId = @collegeId);

-- web node content goes into WebContent
INSERT INTO willow_college.WebContent (id, content, content_textile, name, webSiteId, created, modified)
	SELECT id + 1000, content, content_textile, NULL, webSiteId, NOW(), NOW()
	FROM oncourse_realdata_willow_college.WebNode WHERE isDeleted = 0 AND webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.WebContentVisibility ( WebNodeId, WebContentId, weight,regionKey) 
	SELECT wn.id, wn.id + 1000, 0, 'content'
	FROM willow_college.WebNode AS wn
	JOIN willow_college.WebSite AS ws ON  ws.id = wn.webSiteId
	WHERE ws.collegeId = @collegeId;


INSERT INTO willow_college.WebMenu (id , webNodeId, URL, webSiteId, webMenuParentId, weight, name, created, modified)
	SELECT wn.id, wn.id, '', wn.webSiteID, NULL, wn.weighting, wn.shortName, NOW(), NOW()
	FROM oncourse_realdata_willow_college.WebNode AS wn
	WHERE wn.isDeleted = 0 AND wn.isWebNavigable =1 AND wn.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

UPDATE willow_college.WebMenu AS wm 
	JOIN oncourse_realdata_willow_college.WebNode AS wn ON wn.id = wm.id
	JOIN willow_college.WebMenu AS wmJoin ON wmJoin.id = wn.parentNodeID
	SET wm.webMenuParentId = wn.parentNodeID
	WHERE wm.webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

UPDATE willow_college.WebHostName AS wh 
	JOIN willow_college.WebSite AS ws ON ws.id = wh.webSiteId
	SET wh.name=CONCAT(ws.siteKey, '.test1.oncourse.net.au') 
	WHERE wh.name LIKE '%.test.oncourse.net.au'; 

-- add special aliases for the home page
INSERT INTO willow_college.WebURLAlias ( created, id, modified, urlPath, webNodeId, webSiteId)
	SELECT NOW(), id + 10000, NOW(), '/', id, webSiteId
	FROM oncourse_realdata_willow_college.WebNode 
	WHERE `isDeleted` =0 AND `isWebVisible` = 1
	AND `parentNodeID` IS NULL AND name like 'Home page'
	AND  webSiteId IN (SELECT id FROM willow_college.WebSite WHERE collegeId = @collegeId);

INSERT INTO willow_college.Attendance (id, collegeId, angelId, studentId, sessionId, attendanceType, markerId, created, modified) 
	SELECT a.id, a.collegeId, a.angelId, a.studentId, a.sessionId, a.attendanceType, a.markerId, a.created, a.modified 
	FROM oncourse_realdata_willow_college.Attendance AS a
	JOIN willow_college.Session AS s ON s.id = a.sessionId
	JOIN willow_college.Student AS st ON st.id = a.studentId
	WHERE a.collegeId = @collegeId;