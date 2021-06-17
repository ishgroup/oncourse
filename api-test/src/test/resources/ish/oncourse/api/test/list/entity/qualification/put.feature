@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/qualification'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/qualification'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Update Qualification by admin

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

#       <--->
        * def qualificationToUpdate =

        """
        {"type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"someLevel_UPD",
        "title":"someTitle_UPD",
        "isOffered":true,
        "fieldOfEducation":"2",
        "anzsco":"1",
        "nominalHours":"4",
        "specialization":"3"
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"someLevel_UPD",
        "title":"someTitle_UPD",
        "isOffered":true,
        "fieldOfEducation":"2",
        "anzsco":"1",
        "isCustom":true,
        "nominalHours":4,
        "specialization":"3"
        }
        """
#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Qualification to max allowed length values

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

#       <--->
        * def qualificationToUpdate =

        """
        {"type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "isOffered":true,
        "fieldOfEducation":"123456",
        "anzsco":"123456",
        "nominalHours":"12345678.12",
        "specialization":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A"
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "isOffered":true,
        "fieldOfEducation":"123456",
        "anzsco":"123456",
        "isCustom":true,
        "nominalHours":12345678.12,
        "specialization":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A"
        }
        """
#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Qualification by notadmin with rights

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

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

#       <--->
        * def qualificationToUpdate =

        """
        {"type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"someLevel_UPD",
        "title":"someTitle_UPD",
        "isOffered":true,
        "fieldOfEducation":"2",
        "anzsco":"1",
        "nominalHours":"4",
        "specialization":"3"
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"someLevel_UPD",
        "title":"someTitle_UPD",
        "isOffered":true,
        "fieldOfEducation":"2",
        "anzsco":"1",
        "isCustom":true,
        "nominalHours":4,
        "specialization":"3"
        }
        """
#       <--->  Scenario have been finished. Now find and remove created object from DB
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-+) Update system (not custom) qualification

#       <---> These fields should not be changed: id, type, nationalCode, fieldOfEducation, anzsco, isCustom
        * def qualificationToUpdate =
        """
        {
        "id":30,
        "type":"Skill set",
        "nationalCode":"10218NAT",
        "qualLevel":"Level_UPD",
        "isOffered":false,
        "fieldOfEducation":"0000",
        "anzsco":"123456",
        "isCustom":true,
        "nominalHours":"5",
        "specialization":"6"
        }
        """

        Given path ishPath + '/3'
        And request qualificationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":3,
        "type":"Skill set",
        "nationalCode":"10218NAT",
        "qualLevel":"Certificate I in","title":"Aboriginal Language/s v2",
        "isOffered":false,
        "fieldOfEducation":"0915",
        "anzsco":"422111",
        "isCustom":false,
        "nominalHours":5.0,
        "specialization":'6'
        }
        """

#       <--->  Scenario have been finished. Now change object to default:
        * def qualificationToUpdate =
        """
        {
        "id":3,
        "type":"Skill set",
        "nationalCode":"10218NAT",
        "qualLevel":"Certificate I in","title":"Aboriginal Language/s v2",
        "isOffered":true,
        "fieldOfEducation":"0915",
        "anzsco":"422111",
        "isCustom":false,
        "nominalHours":null,
        "specialization":null
        }
        """

        Given path ishPath + '/3'
        And request qualificationToUpdate
        When method PUT
        Then status 204



    Scenario: (-) Update Qualification by notadmin without rights

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

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

#       <--->
        * def qualificationToUpdate =

        """
        {"type":"Accredited course",
        "nationalCode":"CODE02",
        "qualLevel":"someLevel_UPD",
        "title":"someTitle_UPD",
        "isOffered":true,
        "fieldOfEducation":"2",
        "anzsco":"1",
        "nominalHours":"4",
        "specialization":"3"
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update qualification. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Qualification required fields to empty

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

#       <--->  Update custom Qualification to empty Title:
        * def qualificationToUpdate =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Title is required."

#       <--->  Update custom Qualification to empty Type:
        * def qualificationToUpdate =
        """
        {
        "type":"",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Type is required."

#       <--->  Update custom Qualification to empty qualLevel:
        * def qualificationToUpdate =
        """
        {
        "type":"Qualification",
        "qualLevel":"",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Level is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Qualification National Code

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE82",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE82","someTitle","someLevel",null,"false"])].id

#       <--->  Update custom Qualification to not unique National Code:
        * def qualificationToUpdate =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE99",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Qualification national code cannot be updated."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Qualification fields length to out of range

#       <----->  Add a new entity to update and define id:
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["CODE02","someTitle","someLevel",null,"false"])].id

#       <--->  Update Title to >255 symbols:
        * def qualificationToUpdate =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Title can't be more than 255 chars."

#       <--->  Update Level to >255 symbols:
        * def qualificationToUpdate =
        """
        {
        "type":"Qualification",
        "qualLevel":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "title":"someTitle",
        "nationalCode":"CODE02",
        "isOffered":false
        }
        """

        Given path ishPath + '/' + id
        And request qualificationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Level can't be more than 255 chars."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204