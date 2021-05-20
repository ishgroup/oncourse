UPDATE Attendance SET willowid=null;
UPDATE BinaryData SET willowid=null;
UPDATE BinaryInfo SET willowid=null;
UPDATE BinaryRelation SET willowid=null;
UPDATE Category SET willowid=null;
UPDATE Certificate SET willowid=null;
UPDATE Certificate_Outcome SET willowid=null;
UPDATE ConcessionType SET willowid=null;
UPDATE Contact SET willowid=null;
UPDATE Country SET willowid=null;
UPDATE Course SET willowid=null;
UPDATE CourseClass SET willowid=null;
UPDATE CourseClassTutor SET willowid=null;
UPDATE CourseSession SET willowid=null;
UPDATE Discount SET willowid=null;
UPDATE Discount_ConcessionType SET willowid=null;
UPDATE Discount_CourseClass SET willowid=null;
UPDATE Discount_Membership SET willowid=null;
UPDATE Discount_Membership_Relationtype SET willowid=null;
UPDATE Enrolment SET willowid=null;
UPDATE Invoice SET willowid=null;
UPDATE InvoiceLine SET willowid=null;
UPDATE Language SET willowid=null;
UPDATE Message SET willowid=null;
UPDATE MessageTemplate SET willowid=null;
UPDATE Message_Person SET willowid=null;
UPDATE Module SET willowid=null;
UPDATE Node SET willowid=null;
UPDATE NodeRelation SET willowid=null;
UPDATE NodeRequirement SET willowid=null;
UPDATE Outcome SET willowid=null;
UPDATE PaymentIn SET willowid=null;
UPDATE PaymentInLine SET willowid=null;
UPDATE PaymentOut SET willowid=null;
UPDATE Preference SET willowid=null;
UPDATE Product SET willowid=null;
UPDATE ProductItem SET willowid=null;
UPDATE Qualification SET willowid=null;
UPDATE Room SET willowid=null;
UPDATE Site SET willowid=null;
UPDATE Student SET willowid=null;
UPDATE StudentConcession SET willowid=null;
UPDATE SubCategory SET willowid=null;
UPDATE TrainingPackage SET willowid=null;
UPDATE Tutor SET willowid=null;
UPDATE WaitingList SET willowid=null;
DELETE FROM MODULE WHERE id NOT IN (
	SELECT moduleid AS id FROM Module_Course WHERE moduleid IS NOT NULL
	UNION
	SELECT moduleid AS id FROM Outcome WHERE moduleid IS NOT NULL
);
DELETE FROM LANGUAGE WHERE id NOT IN (
	SELECT languageId AS id FROM Student WHERE languageId IS NOT NULL
);
DELETE FROM COUNTRY WHERE id NOT IN (
	SELECT countryId AS id FROM Site WHERE countryId IS NOT NULL
	UNION
	SELECT countryofbirthid AS id FROM Student WHERE countryofbirthid IS NOT NULL
);
DELETE FROM QUALIFICATION WHERE id NOT IN (
	SELECT qualificationid AS id FROM Certificate WHERE qualificationid IS NOT NULL
	UNION
	SELECT qualificationid AS id FROM Course WHERE qualificationid IS NOT NULL
	UNION
	SELECT defaultQualificationid AS id FROM Module WHERE defaultQualificationid IS NOT NULL
	UNION
	SELECT qualificationid AS id FROM PriorLearning WHERE qualificationid IS NOT NULL
);
DELETE FROM TRAININGPACKAGE WHERE id NOT IN (
	SELECT trainingpackageid AS id FROM MODULE WHERE trainingpackageid IS NOT NULL
	UNION
	SELECT trainingpackageid AS id FROM QUALIFICATION WHERE trainingpackageid IS NOT NULL
);

DELETE FROM QUEUEDRECORD;
DELETE FROM PREFERENCE WHERE name = 'replication.willowreference.version';
DELETE FROM PREFERENCE WHERE name = 'remote.web.url';
DELETE FROM PREFERENCE WHERE name LIKE 'server.soap.%';
DELETE FROM PREFERENCE WHERE is_global <> 1;
DELETE FROM PREFERENCE WHERE userId IS NOT NULL;
DELETE FROM PREFERENCE WHERE name LIKE 'services.%';
DELETE FROM PREFERENCE WHERE name = 'data.referencedataversion';
DELETE FROM PREFERENCE WHERE name = 'email.smtphost';

UPDATE PREFERENCE SET VALUE = NULL WHERE NAME LIKE 'backup.%';
UPDATE PREFERENCE SET VALUESTRING = NULL WHERE NAME LIKE 'backup.%';

call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','MODULE',1,1,1 );
call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','LANGUAGE',1,1,1 );
call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','COUNTRY',1,1,1 );
call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','QUALIFICATION',1,1,1 );
call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','TRAININGPACKAGE',1,1,1 );
call SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE('ONCOURSE','QUEUEDRECORD',1,1,1 );
call SYSCS_UTIL.SYSCS_CHECKPOINT_DATABASE();