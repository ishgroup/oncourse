@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/module'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/module'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        


        
    Scenario: (+) Delete existing custom Module

#       <----->  Add a new custom module for deleting:
        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 204
#       <----->

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["2","1","true"])].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["2"]



    Scenario: (+) Delete existing custom Module by notadmin with rights

#       <--->  Create entity for deleting:

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["2","1","true"])].id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["2"]



    Scenario: (-) Delete existing custom Module by notadmin without rights

#       <--->  Create entity for deleting:
        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["2","1","true"])].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete module. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Delete existing (system) module by admin

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Only custom modules can be deleted."


    Scenario: (-) Delete NOT existing custom Module

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Entity with id = '99999' doesn't exist"
