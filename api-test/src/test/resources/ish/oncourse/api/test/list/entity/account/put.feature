@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/account'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/account'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Update account by admin

#       <----->  Add a new entity for updating and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1000",
        "description":"some descriptions_1000",
        "isEnabled":true,
        "type":"asset"
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1000","true","asset","some descriptions_1000"])].id
#       <----->

        * def accountToUpdate =
        """
        {
        "accountCode":"code1000UPD",
        "description":"some descriptions_1000_UPD",
        "isEnabled":false,
        "type":"asset"
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.accountCode == "code1000UPD"
        And match $.description == "some descriptions_1000_UPD"
        And match $.isEnabled == false

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update account by notadmin with rights

#       <----->  Add a new entity for updating and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1001",
        "description":"some descriptions_1001",
        "isEnabled":true,
        "type":"asset"
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1001","true","asset","some descriptions_1001"])].id

        Given path '/logout'
        And request {}
        When method PUT
        
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

        * def accountToUpdate =
        """
        {
        "accountCode":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A4",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A",
        "isEnabled":true,
        "type":"asset"
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.accountCode == "A3A5A7A9A12A15A18A21A24A27A30A33A36A39A4"
        And match $.description == "A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A"
        And match $.isEnabled == true

        Given path '/logout'
        And request {}
        When method PUT
        
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



    Scenario: (-) Update account by notadmin without rights

#       <----->  Add a new entity for updating and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1002",
        "description":"some descriptions_1002",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1002","true","asset","some descriptions_1002"])].id

        Given path '/logout'
        And request {}
        When method PUT
        
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

        * def accountToUpdate =
        """
        {
        "accountCode":"code1002UPD",
        "description":"some descriptions_1002_UPD",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit account. Please contact your administrator"

        Given path '/logout'
        And request {}
        When method PUT
        
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



    Scenario: (-) Update account required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1003",
        "description":"some descriptions_1003",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1003","true","asset","some descriptions_1003"])].id

#       <--->  Update Code to empty:
        * def accountToUpdate =
        """
        {
        "accountCode":"",
        "description":"some descriptions_1003",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."

#       <--->  Update Description to empty:
        * def accountToUpdate =
        """
        {
        "accountCode":"code1003",
        "description":"",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Description is required."

#       <--->  Update isEnabled to null:
        * def accountToUpdate =
        """
        {
        "accountCode":"code1003",
        "description":"some descriptions_1003",
        "isEnabled":null,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Enabled value is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update account type

#       <----->  Add a new entity to update and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1004",
        "description":"some descriptions_1004",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1004","true","asset","some descriptions_1004"])].id
#       <--->

        * def accountToUpdate =
        """
        {
        "accountCode":"code1004",
        "description":"some descriptions_1004",
        "isEnabled":true,
        "type":"liability",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Cannot change account type."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update account to existing code

#       <----->  Add a new entity to update and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1005",
        "description":"some descriptions_1005",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1005","true","asset","some descriptions_1005"])].id
#       <--->

        * def accountToUpdate =
        """
        {
        "accountCode":"11100",
        "description":"some descriptions_1005",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update account to out of range field length

#       <----->  Add a new entity to update and define its id:
        * def newAccount =
        """
        {
        "accountCode":"code1006",
        "description":"some descriptions_1006",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code1006","true","asset","some descriptions_1006"])].id

#       <---> Update account Code to >40 symbols:
        * def accountToUpdate =
        """
        {
        "accountCode":"55555555555555555555555555555555555555555",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code cannot be more than 40 chars."

#       <---> Update account Description to >500 symbols:
        * def accountToUpdate =
        """
        {
        "accountCode":"code13",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A1",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath + '/' + id
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Description cannot be more than 500 chars."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Disable system account

        * def accountToUpdate =
       """
        {
        "accountCode":"11100",
        "description":"Deposited funds",
        "isEnabled":false,
        "type":"asset",
        "tax":null
        }
        """

        Given path ishPath + '/1'
        And request accountToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Cannot disable default account."