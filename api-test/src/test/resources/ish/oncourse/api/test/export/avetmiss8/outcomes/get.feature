@parallel=false
Feature: Main feature for all GET requests with path 'export/avetmiss8/outcomes'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8/outcomes'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get passed outcomes/enrolments by admin, commenced outcomes now 'Started not assessed'

        * def filtersSettings =
        """
        {
        "flavour":"NCVER (Standard AVETMISS)",
        "fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],
        "outcomesStart":"2016-12-31",
        "outcomesEnd":"2017-12-31",
        "includeLinkedOutcomes":false,
        "fundingContracts":[]
        }
        """
        Given path ishPath
        And request filtersSettings
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPathControl + '/' + processId
        When method GET
        Then status 200

        * match $ == {"status":"#ignore","message":null}

        Given path ishPath + '/' +  processId
        When method GET
        Then status 200
        And match $ contains
        """
        [
        {"ids":"#present","type":"outcome","status":"in progress","category":"Started (not assessed)"},
        {"ids":"#present","type":"enrolment","status":"in progress","category":"Commenced"}
        ]
        """



