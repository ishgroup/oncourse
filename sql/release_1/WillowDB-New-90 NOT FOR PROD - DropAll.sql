set foreign_key_checks = 0 ;

DROP VIEW  IF EXISTS StudentView;
DROP VIEW  IF EXISTS TutorView;

DROP TABLE IF EXISTS Attendance;
DROP TABLE IF EXISTS BinaryInfo;
DROP TABLE IF EXISTS BinaryInfoRelation;
DROP TABLE IF EXISTS Certificate;
DROP TABLE IF EXISTS CertificateOutcome;
DROP TABLE IF EXISTS ChangeRequest;
DROP TABLE IF EXISTS ChangeRequestItem;
DROP TABLE IF EXISTS College;
DROP TABLE IF EXISTS CollegeDomain;
DROP TABLE IF EXISTS ConcessionType;
DROP TABLE IF EXISTS Contact;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS CourseClass;
DROP TABLE IF EXISTS CourseModule;
DROP TABLE IF EXISTS DirectAction;
DROP TABLE IF EXISTS Discount;
DROP TABLE IF EXISTS DiscountConcessionType;
DROP TABLE IF EXISTS DiscountCourseClass;
DROP TABLE IF EXISTS DiscountEnrolment;
DROP TABLE IF EXISTS EO_PK_TABLE;
DROP TABLE IF EXISTS Enrolment;
DROP TABLE IF EXISTS Invoice;
DROP TABLE IF EXISTS InvoiceLine;
DROP TABLE IF EXISTS InvoiceLine_Discount;
DROP TABLE IF EXISTS LicenseFee;
DROP TABLE IF EXISTS Log;
DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS MessagePerson;
DROP TABLE IF EXISTS MessageTemplate;
DROP TABLE IF EXISTS NotificationTemplate;
DROP TABLE IF EXISTS Outcome;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS PaymentIn;
DROP TABLE IF EXISTS PaymentInLine;
DROP TABLE IF EXISTS PaymentOut;
DROP TABLE IF EXISTS PaymentOutTransaction;
DROP TABLE IF EXISTS PaymentTransaction;
DROP TABLE IF EXISTS Preference;
DROP TABLE IF EXISTS QueuedRecord;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Session;
DROP TABLE IF EXISTS SessionTutor;
DROP TABLE IF EXISTS Site;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS StudentConcession;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS TagGroupRequirement;
DROP TABLE IF EXISTS Taggable;
DROP TABLE IF EXISTS TaggableTag;
DROP TABLE IF EXISTS Tutor;
DROP TABLE IF EXISTS TutorRole;
DROP TABLE IF EXISTS WaitingList;
DROP TABLE IF EXISTS WaitingListSite;
DROP TABLE IF EXISTS WebBlockTag;
DROP TABLE IF EXISTS WebBlock;
DROP TABLE IF EXISTS WebBlockType;
DROP TABLE IF EXISTS WebNodeTag;
DROP TABLE IF EXISTS WebNode;
DROP TABLE IF EXISTS WebNodeBlockVisibility;
DROP TABLE IF EXISTS WebNodeContent;
DROP TABLE IF EXISTS WebNodeContentWebBlock;
DROP TABLE IF EXISTS WebNodeType;
DROP TABLE IF EXISTS WebSite;
DROP TABLE IF EXISTS WebTagged;
DROP TABLE IF EXISTS WebTheme;
DROP TABLE IF EXISTS WebURLAlias;
DROP TABLE IF EXISTS WillowUser;
DROP TABLE IF EXISTS _dbupdater;

set foreign_key_checks = 1 ;