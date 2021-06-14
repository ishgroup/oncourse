@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/banking'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update date and remove payment from Banking deposit by admin

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["5400.00"])].id
        * print "id = " + id
#       <--->

        * def bankingToUpdate =
        """
        {
        "settlementDate":"2019-05-20",
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

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "settlementDate":"2019-05-20",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update (Reconcile) banking deposit simultaneously with paymentIn and paymentOut

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-09-25",
        "payments":
            [
            {"id":"p8","paymentId":8,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":50,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
            {"id":"o1000","paymentId":1000,"reconciled":false,"source":"","paymentTypeName":"payment out","paymentMethodName":"Cash","contactId":20,"contactName":"student1 PaymentOut","amount":10,"created":"2019-08-19","status":"Success","paymentDate":"2019-09-19","reconcilable":true}
            ],
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["40.00"])].id
        * print "id = " + id
#       <--->

        * def bankingToUpdate =
        """
        {
        "id":"#(id)",
        "settlementDate":"2019-09-25",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p8","paymentId":8,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":50,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
            {"id":"o1000","paymentId":1000,"reconciled":true,"source":"","paymentTypeName":"payment out","paymentMethodName":"Cash","contactId":20,"contactName":"student1 PaymentOut","amount":10,"created":"2019-08-19","status":"Success","paymentDate":"2019-09-19","reconcilable":true}
            ],
        "total":40,
        "administrationCenterId":200
        }
        """

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "settlementDate":"2019-09-25",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"Yes",
        "payments":
            [
            {"id":"p8","paymentId":8,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":14,"contactName":"stud8","amount":50.00,"created":"2019-07-22","status":"Success","paymentDate":"2019-07-22","reconcilable":true},
            {"id":"o1000","paymentId":1000,"reconciled":true,"source":"","paymentTypeName":"payment out","paymentMethodName":"Cash","contactId":20,"contactName":"student1 PaymentOut","amount":10.00,"created":"2019-08-19","status":"Success","paymentDate":"2019-09-19","reconcilable":true}
            ],
        "total":40.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update reconcile status in Banking deposit by admin

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["5400.00"])].id
        * print "id = " + id
#       <--->

        * def bankingToUpdate =
        """
        {
        "settlementDate":"2019-04-16",
        "reconciledStatus":"Partially",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"onCourse Administrator",
        "reconciledStatus":"Partially",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update date and remove payment from Banking deposit by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["5400.00"])].id
        * print "id = " + id
#       <--->

        * def bankingToUpdate =
        """
        {
        "settlementDate":"2019-05-20",
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

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "settlementDate":"2019-05-20",
        "adminSite":"Default site",
        "createdBy":"UserWithRightsDelete UserWithRightsDelete",
        "reconciledStatus":"No",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update reconcile status in Banking deposit by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["5400.00"])].id
        * print "id = " + id
#       <--->

        * def bankingToUpdate =
        """
        {
        "settlementDate":"2019-04-16",
        "reconciledStatus":"Partially",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "settlementDate":"2019-04-16",
        "adminSite":"Default site",
        "createdBy":"UserWithRightsDelete UserWithRightsDelete",
        "reconciledStatus":"Partially",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Banking deposit by notadmin without rights

#       <----->  Add a new entity to update and define its id:
        * def newBanking =
        """
        {
        "settlementDate":"2019-04-16",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p101","paymentId":101,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":4,"contactName":"stud3","amount":1800.00,"created":"2018-11-30","status":"Success","paymentDate":"2018-11-30","reconcilable":true}
            ],
        "total":5400.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath
        And request newBanking
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Banking'
        And param columns = 'total'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["5400.00"])].id
        * print "id = " + id

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

        * def bankingToUpdate =
        """
        {
        "settlementDate":"2019-05-20",
        "reconciledStatus":"Partially",
        "payments":
            [
            {"id":"p1","paymentId":1,"reconciled":true,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true},
            {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29","reconcilable":true}
            ],
        "total":3600.00,
        "administrationCenterId":200
        }
        """

        Given path ishPath + '/' + id
        And request bankingToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit banking. Please contact your administrator"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Banking deposit

         * def bankingToUpdate =
         """
         {
         "settlementDate":"2019-05-20",
         "adminSite":"Default site",
         "createdBy":"noNameUser",
         "reconciledStatus":"No",
         "payments":
             [
             {"id":"p1","paymentId":1,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":2,"contactName":"stud1","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"},
             {"id":"p2","paymentId":2,"reconciled":false,"source":"office","paymentTypeName":"payment in","paymentMethodName":"Cash","contactId":3,"contactName":"stud2","amount":1800.00,"created":"2018-11-29","status":"Success","paymentDate":"2018-11-29"}
             ],
         "total":3600.00,
         "administrationCenterId":200
         }
         """

         Given path ishPath + '/99999'
         And request bankingToUpdate
         When method PUT
         Then status 400
         And match $.errorMessage == "Banking with id:99999 doesn't exist"







