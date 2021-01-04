@ignore
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/invoice/duplicate'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/invoice/duplicate'
        * def ishPathLogin = 'login'
        * def ishPathInvoice = 'list/entity/invoice'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get invoice duplicate by admin

        Given path ishPath + '/11'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":null,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":null,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"#ignore",
        "dateDue":"#ignore",
        "overdue":0.00,
        "invoiceLines":[{"id":null,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":-300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1}],
        "total":-300.00,
        "amountOwing":-300.00,
        "publicNotes":"some public notes",
        "notes":[],
        "paymentPlans":
            [
            {"id":null,"date":"2019-08-20","type":"Credit note office","successful":true,"amount":-300.00,"entityName":"Invoice"},
            {"id":null,"date":"2022-08-10","type":"Payment due","successful":true,"amount":-100.00,"entityName":"InvoiceDueDate"},
            {"id":null,"date":"2022-09-20","type":"Payment due","successful":true,"amount":-200.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":null,
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """



    Scenario: (+) Get invoice duplicate by notadmin with access rights

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

        Given path ishPath + '/11'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":null,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":null,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"#ignore",
        "dateDue":"#ignore",
        "overdue":0.00,
        "invoiceLines":[{"id":null,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":-300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1}],
        "total":-300.00,
        "amountOwing":-300.00,
        "publicNotes":"some public notes",
        "notes":[],
        "paymentPlans":
            [
            {"id":null,"date":"2019-08-20","type":"Credit note office","successful":true,"amount":-300.00,"entityName":"Invoice"},
            {"id":null,"date":"2022-08-10","type":"Payment due","successful":true,"amount":-100.00,"entityName":"InvoiceDueDate"},
            {"id":null,"date":"2022-09-20","type":"Payment due","successful":true,"amount":-200.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":null,
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """



    Scenario: (-) Get invoice duplicate by notadmin without access rights

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

        Given path ishPath + '/11'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to duplicate invoice. Please contact your administrator"



    Scenario: (-) Get duplicate for not existing invoice

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."





