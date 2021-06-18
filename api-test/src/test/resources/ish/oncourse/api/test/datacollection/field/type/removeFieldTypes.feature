@ignore
@parallel=false
Feature: re-usable feature removes Field types, which have been created in 'createFieldTypes.feature'

    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'
        * def deletePath = 'preference/field/type/'

    Scenario:
        Given path 'preference/field/type'
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'enrolmentFieldType')].id
        * def id2 = get[0] response[?(@.name == 'applicationFieldType')].id
        * def id3 = get[0] response[?(@.name == 'waitingListFieldType')].id
        * def id4 = get[0] response[?(@.name == 'contactFieldType')].id
        * def id5 = get[0] response[?(@.name == 'courseFieldType')].id

        Given path deletePath + id1
        When method DELETE
        Then status 204

        Given path deletePath + id2
        When method DELETE
        Then status 204

        Given path deletePath + id3
        When method DELETE
        Then status 204

        Given path deletePath + id4
        When method DELETE
        Then status 204

        Given path deletePath + id5
        When method DELETE
        Then status 204
