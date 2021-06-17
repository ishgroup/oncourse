@parallel=false
Feature: Main feature for all PUT requests with path 'export/avetmiss8/outcomes'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8/outcomes'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        


    Scenario: (+) Get outcomes/enrolments by admin

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPath
        And request filtersSettings
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (+) Get outcomes/enrolments by notadmin with rights
        
        
#       <--->  Login as notadmin
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

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPath
        And request filtersSettings
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (-) Get outcomes/enrolments by notadmin without rights

        Given path '/logout'
        And request {}
        When method PUT
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":"false","fundingContracts":[{"id":3,"name":"CSO (Community Colleges)","flavour":"CSO (Community Colleges)","active":true},{"id":5,"name":"DETConnect (Queensland)","flavour":"DETConnect (Queensland)","active":true},{"id":13,"name":"AVETARS (ACT)","flavour":"AVETARS (ACT)","active":true}]}

        Given path ishPath
        And request filtersSettings
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit avetmiss export. Please contact your administrator"