@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/outcome'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/outcome'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update status for several Outcomes by admin

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"status": "Satisfactorily completed (81)"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"status": "Not set"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update status for several Outcomes using all filters

        * def outcomeToUpdate =
              """
              {
              "ids":null,
              "diff":{"status":"Not set"},
              "search":"createdOn before now ",
              "filter":"((status == STATUS_ASSESSABLE_PASS) or (status == STATUS_NON_ASSESSABLE_COMPLETED))",
              "tagGroups":[{"tagIds":[236],"entity":"Enrolment","path":"enrolment"}]
              }
              """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.status == "Not set"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.status == "Not set"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.status == "Not set"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
                """
                {
                "ids":null,
                "diff":{"status":"Satisfactorily completed (81)"},
                "search":"createdOn before now ",
                "filter":"((status == STATUS_ASSESSABLE_PASS) or (status == STATUS_NON_ASSESSABLE_COMPLETED))",
                "tagGroups":[{"tagIds":[236],"entity":"Enrolment","path":"enrolment"}]
                }
                """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update startDate for several Outcomes by admin

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"startDate": "2020-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"startDate": "2016-11-01"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update endDate for several Outcomes by admin

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"endDate": "2030-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"endDate": "2025-10-05"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update status for several Outcomes by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"status": "Satisfactorily completed (81)"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.status == "Satisfactorily completed (81)"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"status": "Not set"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update startDate for several Outcomes by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"startDate": "2020-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.startDate == "2020-01-01"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"startDate": "2016-11-01"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (+) Update endDate for several Outcomes by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"endDate": "2030-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

        Given path ishPath + '/102'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

        Given path ishPath + '/103'
        When method GET
        Then status 200
        And match $.endDate == "2030-01-01"

#       <--->  Scenario have been finished. Now change entity back:
        * def outcomeToUpdate =
            """
            {
            "ids": [101,102,103],
            "diff": {"endDate": "2025-10-05"}
            }
            """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 204



    Scenario: (-) Update status for several Outcomes by notadmin without access rights

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

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"status": "Satisfactorily completed (81)"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit outcome. Please contact your administrator."



    Scenario: (-) Update startDate for several Outcomes by notadmin without access rights

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

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"startDate": "2020-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit outcome. Please contact your administrator."



    Scenario: (-) Update endDate for several Outcomes by notadmin without access rights

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

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"endDate": "2030-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit outcome. Please contact your administrator."



    Scenario: (-) Update several Outcomes when startDate > endDate

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"startDate": "2030-01-01"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Can not change start date for Outcome Id:101. Outcome start date should be defore outcome end date."



    Scenario: (-) Update several Outcomes to empty status

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"status": ""}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Status is empty or don't match a list of valid values"



    Scenario: (-) Update several Outcomes to empty startDate

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"startDate": null}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Attribute startDate has null value"



    Scenario: (-) Update several Outcomes to empty endDate

        * def outcomeToUpdate =
          """
          {
          "ids": [101,102,103],
          "diff": {"endDate": null}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Attribute endDate has null value"



    Scenario: (-) Update Status in several Outcomes with printed certificate

        * def outcomeToUpdate =
          """
          {
          "ids": [101,107],
          "diff": {"status": "Not set"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Can not change status for Outcome Id:107. Status cannot be changed for outcome used in printed certificate"



    Scenario: (-) Update not existing several Outcomes

        * def outcomeToUpdate =
          """
          {
          "ids": [101,99999],
          "diff": {"status": "Not set"}
          }
          """

        Given path ishPath
        And request outcomeToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."