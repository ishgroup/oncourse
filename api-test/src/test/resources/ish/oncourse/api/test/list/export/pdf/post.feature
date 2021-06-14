@parallel=false
Feature: Main feature for all POST requests with path 'list/export/pdf'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export/pdf'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathPdfTemplate = 'list/export/pdf/template'
        


        Given path ishPathPdfTemplate
        And param entityName = 'Qualification'
        When method GET
        Then status 200
        And def qualificationPdfExportId = response[0].id


    Scenario: (+) Get processId  by admin

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],"report":"#(qualificationPdfExportId)","overlay":null,"variables":{},"createPreview":false}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#        <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}



    Scenario: (+) Get processId by notadmin

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

        Given path ishPath
        And param entityName = 'Qualification'
        And request {"search":"id == \"3\"","filter":"","tagGroups":[],"sorting":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],"report":"#(qualificationPdfExportId)","overlay":null,"variables":{},"createPreview":false}
        When method POST
        Then status 200
        And match $ == '#notnull'

        * def processId = $

#        <---> Verifying processId:
        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}