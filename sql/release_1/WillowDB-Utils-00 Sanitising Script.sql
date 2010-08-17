use willow_college;

set foreign_key_checks = 0 ;


-- === Purge all Records ===

delete t from Attendance t;
delete t from CertificateOutcome t;
delete t from Outcome t;
delete t from Certificate t;
delete t from WaitingList t;
delete t from DiscountEnrolment t;
delete t from Enrolment t;
delete t from LicenseFee t;
delete t from MessagePerson t;
delete t from Message t;
delete t from Student t;
delete t from SessionTutor t;
delete t from Tutor t;
delete t from StudentConcession t;
delete t from TutorRole t;
delete t from Contact t;
delete t from InvoiceLine_Discount t;
delete t from InvoiceLine t;
delete t from Invoice t;
delete t from PaymentTransaction t;
delete t from PaymentInLine t;
delete t from PaymentIn t;
delete t from PaymentOutTransaction t;
delete t from PaymentOut t;
delete t from WebBlockType t;
delete t from Preference t;
delete t from QueuedRecord t;


-- === Keep Records for specific colleges only ===

-- BinaryInfo/BinaryInfoRelation
delete t from BinaryInfo t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

delete t from BinaryInfoRelation t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- ChangeRequestItem
delete t from ChangeRequestItem t
    join ChangeRequest cr on t.changeRequestId = cr.id
    join College c on cr.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- ChangeRequest
delete t from ChangeRequest t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- ConcessionType
delete t from ConcessionType t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- CourseModule
delete t from CourseModule t
    join Course cor on t.courseId = cor.id
    join College c on cor.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- DiscountCourseClass
delete t from DiscountCourseClass t
    join CourseClass cc on t.courseClassId = cc.id
    join Course cor on cc.courseId = cor.id
    join College c on cor.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- DiscountConcessionType
delete t from DiscountConcessionType t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- SessionTutor
delete t from SessionTutor t
    join Session ss on t.sessionId = ss.id
    join College c on ss.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- Session
delete t from Session t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- CourseClass
delete t from CourseClass t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- Course
delete t from Course t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- Room
delete t from Room t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WaitingListSite
delete t from WaitingListSite t
    join Site s on t.siteId = s.id
    join College c on s.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- Site
delete t from Site t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- DiscountConcessionType
delete t from DiscountConcessionType t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- Discount
delete t from Discount t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;


-- LicenseFee
delete t from LicenseFee t
    join College c on t.college_id = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;


-- MessageTemplate
delete t from MessageTemplate t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- NotificationTemplate
delete t from NotificationTemplate t
    join College c on t.collegeId = c.id
where 
    (c.collegeKey not in ('east', 'scc' )) and (t.collegeID is not null);

-- Taggable
delete t from Taggable t
where entityIdentifier in ('Student', 'Contact', 'Tutor');

delete t from Taggable t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- TaggableTag
delete t from TaggableTag t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

delete t from TaggableTag t
    left join Taggable tag on t.taggableId = tag.id
where 
    tag.id is null;

-- TaggableTag
delete t from TaggableTag t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- TagGroupRequirement
delete t from TagGroupRequirement t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebNodeContentWebBlock
delete t from WebNodeContentWebBlock t
    join WebNodeContent wc on t.webNodeContentId = wc.id
    join WebNode wn on wc.webNodeId = wn.id
    join WebSite ws on wn.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebNodeContent
delete t from WebNodeContent t
    join WebNode wn on t.webNodeId = wn.id
    join WebSite ws on wn.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebNode
delete t from WebNode t
    join WebSite ws on t.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebNodeType
delete t from WebNodeType t
    join WebSite ws on t.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebBlock
delete t from WebBlock t
    join WebSite ws on t.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebURLAlias
delete t from WebURLAlias t
    join WebSite ws on t.webSiteId = ws.id
    join College c on ws.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- WebSite
delete t from WebSite t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

delete t from WebNodeTag t
    left join WebNode wn on wn.id = t.webNodeId
where wn.id is null;

delete t from WebBlockTag t
    left join WebBlock wb on wb.id = t.webBlockId
where wb.id is null;

