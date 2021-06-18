@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/paymentOut'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentOut'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * def ishPathInvoice = 'list/entity/invoice'
        



    Scenario: (+) Create Payment Out by admin

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":50.00,
        "datePayed":"2019-09-01",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":30.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":20.00}
            ],
        "privateNotes":"post 1",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 204

#       <---> Check created paymentOut:
        Given path ishPathPlain
        And param entity = 'PaymentOut'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post 1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "payeeId":29,
        "payeeName":"student2 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":50.00,
        "datePayed":"2019-09-01",
        "dateBanked":null,
        "invoices":
            [
            {"id":35,"dateDue":"2019-09-26","invoiceNumber":37,"amountOwing":-470.00,"amount":30.00},
            {"id":36,"dateDue":"2019-09-27","invoiceNumber":39,"amountOwing":-200.00,"amount":20.00}
            ],
        "privateNotes":"post 1",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """

#       <---> Check updated invoices:
        Given path ishPathInvoice + '/35'
        When method GET
        Then status 200
        And match $.amountOwing == -470.00

        Given path ishPathInvoice + '/36'
        When method GET
        Then status 200
        And match $.amountOwing == -200.00



    Scenario: (+) Create Payment Out by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":2,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":90.00,
        "datePayed":"2019-09-02",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":50.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":40.00}
            ],
        "privateNotes":"post 2",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 204

#       <---> Check created paymentOut:
        Given path ishPathPlain
        And param entity = 'PaymentOut'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post 2"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "payeeId":29,
        "payeeName":"student2 PaymentOut",
        "type":"Cheque",
        "chequeSummary":{"Cheque branch":null,"Cheque drawer":null,"Cheque bank":null},
        "paymentMethodId":2,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":90.00,
        "datePayed":"2019-09-02",
        "dateBanked":null,
        "invoices":
            [
            {"id":35,"dateDue":"2019-09-26","invoiceNumber":37,"amountOwing":-420.00,"amount":50.00},
            {"id":36,"dateDue":"2019-09-27","invoiceNumber":39,"amountOwing":-160.00,"amount":40.00}
            ],
        "privateNotes":"post 2",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"UserWithRightsCreate@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """

#       <---> Check updated invoices:
        Given path ishPathInvoice + '/35'
        When method GET
        Then status 200
        And match $.amountOwing == -420.00

        Given path ishPathInvoice + '/36'
        When method GET
        Then status 200
        And match $.amountOwing == -160.00



    Scenario: (-) Create Payment Out by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        Given path ishPath
        And request {}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create paymentIn. Please contact your administrator"



    Scenario: (-) Create Payment Out with empty datePayed

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"", "dateBanked":"2016-12-31"}
        When method PUT
        Then status 400
        And match $.errorMessage == "Date paid is mandatory"



    Scenario: (-) Create Payment Out with datePayed early than lock date

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"2015-09-10", "dateBanked":"2015-12-31"}
        When method PUT
        Then status 400
        And match $.errorMessage contains "Date paid  must be after lock date: "



    Scenario: (-) Create Payment Out with dateBanked early than datePayed

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2018-12-31"}
        When method PUT
        Then status 400
        And match $.errorMessage == "Date banked must be after Date paid"



    Scenario: (-) Create Payment Out when amount does not match to allocated invoices amount

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":0,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":30.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":20.00}
            ],
        "privateNotes":"some private notes"
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 400
        And match $.errorMessage == "Payment amount does not match to allocated invoices amount"



    Scenario: (-) Create Payment Out when amount greater than total amountOwing

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":0,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":1000.00,
        "datePayed":"2019-09-01",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":600.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":400.00}
            ],
        "privateNotes":"post 1"
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 400
        And match $.errorMessage == "Allocated invoice amount is wrong: $600.00"



    Scenario: (+) Create Payment Out when amount is not equal to total amountOwing

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":0,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":480.00,
        "datePayed":"2019-09-01",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":420.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":160.00}
            ],
        "privateNotes":"post 1"
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 400
        And match $.errorMessage == "Payment amount does not match to allocated invoices amount"



    Scenario: (-) Create Payment Out without Administration centre

        * def newPaymentOut =
        """
        {
        "payeeId":29,
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":50.00,
        "datePayed":"2019-09-01",
        "dateBanked":null,
        "invoices":
            [
            {id:35, dateDue:"2019-09-26", "invoiceNumber":37,  "amount":30.00},
            {id:36, dateDue:"2019-09-27", "invoiceNumber":38,  "amount":20.00}
            ],
        "privateNotes":"post 1",
        "administrationCenterId":null
        }
        """

        Given path ishPath
        And request newPaymentOut
        When method POST
        Then status 400
        And match $.errorMessage == "Administration centre is required"
