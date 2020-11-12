@parallel=false
Feature: Main feature for all GET requests with path 'export/avetmiss8/outcomes'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8/outcomes'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get passed outcomes/enrolments by admin

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
        {"ids":"#present","type":"outcome","status":"in progress","category":"Commenced"},
        {"ids":"#present","type":"enrolment","status":"in progress","category":"Commenced"}
        ]
        """



#    Scenario: (+) Get passed+commenced outcomes/enrolments by admin
#
#        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2019-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}
#
#        Given path ishPath
#        And request filtersSettings
#        When method PUT
#        Then status 200
#
#        * def processId = $
#
#        Given path ishPathControl + '/' + processId
#        When method GET
#        Then status 200
#
#        * match $ == {"status":"#ignore","message":null}
#
#        Given path ishPath + '/' +  processId
#        When method GET
#        Then status 200
#        And match $[*].ids contains [[101],[102],[1],[4]]

##       <---> passed+commenced+notStarted outcomes
#        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2039-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}
#
#        Given path ishPath
#        And request filtersSettings
#        When method PUT
#        Then status 200
#
#        * def processId = $
#
#        Given path ishPathControl + '/' + processId
#        When method GET
#        Then status 200
#
#        * match $ == {"status":"#ignore","message":null}
#
#        Given path ishPath + '/' +  processId
#        When method GET
#        Then status 200
#        And match $[*].ids contains [[101],[102],[1],[4]]

##       <---> No outcomes in period
#        * def filtersSettings = {"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2009-12-31","outcomesEnd":"2013-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}
#
#        Given path ishPath
#        And request filtersSettings
#        When method PUT
#        Then status 200
#
#        * def processId = $
#
#        Given path ishPathControl + '/' + processId
#        When method GET
#        Then status 200
#
#        * match $ == {"status":"#ignore","message":null}
#
#        Given path ishPath + '/' +  processId
#        When method GET
#        Then status 200
#        And match $ == []



#    Scenario: (-) Get outcomes/enrolments when 'flavour' is empty
#
#        * def filtersSettings = {"flavour":"","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}
#
#        Given path ishPath
#        And request filtersSettings
#        When method PUT
#        Then status 400
#        And match response.errorMessage == "Invalid 'flavour' value."
#
#
#
#    Scenario: (-) Get outcomes/enrolments when 'flavour' is nonexistend
#
#        * def filtersSettings = {"flavour":"123456","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2016-12-31","outcomesEnd":"2017-12-31","includeLinkedOutcomes":false,"fundingContracts":[]}
#
#        Given path ishPath
#        And request filtersSettings
#        When method PUT
#        Then status 400
#        And match response.errorMessage == "Invalid 'flavour' value."


