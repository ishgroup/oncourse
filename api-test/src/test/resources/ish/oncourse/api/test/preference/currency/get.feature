@parallel=false
Feature: Main feature for all GET requests with path 'preference/currency'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/currency'
        



    Scenario: (+) Get currency by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"name":"AUSTRALIA","shortCurrencySymbol":"$","currencySymbol":"AUD"}



    Scenario: (+) Get currency by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}


#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"name":"AUSTRALIA","shortCurrencySymbol":"$","currencySymbol":"AUD"}