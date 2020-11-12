@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create payslip by admin

        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription","value":33,"quantity":1}]
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

        Given path ishPath + "/" + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[{"id":218,"name":"ps1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":33.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create payslip by notadmin with access rights

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

        * def newPayslip =
        """
        {
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes2",
        "privateNotes":"some private notes2",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription2","value":66,"quantity":1}]
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

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "publicNotes":"some public notes2",
        "privateNotes":"some private notes2",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[{"id":218,"name":"ps1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription2","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":66.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new payslip by notadmin without access rights

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

        * def newPayslip =
        """
        {
        "tutorId":1,
        "tutorFullName":"tutor1, tutor1",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription10","value":77,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create tutor pay. Please contact your administrator"



    Scenario: (-) Create new payslip with empty Tutor

        * def newPayslip =
        """
        {
        "tutorId":null,
        "tutorFullName":null,
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription10","value":77,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 400
        And match $.errorMessage == "Tutor is required."



    Scenario: (-) Create new payslip with not existing Tutor

        * def newPayslip =
        """
        {
        "tutorId":100,
        "tutorFullName":"tutor100, tutor100",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription10","value":77,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 400
        And match $.errorMessage == "Contact with id:100 doesn't exist"



    Scenario: (-) Create new payslip with empty Description

        * def newPayslip =
        """
        {
        "tutorId":1,
        "tutorFullName":"tutor1, tutor1",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"","value":77,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 400
        And match $.errorMessage == "Description is required."



    Scenario: (-) Create new payslip with empty Amount

        * def newPayslip =
        """
        {
        "tutorId":1,
        "tutorFullName":"tutor1, tutor1",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[{"id":218}],
        "paylines":[{"description":"someDescription10","value":null,"quantity":1}]
        }
        """

        Given path ishPath
        And request newPayslip
        When method POST
        Then status 400
        And match $.errorMessage == "Amount is required."
