@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/banking/reconcile'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking/reconcile'
        * def ishPathBanking = 'list/entity/banking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Reconcile banking deposit by admin

#      <---> Create new banking deposit to reconcile it and get id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPathBanking
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["3600.00"])].id
        * print "id = " + id

#      <---> Reconcile banking deposit:
        * def bankingToReconcile = ["#(id)"]

        Given path ishPath
        And request bankingToReconcile
        When method POST
        Then status 204

        Given path ishPathBanking + '/' + id
        When method GET
        Then status 200
        And match $.reconciledStatus == "Yes"
        And match $.payments[*].reconciled == [true,true]

#       <--->  Scenario have been finished. Now remove banking deposit:
        Given path ishPathBanking + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Reconcile several banking deposits by admin

#      <---> Create 2 new banking deposits to reconcile and get its ids:
        * def newBanking1 =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        * def newBanking2 =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":1800.00,
        "administrationCenterId":200
        }
        """

        Given path ishPathBanking
        And request newBanking1
        When method POST
        Then status 204

        Given path ishPathBanking
        And request newBanking2
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id1 = get[0] response.rows[?(@.values == ["3600.00"])].id
        * def id2 = get[0] response.rows[?(@.values == ["1800.00"])].id
        * print "id1 = " + id1
        * print "id2 = " + id2

#      <---> Reconcile banking deposit:
        * def bankingToReconcile = ["#(id1)","#(id2)"]

        Given path ishPath
        And request bankingToReconcile
        When method POST
        Then status 204

        Given path ishPathBanking + '/' + id1
        When method GET
        Then status 200
        And match $.reconciledStatus == "Yes"
        And match $.payments[*].reconciled == [true,true]

        Given path ishPathBanking + '/' + id2
        When method GET
        Then status 200
        And match $.reconciledStatus == "Yes"
        And match $.payments[*].reconciled == [true]

#       <--->  Scenario have been finished. Now remove banking deposits:
        Given path ishPathBanking + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathBanking + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (+) Reconcile banking deposit by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        

#      <---> Create new banking deposit to reconcile it and get id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPathBanking
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["3600.00"])].id
        * print "id = " + id

#      <---> Reconcile banking deposit:

        * def bankingToReconcile = ["#(id)"]

        Given path ishPath
        And request bankingToReconcile
        When method POST
        Then status 204

        Given path ishPathBanking + '/' + id
        When method GET
        Then status 200
        And match $.reconciledStatus == "Yes"
        And match $.payments[*].reconciled == [true,true]

#       <--->  Scenario have been finished. Now remove banking deposit:
        Given path ishPathBanking + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Reconcile banking deposit by notadmin without access rights

#      <---> Create new banking deposit to reconcile it and get id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPathBanking
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["3600.00"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        

#      <---> Reconcile banking deposit:
        * def bankingToReconcile = ["#(id)"]

        Given path ishPath
        And request bankingToReconcile
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permission to reconcile banking deposits. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove banking deposit by admin:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathBanking + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Reconcile not existing banking deposit

        * def bankingToReconcile = ["99999"]

        Given path ishPath
        And request bankingToReconcile
        When method POST
        Then status 400
        And match $.errorMessage == "Banking with id:99999 doesn't exist"
