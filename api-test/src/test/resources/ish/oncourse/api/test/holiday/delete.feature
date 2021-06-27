@parallel=false
Feature: Main feature for all DELETE requests with path 'holiday'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'holiday'
        



    Scenario: (+) Delete existing (not system) holiday

#       <---> Prepare new holiday to delete it:
        * def someHolidayArray = [{"id":null,"description":"Del#1","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.description == 'Del#1')].id
        * print "id = " + id
#       <--->

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 0
        And match response[*].description !contains 'Del#1'



    Scenario: (-) Delete not existing holiday
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Holiday is not exist."

