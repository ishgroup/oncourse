@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/courseClass/tutor'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update existing Tutor by admin (change confirmedOn only)

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":1,
              "confirmedOn":"2018-01-01",
              "isInPublicity":true
              }
              """

        Given path ishPath
        And request newTutor
        When method POST
        Then status 200

        Given path ishPath + '/6'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.confirmedOn == '2018-01-01')].id
        * print "id = " + id
#       <--->

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":1,
            "roleId":1,
            "confirmedOn":"2018-02-01",
            "isInPublicity":false
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 204
        
#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        