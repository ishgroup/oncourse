@parallel=false
Feature: Main feature for all GET requests with path 'list/export/pdf'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export/pdf'
        * def ishPathLogin = 'login'
        * def ishPathPdfTemplate = 'list/export/pdf/template'
        * def ishPathControl = 'control'
        

#       <---> Gives reports id's which we will use in requests
        Given path ishPathPdfTemplate
        And param entityName = 'Site'
        When method GET
        Then status 200
        * def sitePdfExportId = get[0] response[?(@.name == 'Site List')].id
        * print "id = " + sitePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Module'
        When method GET
        Then status 200
        * def modulePdfExportId = get[0] response[?(@.name == 'Module List')].id
        * print "id = " + modulePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Room'
        When method GET
        Then status 200
        * def roomPdfExportId = get[0] response[?(@.name == 'Rooms List')].id
        * print "id = " + roomPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        * def qualificationPdfExportId = get[0] response[?(@.name == 'Qualifications List')].id
        * print "id = " + qualificationPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'AccountTransaction'
        When method GET
        Then status 200
        * def transactionPdfExportId = get[0] response[?(@.name == 'Transaction Summary')].id
        * print "id = " + transactionPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Account'
        When method GET
        Then status 200
        * def accountPdfExportId = get[0] response[?(@.name == 'Transaction Summary')].id
        * print "id = " + accountPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Payslip'
        When method GET
        Then status 200
        * def payslipPdfExportId = get[0] response[?(@.name == 'Payslip Report')].id
        * print "id = " + payslipPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'CorporatePass'
        When method GET
        Then status 200
        * def corporatePassPdfExportId = get[0] response[?(@.name == 'CorporatePass')].id
        * print "id = " + corporatePassPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Invoice'
        When method GET
        Then status 200
        * def invoicePdfExportId = get[0] response[?(@.name == 'Debtors and Creditors Report')].id
        * print "id = " + invoicePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'PaymentIn'
        When method GET
        Then status 200
        * def paymentInPdfExportId = get[0] response[?(@.name == 'PaymentIn')].id
        * print "id = " + paymentInPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Banking'
        When method GET
        Then status 200
        * def bankingPdfExportId = get[0] response[?(@.name == 'Banking Report')].id
        * print "id = " + bankingPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'WaitingList'
        When method GET
        Then status 200
        * def waitingListPdfExportId = get[0] response[?(@.name == 'Waiting List')].id
        * print "id = " + waitingListPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Application'
        When method GET
        Then status 200
        * def applicationPdfExportId = get[0] response[?(@.name == 'Application List')].id
        * print "id = " + applicationPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Certificate'
        When method GET
        Then status 200
        * def certificatePdfExportId = get[0] response[?(@.name == 'Certificate')].id
        * print "id = " + certificatePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Survey'
        When method GET
        Then status 200
        * def surveyPdfExportId = get[0] response[?(@.name == 'Student feedback List')].id
        * print "id = " + surveyPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Outcome'
        When method GET
        Then status 200
        * def outcomePdfExportId = get[0] response[?(@.name == 'Class Funding')].id
        * print "id = " + outcomePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Audit'
        When method GET
        Then status 200
        * def auditPdfExportId = get[0] response[?(@.name == 'Audit List')].id
        * print "id = " + auditPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Discount'
        When method GET
        Then status 200
        * def discountPdfExportId = get[0] response[?(@.name == 'Discount Take Up Report')].id
        * print "id = " + discountPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Course'
        When method GET
        Then status 200
        * def coursePdfExportId = get[0] response[?(@.name == 'Course List')].id
        * print "id = " + coursePdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'CourseClass'
        When method GET
        Then status 200
        * def courseClassPdfExportId = get[0] response[?(@.name == 'Academic Transcript')].id
        * print "id = " + courseClassPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Enrolment'
        When method GET
        Then status 200
        * def enrolmentPdfExportId = get[0] response[?(@.name == 'Academic Transcript')].id
        * print "id = " + enrolmentPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'ProductItem'
        When method GET
        Then status 200
        * def productItemPdfExportId = get[0] response[?(@.name == 'Voucher Report')].id
        * print "id = " + productItemPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'VoucherProduct'
        When method GET
        Then status 200
        * def voucherProductPdfExportId = get[0] response[?(@.name == 'Voucher Products List')].id
        * print "id = " + voucherProductPdfExportId

        Given path ishPathPdfTemplate
        And param entityName = 'Contact'
        When method GET
        Then status 200
        * def contactPdfExportId = get[0] response[?(@.name == 'Mailing List')].id
        * print "id = " + contactPdfExportId

