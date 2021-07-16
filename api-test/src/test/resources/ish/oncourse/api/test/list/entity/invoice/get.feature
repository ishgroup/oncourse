@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/invoice'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/invoice'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all invoices by admin

        Given path ishPathList
        And param entity = 'Invoice'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","101","3","4","5","6"]



    Scenario: (+) Get invoice by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1,
        "contactId":2,
        "contactName":"stud1",
        "customerReference":null,
        "invoiceNumber":1,
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":null,
        "invoiceDate":"2018-11-29",
        "dateDue":"2018-11-29",
        "overdue":0.00,
        "invoiceLines":
            [
            {"id":1,"title":"stud1 enrolled in course1-2","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":545.45,"discountEachExTax":0.00,"taxEach":54.55,"finalPriceToPayIncTax":null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-12-2018 12:56 AM AEDT","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null},
            {"id":2,"title":"stud1 enrolled in course1-3","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":636.36,"discountEachExTax":0.00,"taxEach":63.64,"finalPriceToPayIncTax":null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-11-2030 12:58 AM AEDT","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null},
            {"id":3,"title":"stud1 enrolled in course1-1","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":454.55,"discountEachExTax":0.00,"taxEach":45.45,"finalPriceToPayIncTax":null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-05-2017 12:54 AM AEST","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null}
            ],
        "total":1800.00,
        "amountOwing":0.00,
        "publicNotes":null,
        "paymentPlans":[{"id":1,"date":"2018-11-29","type":"Invoice office","successful":true,"amount":1800.00,"entityName":"Invoice"},{"id":1,"date":"2018-11-29","type":"payment in (Cash)","successful":true,"amount":1800.00,"entityName":"PaymentIn"}],
        "source":"office",
        "createdByUser":"admin@gmail.com",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """


    Scenario: (-) Get not existing invoice

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '9999' doesn't exist."
        

    Scenario: (+) Get invoice by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":null,
        "invoiceNumber":2,
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":null,
        "invoiceDate":"2018-11-29",
        "dateDue":"2018-11-29",
        "overdue":0.00,
        "invoiceLines":
            [
            {"id":4,"title":"stud2 enrolled in course1-3","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":636.36,"discountEachExTax":0.00,"taxEach":63.64,finalPriceToPayIncTax:null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-11-2030 12:58 AM AEDT","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null},
            {"id":5,"title":"stud2 enrolled in course1-2","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":545.45,"discountEachExTax":0.00,"taxEach":54.55,finalPriceToPayIncTax:null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-12-2018 12:56 AM AEDT","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null},
            {"id":6,"title":"stud2 enrolled in course1-1","quantity":1.00,"unit":null,"incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","discountId":null,"discountName":null,"priceEachExTax":454.55,"discountEachExTax":0.00,"taxEach":45.45,finalPriceToPayIncTax:null,"taxId":1,"taxName":"Australian GST","description":"Course1 starting on 01-05-2017 12:54 AM AEST","courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment":null,"voucher":null,"article":null,"membership":null,"contactId":null}
            ],
        "total":1800.00,
        "amountOwing":0.00,
        "publicNotes":null,
        "paymentPlans":[{"id":2,"date":"2018-11-29","type":"Invoice office","successful":true,"amount":1800.00,"entityName":"Invoice"},{"id":2,"date":"2018-11-29","type":"payment in (Cash)","successful":true,"amount":1800.00,"entityName":"PaymentIn"}],
        "source":"office",
        "createdByUser":"admin@gmail.com",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """



    Scenario: (+) Get list of all invoices by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Invoice'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","101","3","4","5","6"]



    Scenario: (-) Get list of all invoices by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Invoice'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get invoice by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get invoice. Please contact your administrator"
