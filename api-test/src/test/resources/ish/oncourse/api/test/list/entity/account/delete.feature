@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/account'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/account'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing account by admin

#       <----->  Add a new entity for deleting and get its id:
        * def newAccount =
        """
        {
        "accountCode":"code100",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code100","true","asset","some descriptions"])].id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Account with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing account by notadmin with access rights

#       <----->  Add a new entity for deleting and get its id:
        * def newAccount =
        """
        {
        "accountCode":"code101",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code101","true","asset","some descriptions"])].id

        Given path '/logout'
        And request {}
        When method PUT
        
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

#       <---> Verification of deleting:
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Account with id:" + id + " doesn't exist"



    Scenario: (-) Delete existing account by notadmin without access rights

#       <----->  Add a new entity for deleting and get its id:
        * def newAccount =
        """
        {
        "accountCode":"code102",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":3}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code102","true","asset","some descriptions"])].id

        Given path '/logout'
        And request {}
        When method PUT
        
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
        And match $.errorMessage == "Sorry, you have no permissions to delete account. Please contact your administrator"

        Given path '/logout'
        And request {}
        When method PUT
        
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



    Scenario: (-) Delete account assigned to payments in

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete account with transactions."



    Scenario: (-) Delete account assigned to transactions

        Given path ishPath + '/2'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete account with transactions."



    Scenario: (-) Delete account assigned to taxes

        Given path ishPath + '/3'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete account assigned to taxes."



    Scenario: (-) Delete account assigned to discounts

        Given path ishPath + '/10'
        When method DELETE
        Then status 400
        And match $.errorMessage contains "Cannot delete account"



    Scenario: (-) Delete default account

        Given path ishPath + '/11'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete default account."



    Scenario: (-) Delete NOT existing account

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Account with id:99999 doesn't exist"


    Scenario: (-) Delete account with NULL as ID

        Given path ishPath + '/null'
        When method DELETE
        Then status 404

