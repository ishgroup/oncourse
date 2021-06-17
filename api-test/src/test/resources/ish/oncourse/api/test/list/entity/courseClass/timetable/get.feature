@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/timetable'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/timetable'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass timetable by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-04-30T14:54:31.000Z","end":"2017-04-30T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":9,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-01T14:54:31.000Z","end":"2017-05-01T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":4,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-02T14:54:31.000Z","end":"2017-05-02T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":6,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-03T14:54:31.000Z","end":"2017-05-03T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":2,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-04T14:54:31.000Z","end":"2017-05-04T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":3,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-05T14:54:31.000Z","end":"2017-05-05T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":8,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-06T14:54:31.000Z","end":"2017-05-06T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":7,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-07T14:54:31.000Z","end":"2017-05-07T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":10,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-08T14:54:31.000Z","end":"2017-05-08T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true},
        {"id":5,"temporaryId":null,"name":"Course1","code":null,"room":"room1","site":"site1","tutors":["tutor1"],"courseId":1,"contactIds":[1],"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[1],"temporaryTutorIds":[],"start":"2017-05-09T14:54:31.000Z","end":"2017-05-09T15:54:31.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":true}
        ]
        """

    Scenario: (-) Get not existing CourseClass timetable

        Given path ishPath + '/99999'
        When method GET
        Then status 200
        And match $ == []



    Scenario: (+) Get CourseClass timetable by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":42,"temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":["tutor3"],"contactIds":[6],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[6],"temporaryTutorIds":[],"start":"2027-02-01T11:14:40.000Z","end":"2027-02-01T12:14:40.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":false},
        {"id":41,"temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":["tutor3"],"contactIds":[6],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[6],"temporaryTutorIds":[],"start":"2027-02-02T11:14:40.000Z","end":"2027-02-02T12:14:40.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":false},
        {"id":39,"temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":["tutor3"],"contactIds":[6],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[6],"temporaryTutorIds":[],"start":"2027-02-03T11:14:40.000Z","end":"2027-02-03T12:14:40.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":false},
        {"id":40,"temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":["tutor3"],"contactIds":[6],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[6],"temporaryTutorIds":[],"start":"2027-02-04T11:14:40.000Z","end":"2027-02-04T12:14:40.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":false},
        {"id":43,"temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":["tutor3"],"contactIds":[6],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[6],"temporaryTutorIds":[],"start":"2027-02-05T11:14:40.000Z","end":"2027-02-05T12:14:40.000Z","publicNotes":null,"privateNotes":null,"payAdjustment":0,"hasPaylines":false}
        ]
        """

