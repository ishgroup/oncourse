set @collegeId = 10;

select 'Attendance' as description, null as cnt
	union
	
select 'att: old all', count(*) 
from oncourse_realdata_willow_college.Attendance 
where collegeId = @collegeId
	union
select 'att: new', count(*) 
from willow_college.Attendance 
where collegeId = @collegeId

	union
select 'BinaryInfo', null
	union

select 'bin: old all', count(*) 
from oncourse_realdata_willow_college.BinaryInfo c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'BinaryInfo'
where collegeId = @collegeId
	union
select 'bin: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.BinaryInfo c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'BinaryInfo'
where p.collegeId = @collegeId and p.isDeleted = 0
	union
select 'bin: new', count(*) 
from willow_college.BinaryInfo 
where collegeId = @collegeId

	union
select 'BinaryInfoRelation', null
	union
	
select 'bir: old all', count(*) 
from oncourse_realdata_willow_college.BinaryInfoRelation
where binaryInfoId in (
	select c.id 
	from oncourse_realdata_willow_college.BinaryInfo c
		join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'BinaryInfo'
	where collegeId = @collegeId
)
	union
select 'bir: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.BinaryInfoRelation
where binaryInfoId in (
	select c.id 
	from oncourse_realdata_willow_college.BinaryInfo c
		join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'BinaryInfo'
	where p.collegeId = @collegeId and p.isDeleted = 0
)
	union
select 'bir: new', count(*) 
from willow_college.BinaryInfoRelation 
where binaryInfoId in (select id from willow_college.BinaryInfo where collegeId = @collegeId)

	union
select 'Certificate', null
	union

select 'cer: old all', count(*) 
from oncourse_realdata_willow_college.Certificate 
where collegeId = @collegeId
	union
select 'cer: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Certificate 
where collegeId = @collegeId and isDeleted = 0
	union
select 'cer: new', count(*) 
from willow_college.Certificate 
where collegeId = @collegeId

	union
select 'CertificateOutcome', null
	union

select 'coc: old all', count(*) 
from oncourse_realdata_willow_college.CertificateOutcome 
where collegeId = @collegeId
	union
select 'coc: new', count(*) 
from willow_college.CertificateOutcome
where collegeId = @collegeId

	union
select 'ChangeRequest', null
	union

select 'crq: old all', count(*) 
from oncourse_realdata_willow_college.ChangeRequest 
where collegeId = @collegeId
	union
select 'crq: new', count(*) 
from willow_college.ChangeRequest
where collegeId = @collegeId

	union
select 'CollegeDomain', null
	union

select 'cdn: old all', count(*) 
from oncourse_realdata_willow_college.CollegeDomain 
where collegeId = @collegeId
	union
select 'cdn: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.CollegeDomain 
where collegeId = @collegeId and isDeleted = 0
	union
select 'cdn: new', count(*) 
from willow_college.CollegeDomain 
where collegeId = @collegeId

	union
select 'ConcessionType', null
	union

select 'ctp: old all', count(*) 
from oncourse_realdata_willow_college.ConcessionType 
where collegeId = @collegeId
	union
select 'ctp: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.ConcessionType 
where collegeId = @collegeId and isDeleted = 0
	union
select 'ctp: new', count(*) 
from willow_college.ConcessionType 
where collegeId = @collegeId

	union
select 'Contact', null
	union

select 'con: old all', count(*) 
from oncourse_realdata_willow_college.Contact c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'Contact'
where collegeId = @collegeId
	union
select 'con: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Contact c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'Contact'
where p.collegeId = @collegeId and p.isDeleted = 0
	union
select 'new', count(*) 
from willow_college.Contact 
where collegeId = @collegeId

	union
select 'Course', null
	union

select 'old all', count(*) 
from oncourse_realdata_willow_college.Course c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'Course'
where collegeId = @collegeId
	union
select 'old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Course c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'Course'
where p.collegeId = @collegeId and p.isDeleted = 0
	union
select 'con: new', count(*) 
from willow_college.Course 
where collegeId = @collegeId

	union
select 'CourseClass', null
	union

select 'ccl: old all', count(*) 
from oncourse_realdata_willow_college.CourseClass c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'CourseClass'
where collegeId = @collegeId
	union
select 'ccl: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.CourseClass c
	join oncourse_realdata_willow_college.Taggable p on p.id = c.id and entityType = 'CourseClass'
where p.collegeId = @collegeId and p.isDeleted = 0
	union
select 'ccl: new', count(*) 
from willow_college.CourseClass 
where collegeId = @collegeId

	union
select 'CourseModule', null
	union

