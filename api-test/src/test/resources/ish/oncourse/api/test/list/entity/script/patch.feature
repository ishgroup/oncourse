@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/script' without license

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        * def ishPath = 'list/entity/script'
        * def ishPathPlain = 'list/plain'
        * def ishPathLogin = 'login'



    Scenario: (+) Update custom script (with Query panel) by admin using PATCH request

#       <--->  Add entity with Query panel to update and define it's id:
        * def script = {"keyCode":"test.script_1","enabled":true,"name":"script to testing PATCH method","trigger":{"type":"On demand"},"description":"some description","content":"//test"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param search = "name == 'script to testing PATCH method'"
        And param columns = 'name'
        When method GET
        Then status 200
        * def id = get[0] response.rows[0].id
#       <--->

        * def scriptToUpdate = {"id":"#(id)","name":"script to testing PATCH method","description":"some description","enabled":false,"trigger":{"type":"Enrolment cancelled","entityName":null,"cron":null},"content":"//test"}

        Given path ishPath + '/' + id
        And request scriptToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"script to testing PATCH method",
        "description":"some description",
        "enabled":false,
        "trigger":{"type":"Enrolment cancelled","entityName":null,"entityAttribute":null,"cron":null},
        "content":"//test",
        "lastRun":[],
        "keyCode":"test.script_1",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

#       <--->  Scenario have been finished. Now change created script to default:
        * def updateScriptToDefault = {"id":"#(id)","name":"script to testing PATCH method","description":"some description","enabled":true,"trigger":{"type":"On demand"},"content":"//test"}

        Given path ishPath + '/' + id
        And request updateScriptToDefault
        When method PATCH
        Then status 204



    Scenario: (+) Update custom script (with Query panel) by notadmin with access rights using PATCH request

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <--->  Define entity id:
        Given path ishPathPlain
        And param entity = 'Script'
        And param search = 'name == "script to testing PATCH method"'
        And param columns = 'name'
        When method GET
        Then status 200
        * def id = get[0] response.rows[0].id
#       <--->

        * def scriptToUpdate = {"id":"#(id)","name":"script to testing PATCH method","description":"updated description","enabled":false,"trigger":{"type":"Enrolment cancelled","entityName":null,"cron":null},"content":"//test"}

        Given path ishPath + '/' + id
        And request scriptToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"script to testing PATCH method",
        "description":"some description",
        "enabled":false,
        "trigger":{"type":"Enrolment cancelled","entityName":null,"entityAttribute":null,"cron":null},
        "content":"//test",
        "lastRun":[],
        "keyCode":"test.script_1",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

#       <--->  Scenario have been finished. Now change script to default:
        * def updateScriptToDefault = {"id":"#(id)","name":"script to testing PATCH method","description":"some description","enabled":true,"trigger":{"type":"On demand"},"content":"//test"}

        Given path ishPath + '/' + id
        And request updateScriptToDefault
        When method PATCH
        Then status 204
        

    Scenario: (-) Update script (with Query panel) by notadmin without access rights using PATCH request

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <--->  Define entity id:
        Given path ishPathPlain
        And param entity = 'Script'
        And param search = 'name == "script to testing PATCH method"'
        And param columns = 'name'
        When method GET
        Then status 200
        * def id = get[0] response.rows[0].id
#       <--->

        * def scriptToUpdate = {"id":"#(id)","name":"script to testing PATCH method UPD","description":"updated description","enabled":false,"trigger":{"type":"Enrolment cancelled","entityName":null,"cron":null},"content":"//test"}

        Given path ishPath + '/' + id
        And request scriptToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update script. Please contact your administrator"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update existing script (advanced panels are present) by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def scriptToUpdate = {"name":"ecoach enrol UPD","description":"UPD Enrol user to eCoach course upon onCourse enrolment. eCoach course must have the same title as onCourse course name","enabled":true,"trigger":{"type":"Enrolment cancelled","entityName":null,"cron":null},"content":"import groovyx.net.http.ContentType\nimport groovyx.net.http.Method.UPD\nimport groovyx.net.http.RESTClient\n\nBASE_URL = \"\"\napiKey = \"\"\nuserId = \"\"\n\ndef run (args) {\n\t\n\t\t\t\n\tdef enrolment = args.entity\n\n\t// make or get students UPD\n\tdef member = postMember(enrolment)\n\t\n\t//get course id for group add\n\tdef courses =  getCourses()\n\n\tdef course = courses .find { c -> c.title == enrolment.courseClass.course.name }\n\n\t// get groups (classes) in course\n\tdef courseClasses = getGroups(course[\"id\"])\n\tdef courseClass = courseClasses.find { cc -> cc.name == enrolment.courseClass.uniqueCode }\n\n\tif (!courseClass) {\n\t    //create groupClass if one does not exist\n\t    courseClass = postGroup(enrolment, course[\"id\"])\n\t}\n\n\t////add student to group\n\tenrolMemberToGroup(member[\"id\"], courseClass[\"id\"])\n} \n\ndef getCourses() {\n    def client = new RESTClient(BASE_URL)\n    client.headers[\"Authorization\"] = \"ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}\"\n    client.headers[\"Content-Type\"] = \"application/json\"\n    client.headers[\"Accept\"] = \"application/json\"\n\n    client.request(Method.GET, ContentType.JSON) {\n        uri.path = \"/api/v1/courses\"\n        response.success = { resp, result ->\n            return result\n        }\n    }\n}\n\ndef getGroups(courseId) {\n\n    def client = new RESTClient(BASE_URL)\n    client.headers[\"Authorization\"] = \"ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}\"\n    client.headers[\"Content-Type\"] = \"application/json\"\n    client.headers[\"Accept\"] = \"application/json\"\n\n\n    client.request(Method.GET, ContentType.JSON) {\n        uri.path = \"/api/v1/courses/${courseId}/groups\"\n\n        response.success = { resp, result ->\n            return result\n        }\n    }\n}\n\ndef postMember(enrolment) {\n    def client = new RESTClient(BASE_URL)\n    client.headers[\"Authorization\"] = \"ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}\"\n    client.headers[\"Content-Type\"] = \"application/json\"\n    client.headers[\"Accept\"] = \"application/json\"\n\n    client.request(Method.POST, ContentType.JSON) {\n        uri.path = \"/api/v1/members\"\n        uri.query = [\n                username: enrolment.student.contact.email,\n                firstname: enrolment.student.contact.firstName,\n                lastname: enrolment.student.contact.lastName,\n                email: enrolment.student.contact.email\n        ]\n\n        response.success = { resp, result ->\n            return result\n        }\n    }\n}\n\ndef postGroup(enrolment, courseId) {\n\n    def client = new RESTClient(BASE_URL)\n    client.headers[\"Authorization\"] = \"ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}\"\n    client.headers[\"Content-Type\"] = \"application/json\"\n    client.headers[\"Accept\"] = \"application/json\"\n\n    client.request(Method.POST, ContentType.JSON) {\n        uri.path = \"/api/v1/courses/${courseId}/groups\"\n        body = [\n            name: enrolment.courseClass.uniqueCode\n        ]\n\n        response.success = { resp, result ->\n            return result\n        }\n    }\n}\n\ndef enrolMemberToGroup(memberId, groupId) {\n\n    def client = new RESTClient(BASE_URL)\n    client.headers[\"Authorization\"] = \"ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}\"\n    client.headers[\"Content-Type\"] = \"application/json\"\n    client.headers[\"Accept\"] = \"application/json\"\n\n    def id = [memberId] as int[]\n\n    client.request(Method.POST, ContentType.JSON) {\n        uri.path = \"/api/v1/groups/${groupId}/students\"\n        body = [\n            ids: id\n        ]\n\n        response.success = { resp, result ->\n            return result\n        }\n    }\n}"}

        Given path ishPath + '/5'
        And request scriptToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update script. Please contact your administrator"
        