@parallel=false
Feature: Main feature for all GET requests with path 'export/avetmiss8'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        * def ishPathOutcomes = 'export/avetmiss8/outcomes'
        


    Scenario: (+) Get avetmiss8 export result by admin

        * def avetmissExport = {"ids":[101,1,4],"defaultStatus":false,"settings":{"flavour":"NCVER (Standard AVETMISS)","fee":["Fee for service VET (non-funded)","Queensland","New South Wales","Victoria","Tasmania","Australian Capital Territory","Western Australia","South Australia","Northern Territory","No Australian state defined","Non VET"],"outcomesStart":"2017-01-01","outcomesEnd":"2018-05-21","includeLinkedOutcomes":false,"fundingContracts":[],"classIds":[]}}

        Given path ishPath
        And request avetmissExport
        When method PUT
        Then status 200

        * def processId = $

        Given path ishPath + '/' + processId
        When method GET
        Then status 200
        And match $ contains '.txt'