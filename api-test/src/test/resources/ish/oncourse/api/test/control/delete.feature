@parallel=false
Feature: Main feature for all DELETE requests with path 'control'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'control'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathOutcomes = 'export/avetmiss8/outcomes'
        * def ishPathRole = 'role'
        



    Scenario: (+) Interrupt by id under admin

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

        Given path ishPath + '/' + processId
        When method DELETE
        Then status 204



    Scenario: (+) Interrupt by id under notadmin with access rights
        * configure headers = { Authorization: 'UserWithRightsDelete'}

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

        Given path ishPath + '/' + processId
        When method DELETE
        Then status 204



    Scenario: (-) Interrupt by id under notadmin without access rights


        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}
#       <--->

        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}

        Given path ishPathOutcomes
        And request filtersSettings
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit avetmiss export. Please contact your administrator"
