@parallel=false
Feature: Main feature for all GET requests with path 'list/export'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathTemplate = 'list/export/template'
        

#       <---> Gives CSV reports id's which we will use in requests:
        Given path ishPathTemplate
        And param entityName = 'Site'
        When method GET
        Then status 200
        * def siteCsvExportId = get[0] response[?(@.name == 'Site CSV')].id
        * print "id = " + siteCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Module'
        When method GET
        Then status 200
        * def moduleCsvExportId = get[0] response[?(@.name == 'Module CSV')].id
        * print "id = " + moduleCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Room'
        When method GET
        Then status 200
        * def roomCsvExportId = get[0] response[?(@.name == 'Room CSV')].id
        * print "id = " + roomCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        * def qualificationCsvExportId = get[0] response[?(@.name == 'Qualification CSV')].id
        * print "id = " + qualificationCsvExportId

        Given path ishPathTemplate
        And param entityName = 'AccountTransaction'
        When method GET
        Then status 200
        * def transactionCsvExportId = get[0] response[?(@.name == 'Account Transaction CSV')].id
        * print "id = " + transactionCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Account'
        When method GET
        Then status 200
        * def accountCsvExportId = get[0] response[?(@.name == 'Account CSV')].id
        * print "id = " + accountCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Payslip'
        When method GET
        Then status 200
        * def payslipCsvExportId = get[0] response[?(@.name == 'MYOB Activity Payslip CSV')].id
        * print "id = " + payslipCsvExportId

        Given path ishPathTemplate
        And param entityName = 'CorporatePass'
        When method GET
        Then status 200
        * def corporatePassCsvExportId = get[0] response[?(@.name == 'CorporatePass CSV')].id
        * print "id = " + corporatePassCsvExportId

        Given path ishPathTemplate
        And param entityName = 'AbstractInvoice'
        When method GET
        Then status 200
        * def invoiceCsvExportId = get[0] response[?(@.name == 'Debtor Payment Summary')].id
        * print "id = " + invoiceCsvExportId

        Given path ishPathTemplate
        And param entityName = 'PaymentIn'
        When method GET
        Then status 200
        * def paymentInCsvExportId = get[0] response[?(@.name == 'PaymentIn CSV')].id
        * print "id = " + paymentInCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Banking'
        When method GET
        Then status 200
        * def bankingCsvExportId = get[0] response[?(@.name == 'Banking CSV')].id
        * print "id = " + bankingCsvExportId

        Given path ishPathTemplate
        And param entityName = 'WaitingList'
        When method GET
        Then status 200
        * def waitingListCsvExportId = get[0] response[?(@.name == 'WaitingList CSV')].id
        * print "id = " + waitingListCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Application'
        When method GET
        Then status 200
        * def applicationCsvExportId = get[0] response[?(@.name == 'Smart & Skilled Bulk Upload')].id
        * print "id = " + applicationCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Certificate'
        When method GET
        Then status 200
        * def certificateCsvExportId = get[0] response[?(@.name == 'Certificate CQR CSV')].id
        * print "id = " + certificateCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Survey'
        When method GET
        Then status 200
        * def surveyCsvExportId = get[0] response[?(@.name == 'StudentFeedback CSV')].id
        * print "id = " + surveyCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Audit'
        When method GET
        Then status 200
        * def auditCsvExportId = get[0] response[?(@.name == 'Audit Logs CSV')].id
        * print "id = " + auditCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Discount'
        When method GET
        Then status 200
        * def discountCsvExportId = get[0] response[?(@.name == 'Discount CSV')].id
        * print "id = " + discountCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Course'
        When method GET
        Then status 200
        * def courseCsvExportId = get[0] response[?(@.name == 'Course CSV')].id
        * print "id = " + courseCsvExportId

        Given path ishPathTemplate
        And param entityName = 'CourseClass'
        When method GET
        Then status 200
        * def courseClassCsvExportId = get[0] response[?(@.name == 'Attendance CSV')].id
        * print "id = " + courseClassCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Enrolment'
        When method GET
        Then status 200
        * def enrolmentCsvExportId = get[0] response[?(@.name == 'Enrolment CSV')].id
        * print "id = " + enrolmentCsvExportId

        Given path ishPathTemplate
        And param entityName = 'ProductItem'
        When method GET
        Then status 200
        * def productItemCsvExportId = get[0] response[?(@.name == 'Voucher CSV')].id
        * print "id = " + productItemCsvExportId

        Given path ishPathTemplate
        And param entityName = 'VoucherProduct'
        When method GET
        Then status 200
        * def voucherProductCsvExportId = get[0] response[?(@.name == 'Voucher Product CSV')].id
        * print "id = " + voucherProductCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Contact'
        When method GET
        Then status 200
        * def contactCsvExportId = get[0] response[?(@.name == 'Contact CSV')].id
        * print "id = " + contactCsvExportId

        Given path ishPathTemplate
        And param entityName = 'Outcome'
        When method GET
        Then status 200
        * def outcomeCsvExportId = get[0] response[?(@.name == 'Outcome CSV')].id
        * print "id = " + outcomeCsvExportId

        Given path ishPathTemplate
        And param entityName = 'PriorLearning'
        When method GET
        Then status 200
        * def priorLearningCsvExportId = get[0] response[?(@.name == 'Prior Learning CSV')].id
        * print "id = " + priorLearningCsvExportId

