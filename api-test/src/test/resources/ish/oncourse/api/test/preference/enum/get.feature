    @parallel=false
Feature: Main feature for all GET requests with path 'preference/enum'
    
    
    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def enumPath = 'preference/enum'
        
        
        
    Scenario: (+) Get all choices for 'DeliveryMode' enum
        Given path enumPath + '/DeliveryMode'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 18
       
       
    Scenario: (+) Get all choices for 'ClassFundingSource' enum
        Given path enumPath + '/ClassFundingSource'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 8
        
        
    Scenario: (+) Get all choices for 'ExportJurisdiction' enum
        Given path enumPath + '/ExportJurisdiction'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 13
        
        
    Scenario: (+) Get all choices for 'TrainingOrg_Types' enum
        Given path enumPath + '/TrainingOrg_Types'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        
        
    Scenario: (+) Get all choices for 'AddressStates' enum
        Given path enumPath + '/AddressStates'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        
        
    Scenario: (+) Get all choices for 'MaintenanceTimes' enum
        Given path enumPath + '/MaintenanceTimes'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 24
        
        
    Scenario: (-) Get all choices for enum using lowerCase
        Given path enumPath + '/trainingorg_types'
        When method GET
        Then status 400
        And match response.errorMessage == "Wrong enumeration type: trainingorg_types"