-- WebTheme
delete t from WebTheme t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) and c.collegeKey is not null;

-- WillowUser
delete from WillowUser 
where isSuperUser = 0 or isSuperUser is null;

insert into WillowUser 
    (id, created, email, firstName, isActive, isDeleted, isSuperUser, lastName, modified, password, flag1)
values 
    (3, now(), 'marek@ish.com.au', 'Marek', 0, 0, 1, 'Wawrzyczny', now(), 'test', 1);

update WillowUser set
    password = 'test';

-- CollegeDomain
delete t from CollegeDomain t
    join College c on t.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

-- College
delete c from College c
where 
    c.collegeKey not in ('east', 'scc' ) or c.collegeKey is null;

insert into CollegeDomain (aliasedDomainID, collegeId, created, googleAnalyticsAccount, googleDirectionsFrom, hasSSL, isDeleted, modified, name, subsiteCode, webSiteID)
values (null, 10, now(), 'UA-1134422-2', null, null, 0, 0, now(), 'scc.jetty.oncourse.net.au', null, 13);

update College set
      billingCode = collegeKey
    , paymentGatewayAccount = 'test'
    , paymentGatewayPass = 'test'
    , webServicesPass = 'test'
    , webServicesSecurityCode = cast(concat('test',cast(id as binary)) as char)
    , isWebServicePaymentsEnabled = 0
    , isWebSitePaymentsEnabled = 0
    , isTestingWebSitePayments = 1;

set foreign_key_checks = 1 ;


-- Anonymising SQL... for reference:
/*
-- StudentConcession
delete t from StudentConcession t
    join Student s on t.studentId = s.id
    join Taggable tag on tag.id = s.id and tag.entityType = 'Student'
    join College c on tag.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or collegeKey is null;

-- Student
delete t from Student t
    join Taggable tag on tag.id = t.id and tag.entityType = 'Student'
    join College c on tag.collegeId = c.id
where 
    c.collegeKey not in ('east', 'scc' ) or collegeKey is null;

update Student set
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

update Student set
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

update Student set
    lastName =  (case substring(lower(lastName), 1, 1)
        when 'a' then 'Alpha'
        when 'b' then 'Beta'
        when 'c' then 'Charlie'
        when 'd' then 'Delta'
        when 'e' then 'Echo'
        when 'f' then 'Foxtrot'
        when 'g' then 'Golf'
        when 'h' then 'Hotel'
        when 'i' then 'India'
        when 'j' then 'Juliet'
        when 'k' then 'Kilo'
        when 'l' then 'Lima'
        when 'm' then 'Mike'
        when 'n' then 'November'
        when 'o' then 'Owen'
        when 'p' then 'Papa'
        when 'q' then 'Quebec'
        when 'r' then 'Romeo'
        when 's' then 'Sierra'
        when 't' then 'Tango'
        when 'u' then 'Uniform'
        when 'v' then 'Victor'
        when 'w' then 'Whiskey'
        when 'x' then 'Xray'
        when 'y' then 'Yankee'
        when 'z' then 'Zulu'
        else 'Other'
      end);

update Student set
      email = cast(concat(lower(firstName),'.',lower(lastName),'.',cast(id as binary),'@ish.com.au') as char) 
    , password = 'test';

update Student set
    homePhone = cast(substring(concat('7001 ',cast((id * 1000) as binary)),1,9) as char)
where homePhone is not null and homePhone <> '';

update Student set
    mobilePhone = cast(substring(concat('7002 ',cast((id * 1000) as binary)),1,9) as char)
where mobilePhone is not null and mobilePhone <> '';

update Student set
    workPhone = cast(substring(concat('7003 ',cast((id * 1000) as binary)),1,9) as char)
where workPhone is not null and workPhone <> '';

update Student set
    fax = cast(substring(concat('7004 ',cast((id * 1000) as binary)),1,9) as char)
where fax is not null and fax <> '';

update Student set
    birthDate = adddate(birthDate, FLOOR(3650 + RAND() * (-7300)))
where birthDate is not null;

-- Payement
delete t from Payment t
select distinct t.collegeId from Payment t
where t.id not in (select paymentId from Enrolment); 
*/