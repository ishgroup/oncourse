@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathMarking = 'list/entity/payslip/marking'
        


        
    Scenario: (+) Delete existing payslip with status New by admin

#       <----->  Add a new payslip for deleting and get id:
        * def newPayslip =
        """
        {
        "payType":"Employee",
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
        "payType":"Employee",
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
        