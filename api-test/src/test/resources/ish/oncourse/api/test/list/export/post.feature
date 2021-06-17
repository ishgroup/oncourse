@parallel=false
Feature: Main feature for all POST requests with path 'list/export'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathCsvTemplate = 'list/export/template'
        * def ishPathXmlTemplate = 'list/export/template'
        


    Scenario: (+) Get CSV processId by admin

        Given path ishPathCsvTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        And def qualificationCsvExportId = response[0].id

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"entityName":"Qualification","template":"#(qualificationCsvExportId)","search":"id == \"4\"","sorting":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#       <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}


    Scenario: (+) Get CSV processId by notadmin

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

        Given path ishPathCsvTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        And def qualificationCsvExportId = response[0].id

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"entityName":"Qualification","template":"#(qualificationCsvExportId)","search":"id == \"4\"","sorting":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#       <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (+) Get XML processId by admin

        Given path ishPathXmlTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        And def qualificationXmlExportId = response[1].id

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"entityName":"Qualification","template":"#(qualificationXmlExportId)","search":"id == \"4\"","sorting":[{"attribute":"nationalCode","ascending":true}]}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#       <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}


    Scenario: (+) Get XML processId by notadmin

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

        Given path ishPathXmlTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        And def qualificationXmlExportId = response[1].id

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"entityName":"Qualification","template":"#(qualificationXmlExportId)","search":"id == \"3\"","sorting":[{"attribute":"nationalCode","ascending":true}]}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#       <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}