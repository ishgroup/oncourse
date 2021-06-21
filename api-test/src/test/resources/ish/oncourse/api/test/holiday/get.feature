@parallel=false
Feature: Main feature for all GET requests with path 'holiday'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'holiday'
        



    Scenario: (+) Get all holidays

#       <---> Prepare new holidays:
        * def someHolidayArray = [{"id":null,"description":"Description#1","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 1
        And match response[*].description contains 'Description#1'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'Description#1')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].description !contains 'Description#1'
