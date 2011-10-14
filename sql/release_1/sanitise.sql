use willow_college;

set foreign_key_checks = 0 ;

-- === Purge all Records ===

delete t from Attendance t;
delete t from CertificateOutcome t;
delete t from Outcome t;
delete t from Certificate t;
-- delete t from WaitingList t;
delete t from DiscountEnrolment t;
delete t from Enrolment t;
delete t from LicenseFee t;
-- delete t from MessagePerson t;
-- delete t from Message t;
-- delete t from Student t;
-- delete t from SessionTutor t;
-- delete t from Tutor t;
-- delete t from StudentConcession t;
-- delete t from TutorRole t;
-- delete t from Contact t;
delete t from InvoiceLine_Discount t;
delete t from InvoiceLine t;
delete t from Invoice t;
delete t from PaymentTransaction t;
delete t from PaymentInLine t;
delete t from PaymentIn t;
delete t from PaymentOutTransaction t;
delete t from PaymentOut t;
-- delete t from WebBlockType t;
-- delete t from Preference t;
delete t from QueuedRecord t;
delete t from WillowUser t;

insert into WillowUser 
    (id, created, email, firstName, isActive, lastName, modified, password)
values 
    (3, now(), 'marek@ish.com.au', 'Marek', 0, 'Wawrzyczny', now(), 'test');

--  set colleges to have random webservices keys

update College set webServicesSecurityCode= SUBSTRING(MD5(RAND()) FROM 1 FOR 10);

set foreign_key_checks = 1 ;


--  Anonymising SQL... for reerence:

update Contact set
     familyName = SUBSTRING(MD5(RAND()) FROM 1 FOR 7);

update Contact set
    givenName =  (case substring(lower(givenName), 1, 1)
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
    givenName =  (case substring(lower(givenName), 1, 1)
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
      emailaddress = cast(concat(lower(givenName),'.',lower(familyName),'.',cast(id as binary),'@example.com') as char) 
    , password = 'test';

update Contact set
    homePhoneNumber = cast(substring(concat('7001 ',cast((id * 1000) as binary)),1,9) as char)
where homePhoneNumber is not null and homePhoneNumber  <> '';

update Contact set
    mobilePhoneNumber = cast(substring(concat('7002 ',cast((id * 1000) as binary)),1,9) as char)
where mobilePhoneNumber is not null and mobilePhoneNumber <> '';

update Contact set
    businessPhoneNumber = cast(substring(concat('7003 ',cast((id * 1000) as binary)),1,9) as char)
where businessPhoneNumber is not null and businessPhoneNumber <> '';

update Contact set
    faxNumber = cast(substring(concat('7004 ',cast((id * 1000) as binary)),1,9) as char)
where faxNumber is not null and faxNumber <> '';

update Contact set
    dateOfBirth = adddate(dateOfBirth, FLOOR(3650 + RAND() * (-7300)))
where dateOfBirth is not null;

update Contact set street = NULL, suburb = NULL, state = NULL, postcode = NULL;
-- update Student set
--     countryOfBirthId = NULL, languageId = NULL, disabilityType = NULL, englishProficiency = NULL, 
--     highestSchoolLevel = NULL, indigenousStatus = NULL, isOverseasClient = NULL, isStillAtSchool = NULL
--     priorEducationCode = NULL, yearSchoolCompleted = NULL, labourForceType = NULL

