@parallel=false
Feature: Main feature for all GET requests with path 'list/export/pdf/template'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export/pdf/template'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Get templates by admin

        * table getTemplate

            | entity                | templateName                                                                                  |
            | 'Qualification'       | "Qualifications List"                                                                         |
            | 'Module'              | "Module List"                                                                                 |
            | 'Site'                | "Class Timetable Report","Class Timetable Report - Planning","Site Details","Site List"       |
            | 'Room'                | "Rooms List","Room Timetable","Class Timetable Report","Class Timetable Report - Planning"    |
            | 'AccountTransaction'  | "Transaction Detail","Transaction Summary"                                                    |
            | 'Account'             | "Transaction Detail","Transaction Summary"                                                    |
            | 'Payslip'             | "Payslip Report"                                                                              |
            | 'CorporatePass'       | "CorporatePass","CorporatePass List"                                                          |
            | 'Invoice'             | "Debtors and Creditors Report"                                                                |
            | 'PaymentIn'           | "PaymentIn"                                                                                   |
            | 'Banking'             | "Banking Report","Cash Movements Detail Report","Cash Movements Summary Report"               |
            | 'WaitingList'         | "Waiting List"                                                                                |
            | 'Application'         | "Application List"                                                                            |
            | 'Certificate'         | "Certificate","Transcript"                                                                    |
            | 'Survey'              | "Student feedback List"                                                                       |
            | 'Outcome'             | "Class Funding"                                                                               |
            | 'Audit'               | "Audit List"                                                                                  |
            | 'Discount'            | "Discount Take Up Report","Discount Take Up Summary"                                          |
            | 'Course'              | "Course Details","Course List"                                                                |
            | 'Enrolment'           | "Academic Transcript","Assessment Outcomes Per Student","Certificate-Attendance","Commonwealth Assistance Notice","Demographic Data Report","Enrolment Confirmation","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
            | 'ProductItem'         | "Voucher Report","Vouchers"                                                                   |
            | 'VoucherProduct'      | "Voucher Products List"                                                                       |
            | 'Contact'             | "Mailing List","Statement report","Student Attendance Averages","Student Details","Student Transcript","Transaction Detail","Transaction Summary","Transaction report","Tutor Details","Tutors List" |
            | 'CourseClass'         | "Academic Transcript","All Class Details","Assessment Outcome Report","Assessment Outcomes Per Student","Assessment Task Matrix","Budget Details by Class","Budget Summary by Class","Budget Summary by Subject","Budgets Details by Account","Budgets Details by Subject","Cancelled Classes Count","Certificate-Attendance","Class Contact Sheet","Class Delivery Plan","Class Details","Class Funding","Class Hours","Class Information","Class Invoice Record","Class Prepaid Fees Liability","Class Roll","Class Roll - Age","Class Roll - Contact No","Class Roll - Simple","Class Roll - Single Session","Class Roll - USI","Class Sign for Door ","Class Timetable Report","Class Timetable Report - Planning","Class Tutor List","Class Tutor Pay Schedule","Class by Site","Class by Subject","Classes","Commonwealth Assistance Notice","Course Completion Survey","Course Completion Survey Summary","Course Completion Survey Tutor","Demographic Data Report","Discounts by Class","Enrolment Confirmation","Enrolment Summary by Account","Enrolment summary by State","Enrolments and Income by Account","Income journal projection","Income summary projection","Individual Assessment Plan","Individual Training and Assessment Plan","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
#            | 'PriorLearning'       | "" |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getTemplate.feature') getTemplate



    Scenario: (+) Get templates by notadmin with rights

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                                                                                  |
            | 'Qualification'       | "Qualifications List"                                                                         |
            | 'Module'              | "Module List"                                                                                 |
            | 'Site'                | "Class Timetable Report","Class Timetable Report - Planning","Site Details","Site List"       |
            | 'Room'                | "Rooms List","Room Timetable","Class Timetable Report","Class Timetable Report - Planning"    |
            | 'AccountTransaction'  | "Transaction Detail","Transaction Summary"                                                    |
            | 'Account'             | "Transaction Detail","Transaction Summary"                                                    |
            | 'Payslip'             | "Payslip Report"                                                                              |
            | 'CorporatePass'       | "CorporatePass","CorporatePass List"                                                          |
            | 'Invoice'             | "Debtors and Creditors Report"                                                                |
            | 'PaymentIn'           | "PaymentIn"                                                                                   |
            | 'WaitingList'         | "Waiting List"                                                                                |
            | 'Application'         | "Application List"                                                                            |
            | 'Certificate'         | "Certificate","Transcript"                                                                    |
            | 'Survey'              | "Student feedback List"                                                                       |
            | 'Outcome'             | "Class Funding"                                                                               |
            | 'Audit'               | "Audit List"                                                                                  |
            | 'Discount'            | "Discount Take Up Report","Discount Take Up Summary"                                          |
            | 'Course'              | "Course Details","Course List"                                                                |
            | 'Enrolment'           | "Academic Transcript","Assessment Outcomes Per Student","Certificate-Attendance","Commonwealth Assistance Notice","Demographic Data Report","Enrolment Confirmation","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
            | 'ProductItem'         | "Voucher Report","Vouchers"                                                                   |
            | 'VoucherProduct'      | "Voucher Products List"                                                                       |
            | 'Contact'             | "Mailing List","Statement report","Student Attendance Averages","Student Details","Student Transcript","Transaction Detail","Transaction Summary","Transaction report","Tutor Details","Tutors List" |
            | 'CourseClass'         | "Academic Transcript","All Class Details","Assessment Outcome Report","Assessment Outcomes Per Student","Assessment Task Matrix","Budget Details by Class","Budget Summary by Class","Budget Summary by Subject","Budgets Details by Account","Budgets Details by Subject","Cancelled Classes Count","Certificate-Attendance","Class Contact Sheet","Class Delivery Plan","Class Details","Class Funding","Class Hours","Class Information","Class Invoice Record","Class Prepaid Fees Liability","Class Roll","Class Roll - Age","Class Roll - Contact No","Class Roll - Simple","Class Roll - Single Session","Class Roll - USI","Class Sign for Door ","Class Timetable Report","Class Timetable Report - Planning","Class Tutor List","Class Tutor Pay Schedule","Class by Site","Class by Subject","Classes","Commonwealth Assistance Notice","Course Completion Survey","Course Completion Survey Summary","Course Completion Survey Tutor","Demographic Data Report","Discounts by Class","Enrolment Confirmation","Enrolment Summary by Account","Enrolment summary by State","Enrolments and Income by Account","Income journal projection","Income summary projection","Individual Assessment Plan","Individual Training and Assessment Plan","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
