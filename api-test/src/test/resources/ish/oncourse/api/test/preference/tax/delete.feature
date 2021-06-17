@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/tax'
    
    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/tax'
        


    Scenario: (+) Delete existing (not system) taxType
#       Prepare new taxType to delete it
#       <--->
        * def someTaxTypeArray = [{code: 'someName', rate: '0.01', gst: true, payableAccountId: 12, receivableAccountId: 4, description: 'someDescription'}]
        
        Given path ishPath
        And request someTaxTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And def taxType = get[0] response[?(@.code == 'someName')]
        
        Given path ishPath + '/' + taxType.id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].code !contains 'someName'
        
        
    Scenario: (-) Delete system ('GST', 'N', '*') taxType
#       Try to delete 'GST' (id:1) system taxType
        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System tax type can not be deleted"
        
#       Try to delete 'N' (id:2) system taxType
        Given path ishPath + '/2'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System tax type can not be deleted"

#       Try to delete '*' (id:3) system taxType
        Given path ishPath + '/3'
        When method DELETE
        Then status 400
        And match response.errorMessage == "System tax type can not be deleted"


    Scenario: (-) Delete not existing taxType
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Tax is not exist"

