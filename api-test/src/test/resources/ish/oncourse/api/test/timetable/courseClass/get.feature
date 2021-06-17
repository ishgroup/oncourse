@parallel=false
Feature: Main feature for all GET requests with path 'timetable/courseClass'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'timetable/courseClass'
        



    Scenario: (+) Get courseClass timetable by admin

        Given path ishPath
        And param ids = '1'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-04-30T14:54:31.000Z","end":"2017-04-30T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":9,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-01T14:54:31.000Z","end":"2017-05-01T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":4,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-02T14:54:31.000Z","end":"2017-05-02T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":6,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-03T14:54:31.000Z","end":"2017-05-03T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":2,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-04T14:54:31.000Z","end":"2017-05-04T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":3,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-05T14:54:31.000Z","end":"2017-05-05T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":8,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-06T14:54:31.000Z","end":"2017-05-06T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":7,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-07T14:54:31.000Z","end":"2017-05-07T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":10,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-08T14:54:31.000Z","end":"2017-05-08T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":5,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-09T14:54:31.000Z","end":"2017-05-09T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null}
        ]
        """



    Scenario: (+) Get courseClass timetable by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param ids = '1,2'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-04-30T14:54:31.000Z","end":"2017-04-30T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":9,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-01T14:54:31.000Z","end":"2017-05-01T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":4,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-02T14:54:31.000Z","end":"2017-05-02T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":6,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-03T14:54:31.000Z","end":"2017-05-03T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":2,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-04T14:54:31.000Z","end":"2017-05-04T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":3,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-05T14:54:31.000Z","end":"2017-05-05T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":8,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-06T14:54:31.000Z","end":"2017-05-06T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":7,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-07T14:54:31.000Z","end":"2017-05-07T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":10,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-08T14:54:31.000Z","end":"2017-05-08T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":5,"temporaryId":null,"name":"Course1","code":"course1-1","room":"room1","site":"site1","tutors":["tutor1"],"classId":1,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2017-05-09T14:54:31.000Z","end":"2017-05-09T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":18,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2018-11-30T13:56:27.000Z","end":"2018-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":13,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2019-11-30T13:56:27.000Z","end":"2019-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":15,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-11-30T13:56:27.000Z","end":"2020-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":20,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2021-11-30T13:56:27.000Z","end":"2021-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":12,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2022-11-30T13:56:27.000Z","end":"2022-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":11,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2023-11-30T13:56:27.000Z","end":"2023-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":16,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2024-11-30T13:56:27.000Z","end":"2024-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":19,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2025-11-30T13:56:27.000Z","end":"2025-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":14,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2026-11-30T13:56:27.000Z","end":"2026-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null},
        {"id":17,"temporaryId":null,"name":"Course1","code":"course1-2","room":"room1","site":"site1","tutors":["tutor1"],"classId":2,"roomId":null,"siteId":null,"siteTimezone":null,"courseClassTutorIds":[],"temporaryTutorIds":[],"courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2027-11-30T13:56:27.000Z","end":"2027-11-30T14:56:27.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":null,"hasPaylines":null}
        ]
        """



    Scenario: (+) Get timetable for not existing courseClass

        Given path ishPath
        And param ids = '99999'
        When method GET
        Then status 200
        And match $ == []