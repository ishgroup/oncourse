@ignore
@parallel=false
Feature: Re-usable feature to get Document without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        * def userWithPermission = user

#       <--->  Login as notadmin
        * configure headers = { Authorization:  '#(user)'}

        
#       <--->

        Given path ishPath + "/200"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get documents. Please contact your administrator"