#        Given path ishPathTemplate
#        And param entityName = 'ArticleProduct'
#        When method GET
#        Then status 200
#        * def articleProductCsvExportId = get[0] response[?(@.name == '')].id
#        * print "id = " + articleProductCsvExportId

#        Given path ishPathTemplate
#        And param entityName = 'MembershipProduct'
#        When method GET
#        Then status 200
#        * def membershipProductCsvExportId = get[0] response[?(@.name == '')].id
#        * print "id = " + membershipProductCsvExportId



#       <---> Gives XML reports id's which we will use in requests:
        Given path ishPathTemplate
        And param entityName = 'Site'
        When method GET
        Then status 200
        * def siteXmlExportId = get[0] response[?(@.name == 'Site XML')].id
        * print "id = " + siteXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Module'
        When method GET
        Then status 200
        * def moduleXmlExportId = get[0] response[?(@.name == 'Module XML')].id
        * print "id = " + moduleXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Room'
        When method GET
        Then status 200
        * def roomXmlExportId = get[0] response[?(@.name == 'Room XML')].id
        * print "id = " + roomXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        * def qualificationXmlExportId = get[0] response[?(@.name == 'Qualification XML')].id
        * print "id = " + qualificationXmlExportId

        Given path ishPathTemplate
        And param entityName = 'AccountTransaction'
        When method GET
        Then status 200
        * def transactionXmlExportId = get[0] response[?(@.name == 'Account Transaction XML')].id
        * print "id = " + transactionXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Account'
        When method GET
        Then status 200
        * def accountXmlExportId = get[0] response[?(@.name == 'Account XML')].id
        * print "id = " + accountXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Payslip'
        When method GET
        Then status 200
        * def payslipXmlExportId = get[0] response[?(@.name == 'Payslip XML')].id
        * print "id = " + payslipXmlExportId

        Given path ishPathTemplate
        And param entityName = 'CorporatePass'
        When method GET
        Then status 200
        * def corporatePassXmlExportId = get[0] response[?(@.name == 'CorporatePass XML')].id
        * print "id = " + corporatePassXmlExportId

        Given path ishPathTemplate
        And param entityName = 'AbstractInvoice'
        When method GET
        Then status 200
        * def invoiceXmlExportId = get[0] response[?(@.name == 'Invoice XML')].id
        * print "id = " + invoiceXmlExportId

        Given path ishPathTemplate
        And param entityName = 'PaymentIn'
        When method GET
        Then status 200
        * def paymentInXmlExportId = get[0] response[?(@.name == 'PaymentIn XML')].id
        * print "id = " + paymentInXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Banking'
        When method GET
        Then status 200
        * def bankingXmlExportId = get[0] response[?(@.name == 'Banking XML')].id
        * print "id = " + bankingXmlExportId

        Given path ishPathTemplate
        And param entityName = 'WaitingList'
        When method GET
        Then status 200
        * def waitingListXmlExportId = get[0] response[?(@.name == 'WaitingList XML')].id
        * print "id = " + waitingListXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Application'
        When method GET
        Then status 200
        * def applicationXmlExportId = get[0] response[?(@.name == 'Application XML')].id
        * print "id = " + applicationXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Certificate'
        When method GET
        Then status 200
        * def certificateXmlExportId = get[0] response[?(@.name == 'Certificate XML')].id
        * print "id = " + certificateXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Survey'
        When method GET
        Then status 200
        * def surveyXmlExportId = get[0] response[?(@.name == 'StudentFeedback XML')].id
        * print "id = " + surveyXmlExportId 

        Given path ishPathTemplate
        And param entityName = 'Audit'
        When method GET
        Then status 200
        * def auditXmlExportId = get[0] response[?(@.name == 'Audit Logs XML')].id
        * print "id = " + auditXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Discount'
        When method GET
        Then status 200
        * def discountXmlExportId = get[0] response[?(@.name == 'Discount XML')].id
        * print "id = " + discountXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Course'
        When method GET
        Then status 200
        * def courseXmlExportId = get[0] response[?(@.name == 'Course XML')].id
        * print "id = " + courseXmlExportId

        Given path ishPathTemplate
        And param entityName = 'CourseClass'
        When method GET
        Then status 200
        * def courseClassXmlExportId = get[0] response[?(@.name == 'CourseClass XML')].id
        * print "id = " + courseClassXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Enrolment'
        When method GET
        Then status 200
        * def enrolmentXmlExportId = get[0] response[?(@.name == 'Enrolment XML')].id
        * print "id = " + enrolmentXmlExportId

        Given path ishPathTemplate
        And param entityName = 'ProductItem'
        When method GET
        Then status 200
        * def productItemXmlExportId = get[0] response[?(@.name == 'Voucher XML')].id
        * print "id = " + productItemXmlExportId

        Given path ishPathTemplate
        And param entityName = 'VoucherProduct'
        When method GET
        Then status 200
        * def voucherProductXmlExportId = get[0] response[?(@.name == 'VoucherProduct XML')].id
        * print "id = " + voucherProductXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Contact'
        When method GET
        Then status 200
        * def contactXmlExportId = get[0] response[?(@.name == 'Contact XML')].id
        * print "id = " + contactXmlExportId

        Given path ishPathTemplate
        And param entityName = 'Outcome'
        When method GET
        Then status 200
        * def outcomeXmlExportId = get[0] response[?(@.name == 'Outcome XML')].id
        * print "id = " + outcomeXmlExportId

        Given path ishPathTemplate
        And param entityName = 'PriorLearning'
        When method GET
        Then status 200
        * def priorLearningXmlExportId = get[0] response[?(@.name == 'Prior Learning XML')].id
        * print "id = " + priorLearningXmlExportId



    Scenario: (+) Export CSV by admin

        * table getEntityCsv

            | entity                | dataToExport                                                                                                                                                                  | proxyId |
            | 'Qualification'       | {"entityName":"Qualification","template":"#(qualificationCsvExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                      | 1       |
            | 'Module'              | {"entityName":"Module","template":"#(moduleCsvExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                                    | 2       |
            | 'Site'                | {"entityName":"Site","template":"#(siteCsvExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              | 3       |
            | 'Room'                | {"entityName":"Room","template":"#(roomCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                | 4       |
            | 'AccountTransaction'  | {"entityName":"AccountTransaction","template":"#(transactionCsvExportId)","search":"id == \"104\"","sorting":[{"attribute":"transactionDate","ascending":true}]}              | 5       |
            | 'Account'             | {"entityName":"Account","template":1000,"variables":{"varName2":"2019-10-07","varName":"qwerty","varName4":"false","varName3":"2019-10-07T09:32:44.021Z"},"search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true,"complexAttribute":[]}]} | 6 |
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  | 7                                                                                    |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     | 8                                                                                    |
            | 'AbstractInvoice'             | {"entityName":"AbstractInvoice","template":"#(invoiceCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                                 | 9                                                                    |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    | 10                                                                                   |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      | 11                                                                                   |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 12                                                                                   |
            | 'Application'         | {"entityName":"Application","template":"#(applicationCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 13                                                                                   |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 14                                                                                   |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}             | 15                                                                                   |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}                     | 16                                                                                   |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          | 17                                                                                   |
            | 'Course'              | {"entityName":"Course","template":"#(courseCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      | 18                                                                                   |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            | 19                                                                                   |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}          | 20                                                                                   |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} | 21                                                                                   |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductCsvExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]}    | 22                                                                                   |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactCsvExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                | 23                                                                                   |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              | 24                                                                                   |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningCsvExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]} | 25                                                                       |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getCsvWithRights.feature') getEntityCsv



    Scenario: (+) Export XML by admin

        * table getEntityXml

            | entity                | dataToExport                                                                                                                                                                  | proxyId |
            | 'Qualification'       | {"entityName":"Qualification","template":"#(qualificationXmlExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                      | 201     |
            | 'Module'              | {"entityName":"Module","template":"#(moduleXmlExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                                    | 202     |
            | 'Site'                | {"entityName":"Site","template":"#(siteXmlExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              | 203     |
            | 'Room'                | {"entityName":"Room","template":"#(roomXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                | 204     |
            | 'AccountTransaction'  | {"entityName":"AccountTransaction","template":"#(transactionXmlExportId)","search":"id == \"104\"","sorting":[{"attribute":"transactionDate","ascending":true}]}              | 205     |
            | 'Account'             | {"entityName":"Account","template":"#(accountXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true}]}                                   | 206     |
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  | 207     |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     | 208     |
            | 'AbstractInvoice'             | {"entityName":"AbstractInvoice","template":"#(invoiceXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                 | 209     |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    | 210     |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      | 211     |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 212     |
            | 'Application'         | {"entityName":"Application","template":"#(applicationXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 213     |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 214     |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}             | 215     |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}                     | 216     |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          | 217     |
            | 'Course'              | {"entityName":"Course","template":"#(courseXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      | 218     |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            | 219     |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}          | 220     |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} | 221     |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductXmlExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]}    | 222     |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactXmlExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                | 223     |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              | 224     |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningXmlExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]}    | 225 |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getXmlWithRights.feature') getEntityXml



    Scenario: (+) Export CSV without sorting in request by admin

        * table getEntityCsv

            | entity                | dataToExport                                                                                    | proxyId   |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInCsvExportId)","search":"id == \"1\""}          | 101       |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingCsvExportId)","search":"id == \"1000\""}           | 102       |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListCsvExportId)","search":"id == \"1001\""}   | 103       |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditCsvExportId)","search":"id == \"1\""}                  | 104       |

        * call read('getCsvWithRights.feature') getEntityCsv



    Scenario: (+) Export XML without sorting in request by admin

        * table getEntityXml

            | entity                | dataToExport                                                                                       | proxyId |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInXmlExportId)","search":"id == \"1\""}             | 301     |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingXmlExportId)","search":"id == \"1000\""}              | 302     |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListXmlExportId)","search":"id == \"1001\""}      | 303     |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditXmlExportId)","search":"id == \"1\""}                     | 304     |

        * call read('getXmlWithRights.feature') getEntityXml




    Scenario: (+) Export CSV by notadmin with access rights

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getEntityCsv

            | entity                | dataToExport                                                                                                                                                                  | proxyId |
            | 'Qualification'       | {"entityName":"Qualification","template":"#(qualificationCsvExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                      | 1       |
            | 'Module'              | {"entityName":"Module","template":"#(moduleCsvExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                                    | 2       |
            | 'Site'                | {"entityName":"Site","template":"#(siteCsvExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              | 3       |
            | 'Room'                | {"entityName":"Room","template":"#(roomCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                | 4       |
            | 'AccountTransaction'  | {"entityName":"AccountTransaction","template":"#(transactionCsvExportId)","search":"id == \"104\"","sorting":[{"attribute":"transactionDate","ascending":true}]}              | 5       |
            | 'Account'             | {"entityName":"Account","template":1000,"variables":{"varName2":"2019-10-08","varName":"123","varName4":"true","varName3":"2019-10-30T16:17:00.000Z"},"search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true,"complexAttribute":[]}]} | 6|
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  | 7                                                                               |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     |8                                                                                |
            | 'AbstractInvoice'             | {"entityName":"AbstractInvoice","template":"#(invoiceCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                                 | 9                                                               |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    | 10                                                                              |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      | 11                                                                              |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 12                                                                              |
            | 'Application'         | {"entityName":"Application","template":"#(applicationCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 13                                                                              |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 14                                                                              |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}             | 15                                                                              |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}          | 16                                                                                         |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          | 17                                                                              |
            | 'Course'              | {"entityName":"Course","template":"#(courseCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      | 18                                                                              |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            | 19                                                                              |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 20                                                                                     |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} | 21                                                                              |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductCsvExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]} | 22                                                                                 |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactCsvExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                | 23                                                                              |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              | 24                                                                              |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningCsvExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]} | 25                                                                  |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getCsvWithRights.feature') getEntityCsv



    Scenario: (+) Export XML by notadmin with access rights

#       <---> Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * table getEntityXml

            | entity                | dataToExport                                                                                                                                                                  | proxyId |
            | 'Qualification'       | {"entityName":"Qualification","template":"#(qualificationXmlExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                      | 201     |
            | 'Module'              | {"entityName":"Module","template":"#(moduleXmlExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}                                    | 202     |
            | 'Site'                | {"entityName":"Site","template":"#(siteXmlExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              | 203     |
            | 'Room'                | {"entityName":"Room","template":"#(roomXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                | 204     |
            | 'AccountTransaction'  | {"entityName":"AccountTransaction","template":"#(transactionXmlExportId)","search":"id == \"104\"","sorting":[{"attribute":"transactionDate","ascending":true}]}              | 205     |
            | 'Account'             | {"entityName":"Account","template":"#(accountXmlExportId)","variables":{},"search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true}]}                    | 206     |
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  | 207     |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     | 208     |
            | 'AbstractInvoice'             | {"entityName":"AbstractInvoice","template":"#(invoiceXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                 | 209     |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    | 210     |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      | 211     |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 212     |
            | 'Application'         | {"entityName":"Application","template":"#(applicationXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 213     |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 214     |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   | 215               |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}                     | 216     |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          | 217     |
            | 'Course'              | {"entityName":"Course","template":"#(courseXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      | 218     |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            | 219     |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}          | 220     |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} | 221     |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductXmlExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]}    | 222     |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactXmlExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                | 223     |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              | 224     |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningXmlExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]}    | 225  |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getXmlWithRights.feature') getEntityXml




    Scenario: (-) Export CSV by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getEntityCsv

            | entity                | dataToExport                                                                                                                                                                  |
            | 'Site'                | {"entityName":"Site","template":"#(siteCsvExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              |
            | 'Room'                | {"entityName":"Room","template":"#(roomCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                |
            | 'Account'             | {"entityName":"Account","template":"#(accountCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true}]}                                   |
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     |
            | 'AbstractInvoice'     | {"entityName":"AbstractInvoice","template":"#(invoiceCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                                 |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Application'         | {"entityName":"Application","template":"#(applicationCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyCsvExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}             |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}          |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          |
            | 'Course'              | {"entityName":"Course","template":"#(courseCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemCsvExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductCsvExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]} |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactCsvExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeCsvExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningCsvExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]} |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getCsvWithoutRights.feature') getEntityCsv



    Scenario: (-) Export XML by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * table getEntityXml

            | entity                | dataToExport                                                                                                                                                                  |
            | 'Site'                | {"entityName":"Site","template":"#(siteXmlExportId)","search":"id == \"201\"","sorting":[{"attribute":"name","ascending":true}]}                                              |
            | 'Room'                | {"entityName":"Room","template":"#(roomXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true}]}                                                |
            | 'Account'             | {"entityName":"Account","template":"#(accountXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"accountCode","ascending":true}]}                                   |
            | 'Payslip'             | {"entityName":"Payslip","template":"#(payslipXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":true}]}                                  |
            | 'CorporatePass'       | {"entityName":"CorporatePass","template":"#(corporatePassXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"expiryDate","ascending":true}]}                     |
            | 'AbstractInvoice'     | {"entityName":"AbstractInvoice","template":"#(invoiceXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"invoiceNumber","ascending":true}]}                                 |
            | 'PaymentIn'           | {"entityName":"PaymentIn","template":"#(paymentInXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"source","ascending":true}]}                                    |
            | 'Banking'             | {"entityName":"Banking","template":"#(bankingXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"settlementDate","ascending":false,"complexAttribute":[]}]}      |
            | 'WaitingList'         | {"entityName":"WaitingList","template":"#(waitingListXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Application'         | {"entityName":"Application","template":"#(applicationXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Certificate'         | {"entityName":"Certificate","template":"#(certificateXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Survey'              | {"entityName":"Survey","template":"#(surveyXmlExportId)","search":"id == \"1000\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}   |
            | 'Audit'               | {"entityName":"Audit","template":"#(auditXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"created","ascending":true,"complexAttribute":[]}]}                     |
            | 'Discount'            | {"entityName":"Discount","template":"#(discountXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"validFrom","ascending":true,"complexAttribute":[]}]}          |
            | 'Course'              | {"entityName":"Course","template":"#(courseXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"name","ascending":true,"complexAttribute":[]}]}                      |
            | 'CourseClass'         | {"entityName":"CourseClass","template":"#(courseClassXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"code","ascending":true,"complexAttribute":[]}]}            |
            | 'Enrolment'           | {"entityName":"Enrolment","template":"#(enrolmentXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}          |
            | 'ProductItem'         | {"entityName":"ProductItem","template":"#(productItemXmlExportId)","search":"id == \"1001\"","sorting":[{"attribute":"product.name","ascending":true,"complexAttribute":[]}]} |
            | 'VoucherProduct'      | {"entityName":"VoucherProduct","template":"#(voucherProductXmlExportId)","search":"id == \"1002\"","sorting":[{"attribute":"sku","ascending":true,"complexAttribute":[]}]} |
            | 'Contact'             | {"entityName":"Contact","template":"#(contactXmlExportId)","variables":{},"search":"id == \"2\"","sorting":[]}                                                                |
            | 'Outcome'             | {"entityName":"Outcome","template":"#(outcomeXmlExportId)","search":"id == \"1\"","sorting":[{"attribute":"createdOn","ascending":false,"complexAttribute":[]}]}              |
            | 'PriorLearning'       | {"entityName":"PriorLearning","template":"#(priorLearningXmlExportId)","variables":{},"search":"id == \"1002\"","sorting":[{"attribute":"title","ascending":true,"complexAttribute":[]}]}    |
#            | 'ArticleProduct'      |  |
#            | 'MembershipProduct'   |  |

        * call read('getXmlWithoutRights.feature') getEntityXml


