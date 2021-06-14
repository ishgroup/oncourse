@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/qualification'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/qualification'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        

        

    Scenario: (+) Delete existing custom Qualification

#       <----->  Add a new custom qualification for deleting:
        * def newQualification = {"type":"Qualification","qualLevel":"someLevel","title":"someTitle","nationalCode":"CODE01","isOffered":false}

        Given path ishPath
        And request newQualification
        When method POST
        Then status 204
#       <----->

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE01","someTitle","someLevel",null,"false"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["CODE01"]



    Scenario: (+) Delete existing custom Qualification by notadmin with access rights

#       <--->  Create entity for deleting:
        * def newQualification = {"type":"Qualification","qualLevel":"someLevel","title":"someTitle","nationalCode":"CODE01","isOffered":false}

        Given path ishPath
        And request newQualification
        When method POST
        Then status 204

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE01","someTitle","someLevel",null,"false"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["CODE01"]

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"



    Scenario: (-) Delete existing custom Qualification by notadmin without access rights

#       <--->  Create entity for deleting:
        * def newQualification = {"type":"Qualification","qualLevel":"someLevel","title":"someTitle","nationalCode":"CODE01","isOffered":false}

        Given path ishPath
        And request newQualification
        When method POST
        Then status 204

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE01","someTitle","someLevel",null,"false"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete qualification. Please contact your administrator"

#       <---->  Scenario have been finished. Now change back permissions and delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing (system) Qualification by admin

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Only custom qualifications can be deleted."


    Scenario: (-) Delete NOT existing custom Qualification

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Entity with id = '99999' doesn't exist"