#       <----->




    Scenario: (+) Export PDF by admin

        * table getEntityPdf
        | entity                | dataToExport                                                                                                                                                                                                                      |
        | 'Qualification'       | {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[],"report":"#(qualificationPdfExportId)","overlay":null,"variables":{},"createPreview":false}    |
        | 'Module'              | {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[],"report":"#(modulePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'Site'                | {"search":"id == \"201\"","filter":"","tagGroups":[],"sorting":[],"report":"#(sitePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'Room'                | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(roomPdfExportId)","overlay":null,"variables":{},"createPreview":false}             |
        | 'AccountTransaction'  | {"search":"id == \"16\"","filter":"","tagGroups":[],"sorting":[],"report":"#(transactionPdfExportId)","overlay":null,"variables":{},"createPreview":false}     |
        | 'Account'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(accountPdfExportId)","overlay":null,"variables":{"localdateRange_from":"2017-12-01","localdateRange_to":"2019-12-31"},"createPreview":false} |
        | 'Payslip'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(payslipPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'CorporatePass'       | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(corporatePassPdfExportId)","overlay":null,"variables":{},"createPreview":false} |
        | 'Invoice'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(invoicePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'PaymentIn'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(paymentInPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Banking'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(bankingPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'WaitingList'         | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(waitingListPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Application'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(applicationPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Certificate'         | {"search":"id == \"1003\"","filter":"","tagGroups":[],"sorting":[],"report":"#(certificatePdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Survey'              | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(surveyPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Outcome'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(outcomePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'Audit'               | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(auditPdfExportId)","overlay":null,"variables":{},"createPreview":false}            |
        | 'Discount'            | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(discountPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Course'              | {"search":"id == \"5\"","filter":"","tagGroups":[],"sorting":[],"report":"#(coursePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'CourseClass'         | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(courseClassPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Enrolment'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(enrolmentPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'ProductItem'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(productItemPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'VoucherProduct'      | {"search":"id == \"1002\"","filter":"","tagGroups":[],"sorting":[],"report":"#(voucherProductPdfExportId)","overlay":null,"variables":{},"createPreview":false}|
        | 'Contact'             | {"search":"id == \"2\"","filter":"","tagGroups":[],"sorting":[],"report":"#(contactPdfExportId)","overlay":null,"variables":{},"createPreview":false}          |

        * call read('getPdfWithRights.feature') getEntityPdf




    Scenario: (+) Export PDF without sorting in request by admin

        * table getEntityPdf
        | entity                | dataToExport                                                                                                                                                                                                                    |
        | 'PaymentIn'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(paymentInPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Banking'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(bankingPdfExportId)","overlay":null,"variables":{},"createPreview":false}     |
        | 'WaitingList'         | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(waitingListPdfExportId)","overlay":null,"variables":{},"createPreview":false} |
        | 'Audit'               | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(auditPdfExportId)","overlay":null,"variables":{},"createPreview":false}          |

        * call read('getPdfWithRights.feature') getEntityPdf




    Scenario: (+) Export PDF by notadmin with access rights

#       <---> Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table getEntityPdf
        | entity                | dataToExport                                                                                                                                                                                                                      |
        | 'Qualification'       | {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[],"report":"#(qualificationPdfExportId)","overlay":null,"variables":{},"createPreview":false}    |
        | 'Module'              | {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[],"report":"#(modulePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'Site'                | {"search":"id == \"201\"","filter":"","tagGroups":[],"sorting":[],"report":"#(sitePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'Room'                | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(roomPdfExportId)","overlay":null,"variables":{},"createPreview":false}             |
        | 'AccountTransaction'  | {"search":"id == \"16\"","filter":"","tagGroups":[],"sorting":[],"report":"#(transactionPdfExportId)","overlay":null,"variables":{},"createPreview":false}     |
        | 'Account'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(accountPdfExportId)","overlay":null,"variables":{"localdateRange_from":"2017-12-01","localdateRange_to":"2019-12-31"},"createPreview":false} |
        | 'Payslip'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(payslipPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'CorporatePass'       | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(corporatePassPdfExportId)","overlay":null,"variables":{},"createPreview":false} |
        | 'Invoice'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(invoicePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'PaymentIn'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(paymentInPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Banking'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(bankingPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'WaitingList'         | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(waitingListPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Application'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(applicationPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Certificate'         | {"search":"id == \"1003\"","filter":"","tagGroups":[],"sorting":[],"report":"#(certificatePdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Survey'              | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(surveyPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Outcome'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(outcomePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'Audit'               | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(auditPdfExportId)","overlay":null,"variables":{},"createPreview":false}            |
        | 'Discount'            | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(discountPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Course'              | {"search":"id == \"5\"","filter":"","tagGroups":[],"sorting":[],"report":"#(coursePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'CourseClass'         | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(courseClassPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Enrolment'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(enrolmentPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'ProductItem'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(productItemPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'VoucherProduct'      | {"search":"id == \"1002\"","filter":"","tagGroups":[],"sorting":[],"report":"#(voucherProductPdfExportId)","overlay":null,"variables":{},"createPreview":false}|
        | 'Contact'             | {"search":"id == \"2\"","filter":"","tagGroups":[],"sorting":[],"report":"#(contactPdfExportId)","overlay":null,"variables":{},"createPreview":false}          |

        * call read('getPdfWithRights.feature') getEntityPdf




    Scenario: (-) Export PDF by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * table getEntityPdf
        | entity                | dataToExport                                                                                                                                                                                                                      |
        | 'Site'                | {"search":"id == \"201\"","filter":"","tagGroups":[],"sorting":[],"report":"#(sitePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'Room'                | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(roomPdfExportId)","overlay":null,"variables":{},"createPreview":false}             |
        | 'Account'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(accountPdfExportId)","overlay":null,"variables":{"localdateRange_from":"2017-12-01","localdateRange_to":"2019-12-31"},"createPreview":false} |
        | 'Payslip'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(payslipPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'CorporatePass'       | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(corporatePassPdfExportId)","overlay":null,"variables":{},"createPreview":false} |
        | 'Invoice'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(invoicePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'PaymentIn'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(paymentInPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Banking'             | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(bankingPdfExportId)","overlay":null,"variables":{},"createPreview":false}       |
        | 'WaitingList'         | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(waitingListPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Application'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(applicationPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Certificate'         | {"search":"id == \"1003\"","filter":"","tagGroups":[],"sorting":[],"report":"#(certificatePdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'Survey'              | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(surveyPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'Outcome'             | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(outcomePdfExportId)","overlay":null,"variables":{},"createPreview":false}          |
        | 'Audit'               | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(auditPdfExportId)","overlay":null,"variables":{},"createPreview":false}            |
        | 'Discount'            | {"search":"id == \"1001\"","filter":"","tagGroups":[],"sorting":[],"report":"#(discountPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Course'              | {"search":"id == \"5\"","filter":"","tagGroups":[],"sorting":[],"report":"#(coursePdfExportId)","overlay":null,"variables":{},"createPreview":false}           |
        | 'CourseClass'         | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(courseClassPdfExportId)","overlay":null,"variables":{},"createPreview":false}      |
        | 'Enrolment'           | {"search":"id == \"1\"","filter":"","tagGroups":[],"sorting":[],"report":"#(enrolmentPdfExportId)","overlay":null,"variables":{},"createPreview":false}        |
        | 'ProductItem'         | {"search":"id == \"1000\"","filter":"","tagGroups":[],"sorting":[],"report":"#(productItemPdfExportId)","overlay":null,"variables":{},"createPreview":false}   |
        | 'VoucherProduct'      | {"search":"id == \"1002\"","filter":"","tagGroups":[],"sorting":[],"report":"#(voucherProductPdfExportId)","overlay":null,"variables":{},"createPreview":false}|
        | 'Contact'             | {"search":"id == \"2\"","filter":"","tagGroups":[],"sorting":[],"report":"#(contactPdfExportId)","overlay":null,"variables":{},"createPreview":false}          |


        * call read('getPdfWithoutRights.feature') getEntityPdf


