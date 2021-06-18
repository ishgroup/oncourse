@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/module'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/module'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'




    Scenario: (+) Update module by admin

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id
#       <--->

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update module to max allowed length values

#       <----->  Add a new entity to update and define id:

        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id

#       <--->

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":1234567890,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF STUDY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":20,
        "specialization":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":1234567890,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF STUDY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":20,
        "specialization":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update not required fields to empty

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id
#       <--->

        * def moduleToUpdate =
        """
        {
        "creditPoints":null,
        "expiryDays":null,
        "fieldOfEducation":null,
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":null,
        "specialization":null,
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "creditPoints":null,
        "expiryDays":null,
        "fieldOfEducation":null,
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":null,
        "specialization":null,
        "title":"someTitle_UPD"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update module by notadmin with rights

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}


#       <--->

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"OTHER",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"OTHER",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}



        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-+) Update system (not custom) module

#       <---> These fields can not be changed: fieldOfEducation, isCustom, nationalCode, title

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"AUM1001A",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/3'
        And request moduleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"120501",
        "isCustom":false,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"AUM1001A",
        "nominalHours":5.0,
        "specialization":"someSpecialization_UPD",
        "title":"Manage personal career goals"
        }
        """



    Scenario: (-) Update module by notadmin without rights

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update module. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}



        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update module required fields to empty

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode"]

        * def id = get[0] response.rows[?(@.values == ["someCode","someTitle","false"])].id

#       <--->  Update custom Module to empty Title:
        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":""
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Title is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update module National Code

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"code123",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle_123"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["code123"]

        * def id = get[0] response.rows[?(@.values == ["code123","someTitle_123","false"])].id

#       <--->  Update custom module National Code:
        * def moduleToUpdate =
        """
        {
        "creditPoints":1,
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"code456",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle_123"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Module national code cannot be updated."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update module fields length to out of range

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"someCode1",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someCode1"]

        * def id = get[0] response.rows[?(@.values == ["someCode1","someTitle","false"])].id

#       <--->  Update Title to >200 symbols:
        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Title can't be more than 200 chars."

#       <--->  Update Field of Education to >6 symbols:
        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"1234567",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"someCode",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Field Of Education can't be more than 6 chars."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update type for system (not custom) module

#       <---> These fields can not be changed: fieldOfEducation, isCustom, type, nationalCode, title

        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"OTHER",
        "isOffered":true,
        "nationalCode":"AUM1001A",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/3'
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Module type cannot be updated."



    Scenario: (-) Update module to Credit Points = 0

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"YYY",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle456"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["YYY"]

        * def id = get[0] response.rows[?(@.values == ["YYY","someTitle456","false"])].id
#       <--->

        * def moduleToUpdate =
        """
        {
        "creditPoints":0,
        "expiryDays":5,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"YYY",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Credit points must be greater than 0."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update module to Expire Days = 0

#       <----->  Add a new entity to update and define id:
        * def newModule =
        """
        {
        "creditPoints":"1",
        "expiryDays":2,
        "fieldOfEducation":"someEd",
        "isCustom":true,
        "type":"MODULE",
        "isOffered":false,
        "nationalCode":"XXX",
        "nominalHours":3,
        "specialization":"someSpecialization",
        "title":"someTitle567"
        }
        """

        Given path ishPath
        And request newModule
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Module'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["XXX"]

        * def id = get[0] response.rows[?(@.values == ["XXX","someTitle567","false"])].id

#       <--->
        * def moduleToUpdate =
        """
        {
        "creditPoints":12345678.12,
        "expiryDays":0,
        "fieldOfEducation":"123456",
        "isCustom":true,
        "type":"UNIT OF COMPETENCY",
        "isOffered":true,
        "nationalCode":"XXX",
        "nominalHours":5,
        "specialization":"someSpecialization_UPD",
        "title":"someTitle_UPD"
        }
        """

        Given path ishPath + '/' + id
        And request moduleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Expiry days should be greater than 0."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
