@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/payslip'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/payslip'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all payslips by admin

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004","1005","1006"]



    Scenario: (+) Get list of all payslips by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004","1005","1006"]



    Scenario: (+) Get payslip by admin

        Given path ishPath + "/1000"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "payType":"Employee",
        "id":1000,
        "publicNotes":null,
        "privateNotes":null,
        "status":"New",
        "tutorId":1,
        "tutorFullName":"tutor1",
        "tags":[],
        "paylines":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """
        And match $.paylines contains only
        """
            [
            {"id":1006,"dateFor":"2017-05-01","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1009,"dateFor":"2017-05-02","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1001,"dateFor":"2017-05-03","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1008,"dateFor":"2017-05-04","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1010,"dateFor":"2017-05-05","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1004,"dateFor":"2017-05-06","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1002,"dateFor":"2017-05-07","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1007,"dateFor":"2017-05-08","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1003,"dateFor":"2017-05-09","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00},
            {"id":1005,"dateFor":"2017-05-10","description":"Wage for tutor1","className":"course1-1 Course1","type":"Per timetabled hour","budgetedQuantity":1.0000,"budgetedValue":10.00,"quantity":1.0000,"value":10.00}
            ]
        """


    Scenario: (+) Get payslip by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + "/1000"
        When method GET
        Then status 200


    Scenario: (-) Get list of all payslips by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'Payslip'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get payslip by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + "/1000"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get tutor pay. Please contact your administrator"



    Scenario: (-) Get not existing payslip

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Payslip with id:9999 doesn't exist"

