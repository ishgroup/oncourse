package ish.oncourse.server.integration

import groovy.transform.CompileDynamic
import ish.oncourse.server.integration.moodle.MoodleIntegration

@CompileDynamic
class MoodleIntegrationTestHelper {

    final static String BASE_URI = "http://localhost:8888/moodle29/"
    final static String USER_NAME = "admin"
    final static String PASSWORD = "Consulrisk_12"
    final static String SERVICE_NAME = "newint"

    final static String DEFAULT_CONTACT_PASSWORD = "TESTTEST"

    static void main(String... args) {
        MoodleIntegrationTestHelper helper = new MoodleIntegrationTestHelper()
        helper.test()
    }

    /**
     * Checking of Moodle actions:
     * 1. getToken (login by user with credentials in constants)
     * 2. getCourses (fetch available courses and return id of random of them)
     * 3. createUser (create user with UUIDs as first name and last name, generate email address with it and create user
     *      with password from constant)
     * 4. getUserByEmail (return id of created user)
     * 5. enrolUsers (enrol user by fetched user id and course id)
     */
    
    void test() {
        MoodleIntegration module = new MoodleIntegration(USER_NAME, PASSWORD, BASE_URI, SERVICE_NAME)

        def getToken = {
            String token = module.getToken()
            if (token) {
                println("1. success - getToken(), token : ${token}")

            } else {
                println("1. error on getToken(), service unavailable or wrong credentials.")
            }
        }

        def getRandomCourseId = {
            String courseId = null
            def courses = module.getCourses()
            if ("moodle_exception" != courses?.exception) {

                int randomCourseIndex = new Random().nextInt(courses.size)

                def courseName = courses.get(randomCourseIndex).fullname
                courseId = courses.get(randomCourseIndex).id

                println("2. success - getCourses(), random course id : ${courseId}, course name : ${courseName}")
            } else {
                println("2. error on getCourses(), errorMessage : ${courses.message}")
            }
            return courseId
        }

        def createRandomUser = {
            String email = null

            String firstName = UUID.randomUUID().toString()
            String lastName = UUID.randomUUID().toString()

            def contact = [firstName: firstName,
                           lastName : lastName,
                           email    : "${firstName}_${lastName}@mail.com",
                           suburb   : "NSW"]
            String login = lastName

            def response = module.createUser(login, DEFAULT_CONTACT_PASSWORD, contact)
            if ("moodle_exception" != response?.exception) {
                email = contact.email
                String userName = response[0].username
                println("3. success - createUser(), name : ${userName}, password : ${DEFAULT_CONTACT_PASSWORD}, email : ${email}")
            } else {
                println("3. error on createUser(), errorMessage : ${response.message}")
            }
            return email
        }

        def getUserIdByEmail = { String mail ->
            String id = null
            if (mail) {
                def response = module.getUserByEmail(mail)
                if (mail == response[0].email) {
                    id = response[0].id
                    String email = response[0].email
                    println("4. success - getUserByEmail(), user id : ${id}, email : ${email}")
                } else {
                    println("4. error on getUserByEmail(), something went wrong")
                }
            } else {
                println("4. error on getUserByEmail(), input parameter 'mail' is null")
            }
            return id
        }

        def enrolUserOnCourse = { userId, courseId ->
            def response = module.enrolUsers(userId, courseId)
            if ("moodle_exception" != response?.exception) {
                println("5. success - enrolUsers(), user enroled")
            } else {
                println("5. error on enrolUsers(), errorMessage : ${response.message}")
            }
        }

        getToken.call()
        String courseId = getRandomCourseId.call()
        String email = createRandomUser.call()
        String userId = getUserIdByEmail.call(email)
        enrolUserOnCourse.call(userId, courseId)
    }
}