select 'cmd: old all', count(*) 
from oncourse_realdata_willow_college.CourseModule 
where courseId in (select c.id 
	from oncourse_realdata_willow_college.Course c
		join oncourse_realdata_willow_college.Taggable p on p.id = c.id and p.entityType = 'Course'
	where p.collegeId = @collegeId and p.isDeleted = 0)
	union
select 'cmd: new', count(*) 
from willow_college.CourseModule
where courseId in (select id from willow_college.Course where collegeId = @collegeId)

	union
select 'Discount', null
	union

select 'dis: old all', count(*) 
from oncourse_realdata_willow_college.Discount
where collegeId = @collegeId
	union
select 'dis: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Discount
where collegeId = @collegeId and isDeleted = 0
	union
select 'dis: new', count(*) 
from willow_college.Discount 
where collegeId = @collegeId

	union
select 'DiscountConcessionType', null
	union

select 'dct: old all', count(*) 
from oncourse_realdata_willow_college.DiscountConcessionType 
where collegeId = @collegeId
	union
select 'dct: new', count(*) 
from willow_college.DiscountConcessionType
where collegeId = @collegeId

	union
select 'DiscountCourseClass', null
	union

select 'dcc: old all', count(*) 
from oncourse_realdata_willow_college.DiscountCourseClass 
where collegeId = @collegeId
	union
select 'dcc: new', count(*) 
from willow_college.DiscountCourseClass
where collegeId = @collegeId

	union
select 'DiscountEnrolment (on enrolment)', null
	union

select 'de1: old all', count(*) 
from oncourse_realdata_willow_college.DiscountEnrolment
where enrolmentId in (select id from oncourse_realdata_willow_college.Enrolment where collegeId = @collegeId)
	union
select 'de1: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.DiscountEnrolment
where enrolmentId in (select id from oncourse_realdata_willow_college.Enrolment where collegeId = @collegeId and isDeleted = 0)
	union
select 'de1: new', count(*) 
from willow_college.DiscountEnrolment
where enrolmentId in (select id from willow_college.Enrolment where collegeId = @collegeId)

	union
select 'DiscountEnrolment (on discount)', null
	union

select 'de2: old all', count(*) 
from oncourse_realdata_willow_college.DiscountEnrolment
where discountId in (select id from oncourse_realdata_willow_college.Discount where collegeId = @collegeId)
	union
select 'de2: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.DiscountEnrolment
where discountId in (select id from oncourse_realdata_willow_college.Discount where collegeId = @collegeId and isDeleted = 0)
	union
select 'de2: new', count(*) 
from willow_college.DiscountEnrolment
where discountId in (select id from willow_college.Discount where collegeId = @collegeId)

	union
select 'Enrolment/InvoiceLine/PaymentInLine', null
	union

select 'enr: old all', count(*) 
from oncourse_realdata_willow_college.Enrolment
where collegeId = @collegeId
	union
select 'enr: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Enrolment
where collegeId = @collegeId and isDeleted = 0
	union
select 'enr: new', count(*) 
from willow_college.Enrolment 
where collegeId = @collegeId
	union
select 'enr: new', count(*) 
from willow_college.InvoiceLine 
where collegeId = @collegeId
	union
select 'pln: new', count(*) 
from willow_college.PaymentInLine 
where paymentInId in (select id from willow_college.PaymentIn where collegeId = @collegeId)

	union
select 'PaymentIn/Invoice', null
	union

select 'pay: old all', count(*) 
from oncourse_realdata_willow_college.Payment
where collegeId = @collegeId
	union
select 'pay: old ex Deleted', count(*) 
from oncourse_realdata_willow_college.Payment
where collegeId = @collegeId and isDeleted = 0
	union
select 'pay: new', count(*) 
from willow_college.PaymentIn 
where collegeId = @collegeId
	union
select 'inv: new', count(*) 
from willow_college.Invoice 
where collegeId = @collegeId

;


/*
InvoiceLine_Discount
LicenseFee
Log
Message
MessagePerson
MessageTemplate
NotificationTemplate

Outcome
PaymentOut
PaymentOutTransaction
PaymentTransaction
Preference
QueuedRecord
Room
Session

SessionTutor
Site
Student
StudentConcession
Tag
TagGroupRequirement
Taggable
TaggableTag
Tutor
TutorRole
WaitingList
WaitingListSite

WebBlock
WebBlockTag
WebBlockType
WebNode
WebNodeContent
WebNodeContentWebBlock
WebNodeTag
WebNodeType
WebSite
WebTheme
WebURLAlias

WillowUser
*/