@parallel=false
Feature: Main feature for all PUT requests with path 'export/avetmiss8'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathOutcomes = 'export/avetmiss8/outcomes'
        



    Scenario: (+) Export avetmiss8 (Commenced) by admin

        * def avetmissExport = {"ids":[2,5,102,104,105],"defaultStatus":false,"settings":{"flavour":"CSO (Community Colleges)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":null,"outcomesEnd":null,"includeLinkedOutcomes":false,"fundingContracts":[],"classIds":[]}}

        Given path ishPath
        And request avetmissExport
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}

        Given path ishPath + '/' + processId
        When method GET
        Then status 200
        And match $ contains '.txt'



    Scenario: (+) Export avetmiss8 (Custom date range) by admin

        * def avetmissExport = {"ids":[3,6,103,2,5,102,104,105,101,1,4],"defaultStatus":false,"settings":{"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2015-12-31","outcomesEnd":"2039-12-31","includeLinkedOutcomes":false,"fundingContracts":[],"classIds":[]}}

        Given path ishPath
        And request avetmissExport
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}

        Given path ishPath + '/' + processId
        When method GET
        Then status 200
        And match $ contains '.txt'



    Scenario: (+) Export avetmiss8 by notadmin with access rights

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

        * def avetmissExport = {"ids":[101,1,4],"defaultStatus":false,"settings":{"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[],"classIds":[]}}

        Given path ishPath
        And request avetmissExport
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}

        Given path ishPath + '/' + processId
        When method GET
        Then status 200
        And match $ contains '.txt'



    Scenario: (-) Export avetmiss8 by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def avetmissExport = {"ids":[101,1,4],"defaultStatus":false,"settings":{"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[],"classIds":[]}}

        Given path ishPath
        And request avetmissExport
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit avetmiss export. Please contact your administrator"


        

