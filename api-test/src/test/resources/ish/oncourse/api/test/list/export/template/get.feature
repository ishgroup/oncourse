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
            | 'Qualification'       | "Qualification CSV"                                                            |
            | 'Module'              | "Module CSV"                                                                   |
            | 'Site'                | "Site CSV"                                                                     |
            | 'Room'                | "Room CSV"                                                                     |
            | 'AccountTransaction'  | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","grouped MYOB"    |
            | 'Account'             | "Account CSV"                                                                  |
            | 'Payslip'             | "MYOB Activity Payslip CSV","Payslip CSV","Payslip MicroPay"     |
            | 'CorporatePass'       | "CorporatePass CSV"                                                            |
            | 'AbstractInvoice'             | "Debtor Payment Summary","Invoice CSV","InvoiceLine CSV"                |
            | 'PaymentIn'           | "PaymentIn CSV"                                                                |
            | 'Banking'             | "Banking CSV"                                                                  |
            | 'WaitingList'         | "WaitingList CSV"                                                              |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                         |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV"                                 |
            | 'Survey'              | "StudentFeedback CSV"                                                          |
            | 'Audit'               | "Audit Logs CSV"                                                            |
            | 'Discount'            | "Discount CSV"                                                                 |
            | 'Course'              | "Course CSV"                                                                   |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV","Class outcomes CSV","CourseClass CSV","CourseClass Sessions CSV","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV","Extended outcomes CSV"                                        |
            | 'ProductItem'         | "Voucher CSV"                                                                  |
            | 'VoucherProduct'      | "Voucher Product CSV"                                                          |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome CSV"                                                                  |
            | 'PriorLearning'       | "Prior Learning CSV"                                                           |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by admin

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML"        |
            | 'Module'              | "Module XML"               |
            | 'Site'                | "Site XML"                 |
            | 'Room'                | "Room XML"                 |
            | 'AccountTransaction'  | "Account Transaction XML"  |
            | 'Account'             | "Account XML"              |
            | 'Payslip'             | "Payslip XML"              |
            | 'CorporatePass'       | "CorporatePass XML"        |
            | 'AbstractInvoice'             | "Invoice XML"              |
            | 'PaymentIn'           | "PaymentIn XML"            |
            | 'Banking'             | "Banking XML"              |
            | 'WaitingList'         | "WaitingList XML"          |
            | 'Application'         | "Smart & Skilled Bulk Upload"          |
            | 'Certificate'         | "Certificate XML"          |
            | 'Survey'              | "StudentFeedback XML"      |
            | 'Audit'               | "Audit Logs XML"                |
            | 'Discount'            | "Discount XML"             |
            | 'Course'              | "Course XML"               |
            | 'CourseClass'         | "CourseClass XML","Indesign Brochure XML" |
            | 'Enrolment'           | "Enrolment XML"            |
            | 'ProductItem'         | "Voucher XML"              |
            | 'VoucherProduct'      | "VoucherProduct XML"       |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome XML"              |
            | 'PriorLearning'       | "Prior Learning XML"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (+) Get CSV templates by notadmin with rights: Delete

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                 |
            | 'Qualification'       | "Qualification CSV"   |
            | 'Module'              | "Module CSV"          |
            | 'Site'                | "Site CSV"            |
            | 'Room'                | "Room CSV"            |
            | 'AccountTransaction'  | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","grouped MYOB"    |
            | 'Account'             | "Account CSV"                                                                      |
            | 'Payslip'             | "MYOB Activity Payslip CSV","Payslip CSV","Payslip MicroPay"         |
            | 'CorporatePass'       | "CorporatePass CSV"                                                                |
            | 'AbstractInvoice'             | "Debtor Payment Summary","Invoice CSV","InvoiceLine CSV"                    |
            | 'PaymentIn'           | "PaymentIn CSV"                                                                    |
            | 'Banking'             | "Banking CSV"                                                                      |
            | 'WaitingList'         | "WaitingList CSV"                                                                  |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                             |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV"                                     |
            | 'Survey'              | "StudentFeedback CSV"                                                              |
            | 'Audit'               | "Audit Logs CSV"                                                                |
            | 'Discount'            | "Discount CSV"                                                                     |
            | 'Course'              | "Course CSV"                                                                       |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV","Class outcomes CSV","CourseClass CSV","CourseClass Sessions CSV","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV","Extended outcomes CSV"                                            |
            | 'ProductItem'         | "Voucher CSV"                                                                      |
            | 'VoucherProduct'      | "Voucher Product CSV"                                                              |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome CSV"                                                                  |
            | 'PriorLearning'       | "Prior Learning CSV"                                                           |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by notadmin with rights: Delete

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML"        |
            | 'Module'              | "Module XML"               |
            | 'Site'                | "Site XML"                 |
            | 'Room'                | "Room XML"                 |
            | 'AccountTransaction'  | "Account Transaction XML"  |
            | 'Account'             | "Account XML"              |
            | 'Payslip'             | "Payslip XML"              |
            | 'CorporatePass'       | "CorporatePass XML"        |
            | 'AbstractInvoice'             | "Invoice XML"              |
            | 'PaymentIn'           | "PaymentIn XML"            |
            | 'Banking'             | "Banking XML"              |
            | 'WaitingList'         | "WaitingList XML"          |
            | 'Application'         | "Smart & Skilled Bulk Upload"          |
            | 'Certificate'         | "Certificate XML"          |
            | 'Survey'              | "StudentFeedback XML"      |
            | 'Audit'               | "Audit Logs XML"                |
            | 'Discount'            | "Discount XML"             |
            | 'Course'              | "Course XML"               |
            | 'CourseClass'         | "CourseClass XML","Indesign Brochure XML" |
            | 'Enrolment'           | "Enrolment XML"            |
            | 'ProductItem'         | "Voucher XML"              |
            | 'VoucherProduct'      | "VoucherProduct XML"       |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome XML"              |
            | 'PriorLearning'       | "Prior Learning XML"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (+) Get CSV templates by notadmin with rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                 |
            | 'Qualification'       | "Qualification CSV"   |
            | 'Module'              | "Module CSV"          |
            | 'AccountTransaction'  | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","grouped MYOB"    |

         * call read('getCsvTemplate.feature') getTemplate



    Scenario: (+) Get XML templates by notadmin with rights: Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Qualification'       | "Qualification XML"        |
            | 'Module'              | "Module XML"               |
            | 'AccountTransaction'  | "Account Transaction XML"  |

         * call read('getXmlTemplate.feature') getTemplate



    Scenario: (-) Get CSV templates by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                                                                      |
            | 'Site'                | "Qualification CSV"                                                        |
            | 'Room'                | "Room CSV"                                                                 |
            | 'Account'             | "Account CSV"                                                              |
            | 'Payslip'             | "MYOB Activity Payslip CSV","Payslip CSV","Payslip MicroPay" |
            | 'CorporatePass'       | "CorporatePass CSV"                                                        |
            | 'AbstractInvoice'             | "Debtor Payment Summary","Invoice CSV","InvoiceLine CSV"            |
            | 'PaymentIn'           | "PaymentIn CSV"                                                            |
            | 'Banking'             | "Banking CSV"                                                              |
            | 'WaitingList'         | "WaitingList CSV"                                                          |
            | 'Application'         | "Smart & Skilled Bulk Upload"                                                     |
            | 'Certificate'         | "Certificate CQR CSV export","Certificate CSV"                             |
            | 'Survey'              | "StudentFeedback CSV"                                                      |
            | 'Audit'               | "Audit Logs CSV"                                                        |
            | 'Discount'            | "Discount CSV"                                                             |
            | 'Course'              | "Course CSV"                                                               |
            | 'CourseClass'         | "Attendance CSV","Class Budget Summary CSV","Class outcomes CSV","CourseClass CSV","CourseClass Sessions CSV","Extended outcomes CSV" |
            | 'Enrolment'           | "Enrolment CSV","Extended outcomes CSV"                                    |
            | 'ProductItem'         | "Voucher CSV"                                                              |
            | 'VoucherProduct'      | "Voucher Product CSV"                                                      |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome CSV"                                                              |
            | 'PriorLearning'       | "Prior Learning CSV"                                                       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getCsvTemplateWithoutRights.feature') getTemplate



    Scenario: (-) Get XML templates by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getTemplate

            | entity                | templateName                      |
            | 'Site'                | "Site XML"                 |
            | 'Room'                | "Room XML"                 |
            | 'Account'             | "Account XML"              |
            | 'Payslip'             | "Payslip XML"              |
            | 'CorporatePass'       | "CorporatePass XML"        |
            | 'AbstractInvoice'             | "Invoice XML"              |
            | 'PaymentIn'           | "PaymentIn XML"            |
            | 'Banking'             | "Banking XML"              |
            | 'WaitingList'         | "WaitingList XML"          |
            | 'Application'         | "Smart & Skilled Bulk Upload"          |
            | 'Certificate'         | "Certificate XML"          |
            | 'Survey'              | "StudentFeedback XML"      |
            | 'Audit'               | "Audit Logs XML"                |
            | 'Discount'            | "Discount XML"             |
            | 'Course'              | "Course XML"               |
            | 'CourseClass'         | "CourseClass XML","Indesign Brochure XML" |
            | 'Enrolment'           | "Enrolment XML"            |
            | 'ProductItem'         | "Voucher XML"              |
            | 'VoucherProduct'      | "VoucherProduct XML"       |
            | 'Contact'             | "Account Transaction CSV","Account Transaction MYOB","Account Transaction Segmentation CSV","Account Transaction XML","Contact CSV","Contact XML","NSW OLGR CSV","grouped MYOB" |
            | 'Outcome'             | "Outcome XML"              |
            | 'PriorLearning'       | "Prior Learning XML"       |
#            | 'ArticleProduct'      | "" |
#            | 'MembershipProduct'   | "" |

         * call read('getXmlTemplateWithoutRights.feature') getTemplate