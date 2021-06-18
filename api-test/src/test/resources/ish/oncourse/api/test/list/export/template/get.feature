@parallel=false
Feature: Main feature for all GET requests with path 'list/export/template'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export/template'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Get CSV templates by admin

        * table getTemplate

            | entity                | templateName                                                                          |
            | 'Qualification'       | "Qualification CSV export"                                                            |
            | 'Module'              | "Module CSV export"                                                                   |
            | 'Site'                | "Site CSV export"                                                                     |
            | 'Room'                | "Room CSV export"                                                                     |
            | 'AccountTransaction'  | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","grouped MYOB export"    |
            | 'Account'             | "Account CSV export"                                                                  |
            | 'Payslip'             | "MYOB Activity Payslip CSV export","Payslip CSV export","Payslip MicroPay export"     |
            | 'CorporatePass'       | "CorporatePass CSV export"                                                            |
            | 'Invoice'             | "Debtor Payment Summary","Invoice CSV export","InvoiceLine CSV export"                |
            | 'PaymentIn'           | "PaymentIn CSV export"                                                                |
            | 'Banking'             | "Banking CSV export"                                                                  |
            | 'WaitingList'         | "WaitingList CSV export"                                                              |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                         |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV export"                                 |
            | 'Survey'              | "StudentFeedback CSV export"                                                          |
            | 'Audit'               | "Audit Logging CSV export"                                                            |
            | 'Discount'            | "Discount CSV export"                                                                 |
            | 'Course'              | "Course CSV export"                                                                   |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV export","Class outcomes CSV export","CourseClass CSV export","CourseClass Sessions CSV export","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV export","Extended outcomes CSV"                                        |
            | 'ProductItem'         | "Voucher CSV export"                                                                  |
            | 'VoucherProduct'      | "Voucher Product CSV export"                                                          |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome CSV export"                                                                  |
            | 'PriorLearning'       | "Prior Learning CSV export"                                                           |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by admin

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML export"        |
            | 'Module'              | "Module XML export"               |
            | 'Site'                | "Site XML export"                 |
            | 'Room'                | "Room XML export"                 |
            | 'AccountTransaction'  | "Account Transaction XML export"  |
            | 'Account'             | "Account XML export"              |
            | 'Payslip'             | "Payslip XML export"              |
            | 'CorporatePass'       | "CorporatePass XML export"        |
            | 'Invoice'             | "Invoice XML export"              |
            | 'PaymentIn'           | "PaymentIn XML export"            |
            | 'Banking'             | "Banking XML export"              |
            | 'WaitingList'         | "WaitingList XML export"          |
            | 'Application'         | "Application XML export"          |
            | 'Certificate'         | "Certificate XML export"          |
            | 'Survey'              | "StudentFeedback XML export"      |
            | 'Audit'               | "Audit XML export"                |
            | 'Discount'            | "Discount XML export"             |
            | 'Course'              | "Course XML export"               |
            | 'CourseClass'         | "CourseClass XML export","Indesign Brochure XML export" |
            | 'Enrolment'           | "Enrolment XML export"            |
            | 'ProductItem'         | "Voucher XML export"              |
            | 'VoucherProduct'      | "VoucherProduct XML export"       |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome XML export"              |
            | 'PriorLearning'       | "Prior Learning XML export"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (+) Get CSV templates by notadmin with rights: Delete

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                 |
            | 'Qualification'       | "Qualification CSV export"   |
            | 'Module'              | "Module CSV export"          |
            | 'Site'                | "Site CSV export"            |
            | 'Room'                | "Room CSV export"            |
            | 'AccountTransaction'  | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","grouped MYOB export"    |
            | 'Account'             | "Account CSV export"                                                                      |
            | 'Payslip'             | "MYOB Activity Payslip CSV export","Payslip CSV export","Payslip MicroPay export"         |
            | 'CorporatePass'       | "CorporatePass CSV export"                                                                |
            | 'Invoice'             | "Debtor Payment Summary","Invoice CSV export","InvoiceLine CSV export"                    |
            | 'PaymentIn'           | "PaymentIn CSV export"                                                                    |
            | 'Banking'             | "Banking CSV export"                                                                      |
            | 'WaitingList'         | "WaitingList CSV export"                                                                  |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                             |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV export"                                     |
            | 'Survey'              | "StudentFeedback CSV export"                                                              |
            | 'Audit'               | "Audit Logging CSV export"                                                                |
            | 'Discount'            | "Discount CSV export"                                                                     |
            | 'Course'              | "Course CSV export"                                                                       |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV export","Class outcomes CSV export","CourseClass CSV export","CourseClass Sessions CSV export","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV export","Extended outcomes CSV"                                            |
            | 'ProductItem'         | "Voucher CSV export"                                                                      |
            | 'VoucherProduct'      | "Voucher Product CSV export"                                                              |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome CSV export"                                                                  |
            | 'PriorLearning'       | "Prior Learning CSV export"                                                           |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by notadmin with rights: Delete

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML export"        |
            | 'Module'              | "Module XML export"               |
            | 'Site'                | "Site XML export"                 |
            | 'Room'                | "Room XML export"                 |
            | 'AccountTransaction'  | "Account Transaction XML export"  |
            | 'Account'             | "Account XML export"              |
            | 'Payslip'             | "Payslip XML export"              |
            | 'CorporatePass'       | "CorporatePass XML export"        |
            | 'Invoice'             | "Invoice XML export"              |
            | 'PaymentIn'           | "PaymentIn XML export"            |
            | 'Banking'             | "Banking XML export"              |
            | 'WaitingList'         | "WaitingList XML export"          |
            | 'Application'         | "Application XML export"          |
            | 'Certificate'         | "Certificate XML export"          |
            | 'Survey'              | "StudentFeedback XML export"      |
            | 'Audit'               | "Audit XML export"                |
            | 'Discount'            | "Discount XML export"             |
            | 'Course'              | "Course XML export"               |
            | 'CourseClass'         | "CourseClass XML export","Indesign Brochure XML export" |
            | 'Enrolment'           | "Enrolment XML export"            |
            | 'ProductItem'         | "Voucher XML export"              |
            | 'VoucherProduct'      | "VoucherProduct XML export"       |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome XML export"              |
            | 'PriorLearning'       | "Prior Learning XML export"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (+) Get CSV templates by notadmin with rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                 |
            | 'Qualification'       | "Qualification CSV export"   |
            | 'Module'              | "Module CSV export"          |
            | 'AccountTransaction'  | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","grouped MYOB export"    |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by notadmin with rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML export"        |
            | 'Module'              | "Module XML export"               |
            | 'AccountTransaction'  | "Account Transaction XML export"  |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (-) Get CSV templates by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                                                                      |
            | 'Site'                | "Qualification CSV export"                                                        |
            | 'Room'                | "Room CSV export"                                                                 |
            | 'Account'             | "Account CSV export"                                                              |
            | 'Payslip'             | "MYOB Activity Payslip CSV export","Payslip CSV export","Payslip MicroPay export" |
            | 'CorporatePass'       | "CorporatePass CSV export"                                                        |
            | 'Invoice'             | "Debtor Payment Summary","Invoice CSV export","InvoiceLine CSV export"            |
            | 'PaymentIn'           | "PaymentIn CSV export"                                                            |
            | 'Banking'             | "Banking CSV export"                                                              |
            | 'WaitingList'         | "WaitingList CSV export"                                                          |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                     |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV export"                             |
            | 'Survey'              | "StudentFeedback CSV export"                                                      |
            | 'Audit'               | "Audit Logging CSV export"                                                        |
            | 'Discount'            | "Discount CSV export"                                                             |
            | 'Course'              | "Course CSV export"                                                               |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV export","Class outcomes CSV export","CourseClass CSV export","CourseClass Sessions CSV export","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV export","Extended outcomes CSV"                                    |
            | 'ProductItem'         | "Voucher CSV export"                                                              |
            | 'VoucherProduct'      | "Voucher Product CSV export"                                                      |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome CSV export"                                                              |
            | 'PriorLearning'       | "Prior Learning CSV export"                                                       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplateWithoutRights.feature') getTemplate



    Scenario: (-) Get XML templates by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Site'                | "Site XML export"                 |
            | 'Room'                | "Room XML export"                 |
            | 'Account'             | "Account XML export"              |
            | 'Payslip'             | "Payslip XML export"              |
            | 'CorporatePass'       | "CorporatePass XML export"        |
            | 'Invoice'             | "Invoice XML export"              |
            | 'PaymentIn'           | "PaymentIn XML export"            |
            | 'Banking'             | "Banking XML export"              |
            | 'WaitingList'         | "WaitingList XML export"          |
            | 'Application'         | "Application XML export"          |
            | 'Certificate'         | "Certificate XML export"          |
            | 'Survey'              | "StudentFeedback XML export"      |
            | 'Audit'               | "Audit XML export"                |
            | 'Discount'            | "Discount XML export"             |
            | 'Course'              | "Course XML export"               |
            | 'CourseClass'         | "CourseClass XML export","Indesign Brochure XML export" |
            | 'Enrolment'           | "Enrolment XML export"            |
            | 'ProductItem'         | "Voucher XML export"              |
            | 'VoucherProduct'      | "VoucherProduct XML export"       |
            | 'Contact'             | "Account Transaction CSV export","Account Transaction MYOB export","Account Transaction Segmentation CSV export","Account Transaction XML export","Contact CSV export","Contact XML export","NSW OLGR CSV export","grouped MYOB export" |
            | 'Outcome'             | "Outcome XML export"              |
            | 'PriorLearning'       | "Prior Learning XML export"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplateWithoutRights.feature') getTemplate