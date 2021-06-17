@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/qualification'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/qualification'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create new custom Qualification by admin

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE01",
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
        And match $.rows[*].values[*] contains ["CODE01"]

        * def id = get[0] response.rows[?(@.values == ["CODE01","someTitle","someLevel",null,"false"])].id

#       Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create new custom Qualification with max length allowed for qualLevel, title, nationalCode

#       <---> Create entity with length for qualLevel: 256 chars, title: 256 chars, nationalCode: 12 chars
        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",
        "nationalCode":"A3A5A7A9A12A",
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
        And match $.rows[*].values[*] contains ["A3A5A7A9A12A"]

        * def id = get[0] response.rows[?(@.values == ["A3A5A7A9A12A","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256",null,"false"])].id

#       Scenario have been finished. Now find and remove created object from DB:
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}



    Scenario: (+) Create new custom Qualification by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE01",
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
        And match $.rows[*].values[*] contains ["CODE01"]

        * def id = get[0] response.rows[?(@.values == ["CODE01","someTitle","someLevel",null,"false"])].id

#       <---->  Scenario have been finished. Now delete created entity from db:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new custom Qualification by notadmin without access rights

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

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE01",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create qualification. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity from db:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"



    Scenario: (-) Create new custom Qualification with empty Type

        * def someQualification =
        """
        {
        "type":"",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"CODE01",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "Type is required."



    Scenario: (-) Create new custom Qualification with empty Level

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"",
        "title":"someTitle",
        "nationalCode":"CODE01q",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "Level is required."



    Scenario: (-) Create new custom Qualification with empty Title

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"",
        "nationalCode":"CODE01w",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "Title is required."



    Scenario: (-) Create new custom Qualification with empty National Code

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "National code is required."



    Scenario: (-) Create new custom Qualification with not unique National Code

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"10218NAT",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "National code must be unique."



    Scenario: (-) Create new custom Qualification with National Code value >12 symbols

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"someTitle",
        "nationalCode":"1234567890123",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "National code can't be more than 12 chars."



    Scenario: (-) Create new custom Qualification with Title value >256 symbols

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"someLevel",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A2",
        "nationalCode":"CODE01e",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "Title can't be more than 255 chars."



    Scenario: (-) Create new custom Qualification with Level value >256 symbols

        * def someQualification =
        """
        {
        "type":"Qualification",
        "qualLevel":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A2",
        "title":"someTitle",
        "nationalCode":"CODE01r",
        "isOffered":false
        }
        """

        Given path ishPath
        And request someQualification
        When method POST
        Then status 400
        And match $.errorMessage == "Level can't be more than 255 chars."


