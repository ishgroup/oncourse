@parallel=false
Feature: Main feature for all POST requests with path 'tag'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'tag'
        * def ishPathLogin = 'login'
        


        
    Scenario: (+) Add new valid 'tag group'

#       >>> Add a new entity by admin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'tagName1')]
        * def id = get[0] response[?(@.name == 'tagName1')].id
        * def tagRequirements = get[0] response[?(@.name == 'tagName1')].requirements
        * def childTagsList = get[0] response[?(@.name == 'tagName1')].childTags[?(@.name == 'child tag1')]

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"tagName1",
                "status":"Show on website",
                "urlPath":"urlPath",
                "content":"Content text"
        }
        """

        * match tagRequirements[*].type contains ["Course"]
        * match tagRequirements[*].mandatory == [false]
        * match tagRequirements[*].limitToOneTag == [true]

        * match childTagsList contains
        """
        {
            "name":"child tag1",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Content text"
        }
        """
#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (+) Add new valid 'tag group' with different values for 'Name' and 'URL path'

#       >>> Add a new entity by admin:
        * def newTagGroup =
        """
        {
            "name":"tagName2",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath1",
            "content":"Content text",
            "weight":1,
            "requirements":
                [
                {"type":"Application","mandatory":false,"limitToOneTag":false,"system":false},
                {"type":"Enrolment","mandatory":false,"limitToOneTag":false,"system":false},
                {"type":"Assessment","mandatory":true,"limitToOneTag":true,"system":false},
                {"type":"WaitingList","mandatory":true,"limitToOneTag":true,"system":false},
                {"type":"Room","mandatory":true,"limitToOneTag":true,"system":false},
                {"type":"Document","mandatory":false,"limitToOneTag":false,"system":false},
                {"type":"Contact","mandatory":false,"limitToOneTag":true,"system":false},
                {"type":"Site","mandatory":false,"limitToOneTag":false,"system":false},
                {"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}
                ],
           "childTags":
                [{
                    "name":"child tag2",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath2",
                    "content":"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis accumsan cursus ligula, vitae efficitur ante vulputate sit amet. Pellentesque pharetra orci magna, nec sodales metus ornare at. Mauris sed laoreet ex. Cras commodo condimentum purus, ut venenatis orci accumsan sit amet. Pellentesque posuere velit odio, ut fringilla quam rhoncus sed. Fusce facilisis vel elit a vestibulum. Proin consectetur at nisi sed aliquet. Mauris vulputate blandit nunc. Duis nulla dolor, tincidunt nec facilisis id, gravida eu nisl. Proin congue sem ac ultrices faucibus. Mauris mi nulla, ullamcorper quis ante sit amet, vehicula porttitor lorem.",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag3",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath3",
                    "content":"Maecenas lacinia auctor mauris eget sodales. Ut in eros mollis, viverra urna id, elementum eros. Etiam consectetur molestie arcu, sed ornare mauris hendrerit sit amet. Aenean sollicitudin ut urna in interdum. Donec quam enim, sodales sit amet viverra at, dapibus vitae nunc. Suspendisse potenti. Morbi iaculis tempor nunc.",
                    "weight":2,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName2"

        * def tagGroup = get[0] response[?(@.name == 'tagName2')]
        * def id = get[0] response[?(@.name == 'tagName2')].id
        * def tagRequirements = get[0] response[?(@.name == 'tagName2')].requirements
        * def childTagsList1 = get[0] response[?(@.name == 'tagName2')].childTags[?(@.name == 'child tag2')]
        * def childTagsList2 = get[0] response[?(@.name == 'tagName2')].childTags[?(@.name == 'child tag3')]

        * match tagGroup contains
         """
         {   "name":"tagName2",
             "status":"Show on website",
             "urlPath":"urlPath1",
             "content":"Content text"
         }
         """

        * match tagRequirements[*].type contains ["Assessment","Application","WaitingList","Contact","Enrolment","Room","Site","Payslip","Document"]

        * match childTagsList1 contains
        """
        {
            "name":"child tag2",
            "status":"Show on website",
            "urlPath":"urlPath2",
            "content":"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis accumsan cursus ligula, vitae efficitur ante vulputate sit amet. Pellentesque pharetra orci magna, nec sodales metus ornare at. Mauris sed laoreet ex. Cras commodo condimentum purus, ut venenatis orci accumsan sit amet. Pellentesque posuere velit odio, ut fringilla quam rhoncus sed. Fusce facilisis vel elit a vestibulum. Proin consectetur at nisi sed aliquet. Mauris vulputate blandit nunc. Duis nulla dolor, tincidunt nec facilisis id, gravida eu nisl. Proin congue sem ac ultrices faucibus. Mauris mi nulla, ullamcorper quis ante sit amet, vehicula porttitor lorem."
        }
        """

        * match childTagsList2 contains
        """
        {
            "name":"child tag3",
            "status":"Show on website",
            "urlPath":"urlPath3",
            "content":"Maecenas lacinia auctor mauris eget sodales. Ut in eros mollis, viverra urna id, elementum eros. Etiam consectetur molestie arcu, sed ornare mauris hendrerit sit amet. Aenean sollicitudin ut urna in interdum. Donec quam enim, sodales sit amet viverra at, dapibus vitae nunc. Suspendisse potenti. Morbi iaculis tempor nunc."
        }
        """
#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (+) Add new valid tag group with 'Name' of 512 symbols

#       >>> Add a new entity by admin:
        * def newTagGroup =
        """
        {
            "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5')]
        * def id = get[0] response[?(@.name == 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5')].id

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5",
                "status":"Private",
                "system":false,
                "urlPath":"urlPath",
                "content":"Content text"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (+) Add new valid tag group with 'URL Path' of 128 symbols

#       >>> Add a new entity by admin:
        * def newTagGroup =
        """
        {
            "name":"tagName4",
            "status":"Show on website",
            "system":false,
            "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A1",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'tagName4')]
        * def id = get[0] response[?(@.name == 'tagName4')].id

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"tagName4",
                "status":"Show on website",
                "system":false,
                "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A1",
                "content":"Content text"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (+) Add a few valid child tags in predefined order

#       >>> Add a new entity by admin:
        * def newTagGroup =
        """
        {
            "name":"tagName50",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath1",
            "content":"Content text",
            "weight":1,
            "requirements":
                [
                {"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}
                ],
           "childTags":
                [{
                    "name":"child tag4",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath4",
                    "content":"text",
                    "weight":4,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag2",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath2",
                    "content":"text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag5",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath5",
                    "content":"text",
                    "weight":3,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"text",
                    "weight":2,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag3",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath3",
                    "content":"text",
                    "weight":5,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Order assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName50"

        * def id = get[0] response[?(@.name == 'tagName50')].id
        * def childTagsList = get[0] response[?(@.name == 'tagName50')].childTags

        * match childTagsList[*].name == ["child tag2","child tag1","child tag5","child tag4","child tag3"]

#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (+) Add new valid tag group by notadmin with permissions: Hide-View-Print-Edit-Create-Delete

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'tagName1')]
        * def id = get[0] response[?(@.name == 'tagName1')].id
        * def tagRequirements = get[0] response[?(@.name == 'tagName1')].requirements
        * def childTagsList = get[0] response[?(@.name == 'tagName1')].childTags[?(@.name == 'child tag1')]

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"tagName1",
                "status":"Show on website",
                "urlPath":"urlPath",
                "content":"Content text"
        }
        """

        * match tagRequirements[*].type contains ["Course"]
        * match tagRequirements[*].mandatory == [false]
        * match tagRequirements[*].limitToOneTag == [true]

        * match childTagsList contains
        """
        {
            "name":"child tag1",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Content text"
        }
        """

#       <---->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Add new valid tag group by notadmin with permissions: Hide-View-Print-Edit-Create

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'tagName1')]
        * def id = get[0] response[?(@.name == 'tagName1')].id
        * def tagRequirements = get[0] response[?(@.name == 'tagName1')].requirements
        * def childTagsList = get[0] response[?(@.name == 'tagName1')].childTags[?(@.name == 'child tag1')]

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"tagName1",
                "status":"Show on website",
                "urlPath":"urlPath",
                "content":"Content text"
        }
        """

        * match tagRequirements[*].type contains ["Course"]
        * match tagRequirements[*].mandatory == [false]
        * match tagRequirements[*].limitToOneTag == [true]

        * match childTagsList contains
        """
        {
            "name":"child tag1",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Content text"
        }
        """

#       <---->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Add new valid tag group by notadmin with permissions: Hide-View-Print-Edit

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 403



    Scenario: (-) Add new valid tag group by notadmin with permissions: Hide-View-Print

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 403



    Scenario: (-) Add new valid tag group by notadmin with permissions: Hide-View

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 403



    Scenario: (-) Add new valid tag group by notadmin with permissions: Hide

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       >>> Add a new entity by notadmin:
        * def newTagGroup =
        """
        {
            "name":"tagName1",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Content text",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath",
                    "content":"Content text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 403



    Scenario: (-) Add new invalid empty tag group

        * def newTagGroup = {}

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "Name should be set."



    Scenario: (-) Add new invalid root tag without 'Name'

        * def newTagGroup =
        """
        {
            "name":"",
            "status":"Show on website",
            "system":false,
            "urlPath":"someUrlPath",
            "content":"someContent",
            "weight":1,
            "requirements":
            [{
                "type":"Course",
                "mandatory":false,
                "limitToOneTag":true,
                "system":false
            }],
            "childTags":
            [{
                "name":"child tag1",
                "status":"Show on website",
                "system":false,
                "urlPath":"someUrlPath",
                "content":"someContent",
                "weight":1,
                "requirements":[],
                "childTags":[]
            }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "Name should be set."



    Scenario: (-) Add new invalid child tag without 'Name'

        * def newTagGroup =
        """
        {
            "name":"anyName",
            "status":"Show on website",
            "system":false,
            "urlPath":"someUrlPath",
            "content":"someContent",
            "weight":1,
            "requirements":
            [{
                "type":"Course",
                "mandatory":false,
                "limitToOneTag":true,
                "system":false
            }],
            "childTags":
            [{
                "name":"",
                "status":"Show on website",
                "system":false,
                "urlPath":"someUrlPath",
                "content":"someContent",
                "weight":1,
                "requirements":[],
                "childTags":[]
            }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "Name should be set."



    Scenario: (-) Add new invalid tag group without 'requirements'

        * def newTagGroup =
        """
        {
            "name":"tagName10",
            "status":"Show on website",
            "system":false,
            "urlPath":"someUrlPath",
            "content":"someContent",
            "weight":1,
            "requirements":[],
            "childTags":
            [{
                "name":"child tag1",
                "status":"Show on website",
                "system":false,
                "urlPath":"someUrlPath",
                "content":"someContent",
                "weight":1,
                "requirements":[],
                "childTags":[]
            }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "At least one requirement should be set for root tag."



    Scenario: (-) Add new invalid tag group with not existing 'requirements' type

        * def newTagGroup =
        """
        {
            "name":"tagName10",
            "status":"Show on website",
            "system":false,
            "urlPath":"someUrlPath",
            "content":"someContent",
            "weight":1,
            "requirements":
            [{
                "type":"notExisting",
                "mandatory":false,
                "limitToOneTag":true,
                "system":false
            }],
            "childTags":
            [{
                "name":"child tag1",
                "status":"Show on website",
                "system":false,
                "urlPath":"someUrlPath",
                "content":"someContent",
                "weight":1,
                "requirements":[],
                "childTags":[]
            }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "Invalid requirement type."



    Scenario: (-) Add new invalid tag group with existing name

        * def newTagGroup =
        """
        {
            "name":"Assessment method",
            "status":"Show on website",
            "system":false,
            "urlPath":"someUrlPath",
            "content":"someContent",
            "weight":1,
            "requirements":
                [{
                    "type":"Course",
                    "mandatory":false,
                    "limitToOneTag":true,
                    "system":false
                }],
            "childTags":
                [{
                    "name":"child tag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"someUrlPath",
                    "content":"someContent",
                    "weight":1,
                    "requirements":[],"childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "Name should be unique."



    Scenario: (-) Add new invalid child tag with existing name

        * def newTagGroup =
        """
        {   "name":"tagName12",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath1",
            "content":"someContent",
            "weight":1,
            "requirements":
                [
                {"type":"Site","mandatory":false,"limitToOneTag":false,"system":false},
                {"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}
                ],
           "childTags":
                [{
                    "name":"child tag20",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"someUrlPath",
                    "content":"text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"child tag20",
                    "status":"Show on website",
                    "system":false,"urlPath":"urlPath3",
                    "content":"text2",
                    "weight":2,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "The tag name is not unique within its parent tag."



    Scenario: (-) Add new invalid tag group with 'Name' length >512 symbols

        * def newTagGroup =
        """
        {   "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A51",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath1",
            "content":"someContent",
            "weight":1,
            "requirements":
                [
                {"type":"Payslips","mandatory":true,"limitToOneTag":true,"system":false}
                ],
           "childTags":
                [{
                    "name":"child tag22",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath2",
                    "content":"text",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 512."



    Scenario: (-) Add new invalid tag group with 'URL Path' length >128 symbols

        * def newTagGroup =
        """
        {   "name":"tagName30",
            "status":"Show on website",
            "system":false,
            "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A12",
            "content":"someContent",
            "weight":1,
            "requirements":
                [
                {"type":"Payslips","mandatory":true,"limitToOneTag":true,"system":false}
                ],
           "childTags":
                [{
                    "name":"child tag22",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath2",
                    "content":"someContent",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 128."


