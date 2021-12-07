@parallel=false
Feature: Main feature for all GET requests with path 'timetable/session'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'timetable/session'
        



    Scenario: (+) Get sessions by admin

        Given path ishPath
        And param ids = '4,5,6'
        When method GET
        Then status 200
        And match response ==
        """
        [
        {"id":4,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-02T14:54:31.000Z","end":"2017-05-02T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null},
        {"id":6,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-03T14:54:31.000Z","end":"2017-05-03T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null},
        {"id":5,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-09T14:54:31.000Z","end":"2017-05-09T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null}
        ]
        """



    Scenario: (+) Get sessions by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param ids = '7,8,9'
        When method GET
        Then status 200
        And match response ==
        """
        [
        {"id":9,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-01T14:54:31.000Z","end":"2017-05-01T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null},
        {"id":8,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-06T14:54:31.000Z","end":"2017-05-06T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null},
        {"id":7,"temporaryId":null,"classId":1,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-07T14:54:31.000Z","end":"2017-05-07T15:54:31.000Z","publicNotes":null,"privateNotes":null,"hasPaylines":null}
        ]
        """


