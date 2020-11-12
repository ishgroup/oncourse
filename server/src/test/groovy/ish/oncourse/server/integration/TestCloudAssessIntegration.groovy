package ish.oncourse.server.integration

import ish.oncourse.server.integration.cloudassess.CloudAssessIntegration

/**
 * For Cloud Assess testing
 */
class TestCloudAssessIntegration {

    static void main(String[] args) {
        def cloudassess = new CloudAssessIntegration(
                username: "andrew+ish@cloudassess.com.au",
                apiKey: "e758c4aa97e437ea86a98b5095ff7530",
//                clientId: "4222594d290f9f445af7825622c15c3a05260fd4b10ffda91f0f49b980aa95b1",
//                clientSecret: "413f018f8f6fb18abb41aaee017eb23cbef0aa0b64c8b6b737c63c7624b2766a",
                orgId: "170",
//                BASE_URL: "https://staging.assessapp.com"
        )

        def login = cloudassess.login()
        println "\nLogin complete:\n${login}"

        def accessToken = login.access_token
        println "accessToken = ${accessToken}"


        def attempt = 3
        
        
        //outcomes
        def outcomes = cloudassess.outcomes(new Date() - 720).response
        println outcomes

        // --------Membership search---------------------
        def email = "Macintosh${attempt}@mailtest.com".toString()
        def firstName = "Macintosh"
        def lastName = "Chadwick"
        def angelId = 556000 + attempt

        println "Student: email= ${email} firsName= ${firstName} lastName= ${lastName} angelId= ${angelId}"

        def membershipSearch= cloudassess.membershipSearch(accessToken, "Macintosh3")
        println "membershipSearch complete:\n${membershipSearch}"
        println "found ${membershipSearch.response.size()} memberships"

        def result = membershipSearch.response.findAll{ it -> it.email == email}
        println "Result:\n${result}\n"

        // ---------membership create--------------------
//        def membershipCreate = cloudassess.membershipCreate(accessToken, email, firstName, lastName, angelId.toString(), angelId)
//        println "membershipCreate complete:\n${membershipCreate}\n"
//
//        def membershipId = membershipCreate.response.id
//        println "membershipId = ${membershipId}"

        // ----------course search-------------------
        def courseSearch = cloudassess.courseSearch(accessToken)
        println "courseSearch complete:\n${courseSearch}"
        println "found ${courseSearch.response.size()} courses\n"

        // -----------course create------------------
        def code = "AB${attempt}".toString()
        def name = "Accredita Barista"
        def qualCode = "SITHFAB201"
        def cangelId = 3000 + attempt


//        def courseCreate = cloudassess.courseCreate(accessToken, code, name, qualCode, cangelId)
//        println "courseCreate complete:\n${courseCreate}\n"
//
//        def course_Id = courseCreate.response.id
//        println "course_Id = ${course_Id}"
//
//
//        //courseIds: 2432, 2433, 2434, 2435, 2436, 2437
        def courseId = 2432
        def mId = 16252
//        println "test courseId = ${courseId}\n"


        // -------------enrolment search----------------
//        def enrolmentSearch = cloudassess.enrolmentSearch(accessToken, courseId, mId)
//        println "enrolmentSearch complete:\n${enrolmentSearch}"
//        println "found ${enrolmentSearch.response.size()} courses\n"
//
//        println enrolmentSearch.response[1].student.first_name


        // ----------enrolment create-------------------
        def enrolmentCreate = cloudassess.enrolmentCreate(accessToken, courseId, mId)
        println "enrolmentCreate complete:\n${enrolmentCreate}\n"

    }
}
