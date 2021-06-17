@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/account/depositAccounts'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/account/depositAccounts'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get accounts for deposit banking by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $ contains
        """
        [{
        "id":1,
        "accountCode":"11100",
        "description":"Deposited funds",
        "isEnabled":true,
        "type":"asset",
        "tax":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "isDefaultAccount":true
        }]
        """



    Scenario: (+) Get accounts for deposit banking by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $ contains
        """
        [{
        "id":1,
        "accountCode":"11100",
        "description":"Deposited funds",
        "isEnabled":true,
        "type":"asset",
        "tax":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "isDefaultAccount":true
        }]
        """



    Scenario: (-) Get accounts for deposit banking by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permission to create banking. Please contact your administrator"

