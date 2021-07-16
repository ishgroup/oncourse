@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/assessment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/assessment'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Assessment by admin

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code051",
        "name":"delete assessment 51",
        "tags":[{"id":195}],
        "active":true,
        "description":"some description 51",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code051"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Assessment by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newAssessment =
        """
        {
        "code":"code052",
        "name":"delete assessment 52",
        "tags":[{"id":195}],
        "active":true,
        "description":"some description 52",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code052"])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."
        


    Scenario: (-) Delete NOT existing Assessment

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."
