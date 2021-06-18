@parallel=false
Feature: Main feature for all DELETE requests with path 'filter'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'filter'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Delete existing filter for 'Qualification' by admin

#       <--->  Add new filter and get its id:
        Given path ishPath
        And param entity = 'Qualification'
        And request {"name":"filter222","entity":"Qualification","expression":"( isOffered == true or isOffered == false ) and ~ \"Aboriginal\" ","showForCurrentOnly":true}
        When method POST
        Then status 204

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'filter222')].id
#       <--->

        Given path ishPath + '/' + id
        And param entity = 'Qualification'
        When method DELETE
        Then status 204

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $[*].name !contains "filter222"



     Scenario: (+) Delete existing filter for 'Account' by notadmin with access rights

 #       <--->  Add new filter and get its id:
         Given path ishPath
         And param entity = 'Account'
         And request {"name":"filter223","entity":"Account","expression":"id == 1","showForCurrentOnly":false}
         When method POST
         Then status 204

         Given path ishPath
         And param entity = 'Account'
         When method GET
         Then status 200

         * def id = get[0] response[?(@.name == 'filter223')].id
         
         Given path '/logout'
         And request {}
         When method PUT
         
 #       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

 #       <--->

         Given path ishPath + '/' + id
         And param entity = 'Account'
         When method DELETE
         Then status 204

         Given path ishPath
         And param entity = 'Account'
         When method GET
         Then status 200
         And match $[*].name !contains "filter223"



    Scenario: (-) Delete not visible filter by notadmin with access rights

#       <--->  Add new filter and get its id:
        Given path ishPath
        And param entity = 'Account'
        And request {"name":"filter225","entity":"Account","expression":"id == 1","showForCurrentOnly":true}
        When method POST
        Then status 204

        Given path ishPath
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'filter225')].id

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/' + id
        And param entity = 'Account'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Filter with id '" + id + "' does not exist."

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        And param entity = 'Account'
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing filter for 'Account' by notadmin without access rights

#       <--->  Add new filter and get its id:
        Given path ishPath
        And param entity = 'Account'
        And request {"name":"filter224","entity":"Account","expression":"id == 1","showForCurrentOnly":true}
        When method POST
        Then status 204

        Given path ishPath
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'filter224')].id
        
        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/' + id
        And param entity = 'Account'
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete custom filters for this entity. Please contact your administrator"

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        And param entity = 'Account'
        When method DELETE
        Then status 204

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $[*].name !contains "filter224"



    Scenario: (-) Delete not existing filter

        Given path ishPath + '/9999'
        And param entity = 'Account'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Filter with id '9999' does not exist."




