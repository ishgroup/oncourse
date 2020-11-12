@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathMarking = 'list/entity/payslip/marking'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing payslip with status New by admin

#       <----->  Add a new payslip for deleting and get id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes299",
        "privateNotes":"some private notes299",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription299","value":44.00,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Payslip with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing payslip with status Completed by admin

#       <----->  Add a new payslip for deleting and get id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes300",
        "privateNotes":"some private notes300",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription300","value":44.00,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

#       <---> Change status to Completed:
        * def newStatus = {"ids":['#(id)'], "status":"Completed"}

        Given path ishPathMarking
        And request newStatus
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[7].values[3] == "Completed"
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Payslip with id:" + id + " doesn't exist"



    Scenario: (+) Delete existing payslip by notadmin with access rights

#       <----->  Add a new payslip for deleting and get id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes400",
        "privateNotes":"some private notes400",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription101","value":44.00,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Payslip with id:" + id + " doesn't exist"



    Scenario: (-) Delete existing payslip by notadmin without access rights

#       <----->  Add a new payslip for deleting and get id:
        * def newPayslip =
        """
        {
        "tutorId":1,
        "tutorFullName":"tutor1, tutor1",
        "publicNotes":"some public notes500",
        "privateNotes":"some private notes500",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription102","value":44.00,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete tutor pay. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete payslip with status Approved

        Given path ishPath + '/1002'
        When method DELETE
        Then status 400
        And match response.errorMessage contains "Cannot delete tutor pay with"



    Scenario: (-) Delete payslip with status Paid/Exported

        Given path ishPath + '/1003'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Cannot delete tutor pay with Paid/Exported status."



    Scenario: (-) Delete NOT existing payslip

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Payslip with id:99999 doesn't exist"



    Scenario: (-) Delete payslip with NULL as ID

        Given path ishPath + '/null'
        When method DELETE
        Then status 404

