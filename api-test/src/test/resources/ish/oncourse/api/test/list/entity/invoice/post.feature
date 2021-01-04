@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/invoice'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/invoice'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create invoice by admin

        * def newInvoice =
        """
        {
        "billToAddress":"address str. Adelaide SA 5000",
        "createdByUser":null,
        "dateDue":"2019-09-09",
        "invoiceDate":"2019-09-09",
        "invoiceNumber":0,
        "publicNotes":null,
        "shippingAddress":"some shipping address",
        "customerReference":"invoice100",
        "sendEmail":true,
        "invoiceLines":[{"quantity":1,"incomeAccountId":8,"taxId":1,"taxEach":20,"discountEachExTax":10,"priceEachExTax":200,"title":"invoiceLine1","unit":"kg","description":"some description"}],
        "paymentPlans":[{"amount":210,"date":null,"entityName":"Invoice","id":null,"successful":true,"type":"Invoice office"}],
        "overdue":0,
        "contactName":"stud1 stud1",
        "contactId":2,
        "total":210
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Invoice'
        And param search = 'customerReference == "invoice100"'
        And param columns = 'customerReference'
        When method GET
        Then status 200

        * def id = response.rows[0].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice100",
        "invoiceNumber":"#number",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2019-09-09",
        "dateDue":"2019-09-09",
        "overdue":210.00,
        "invoiceLines":
            [{
            "id":"#number",
            "title":"invoiceLine1",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":8,
            "incomeAccountName":"Merchandise 42000",
            "cosAccountId":null,
            "cosAccountName":null,
            "priceEachExTax":200.00,
            "discountEachExTax":10.00,
            "taxEach":20.00,
            "finalPriceToPayIncTax":null,
            "taxId":1,
            "taxName":"Australian GST",
            "description":"some description",
            "courseClassId":null,
            "courseName":null,
            "courseCode":null,
            "classCode":null,
            "enrolmentId":null,
            "enrolledStudent":null,
            "courseId":null,
            "enrolment": null,
            "voucher": null,
            "article": null,
            "membership": null,
            "contactId": null
            }],
        "total":210.00,
        "amountOwing":210.00,
        "publicNotes":null,
        "paymentPlans":[{"id":"#number","date":"#ignore","type":"Invoice office","successful":true,"amount":210.00,"entityName":"Invoice"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":true,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Create invoice with correct Payment Plan

        * def newInvoice =
        """
        {
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice108p",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":null,
        "invoiceDate":"2023-06-01",
        "dateDue":"2023-06-23",
        "invoiceLines":
            [{
            "title":"invoiceLine5",
            "quantity":2.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":150.00,
            "discountEachExTax":0.00,
            "taxEach":15.00,
            "taxId":1,
            "description":null,
            "courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null
            }],
        "publicNotes":null,
        "paymentPlans":
            [
            {"date":"2023-06-23","type":"Payment due","successful":true,"amount":30.00,"entityName":"InvoiceDueDate"},
            {"date":"2023-07-23","type":"Payment due","successful":true,"amount":300.00,"entityName":"InvoiceDueDate"},
            {"date":"2019-06-21","type":"Invoice office","successful":true,"amount":330.00,"entityName":"Invoice"}
            ],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Invoice'
        And param search = 'customerReference == "invoice108p"'
        And param columns = 'customerReference'
        When method GET
        Then status 200

        * def id = response.rows[0].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice108p",
        "invoiceNumber":"#number",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":null,
        "invoiceDate":"2023-06-01",
        "dateDue":"2023-06-23",
        "overdue":0.0,
        "invoiceLines":[{"id":"#number","title":"invoiceLine5","quantity":2.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":150.00,"discountEachExTax":0.00,"taxEach":15.00,"finalPriceToPayIncTax":null,,"taxId":1,"taxName":"Australian GST","description":null,"courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":330.00,
        "amountOwing":330.00,
        "publicNotes":null,
        "paymentPlans":
            [
            {"id":"#number","date":"#ignore","type":"Invoice office","successful":true,"amount":330.00,"entityName":"Invoice"},
            {"id":"#number","date":"2023-06-23","type":"Payment due","successful":true,"amount":30.00,"entityName":"InvoiceDueDate"},
            {"id":"#number","date":"2023-07-23","type":"Payment due","successful":true,"amount":300.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Create GST invoice with correct Payment Plan

        * def newInvoice =
        """
        {
        "amountOwing":0,
        "billToAddress":null,
        "createdByUser":null,
        "dateDue":"2029-08-13",
        "invoiceDate":"2029-08-13",
        "invoiceNumber":0,
        "publicNotes":null,
        "shippingAddress":null,
        "customerReference":"invoice108pp",
        "sendEmail":true,
        "invoiceLines":[{"quantity":1,"incomeAccountId":8,"taxId":1,"taxEach":9.09,"discountEachExTax":0,"priceEachExTax":90.91,"title":"testLine"}],
        "paymentPlans":[{"amount":100,"date":"2029-08-13","entityName":"Invoice","id":null,"successful":true,"type":"Invoice office"},{"id":null,"date":"2029-08-13","type":"Payment due","successful":false,"amount":100,"entityName":"InvoiceDueDate"}],
        "overdue":0,
        "contactId":3,
        "total":100
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Invoice'
        And param search = 'customerReference == "invoice108pp"'
        And param columns = 'customerReference'
        When method GET
        Then status 200

        * def id = response.rows[0].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice108pp",
        "invoiceNumber":"#number",
        "billToAddress":null,
        "shippingAddress":null,
        "invoiceDate":"2029-08-13",
        "dateDue":"2029-08-13",
        "overdue":0.00,
        "invoiceLines":[{"id":"#number","title":"testLine","quantity":1.00,"unit":null,"incomeAccountId":8,"incomeAccountName":"Merchandise 42000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":90.91,"discountEachExTax":0.00,"taxEach":9.09,"finalPriceToPayIncTax": null,"taxId":1,"taxName":"Australian GST","description":null,"courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":100.00,
        "amountOwing":100.00,
        "publicNotes":null,
        "paymentPlans":[{"id":"#number","date":"#ignore","type":"Invoice office","successful":true,"amount":100.00,"entityName":"Invoice"},{"id":"#number","date":"2029-08-13","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":true,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Create invoice with Total amount == 0

        * def newInvoice =
        """
        {
        "amountOwing":0,
        "billToAddress":null,
        "createdByUser":null,
        "dateDue":"2019-08-31",
        "invoiceDate":"2019-08-08",
        "invoiceNumber":0,
        "publicNotes":null,
        "shippingAddress":null,
        "customerReference":"invoice108w",
        "sendEmail":"false",
        "invoiceLines":[{"quantity":1,"incomeAccountId":8,"taxId":1,"taxEach":0,"discountEachExTax":0,"priceEachExTax":0,"title":"someTitle"}],
        "paymentPlans":[{"amount":0,"date":"2019-08-08","entityName":"Invoice","id":null,"successful":true,"type":"Invoice office"}],
        "overdue":0,
        "contactId":3,
        "total":0
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Invoice'
        And param search = 'customerReference == "invoice108w"'
        And param columns = 'customerReference'
        When method GET
        Then status 200

        * def id = response.rows[0].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice108w",
        "invoiceNumber":"#number",
        "billToAddress":null,
        "shippingAddress":null,
        "invoiceDate":"2019-08-08",
        "dateDue":"2019-08-31",
        "overdue":0.00,
        "invoiceLines":[{"id":"#number","title":"someTitle","quantity":1.00,"unit":null,"incomeAccountId":8,"incomeAccountName":"Merchandise 42000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":0.00,"discountEachExTax":0.00,"taxEach":0.00,"finalPriceToPayIncTax": null,"taxId":1,"taxName":"Australian GST","description":null,"courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null,"courseId":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":0.00,
        "amountOwing":0.00,
        "publicNotes":null,
        "paymentPlans":[{"id":"#number","date":"#ignore","type":"Invoice office","successful":true,"amount":0.00,"entityName":"Invoice"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (-) Create invoice with incorrect Payment Plan

        * def newInvoice =
        """
        {
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice109p",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":null,
        "invoiceDate":"2022-06-01",
        "dateDue":"2022-06-23",
        "invoiceLines":
            [{
            "title":"invoiceLine5",
            "quantity":2.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":150.00,
            "discountEachExTax":0.00,
            "taxEach":15.00,
            "taxId":1,
            "description":null,
            "courseClassId":null,"courseName":null,"courseCode":null,"classCode":null,"enrolmentId":null,"enrolledStudent":null
            }],
        "publicNotes":null,
        "paymentPlans":
            [
            {"date":"2022-06-23","type":"Payment due","successful":true,"amount":30.00,"entityName":"InvoiceDueDate"},
            {"date":"2022-07-23","type":"Payment due","successful":true,"amount":50.00,"entityName":"InvoiceDueDate"},
            {"date":"2018-06-21","type":"Invoice office","successful":true,"amount":330.00,"entityName":"Invoice"}
            ],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "The payment plan adds up to $80.00 but the invoice total is $330.00. These must match before you can save this invoice."



    Scenario: (-) Create new invoice with empty Contact

        * def newInvoice =
        """
        {
        "contactId":null,
        "contactName":"stud1",
        "customerReference":"invoice102",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-30",
        "invoiceLines":
            [{
            "title":"invoiceLine3",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":200.00,
            "discountEachExTax":10.00,
            "taxId":1,
            "description":"some description"
            }],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "Contact id is required."



    Scenario: (-) Create new invoice with empty invoiceLine

        * def newInvoice =
        """
        {
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice103",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-30",
        "invoiceLines":[],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "At least 1 invoice line required."



    Scenario: (-) Create new invoice with empty Invoice Title

        * def newInvoice =
        """
        {
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice104",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-30",
        "invoiceLines":
            [{
            "title":"",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":200.00,
            "discountEachExTax":10.00,
            "taxId":1,
            "description":"some description"
            }],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "Invoice line title is required."



    Scenario: (-) Create new invoice when discountEachExTax > priceEachExTax

        * def newInvoice =
        """
        {
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice105",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-30",
        "invoiceLines":
            [{
            "title":"invoiceLine4",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":9.99,
            "discountEachExTax":10.00,
            "taxId":1,
            "description":"some description"
            }],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "Discount is too large."



    Scenario: (-) Create new invoice for not existing Contact

        * def newInvoice =
        """
        {
        "contactId":99999,
        "contactName":"stud1",
        "customerReference":"invoice106",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-30",
        "invoiceLines":
            [{
            "title":"invoiceLine4",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":200.00,
            "discountEachExTax":10.00,
            "taxId":1,
            "description":"some description"
            }],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "Contact with id=99999 not found."



    Scenario: (-) Create new invoice when dateDue early than invoiceDate

        * def newInvoice =
        """
        {
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice107",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2028-11-29",
        "dateDue":"2028-11-28",
        "invoiceLines":
            [{
            "title":"invoiceLine4",
            "quantity":1.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":200.00,
            "discountEachExTax":10.00,
            "taxId":1,
            "description":"some description"
            }],
        "publicNotes":"some public notes",
        "paymentPlans":[],
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 400
        And match $.errorMessage == "Date due must be after invoice date or have the same date."



    Scenario: (+) Create invoice by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def newInvoice =
        """
        {
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice101",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2025-06-01",
        "dateDue":"2025-06-01",
        "invoiceLines":
            [{
            "title":"invoiceLine2",
            "quantity":11.00,
            "unit":"kg",
            "incomeAccountId":7,
            "cosAccountId":null,
            "priceEachExTax":180.00,
            "discountEachExTax":0.00,
            "taxEach":0.00,
            "taxId":2,
            "description":"some invoice description",
            "courseClassId":1,
            "classCode":"1",
            "enrolmentId":115,
            "enrolledStudent":"stud1",
            "courseId":null
            }],
        "publicNotes":"some public notes",
        "sendEmail":false
        }
        """

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Invoice'
        And param search = 'customerReference == "invoice101"'
        And param columns = 'customerReference'
        When method GET
        Then status 200

        * def id = response.rows[0].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":2,
        "contactName":"stud1",
        "customerReference":"invoice101",
        "invoiceNumber":"#number",
        "billToAddress":"address str. Adelaide SA 5000",
        "shippingAddress":"some shipping address",
        "invoiceDate":"2025-06-01",
        "dateDue":"2025-06-01",
        "overdue":0.00,
        "invoiceLines":
            [{
            "id":"#number",
            "title":"invoiceLine2",
            "quantity":11.00,
            "unit":"kg",
            "incomeAccountId":7,
            "incomeAccountName":"Student enrolments 41000",
            "cosAccountId":null,
            "cosAccountName":null,
            "priceEachExTax":180.00,
            "discountEachExTax":0.00,
            "taxEach":0.00,
            "finalPriceToPayIncTax": null,
            "taxId":2,
            "taxName":"GST exempt",
            "description":"some invoice description",
            "courseClassId":1,
            "courseName":"Course1",
            "courseCode":"course1",
            "classCode":"1",
            "enrolmentId":115,
            "enrolledStudent":"stud1",
            "courseId":1,
            "enrolment": null,
            "voucher": null,
            "article": null,
            "membership": null,
            "contactId": null
            }],
        "total":1980.00,
        "amountOwing":1980.00,
        "publicNotes":"some public notes",
        "paymentPlans":
            [{
            "id":"#number",
            "date":"#ignore",
            "type":"Invoice office",
            "successful":true,
            "amount":1980.00,
            "entityName":"Invoice"
            }],
        "source":"office",
        "createdByUser":"UserWithRightsCreate",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (-) Create new invoice by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def newInvoice = {}

        Given path ishPath
        And request newInvoice
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create invoice. Please contact your administrator"
