@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/application'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/application'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Application by admin

#       <----->  Add a new entity for deleting and get id:
        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":85.00,
        "enrolBy":"2031-04-04",
        "reason":"Some reason 10",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 10"])].id
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



    Scenario: (+) Delete existing Application by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":85.00,
        "enrolBy":"2031-04-04",
        "reason":"Some reason 11",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 11"])].id
        * print "id = " + id

        Given path '/logout'
        And request {}
        When method PUT
        
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



    Scenario: (-) Delete existing Application by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":85.00,
        "enrolBy":"2031-04-04",
        "reason":"Some reason 12",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 12"])].id
        * print "id = " + id

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete application. Please contact your administrator"

        Given path '/logout'
        And request {}
        When method PUT
        
#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing Application with not New status

#       <----->  Add a new entity for deleting and get id:
        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":82.00,
        "enrolBy":"2031-04-04",
        "reason":"Some reason 15",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 15"])].id
        * print "id = " + id

#       <--->  Delete Application with status Offered:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with not NEW status"

#       <--->  Delete Application with status Rejected:
        * def applicationToUpdate = {"id":"#(id)","contactId":3,"courseId":4,"status":"Rejected","source":"office","feeOverride":82.00,"enrolBy":"2031-04-04","reason":"Some reason 15","documents":[],"tags":[],"customFields":{}}

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with not NEW status"

#       <--->  Delete Application with status Withdrawn:
        * def applicationToUpdate = {"id":"#(id)","contactId":3,"courseId":4,"status":"Withdrawn","source":"office","feeOverride":82.00,"enrolBy":"2031-04-04","reason":"Some reason 15","documents":[],"tags":[],"customFields":{}}

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with not NEW status"

#       <--->  Delete Application with status In progress:
        * def applicationToUpdate = {"id":"#(id)","contactId":3,"courseId":4,"status":"In progress","source":"office","feeOverride":82.00,"enrolBy":"2031-04-04","reason":"Some reason 15","documents":[],"tags":[],"customFields":{}}

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with not NEW status"

#       <--->  Delete Application with status Accepted:
        * def applicationToUpdate = {"id":"#(id)","contactId":3,"courseId":4,"status":"Accepted","source":"office","feeOverride":82.00,"enrolBy":"2031-04-04","reason":"Some reason 15","documents":[],"tags":[],"customFields":{}}

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with not NEW status"



    Scenario: (-) Delete Application with attached document

#       <----->  Add a new entity for deleting and get id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-01-01",
        "reason":"Some reason 100",
        "documents":[{"id":200}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 100"])].id
        * print "id = " + id
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete application with attached documents. Check removed documents."

#       <---->  Scenario have been finished. Now remove attached document and then delete created entity:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-01-01",
        "reason":"Some reason 100",
        "documents":[],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing Application

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (-) Delete Application with NULL as ID

        Given path ishPath + '/null'
        When method DELETE
        Then status 404

