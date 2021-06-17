@parallel=false
Feature: Main feature for all GET requests with path 'control'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'control'
        * def ishPathLogin = 'login'
        * def ishPathLogout = 'logout'
        * def ishPathControl = 'control'
        * def ishPathOutcomes = 'export/avetmiss8/outcomes'
        * def ishPathRole = 'role'
        



    Scenario: (+) Get control status by id by admin

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPathOutcomes
        And request filtersSettings
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPath + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (+) Get control status by id by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPathOutcomes
        And request filtersSettings
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPath + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (-) Get control status by id by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogout
        And request {}
        When method PUT

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPathOutcomes
        And request filtersSettings
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit avetmiss export. Please contact your administrator"
