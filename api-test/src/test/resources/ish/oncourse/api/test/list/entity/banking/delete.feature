@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/banking'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/banking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete existing Banking deposit by admin

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
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Banking with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing Banking deposit by notadmin with access rights

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

#       <----->  Add a new entity for deleting and get id:
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

#       <--->  Delete entity:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Banking with id:" + id + " doesn't exist"



    Scenario: (-) Delete existing Banking deposit by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
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
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete banking. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing Banking deposit

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Banking with id:99999 doesn't exist"
        
