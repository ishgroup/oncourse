@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/account'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/account'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create account by admin

        * def newAccount =
        """
        {
        "accountCode":"code1",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"income",
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

        * def id = get[0] response.rows[?(@.values == ["code1","true","income","some descriptions"])].id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "accountCode":"code1",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"income",
        "tax":{"id":1,"code":"GST","editable":null,"systemType":null,"gst":null,"rate":null,"payableAccountId":null,"receivableAccountId":null,"description":null,"created":null,"modified":null}
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create account by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        * def newAccount =
        """
        {
        "accountCode":"code2",
        "description":"some descriptions",
        "isEnabled":false,
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

        * def id = get[0] response.rows[?(@.values == ["code2","false","asset","some descriptions"])].id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "accountCode":"code2",
        "description":"some descriptions",
        "isEnabled":false,
        "type":"asset",
        "tax":null
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create account with max allowed fields length

        * def newAccount =
        """
        {
        "accountCode":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A4",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A",
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

        * def id = get[0] response.rows[?(@.values == ["A3A5A7A9A12A15A18A21A24A27A30A33A36A39A4","true","asset","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A"])].id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "accountCode":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A4",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A",
        "isEnabled":true,
        "type":"asset",
        "tax":null
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new account by notadmin without access rights

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

        * def newAccount =
        """
        {
        "accountCode":"code3",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create account. Please contact your administrator"



    Scenario: (-) Create account by admin if isEnabled is null

        * def newAccount =
        """
        {
        "accountCode":"code10",
        "description":"some descriptions_10",
        "isEnabled":null,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Enabled value is required."



    Scenario: (-) Create new account with empty Type

        * def newAccount =
        """
        {
        "accountCode":"code11",
        "description":"some descriptions",
        "isEnabled":true,
        "type":null,
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Account type is required."



    Scenario: (-) Create new account with empty Description

        * def newAccount =
        """
        {
        "accountCode":"code12",
        "description":"",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Description is required."



    Scenario: (-) Create new account with empty Code

        * def newAccount =
        """
        {
        "accountCode":"",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create new account with not unique Code

        * def newAccount =
        """
        {
        "accountCode":"11100",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":2}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create new account with non-existing tax

        * def newAccount =
        """
        {
        "accountCode":"code13",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":999}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Tax with id:999 doesn't exist"



    Scenario: (-) Create account with out of range allowed fields length

#       <---> Create account with Code >40 symbols:
        * def newAccount =
        """
        {
        "accountCode":"55555555555555555555555555555555555555555",
        "description":"some descriptions",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Code cannot be more than 40 chars."

#       <---> Create account with Description >500 symbols:
        * def newAccount =
        """
        {
        "accountCode":"code13",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A260A264A268A272A276A280A284A288A292A296A300A304A308A312A316A320A324A328A332A336A340A344A348A352A356A360A364A368A372A376A380A384A388A392A396A400A404A408A412A416A420A424A428A432A436A440A444A448A452A456A460A464A468A472A476A480A484A488A492A496A500A1",
        "isEnabled":true,
        "type":"asset",
        "tax":{"id":1}
        }
        """

        Given path ishPath
        And request newAccount
        When method POST
        Then status 400
        And match $.errorMessage == "Description cannot be more than 500 chars."

