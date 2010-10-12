use willow_college;

set foreign_key_checks = 0 ;

-- === Purge all Records ===

delete t from Attendance t;
delete t from CertificateOutcome t;
delete t from Outcome t;
delete t from Certificate t;
--delete t from WaitingList t;
delete t from DiscountEnrolment t;
delete t from Enrolment t;
delete t from LicenseFee t;
--delete t from MessagePerson t;
--delete t from Message t;
--delete t from Student t;
--delete t from SessionTutor t;
--delete t from Tutor t;
--delete t from StudentConcession t;
--delete t from TutorRole t;
--delete t from Contact t;
delete t from InvoiceLine_Discount t;
delete t from InvoiceLine t;
delete t from Invoice t;
delete t form Payment; 
delete t from PaymentTransaction t;
delete t from PaymentInLine t;
delete t from PaymentIn t;
delete t from PaymentOutTransaction t;
delete t from PaymentOut t;
--delete t from WebBlockType t;
--delete t from Preference t;
delete t from QueuedRecord t;
delete t from WillowUser t;

insert into WillowUser 
    (id, created, email, firstName, isActive, isDeleted, isSuperUser, lastName, modified, password, flag1)
values 
    (3, now(), 'marek@ish.com.au', 'Marek', 0, 0, 1, 'Wawrzyczny', now(), 'test', 1);

-- set colleges to have random webservices keys

update college set webServicesSecurityCode= SUBSTRING(MD5(RAND()) FROM 1 FOR 10);

-- fix this to update college domains
insert into CollegeDomain (aliasedDomainID, collegeId, created, googleAnalyticsAccount, googleDirectionsFrom, hasSSL, isDeleted, modified, name, subsiteCode, webSiteID)
values 
	  (null, 10, now(), 'UA-1134422-2', null, 0, 0, now(), 'scc.test1.oncourse.net.au', null, 13)
	, (null, 10, now(), 'UA-1134422-2', null, 0, 0, now(), 'scc.staging1.oncourse.net.au', null, 13)
	, (null, 10, now(), 'UA-1134422-2', null, 0, 0, now(), 'scc.live1.oncourse.net.au', null, 13)
	, (null, 10, now(), 'UA-1134422-2', null, 0, 0, now(), 'scc.jetty.oncourse.net.au', null, 13);


insert into CollegeDomain 
  (collegeId, webSiteId, aliasedDomainId, isDeleted, hasSSL, created, modified, name, googleAnalyticsAccount, googleDirectionsFrom, subsiteCode) 
  select collegeId, webSiteId, aliasedDomainId, isDeleted, hasSSL, created, modified, 'scc.jetty.oncourse.net.au', googleAnalyticsAccount, googleDirectionsFrom, subsiteCode
  from CollegeDomain 
  where name = 'scc.local.oncourse.net.au';
  
set foreign_key_checks = 1 ;


-- Anonymising SQL... for reference:

update Contact set
     lastName = SUBSTRING(MD5(RAND()) FROM 1 FOR 7);

update Contact set
    firstName =  (case substring(lower(firstName), 1, 1)
        when 'a' then 'Adam'
        when 'b' then 'Bill'
        when 'c' then 'Charles'
        when 'd' then 'Daniel'
        when 'e' then 'Evan'
        when 'f' then 'Frank'
        when 'g' then 'George'
        when 'h' then 'Henry'
        when 'i' then 'Ivan'
        when 'j' then 'John'
        when 'k' then 'Kelvin'
        when 'l' then 'Luke'
        when 'm' then 'Mark'
        when 'o' then 'Oscar'
        when 'p' then 'Peter'
        when 'q' then 'Quentin'
        when 'r' then 'Robert'
        when 's' then 'Simon'
        when 't' then 'Thomas'
        when 'u' then 'Ugo'
        when 'v' then 'Vincent'
        when 'w' then 'William'
        when 'x' then 'Xavier'
        when 'y' then 'Yuri'
        when 'z' then 'Zareb'
        else 'John'
      end)
where isMale = 1;
update Contact set
    firstName =  (case substring(lower(firstName), 1, 1)
        when 'a' then 'Alice'
        when 'b' then 'Beth'
        when 'c' then 'Clare'
        when 'd' then 'Diana'
        when 'e' then 'Elen'
        when 'f' then 'Fiona'
        when 'g' then 'Gale'
        when 'h' then 'Helen'
        when 'i' then 'Irene'
        when 'j' then 'Julia'
        when 'k' then 'Kathy'
        when 'l' then 'Lara'
        when 'm' then 'Melody'
        when 'n' then 'Nicole'
        when 'o' then 'Olivia'
        when 'p' then 'Paula'
        when 'q' then 'Qi'
        when 'r' then 'Rose'
        when 's' then 'Sabine'
        when 't' then 'Tracy'
        when 'u' then 'Ula'
        when 'v' then 'Vida'
        when 'w' then 'Wilma'
        when 'x' then 'Xena'
        when 'y' then 'Yuki'
        when 'z' then 'Zabrina'
        else 'Jane'
      end)
where isMale = 0;

update Contact set
      email = cast(concat(lower(firstName),'.',lower(lastName),'.',cast(id as binary),'@ish.com.au') as char) 
    , password = 'test';

update Contact set
    homePhone = cast(substring(concat('7001 ',cast((id * 1000) as binary)),1,9) as char)
where homePhone is not null and homePhone <> '';

update Contact set
    mobilePhone = cast(substring(concat('7002 ',cast((id * 1000) as binary)),1,9) as char)
where mobilePhone is not null and mobilePhone <> '';

update Contact set
    workPhone = cast(substring(concat('7003 ',cast((id * 1000) as binary)),1,9) as char)
where workPhone is not null and workPhone <> '';

update Contact set
    fax = cast(substring(concat('7004 ',cast((id * 1000) as binary)),1,9) as char)
where fax is not null and fax <> '';

update Contact set
    birthDate = adddate(birthDate, FLOOR(3650 + RAND() * (-7300)))
where birthDate is not null;

--update Student set
--    countryOfBirthId = NULL, languageId = NULL, disabilityType = NULL, englishProficiency = NULL, 
--    highestSchoolLevel = NULL, indigenousStatus = NULL, isOverseasClient = NULL, isStillAtSchool = NULL
--    priorEducationCode = NULL, yearSchoolCompleted = NULL, labourForceType = NULL

