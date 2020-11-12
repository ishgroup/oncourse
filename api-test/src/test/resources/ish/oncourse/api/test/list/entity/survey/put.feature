@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/survey'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/survey'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


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
        "tutors":{"6":"tutor3"}
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
        "tutors":{"6":"tutor3"}
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
        "tutors":{"6":"tutor3"}
        }
        """

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/1004'
        And request surveyToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Survey by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

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
        "tutors":{"6":"tutor3"}
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
        "tutors":{"6":"tutor3"}
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
        "visibility":"Not testimonial",
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
        "visibility":"Not testimonial",
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

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/1004'
        And request surveyToDefault
        When method PUT
        Then status 204



    Scenario: (-) Update Survey by notadmin without access rights

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
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit student feedback. Please contact your administrator"



    Scenario: (-) Update Survey testimonial to empty

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
        "testimonial":"",
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
        Then status 400
        And match $.errorMessage == "Published testimonial cannot be blank."



    Scenario: (-) Update Survey visibility to not existing value

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
        "visibility":"Not existing item",
        "testimonial":"5 stars",
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
        Then status 400
        And match $.errorMessage == "Visibility is required."



    Scenario: (-) Update Survey 'score' to not existing value

#       <---> Change 'netPromoterScore' to 11:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":11,"venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'netPromoterScore' to null:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":null,"venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'netPromoterScore' to '0':
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":0,"venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'netPromoterScore' to empty:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":"","venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'netPromoterScore' to negative:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":-5,"venueScore":5,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."

#       <---> Change 'venueScore' to 6:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":6,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'venueScore' to null:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":null,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'venueScore' to '0':
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":0,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'venueScore' to empty:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":"","courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'venueScore' to negative:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":-3,"courseScore":5,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."

#       <---> Change 'courseScore' to 6:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":6,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'courseScore' to null:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":null,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'courseScore' to '0':
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":0,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'courseScore' to empty:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":"","tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'courseScore' to negative:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":-3,"tutorScore":5,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."

#       <---> Change 'tutorScore' to 6:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":6,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'tutorScore' to null:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":null,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'tutorScore' to '0':
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":0,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'tutorScore' to empty:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":"","visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."
#       <---> Change 'tutorScore' to negative:
        * def surveyToUpdate = {"id":1004,"studentContactId":10,"studentName":"stud4","netPromoterScore":1,"venueScore":5,"courseScore":5,"tutorScore":-3,"visibility":"Public testimonial","testimonial":"5 stars","comment":"Course is nice schedule but Tutor need to be more friendly and informative.","customFields":{},"siteId":201,"siteName":"site1","roomId":2,"roomName":"room2","classId":6,"className":"course4-1 Course4","tutors":{"6":"tutor3"}}

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Not allowed value for score."



    Scenario: (-) Update Survey disabled field

#       <---> Try to change disabled values:
        * def surveyToUpdate =
        """
        {
        "id":1004,
        "studentContactId":2,
        "studentName":"stud1",
        "netPromoterScore":1,
        "venueScore":5,
        "courseScore":5,
        "tutorScore":5,
        "visibility":"Public testimonial",
        "testimonial":"5 stars",
        "comment":"New comment upd",
        "customFields":{},
        "siteId":202,
        "siteName":"site2",
        "roomId":1,
        "roomName":"room1",
        "classId":5,
        "className":"course2-2 Course2",
        "tutors":{"5":"tutor2"}
        }
        """

        Given path ishPath + '/1004'
        And request surveyToUpdate
        When method PUT
        Then status 204

#        <---> Assertion (values should not be changed):
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
        "testimonial":"5 stars",
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



    Scenario: (-) Update not existing Survey

#       <---> Change 'visibility' and 'testimonial':
        * def surveyToUpdate =
        """
        {
        "id":99999,
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

        Given path ishPath + '/99999'
        And request surveyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."