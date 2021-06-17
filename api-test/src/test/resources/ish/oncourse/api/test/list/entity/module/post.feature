@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/module'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/module'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create new custom Module by admin

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["2"]

        * def id = get[0] response.rows[?(@.values == ["2","1","true"])].id

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create new custom Module with max length allowed for title, nationalCode, fieldOfEducation

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"123456","isCustom":true,"type":"OTHER","isOffered":true,"nationalCode":"123456789012","nominalHours":7,"specialization":"4","title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["123456789012"]

        * def id = get[0] response.rows[?(@.values == ["123456789012","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A","true"])].id

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create new custom Module by notadmin with access rights

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

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"notadmin_title1"}
        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["notadmin_title1"]

        * def id = get[0] response.rows[?(@.values == ["2","notadmin_title1","true"])].id

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



    Scenario: (-) Create new custom Module by notadmin without access rights

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

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create module. Please contact your administrator"

#       <---->  Scenario have been finished. Now change back permissions:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"



    Scenario: (-) Create new custom Module with empty National Code

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "National code is required."



    Scenario: (-) Create new custom Module with empty Title

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":""}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Title is required."



    Scenario: (-) Create new custom Module with not unique National Code

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"AUM1001A","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "National code must be unique."



    Scenario: (-) Create new custom Module with empty type

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Module type is required."



    Scenario: (-) Create new custom Module with National Code value >12 symbols

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"SymbolsName13","nominalHours":7,"specialization":"4","title":"1"}


        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "National code can't be more than 12 chars."



    Scenario: (-) Create new custom Module with Title value >200 symbols

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Title can't be more than 200 chars."



    Scenario: (-) Create new custom Module with Field of Education value >6 symbols

        * def newModule = {"creditPoints":"5","expiryDays":6,"fieldOfEducation":"1234567","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"2","nominalHours":7,"specialization":"4","title":"1"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Field Of Education can't be more than 6 chars."



    Scenario: (-) Create new custom Module with Credit Points = 0

        * def newModule = {"creditPoints":"0.00","expiryDays":6,"fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"HJK","nominalHours":7,"specialization":"4","title":"456"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Credit points must be greater than 0."



    Scenario: (-) Create new custom Module with Expire Days = 0

        * def newModule = {"creditPoints":"5","expiryDays":"0","fieldOfEducation":"3","isCustom":true,"type":"MODULE","isOffered":true,"nationalCode":"KUY","nominalHours":7,"specialization":"4","title":"457"}

        Given path ishPath
        And request newModule
        When method POST
        Then status 400
        And match $.errorMessage == "Expiry days should be greater than 0."





