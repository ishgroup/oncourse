@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/invoice'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/invoice'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        * def invoiceToDefault = {"id":11,"contactId":3,"contactName":"stud2","customerReference":"invoice for update","invoiceNumber":12,"billToAddress":"address str.1","shippingAddress":"shipping address1","invoiceDate":"2022-08-01","dateDue":"2022-08-11","overdue":0.00,"invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1"}],"total":300.00,"amountOwing":300.00,"publicNotes":"some public notes","paymentPlans":[{"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],"source":"office","createdByUser":"admin","sendEmail":false}



    Scenario: (+) Update Invoice by admin

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"finalPriceToPayIncTax":null,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":2,"date":"2022-08-15","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"},
            {"id":1,"date":"2022-09-25","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + "/11"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-15",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"finalPriceToPayIncTax": null,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":2,"date":"2022-08-15","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"},
            {"id":1,"date":"2022-09-25","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath +"/11"
        And request invoiceToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Invoice by notadmin with access rights

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

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-11",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":2,"date":"2022-08-11","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"},
            {"id":1,"date":"2022-09-21","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + "/11"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-11",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"finalPriceToPayIncTax":null,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":2,"date":"2022-08-11","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"},
            {"id":1,"date":"2022-09-21","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath +"/11"
        And request invoiceToDefault
        When method PUT
        Then status 204



    Scenario: (-) Update Invoice paymentPlan to incorrect paymentPlans

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-11",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1"}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":1,"date":"2022-08-11","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},
            {"id":2,"date":"2022-09-21","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The payment plan adds up to $200.00 but the invoice total is $300.00. These must match before you can save this invoice."



    Scenario: (-) Update Invoice by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update upd",
        "invoiceNumber":12,
        "billToAddress":"address str.1 upd",
        "shippingAddress":"shipping address1 upd",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"finalPriceToPayIncTax":null,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes upd",
        "paymentPlans":
            [
            {"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},
            {"id":2,"date":"2022-08-11","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"},
            {"id":1,"date":"2022-09-21","type":"Payment due","successful":true,"amount":150.00,"entityName":"InvoiceDueDate"}
            ],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update invoice. Please contact your administrator"



    Scenario: (-) Update Invoice required fields to empty

#       <---> Contact to empty:
        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":null,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":12,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1"}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes",
        "paymentPlans":[{"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Contact id is required."


#       <---> Invoice date to empty:
        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":12,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":null,
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes",
        "paymentPlans":[{"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Invoice date is required."



    Scenario: (-) Update Invoice Contact to another person

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":2,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":12,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes",
        "paymentPlans":[{"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (Contact should not be changed):
        Given path ishPath + "/11"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":11,"contactId":3,"contactName":"stud2","customerReference":"invoice for update","invoiceNumber":12,"billToAddress":"address str.1","shippingAddress":"shipping address1","invoiceDate":"2022-08-01","dateDue":"2022-08-10","overdue":0.0,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.0,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.0,"discountEachExTax":0.0,"taxEach":0.0,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1,"finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.0,"amountOwing":300.0,"publicNotes":"some public notes","paymentPlans":[{"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.0,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.0,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.0,"entityName":"InvoiceDueDate"}],"source":"office","createdByUser":"admin","sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """



    Scenario: (-) Update Invoice existing invoiceLine

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":12,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes",
        "paymentPlans":[{"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (invoiceLine should not be changed):
        Given path ishPath + "/11"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":11,"contactId":3,"contactName":"stud2","customerReference":"invoice for update","invoiceNumber":12,"billToAddress":"address str.1","shippingAddress":"shipping address1","invoiceDate":"2022-08-01","dateDue":"2022-08-10","overdue":0.0,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.0,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.0,"discountEachExTax":0.0,"taxEach":0.0,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1,"finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.0,"amountOwing":300.0,"publicNotes":"some public notes","paymentPlans":[{"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.0,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.0,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.0,"entityName":"InvoiceDueDate"}],"source":"office","createdByUser":"admin","sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """



    Scenario: (-) Update Invoice invoiceDate

        * def invoiceToUpdate =
        """
        {
        "id":11,
        "contactId":3,
        "contactName":"stud2",
        "customerReference":"invoice for update",
        "invoiceNumber":12,
        "billToAddress":"address str.1",
        "shippingAddress":"shipping address1",
        "invoiceDate":"2020-01-01",
        "dateDue":"2022-08-10",
        "overdue":0.00,
        "invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.00,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.00,"discountEachExTax":0.00,"taxEach":0.00,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.00,
        "amountOwing":300.00,
        "publicNotes":"some public notes",
        "paymentPlans":[{"id":11,"date":"2019-07-02","type":"Invoice office","successful":true,"amount":300.00,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.00,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.00,"entityName":"InvoiceDueDate"}],
        "source":"office",
        "createdByUser":"admin",
        "sendEmail":false
        }
        """

        Given path ishPath +"/11"
        And request invoiceToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (invoiceDate should not be changed):
        Given path ishPath + "/11"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":11,"contactId":3,"contactName":"stud2","customerReference":"invoice for update","invoiceNumber":12,"billToAddress":"address str.1","shippingAddress":"shipping address1",
        "invoiceDate":"2022-08-01",
        "dateDue":"2022-08-10","overdue":0.0,"invoiceLines":[{"id":112,"title":"Invoice for UPD","quantity":1.0,"unit":"kg","incomeAccountId":7,"incomeAccountName":"Student enrolments 41000","cosAccountId":null,"cosAccountName":null,"priceEachExTax":300.0,"discountEachExTax":0.0,"taxEach":0.0,"taxId":2,"taxName":"GST exempt","description":"some description","courseClassId":1,"courseName":"Course1","courseCode":"course1","classCode":"1","enrolmentId":1,"enrolledStudent":"stud1","courseId":1,"finalPriceToPayIncTax":null,"enrolment": null,"voucher": null,"article": null,"membership": null,"contactId": null}],
        "total":300.0,"amountOwing":300.0,"publicNotes":"some public notes","paymentPlans":[{"id":11,"date":"2019-07-03","type":"Invoice office","successful":true,"amount":300.0,"entityName":"Invoice"},{"id":2,"date":"2022-08-10","type":"Payment due","successful":true,"amount":100.0,"entityName":"InvoiceDueDate"},{"id":1,"date":"2022-09-20","type":"Payment due","successful":true,"amount":200.0,"entityName":"InvoiceDueDate"}],"source":"office","createdByUser":"admin","sendEmail":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "relatedFundingSourceId":null
        }
        """


