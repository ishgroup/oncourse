@parallel=false
Feature: Main feature for all POST requests with path 'holiday'

    Background: Authorize first
        * callonce read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'holiday'
        



    Scenario: (+) Create new valid Holidays

        * def someHolidayArray = [{"id":null,"description":"Description#2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == [{"id":"#ignore","description":"Description#2","startDate":"2020-02-12","endDate":"2025-02-12","startDateTime":null,"endDateTime":null,"repeat":"day","repeatEnd":"onDate","repeatEndAfter":null,"repeatOn":"2024-02-12","created":"#ignore","modified":"#ignore"}]

#       <---> Scenario have been finished. Now find and remove created object from DB
        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'Description#2')].id
        * print "id = " + id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 0
        And match response[*].description !contains 'Description#2'



    Scenario: (+) Create a bunch (different 'allDay', 'repeatEndAfter', 'onDate') of valid Holidays

        * def someHolidayArray =
        """
        [
        {"id":null,"description":"Description#3-1","startDateTime":null,"endDateTime":null,"repeatEnd":"after","repeat":"day","repeatEndAfter":"5","startDate":"2020-02-12","endDate":"2020-02-12"},
        {"id":null,"description":"Description#3-2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"month","repeatEndAfter":0,"repeatOn":"2022-02-12","startDate":"2020-02-12","endDate":"2022-02-12"},
        {"id":null,"description":"Description#3-3","startDateTime":null,"endDateTime":null,"repeatEnd":"never","repeat":"week","repeatEndAfter":0,"startDate":"2020-02-12","endDate":"2020-02-12"},
        {"id":null,"description":"Description#3-4","startDateTime":"2020-02-12T11:41:56.719Z","endDateTime":"2020-02-12T12:41:56.719Z","repeatEnd":"after","repeat":"hour","repeatEndAfter":"8"},
        {"id":null,"description":"Description#3-5","startDateTime":"2020-02-12T11:41:56.112Z","endDateTime":"2021-02-12T12:41:00.000Z","repeatEnd":"never","repeat":"month","repeatEndAfter":0},
        {"id":null,"description":"Description#3-6","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2022-02-12T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":"2022-02-12"}
        ]
        """

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response contains
        """
        {
        "repeatEnd":"after",
        "startDateTime":null,
        "endDate":"2020-02-12",
        "created":"#ignore",
        "repeat":"day",
        "description":"Description#3-1",
        "repeatEndAfter":5,
        "modified":"#ignore",
        "id":"#ignore",
        "endDateTime":null,
        "repeatOn":null,
        "startDate":"2020-02-12"
        }
        """

 #      <---> Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.description == 'Description#3-1')].id
        * def id2 = get[0] response[?(@.description == 'Description#3-2')].id
        * def id3 = get[0] response[?(@.description == 'Description#3-3')].id
        * def id4 = get[0] response[?(@.description == 'Description#3-4')].id
        * def id5 = get[0] response[?(@.description == 'Description#3-5')].id
        * def id6 = get[0] response[?(@.description == 'Description#3-6')].id

        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id3)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id4)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id5)'}
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id6)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 0
        And match response[*].description !contains ['Description#3-1', 'Description#3-2', 'Description#3-3', 'Description#3-4', 'Description#3-5', 'Description#3-6']



    Scenario: (-) Create invalid (empty) Holidays

        * def someHolidayArray = [{}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Time or date interval of holiday required."



    Scenario: (-) Create invalid ('Start' field is empty) Holidays

        * def someHolidayArray = [{"id":null,"description":"q#2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":null,"endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Date interval should be provided by start and end dates."



    Scenario: (-) Create invalid ('End' field is empty) Holidays

        * def someHolidayArray = [{"id":null,"description":"w#2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":null}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Date interval should be provided by start and end dates."



    Scenario: (-) Create invalid ('On Date' field is empty) Holidays

        * def someHolidayArray = [{"id":null,"description":"e#3-2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"month","repeatEndAfter":0,"repeatOn":null,"startDate":"2020-02-12","endDate":"2022-02-12"}]

         Given path ishPath
         And request someHolidayArray
         When method POST
         Then status 400
         And match response.errorMessage == "On date must be specified."



    Scenario: (-) Create new invalid ('End' < 'Start') Holidays

        * def someHolidayArray = [{"id":null,"description":"r#3-2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"month","repeatEndAfter":0,"repeatOn":"2022-02-12","startDate":"2020-02-12","endDate":"2020-02-11"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Holiday end date must be after start date."



    Scenario: (-) Create new invalid ('repeatEndAfter' is '0') Holidays

        * def someHolidayArray = [{"id":null,"description":"t","startDateTime":"2020-02-12T12:03:37.378Z","endDateTime":"2020-02-12T13:03:37.378Z","repeatEnd":"after","repeat":"week","repeatEndAfter":"0"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Times count must be specified as positive number."



    Scenario: (-) Create new invalid ('repeatEndAfter' is negative '-1') Holidays

        * def someHolidayArray = [{"id":null,"description":"y","startDateTime":"2020-02-12T11:55:54.183Z","endDateTime":"2020-02-12T12:55:54.183Z","repeatEnd":"after","repeat":"month","repeatEndAfter":-1}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Times count must be specified as positive number."



    Scenario: (-) Create a bunch of Holidays some of them are INVALID because without 'startDate'

        * def someHolidayArray =
        """
        [
        {"id":null,"description":"u#3-1","startDateTime":null,"endDateTime":null,"repeatEnd":"after","repeat":"day","repeatEndAfter":"5","startDate":"2020-02-12","endDate":"2020-02-12"},
        {"id":null,"description":"i#3-2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"month","repeatEndAfter":0,"repeatOn":"2022-02-12","startDate":null,"endDate":"2022-02-12"}
        ]
        """

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 400
        And match response.errorMessage == "Date interval should be provided by start and end dates."



    Scenario: (+) Update existing Holidays

#       <---> Prepare new Holiday to update it:
        * def someHolidayArray = [{"id":null,"description":"UPD#1","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'UPD#1')].id
        * print "id = " + id
#       <--->

       * def holidayToUpdateArray = [{id:"#(id)","description":"UPD#1upd","startDateTime":"2020-02-12T11:00:00.000Z","endDateTime":"2020-02-12T12:00:00.000Z","repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2021-02-12","startDate":null,"endDate":null}]

        Given path ishPath
        And request holidayToUpdateArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response == 
        """
            [{
            "repeatEnd":"onDate",
            "startDateTime":"2020-02-12T11:00:00.000Z",
            "endDate":null,
            "created":"#ignore",
            "repeat":"day",
            "description":"UPD#1upd",
            "repeatEndAfter":null,
            "modified":"#ignore",
            "id":"#ignore",
            "endDateTime":"2020-02-12T12:00:00.000Z",
            "repeatOn":"2021-02-12",
            "startDate":null}]
        """
#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update 'Start' to empty

#       <---> Prepare new Holiday to update it:
        * def someHolidayArray = [{"id":null,"description":"UPD#2","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'UPD#2')].id
        * print "id = " + id
#       <--->

        * def holidayToUpdateArray = [{id:"#(id)","description":"UPD#2upd","startDateTime":null,"endDateTime":"2020-02-12T12:41:56.719Z","repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2021-02-12","startDate":null,"endDate":null}]

        Given path ishPath
        And request holidayToUpdateArray
        When method POST
        Then status 400
        And match $.errorMessage == "Time interval should be provided by start and end time."

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update 'End' to empty

#       <---> Prepare new Holiday to update it:
        * def someHolidayArray = [{"id":null,"description":"UPD#3","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":"2025-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'UPD#3')].id
        * print "id = " + id
#       <--->

        * def holidayToUpdateArray = [{id:"#(id)","description":"UPD#3upd","startDateTime":null,"endDateTime":null,"repeatEnd":"onDate","repeat":"day","repeatEndAfter":0,"repeatOn":"2024-02-12","startDate":"2020-02-12","endDate":null}]

        Given path ishPath
        And request holidayToUpdateArray
        When method POST
        Then status 400
        And match $.errorMessage == "Date interval should be provided by start and end dates."

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update 'On Date' to empty

#       <---> Prepare new Holiday to update it:
        * def someHolidayArray = [{"id":null,"description":"UPD#4","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2022-02-12T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":"2022-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'UPD#4')].id
        * print "id = " + id
#       <--->

        * def holidayToUpdateArray = [{id:"#(id)","description":"UPD#4upd","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2022-02-12T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":null}]

        Given path ishPath
        And request holidayToUpdateArray
        When method POST
        Then status 400
        And match $.errorMessage == "On date must be specified."

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update 'End' to incorrect: 'End' < 'Start'

#       <---> Prepare new Holiday to update it:
        * def someHolidayArray = [{"id":null,"description":"UPD#5","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2022-02-12T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":"2022-02-12"}]

        Given path ishPath
        And request someHolidayArray
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.description == 'UPD#5')].id
        * print "id = " + id
#       <--->

        * def holidayToUpdateArray = [{id:"#(id)","description":"UPD#5upd","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2020-02-11T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":"2022-02-12"}]

        Given path ishPath
        And request holidayToUpdateArray
        When method POST
        Then status 400
        And match $.errorMessage == "Holiday end time must be after start time."

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * call read('../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (-) Update not existing Holidays

        * def nonExistingHolidayToUpdateArray = [{id:"99999","description":"UPD#6upd","startDateTime":"2020-02-12T11:41:53.042Z","endDateTime":"2021-02-11T12:41:00.000Z","repeatEnd":"onDate","repeat":"week","repeatEndAfter":0,"repeatOn":"2022-02-12"}]

        Given path ishPath
        And request nonExistingHolidayToUpdateArray
        When method POST
        Then status 400
        And match response.errorMessage == "Holiday 99999 is not exist."