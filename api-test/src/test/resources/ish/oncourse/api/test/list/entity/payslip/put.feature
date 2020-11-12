@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update payslip by admin

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes50",
        "privateNotes":"some private notes50",
        "tags":[],
        "paylines":[{"description":"someDescription","value":99.00,"quantity":1}]
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
#       <--->

        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes50_upd",
        "privateNotes":"some private notes50_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription_upd","value":120.00,"quantity":1}]
        }
        """

        Given path ishPath + '/' + id
        And request payslipToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "publicNotes":"some public notes50_upd",
        "privateNotes":"some private notes50_upd",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[{"id":218,"name":"ps1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription_upd","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":120.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update payslip by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes51",
        "privateNotes":"some private notes51",
        "tags":[],
        "paylines":[{"description":"someDescription","value":199.00,"quantity":1}]
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
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes51_upd",
        "privateNotes":"some private notes51_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription_upd","value":220.00,"quantity":1}]
        }
        """

        Given path ishPath + '/' + id
        And request payslipToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "publicNotes":"some public notes51_upd",
        "privateNotes":"some private notes51_upd",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[{"id":218,"name":"ps1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription_upd","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":220.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update payslip by notadmin without rights

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55",
        "privateNotes":"some private notes55",
        "tags":[],
        "paylines":[{"description":"someDescription","value":90.00,"quantity":1}]
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
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55_upd",
        "privateNotes":"some private notes55_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription_upd","value":130.00,"quantity":1}]
        }
        """

        Given path ishPath + '/' + id
        And request payslipToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update tutor pay. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update payslip required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55",
        "privateNotes":"some private notes55",
        "tags":[],
        "paylines":[{"description":"someDescription","value":90.00,"quantity":1}]
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

#       <--->  Update payslip to empty Description:
        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55_upd",
        "privateNotes":"some private notes55_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"","value":130.00,"quantity":1}]
        }
        """

        Given path ishPath + '/' + id
        And request payslipToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Description is required."

#       <--->  Update payslip to empty Value:
        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55_upd",
        "privateNotes":"some private notes55_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription_upd","value":null,"quantity":1}]
        }
        """

        Given path ishPath + '/' + id
        And request payslipToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Amount is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update tutor to another person

        * def payslipToUpdate =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes55_upd",
        "privateNotes":"some private notes55_upd",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription_upd","value":130.00,"quantity":1}]
        }
        """

        Given path ishPath + '/1000'
        And request payslipToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Cannot change tutor for tutor pay."



