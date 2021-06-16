@parallel=false
Feature: Main feature for all GET requests with path 'role'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'role'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get all user roles by admin

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        And match response[*].name contains
        """
        [
        "Administration Manager",
        "Enrolment Officer",
        "Course Manager",
        "Financial Manager",
        "RoleWithRightsDelete",
        "RoleWithRightsCreate",
        "RoleWithRightsEdit",
        "RoleWithRightsPrint",
        "RoleWithRightsView",
        "RoleWithRightsHide"
        ]
        """


    Scenario: (-) Get all user roles by notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath
        When method GET
        Then status 403


