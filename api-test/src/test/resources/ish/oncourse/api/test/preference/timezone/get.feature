@parallel=false
Feature: Main feature for all GET requests with path 'preference/timezone'

    Background:
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'

        * def ishPathLogin = 'login'
        * def ishPath = 'preference/timezone'


    Scenario: (+) Get all timezones by admin

       Given path ishPath
       When method GET
       Then status 200
       And match response contains "Australia/Brisbane"
       And match response contains "Australia/Perth"
       And match response contains "Australia/Sydney"



    Scenario: (+) Get all timezones by notadmin

        * configure headers = { Authorization:  'UserWithRightsHide'}

       Given path ishPath
       When method GET
       Then status 200
       And match response contains "Australia/Brisbane"
       And match response contains "Australia/Perth"
       And match response contains "Australia/Sydney"