#            | 'PriorLearning'       | "" |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getTemplate.feature') getTemplate



    Scenario: (+) Get templates by notadmin with rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                                                                                  |
            | 'AccountTransaction'  | "Transaction Detail","Transaction Summary"                                                    |

         * call read('getTemplate.feature') getTemplate



    Scenario: (-) Get templates by notadmin without rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                                                                                  |
            | 'Site'                | "Qualification PDF export"                                                                    |
            | 'Room'                | "Room PDF export"                                                                             |
            | 'Account'             | "Transaction Detail","Transaction Summary"                                                    |
            | 'Payslip'             | "Payslip Report"                                                                              |
            | 'CorporatePass'       | "CorporatePass","CorporatePass List"                                                          |
            | 'Invoice'             | "Debtors and Creditors Report"                                                                |
            | 'PaymentIn'           | "PaymentIn"                                                                                   |
            | 'Banking'             | "Banking Report","Cash Movements Detail Report","Cash Movements Summary Report"               |
            | 'WaitingList'         | "Waiting List"                                                                                |
            | 'Application'         | "Application List"                                                                            |
            | 'Certificate'         | "Certificate","Transcript"                                                                    |
            | 'Survey'              | "Student feedback List"                                                                       |
            | 'Outcome'             | "Class Funding"                                                                               |
            | 'Audit'               | "Audit List"                                                                                  |
            | 'Discount'            | "Discount Take Up Report","Discount Take Up Summary"                                          |
            | 'Course'              | "Course Details","Course List"                                                                |
            | 'Enrolment'           | "Academic Transcript","Assessment Outcomes Per Student","Certificate-Attendance","Commonwealth Assistance Notice","Demographic Data Report","Enrolment Confirmation","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
            | 'ProductItem'         | "Voucher Report","Vouchers"                                                                   |
            | 'VoucherProduct'      | "Voucher Products List"                                                                       |
            | 'Contact'             | "Mailing List","Statement report","Student Attendance Averages","Student Details","Student Transcript","Transaction Detail","Transaction Summary","Transaction report","Tutor Details","Tutors List" |
            | 'CourseClass'         | "Academic Transcript","All Class Details","Assessment Outcome Report","Assessment Outcomes Per Student","Assessment Task Matrix","Budget Details by Class","Budget Summary by Class","Budget Summary by Subject","Budgets Details by Account","Budgets Details by Subject","Cancelled Classes Count","Certificate-Attendance","Class Contact Sheet","Class Delivery Plan","Class Details","Class Funding","Class Hours","Class Information","Class Invoice Record","Class Prepaid Fees Liability","Class Roll","Class Roll - Age","Class Roll - Contact No","Class Roll - Simple","Class Roll - Single Session","Class Roll - USI","Class Sign for Door ","Class Timetable Report","Class Timetable Report - Planning","Class Tutor List","Class Tutor Pay Schedule","Class by Site","Class by Subject","Classes","Commonwealth Assistance Notice","Course Completion Survey","Course Completion Survey Summary","Course Completion Survey Tutor","Demographic Data Report","Discounts by Class","Enrolment Confirmation","Enrolment Summary by Account","Enrolment summary by State","Enrolments and Income by Account","Income journal projection","Income summary projection","Individual Assessment Plan","Individual Training and Assessment Plan","Sales Report","Sales by Course Location","Student Contact List","Student Special Needs","Total Discounts" |
#            | 'PriorLearning'       | "" |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getTemplateWithoutRights.feature') getTemplate