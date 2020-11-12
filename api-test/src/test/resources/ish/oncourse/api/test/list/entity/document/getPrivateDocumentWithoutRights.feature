@ignore
@parallel=false
Feature: Re-usable feature to get Document without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        * def userWithPermission = user

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: '#(user)', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + "/200"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get documents. Please contact your administrator"

