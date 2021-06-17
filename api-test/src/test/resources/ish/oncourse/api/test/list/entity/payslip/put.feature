@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Update payslip by admin

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "payType":"Employee",
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
        "payType":"Employee",
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
        "payType":"Employee",
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
        