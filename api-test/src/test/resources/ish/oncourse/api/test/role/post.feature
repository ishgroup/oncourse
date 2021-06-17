@parallel=false
Feature: Main feature for all POST requests with path 'role'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'role'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Create new user Role

        * def someRole = { "name":"Test Role","rights":{"Certificate":"Create","Waiting list":"Print","Enrolment":"Create","Site":"Create","Room":"Create","Course":"Create","Class":"Create","Enrolment outcomes":"Edit","Budget":"View","Session":"Delete","Discount":"Create","Contact":"Delete","Users":"Delete","Purchase":"Delete","Invoice":"Create","Payment in":"Delete","Payment out":"Delete","Banking":"true","Reconciliation":"true","Account":"View","Transaction":"View","Report":"Create","Documents":"Create","Tag":"Create","Web page":"Create","Financial preferences":"View","General preferences":"Edit","Class duplication/rollover":"true","Class cancellation":"true","Exporting to XML":"true","Creating certificate from class":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","SMS over 50 contacts":"true","Email over 50 contacts":"true","Contact merging":"true","Enrolment cancellation and transferring":"true"} }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 11
        And match response[*].name contains "Test Role"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Test Role')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        And match response[*].name !contains "Test Role"



    Scenario: (+) Create new user Role only with name

        * def someRole = { "name":"Test Role" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 11
        And match response[*].name contains "Test Role"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Test Role')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        And match response[*].name !contains "Test Role"



    Scenario: (+) Create new user Role with 45 symbols in name

        * def someRole = { "name":"roleOf45SymbolsInName_12345678901234567890123","rights":{"Certificate":"Create","Waiting list":"Print","Enrolment":"Create","Site":"Create","Room":"Create","Course":"Create","Class":"Create","Enrolment outcomes":"Edit","Budget":"View","Session":"Delete","Discount":"Create","Contact":"Delete","Users":"Delete","Purchase":"Delete","Invoice":"Create","Payment in":"Delete","Payment out":"Delete","Banking":"true","Reconciliation":"true","Account":"View","Transaction":"View","Report":"Create","Documents":"Create","Tag":"Create","Web page":"Create","Financial preferences":"View","General preferences":"Edit","Class duplication/rollover":"true","Class cancellation":"true","Exporting to XML":"true","Creating certificate from class":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","SMS over 50 contacts":"true","Email over 50 contacts":"true","Contact merging":"true","Enrolment cancellation and transferring":"true"} }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 11
        And match response[*].name contains "roleOf45SymbolsInName_12345678901234567890123"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'roleOf45SymbolsInName_12345678901234567890123')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        And match response[*].name !contains "roleOf45SymbolsInName_12345678901234567890123"



    Scenario: (-) Create new user Role by notadmin

        * configure headers = { Authorization:  'UserWithRightsDelete'}
        

        * def someRole = { "name":"Test Role" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 403
        And match $.errorMessage == "Only users with admin rights can do it. Please contact your administrator"



    Scenario: (-) Create user role with not unique name

        * def someRole = { "name":"Test Role" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204

        Given path ishPath
        And request someRole
        When method POST
        Then status 400
        And match response.errorMessage == "User role name should be unique"

#       Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Test Role')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Create new invalid (empty) Role

        * def someRole = {}

        Given path ishPath
        And request someRole
        When method POST
        Then status 400
        And match response.errorMessage == "User role name should be specified"



    Scenario: (-) Create new Role with empty name

        * def someRole = { "name":"" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 400
        And match response.errorMessage == "User role name should be specified"



    Scenario: (-) Create new user Role with > 45 symbols in name

        * def someRole = { "name":"roleOf46SymbolsInName_123456789012345678901234","rights":{"Certificate":"Create","Waiting list":"Print","Enrolment":"Create","Site":"Create","Room":"Create","Course":"Create","Class":"Create","Enrolment outcomes":"Edit","Budget":"View","Session":"Delete","Discount":"Create","Contact":"Delete","Users":"Delete","Purchase":"Delete","Invoice":"Create","Payment in":"Delete","Payment out":"Delete","Banking":"true","Reconciliation":"true","Account":"View","Transaction":"View","Report":"Create","Documents":"Create","Tag":"Create","Web page":"Create","Financial preferences":"View","General preferences":"Edit","Class duplication/rollover":"true","Class cancellation":"true","Exporting to XML":"true","Creating certificate from class":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","SMS over 50 contacts":"true","Email over 50 contacts":"true","Contact merging":"true","Enrolment cancellation and transferring":"true"} }

        Given path ishPath
        And request someRole
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 45."



    Scenario: (+) Update existing Role

#       <--->  Prepare new Role to update it
        * def someRole = { "name":"Test Role","rights":{"Waiting list":"Print","Enrolment":"Print","Site":"Print","Room":"Print","Course":"Print","Class":"Edit","Budget":"View","Session":"Delete","Discount":"Print","Contact":"Create","Users":"View","Purchase":"Delete","Invoice":"Delete","Payment in":"Delete","Payment out":"Delete","Banking":"true","Reconciliation":"true","Account":"Delete","Transaction":"Delete","Report":"Create","Financial preferences":"View","General preferences":"Edit","Class cancellation":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","Enrolment cancellation and transferring":"true"} }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'Test Role')].id
        * def updatedRole = { "id":"#(id)", "name":"Updated Role","rights":{"Waiting list":"Print","Enrolment":"Print","Site":"Print","Room":"Print","Course":"Print","Class":"Edit","Budget":"View","Session":"Delete","Discount":"Print","Contact":"Edit","Users":"Edit","Purchase":"Delete","Invoice":"Delete","Payment in":"Delete","Payment out":"Delete","Banking":"false","Reconciliation":"true","Account":"Delete","Transaction":"Delete","Report":"Create","Financial preferences":"View","General preferences":"Edit","Class cancellation":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","Enrolment cancellation and transferring":"true"} }

        Given path ishPath
        And request updatedRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "Updated Role"

        * def list = get response[?(@.name == 'Updated Role')]
        * match list[*].rights.Users contains 'Edit'
        * match list[*].rights.Contact contains 'Edit'
        * match each list[*].rights[*].Banking == false

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update user role name to existing one

#       <--->  Prepare new Role to update it:
        * def someRole1 = { "name":"Test Role1","rights":{"Certificate":"View","Waiting list":"Create","Enrolment":"Create","Site":"Print","Room":"Print","Course":"Print","Class":"Print","Discount":"Print","Contact":"Create","Purchase":"Print","Invoice":"Print","Payment in":"Print","Payment out":"Print","SMS up to 50 contacts":"true","Email up to 50 contacts":"true"} }
        * def someRole2 = { "name":"Test Role2","rights":{"Qualification reference data":"Edit","Certificate":"Create","Waiting list":"Delete","Enrolment":"Delete","Site":"Delete","Room":"Delete","Course":"Delete","VET Course details":"true","Class":"Delete","Enrolment outcomes":"Edit","Session":"Delete","Discount":"Delete","Contact":"Create","Users":"View","Purchase":"Print","Invoice":"Edit","Payment in":"Print","Payment out":"Print","Account":"View","Transaction":"View","Report":"Create","Documents":"Delete","Tag":"Delete","Web page":"Delete","Financial preferences":"View","General preferences":"Edit","Class duplication/rollover":"true","Class cancellation":"true","Exporting to XML":"true","Creating certificate from class":"true","SMS up to 50 contacts":"true","Email up to 50 contacts":"true","SMS over 50 contacts":"true","Email over 50 contacts":"true","Contact merging":"true","Enrolment cancellation and transferring":"true","Export AVETMISS":"true","Data import":"true","Export DET AVETMISS":"View"} }

        Given path ishPath
        And request someRole1
        When method POST
        Then status 204

        Given path ishPath
        And request someRole2
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Test Role1')].id
        * def updatedRole = { "id":"#(id1)", "name":"Test Role2" }

        Given path ishPath
        And request updatedRole
        When method POST
        Then status 400
        And match response.errorMessage == "User role name should be unique"

#       Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'Test Role1')].id
        * def id2 = get[0] response[?(@.name == 'Test Role2')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}



    Scenario: (-) Update Role name to empty value

#       <--->  Prepare new Role to update it:
        * def someRole = { "name":"anyUserRole" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'anyUserRole')].id
        * def updatedRole = { "id":"#(id)", "name":"" }

        Given path ishPath
        And request updatedRole
        When method POST
        Then status 400
        And match response.errorMessage == "User role name should be specified"

#       Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update Role name to > 45 symbols

#       <--->  Prepare new Role to update it
        * def someRole = { "name":"tooLongName" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'tooLongName')].id
        * def updatedRole = { "id":"#(id)", "name":"roleOf46SymbolsInName_123456789012345678901234" }

        Given path ishPath
        And request updatedRole
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 45."

#       Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update not existing userRole

        * def nonExistingRoleToUpdate = {id: '99999',"name":"notExistingRole"}

        Given path ishPath
        And request nonExistingRoleToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "User Role is not exist"