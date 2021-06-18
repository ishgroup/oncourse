
@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/paymentIn'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentIn'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update Payment In by admin

        Given path ishPath + '/1'
        And request {"dateBanked":"2029-08-27","administrationCenterId":200}
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $.dateBanked == "2029-08-27"

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath + '/1'
        And request {"dateBanked": null,"administrationCenterId":200}
        When method PUT
        Then status 204



    Scenario: (+) Update Payment In by notadmin with access rights Hide

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/1'
        And request {"dateBanked":"2029-08-27","administrationCenterId":200}
        When method PUT
        Then status 204

#       <---> Assertion under admin:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $.dateBanked == "2029-08-27"

#       <--->  Scenario have been finished. Now update entity to default:
        Given path ishPath + '/1'
        And request {"dateBanked": null,"administrationCenterId":200}
        When method PUT
        Then status 204



    Scenario: (-) Update not existing Payment In

        Given path ishPath + '/99999'
        And request {"dateBanked":"2029-08-27","administrationCenterId":200}
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."


