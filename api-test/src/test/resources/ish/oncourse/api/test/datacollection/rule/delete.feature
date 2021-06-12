@parallel=false
Feature: Main feature for all DELETE requests with path '/datacollection/rule'
    

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/rule'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Delete existing datacollection rule
        Given path ishPath
        When method GET
        Then status 200
        * match response[?(@.name=='someName')] == '#[0]'

#       Prepare new data collection rule to delete it
#       <--->
        * def rule = {name: 'someName', enrolmentFormName: 'Non-accredited course enrolment form (Enrolment)', applicationFormName: 'Application form (Application)', waitingListFormName: 'Waiting list form (Waiting List)', productFormName: 'Default Field form (Product)', voucherFormName: 'Default Field form (Voucher)', membershipFormName: 'Default Field form (Membership)'}
        Given path ishPath
        And request rule
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * match response[?(@.name=='someName')] == '#[1]'
        * def rules = get response[?(@.name=='someName')]

        Given path ishPath + '/' + rules[0].id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * match response[?(@.name=='someName')] == '#[0]'
        
        
    Scenario: (-) Delete datacollection rules with relation
        
        Given path ishPath + '/102'
        When method DELETE
        Then status 400
        And match $.errorMessage contains "The data collection form rule not be deleted, used for courses:"

        
    Scenario: (-) Delete not existing datacollection rule
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection rule 100000 is not exist"
        
    
    
    Scenario: (-) Delete datacollection rule with null ID
        Given path ishPath + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection rule id 'null' is incorrect. It must contain of only numbers"