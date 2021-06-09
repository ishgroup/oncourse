@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/corporatepass'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/corporatepass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing Corporate Pass by admin

#       <----->  Add a new entity for deleting and get id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass30",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass30"])].id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "CorporatePass with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing Corporate Pass by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass31",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass31"])].id

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
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "CorporatePass with id:" + id + " doesn't exist"



    Scenario: (-) Delete existing Corporate Pass by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newCorporatePass =
        """
        {
        "contactId":9,
        "contactFullName":"company #3",
        "password":"pass32",
        "expiryDate":"2040-03-31",
        "invoiceEmail":"co3@gmail.com",
        "linkedDiscounts":[],
        "linkedSalables":[]
        }
        """

        Given path ishPath
        And request newCorporatePass
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'CorporatePass'
        And param columns = 'password'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["pass32"])].id

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
        And match $.errorMessage == "Sorry, you have no permissions to delete corporate pass. Please contact your administrator"

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



    Scenario: (-) Delete existing Corporate Pass with relation

        Given path ishPath + '/1001'
        When method DELETE
        Then status 400
        And match response.errorMessage == "CorporatePass cannot be deleted because it has already been used."

        Given path ishPath + '/1002'
        When method DELETE
        Then status 400
        And match response.errorMessage == "CorporatePass cannot be deleted because it has already been used."



    Scenario: (-) Delete NOT existing Corporate Pass

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "CorporatePass with id:99999 doesn't exist"
        
