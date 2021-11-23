@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/payslip/marking'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathPayslip = 'list/entity/payslip'
        * def ishPath = 'list/entity/payslip/marking'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Change status from New to Completed by admin

#       <----->  Add a new entity to update and define its id:
        * def newPayslip =
        """
        {
        "payType":"Employee",
        "tutorId":5,
        "tutorFullName":"tutor2, tutor2",
        "publicNotes":"some public notes",
        "privateNotes":"some private notes",
        "tags":[],
        "paylines":[{"description":"someDescription","value":399,"quantity":1}]
        }
        """

        Given path ishPathPayslip
        And request newPayslip
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200

        * def id = get[0] response.rows[7].id
        * print "id = " + id
#       <--->

        * def newStatus = {"ids":['#(id)'], "status":"Completed"}

        Given path ishPath
        And request newStatus
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[7].values[4] == "Completed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathPayslip + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Change status from Approved to Paid/Exported by admin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newStatus = {"ids":[1002], "status":"Paid/Exported"}

        Given path ishPath
        And request newStatus
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[2].values[4] == "Paid/Exported"



    Scenario: (+) Change status from Completed to Approved by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newStatus = {"ids":[1001], "status":"Approved"}

        Given path ishPath
        And request newStatus
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[1].values[4] == "Approved"



    Scenario: (-) Change status from Completed to New

         * def newStatus = {"ids":[1004], "status":"New"}

         Given path ishPath
         And request newStatus
         When method POST
         Then status 204

         Given path ishPathList
         And param entity = 'Payslip'
         When method GET
         Then status 200
         And match $.rows[4].values[4] == "New"



    Scenario: (-) Change status from Approved to Completed

         * def newStatus = {"ids":[1005], "status":"Completed"}

         Given path ishPath
         And request newStatus
         When method POST
         Then status 204

         Given path ishPathList
         And param entity = 'Payslip'
         When method GET
         Then status 200
         And match $.rows[5].values[4] == "Completed"



    Scenario: (-) Change status from Paid/Exported to Approved

        * def newStatus = {"ids":[1006], "status":"Approved"}

        Given path ishPath
        And request newStatus
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[6].values[4] == "Paid/Exported"


