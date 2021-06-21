@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/paymentOut'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentOut'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * def ishPathBanking = 'list/entity/banking'
        



    Scenario: (+) Update Payment Out by admin

        Given path ishPath
        And request {"id":1000,"privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2019-09-11","administrationCenterId":202,"paymentMethodId":0}
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $.datePayed == "2019-09-10"
        And match $.dateBanked == "2019-09-11"
        And match $.privateNotes == "some private notes"
        And match $.administrationCenterId == 202

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath
        And request {"id":1000,"privateNotes":"","datePayed":"2019-09-19", "dateBanked":null,"administrationCenterId":202,"paymentMethodId":0}
        When method PUT
        Then status 204



    Scenario: (+) Update Payment Out banking day and check banking

        Given path ishPath
        And request {"id":1000,"privateNotes":"","datePayed":"2018-05-01", "dateBanked":"2018-05-02","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 204

#       <---> Check created banking:
        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'settlementDate'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["2018-05-02"])].id
        * print "id = " + id

        Given path ishPathBanking + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "settlementDate":"2018-05-02",
        "adminSite":"Default site 2",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":[{"id":"o1000","paymentId":1000,"reconciled":false,"source":"","paymentTypeName":"payment out","paymentMethodName":"Cash","contactId":20,"contactName":"student1 PaymentOut","amount":10.00,"created":"2019-08-19","status":"Success","paymentDate":"2018-05-01","reconcilable":true}],
        "total":-10.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":202
        }
        """

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath
        And request {"id":1000,"privateNotes":"","datePayed":"2019-09-19", "dateBanked":null,"administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 204

#       Check removed bankind:
        Given path ishPathBanking + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Banking with id:" + id + " doesn't exist"

#       Check updated to default paymentOut:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $.datePayed == "2019-09-19"
        And match $.dateBanked == null
        And match $.privateNotes == ""



    Scenario: (+) Update Payment Out by notadmin with access rights Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And request {"id":1000,"privateNotes":"some private notes","datePayed":"2019-09-01", "dateBanked":"2019-09-02","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 204

#       <---> Assertion under admin:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $.datePayed == "2019-09-01"
        And match $.dateBanked == "2019-09-02"
        And match $.privateNotes == "some private notes"

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath
        And request {"id":1000,"privateNotes":"","datePayed":"2019-09-19", "dateBanked":null,"administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 204



    Scenario: (-) Update Payment Out datePayed to empty

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"", "dateBanked":"2016-12-31","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 400
        And match $.errorMessage == "Date paid is mandatory"



    Scenario: (-) Update Payment Out datePayed to early than lock date

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"2015-09-10", "dateBanked":"2015-12-31","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 400
        And match $.errorMessage contains "Date paid  must be after lock date: "



    Scenario: (-) Update Payment Out dateBanked to early than datePayed

        Given path ishPath
        And request {"id":1000, "privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2018-12-31","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 400
        And match $.errorMessage == "Date banked must be after Date paid"



    Scenario: (-) Update Payment Out disabled fields

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":2,
        "payeeName":"stud1 stud1",
        "type":"Cash",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":1,
        "status":"Fail",
        "accountOut":2,
        "amount":99.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":99.00}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Update Payment Out to wrong payee

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":2,
        "payeeName":"stud1 stud1",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Update Payment Out type

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Update Payment Out status

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Failed",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Update Payment Out amount

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Update Payment Out invoice to another

        * def paymentOutToUpdate =
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":100.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":14}],
        "privateNotes":"",
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request paymentOutToUpdate
        When method PUT
        Then status 204

#       <---> Assertion. There should not be any changes:
        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (+) Update Administration centre in Payment Out assigned to banking

        Given path ishPath
        And request {"id":1000,"privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2019-09-11","administrationCenterId":202,"paymentMethodId":0}
        When method PUT
        Then status 204

#       <---> Change Administration centre for payment assigned to banking:
        Given path ishPath
        And request {"id":1000,"privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2019-09-11","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 400
        And match $.errorMessage == "Administration centre can not be changed for payment assigned to banking"

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath
        And request {"id":1000,"privateNotes":"","datePayed":"2019-09-19", "dateBanked":null,"administrationCenterId":202,"paymentMethodId":0}
        When method PUT
        Then status 204



    Scenario: (-) Update not existing Payment Out

        Given path ishPath
        And request {"id":99999,"privateNotes":"some private notes","datePayed":"2019-09-10", "dateBanked":"2019-09-11","administrationCenterId":200,"paymentMethodId":0}
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."


