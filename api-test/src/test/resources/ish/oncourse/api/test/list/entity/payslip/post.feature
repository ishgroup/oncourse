@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/payslip'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create payslip by admin

        * def newPayslip =
        """
        {
        "payType":"Employee",
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[218],
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
        And match $ contains
        """
        {
        "payType":"Employee",
        "id":"#number",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[218],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":33.00}],
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create payslip by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def newPayslip =
        """
        {
        "payType":"Employee",
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes2",
        "privateNotes":"some private notes2",
        "tags":[218],
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
        And match $ contains
        """
        {
        "id":"#number",
        "payType":"Employee",
        "publicNotes":"some public notes2",
        "privateNotes":"some private notes2",
        "status":"New",
        "tutorId":5,
        "tutorFullName":"tutor2",
        "tags":[218],
        "paylines":[{"id":"#number","dateFor":"#ignore","description":"someDescription2","className":null,"type":null,"budgetedQuantity":null,"budgetedValue":null,"quantity":1.0000,"value":66.00}],
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        