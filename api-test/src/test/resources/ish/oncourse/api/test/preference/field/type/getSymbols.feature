@ignore
@parallel=false
Feature: re-usable feature to get not allowed symbols and check it returns 'status 400' and error message

    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'

    Scenario:
#        * def string = {symbol: '#(symbol)'}

        * def someFieldType = [{"name":"fieldType#2","defaultValue":"someValue","fieldKey": '#(symbol)',"mandatory":false,"sortOrder":0,"entityType":"Contact"}]

        Given path 'preference/field/type'
        And request someFieldType
        When method POST
        Then status 400
        And match response.errorMessage == "The custom field key can contains alphanumeric symbols only"
