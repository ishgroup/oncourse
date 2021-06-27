@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/survey'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/survey'
        * def ishPathList = 'list'
        


        * def surveyToDefault = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}


    Scenario: (+) Update Survey 'visibility' by admin

#       <---> Change 'testimonial':
        * def surveyToUpdate =
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":1,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Public testimonial",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":{"6":"tutor3"}
        }
        """

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/1004'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":1,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Public testimonial",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":"#ignore"
        }
        """

#       <---> Change 'visibility':
        * def surveyToUpdate =
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":1,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Waiting review",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":{"6":"tutor3"}
        }
        """

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/1004'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":1,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Waiting review",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":"#ignore"
        }
        """

#       <---> Change 'score':
        * def surveyToUpdate =
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":10,
        "venueScore":1,
        "courseScore":2,
        "tutorScore":3,
        "visibility":"Hidden by student",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":{"6":"tutor3"}
        }
        """

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/1004'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1004,
        "studentContactId":10,
        "studentName":"stud4",
        "netPromoterScore":10,
        "venueScore":1,
        "courseScore":2,
        "tutorScore":3,
        "visibility":"Hidden by student",
        "testimonial":"5 stars UPD",
        "comment":"Course is nice schedule but Tutor need to be more friendly and informative.",
        "customFields":{},
        "siteId":201,
        "siteName":"site1",
        "roomId":2,
        "roomName":"room2",
        "classId":6,
        "className":"course4-1 Course4",
        "tutors":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/1004'
        And request surveyToDefault
        When method PUT
        Then status 204

