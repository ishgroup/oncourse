@parallel=false
Feature: Main feature for all POST requests with path 'preference/fundingcontract'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/fundingcontract'
        



    Scenario: (+) Add new inactive Funding Contract by admin

#       <--->  Add new entity and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-01"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-01')].id
        * def activeStatus = get[0] response[?(@.name == 'FC-01')].active

        * print "id = " + id
        * print "active = " + activeStatus

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 11
        And match response[*].name contains "FC-01"
        And assert activeStatus == false

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add new active Funding Contract by admin

#       <--->  Add new entity and get its id:
        * def newFundingContract = [{"active":true,"flavour":"WA RAPT","name":"FC-02"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-02')].id
        * def activeStatus = get[0] response[?(@.name == 'FC-02')].active

        * print "id = " + id
        * print "active = " + activeStatus

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 11
        And match response[*].name contains "FC-02"
        And assert activeStatus == true

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Change existing Funding Contract by admin

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-04"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-04')].id
        * print "id = " + id

#       <---> Update entity:
        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"AVETARS (ACT)","name":"FC-04_UPD"}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def updatedObject = get[0] response[?(@.name == 'FC-04_UPD')]
        * print "updatedObject = " + updatedObject

#       <---> Assertion:
        * assert updatedObject.name == "FC-04_UPD"
        * assert updatedObject.active == true
        * assert updatedObject.flavour == "AVETARS (ACT)"

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add new Funding Contract by notadmin

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}


#       <--->

        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-01"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 403



    Scenario: (-) Add new Funding Contract with empty Name

        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":""}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required"



    Scenario: (-) Add new Funding Contract with empty Flavour

        * def newFundingContract = [{"active":false,"flavour":"","name":"FC-03"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 400
        And match $.errorMessage == "Flavour is required"



    Scenario: (-) Add new Funding Contract with existing Name

        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"STARS (WA)"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 400
        And match $.errorMessage == "Name must be unique."



    Scenario: (-) Change Funding Contract to empty Name

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-05"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-05')].id
        * print "id = " + id

#       <---> Update entity:
        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"AVETARS (ACT)","name":""}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required"

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Change Funding Contract to empty Flavour

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-06"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-06')].id
        * print "id = " + id

#       <---> Update entity:
        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"","name":"FC-06"}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Flavour is required"

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Change Funding Contract to NOT existing Flavour

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-07"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-07')].id
        * print "id = " + id

#       <---> Update entity:
        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"notExistingValue","name":"FC-07"}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Flavour is required"

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Change Funding Contract to existing Name

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-08"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-08')].id
        * print "id = " + id

#       <---> Update entity:
        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"AVETARS (ACT)","name":"STARS (WA)"}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Name must be unique."

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Change existing Funding Contract by notadmin

#       <--->  Add new entity to update and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-09"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-09')].id
        * print "id = " + id

#       <--->  Login as notadmin and change entity:
        * configure headers = { Authorization:  'UserWithRightsDelete'}



        * def fundingContractToUpdate = [{"id":"#(id)","active":true,"flavour":"AVETARS (ACT)","name":"FC-09_UPD"}]

        Given path ishPath
        And request fundingContractToUpdate
        When method POST
        Then status 403

#       <---> Scenario have been finished. Now remove created entity from db:
        * configure headers = { Authorization: 'admin'}
        
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

