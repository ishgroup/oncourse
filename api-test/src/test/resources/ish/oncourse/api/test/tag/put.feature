@parallel=false
Feature: Main feature for all PUT requests with path 'tag'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'tag'
        * def ishPathLogin = 'login'
        

        * def newTagGroupCreatedForNotadmin = {"name":"tagName500","status":"Show on website","urlPath":"urlPath","content":"Any text","weight":1,"requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],"childTags":[{"name":"childTag1","status":"Show on website","urlPath":"urlPath1","content":"Any text1","weight":1,},{"name":"childTag2","status":"Show on website","urlPath":"urlPath2","content":"Any text2","weight":2,}]}
        * def newTagGroupUpdatedByNotadmin = {"name":"tagName501","status":"Show on website","urlPath":"urlPath","content":"Any text","weight":1,"requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],"childTags":[{"name":"childTag1","status":"Show on website","urlPath":"urlPath1","content":"Any text1","weight":1,},{"name":"childTag2","status":"Show on website","urlPath":"urlPath2","content":"Any text2","weight":2,}]}



    Scenario: (+) Update existing tag group by admin

#       >>>  Add a new entity to update it:
        * def newTagGroup =
        """
        {
            "name":"tagName100",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                },
                {
                    "name":"childTag2",
                    "status":"Show on website",
                    "urlPath":"urlPath2",
                    "content":"Any text2",
                    "weight":2,
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

#       >>> Update entity:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName100"
#       <--->

        * def id = get[0] response[?(@.name == 'tagName100')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName100')].childTags[?(@.name == 'childTag1')].id
        * def chuldTagId2 = get[0] response[?(@.name == 'tagName100')].childTags[?(@.name == 'childTag2')].id

        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName100_UPD",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath_UPD",
            "content":"Any text_UPD",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1_UPD",
                    "status":"Private",
                    "system":false,
                    "urlPath":"urlPath1_UPD",
                    "content":"Any text1_UPD",
                    "weight":2,
                    "requirements":[],
                    "childTags":[]
                },
                {   "id":"#(chuldTagId2)",
                    "name":"childTag2_UPD",
                    "status":"Private",
                    "system":false,
                    "urlPath":"urlPath2_UPD",
                    "content":"Any text2_UPD",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                },
                {
                    "name":"childTag3_UPD",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath3_UPD",
                    "content":"Any text3_UPD",
                    "weight":3,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName100_UPD"

        * def tagGroup = get[0] response[?(@.name == 'tagName100_UPD')]
        * def tagRequirements = get[0] response[?(@.name == 'tagName100_UPD')].requirements
        * def childTagsList = get[0] response[?(@.name == 'tagName100_UPD')].childTags
        * def childTagsList1 = get[0] response[?(@.name == 'tagName100_UPD')].childTags[?(@.name == 'childTag1_UPD')]
        * def childTagsList2 = get[0] response[?(@.name == 'tagName100_UPD')].childTags[?(@.name == 'childTag2_UPD')]
        * def childTagsList3 = get[0] response[?(@.name == 'tagName100_UPD')].childTags[?(@.name == 'childTag3_UPD')]

#       >>> Check updated root tag:
        * match tagGroup contains
        """
        {
            "name":"tagName100_UPD",
            "status":"Private",
            "urlPath":"urlPath_UPD",
            "content":"Any text_UPD"
        }
        """

#       >>> Check updated requirements:
        * match tagRequirements[*].type contains ["Site"]

#       >>> Check updated child tags order:
        * match childTagsList[*].name == ["childTag2_UPD","childTag1_UPD","childTag3_UPD"]

#       >>> Check updated child tags:
        * match childTagsList1 contains
        """
        {
            "name":"childTag1_UPD",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath1_UPD",
            "content":"Any text1_UPD"
        }
        """

        * match childTagsList2 contains
        """
        {
            "name":"childTag2_UPD",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath2_UPD",
            "content":"Any text2_UPD"
        }
        """

        * match childTagsList3 contains
        """
        {
            "name":"childTag3_UPD",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath3_UPD",
            "content":"Any text3_UPD"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Update root tag 'Name' to 512 symbols

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName102",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName102"

        * def id = get[0] response[?(@.name == 'tagName102')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName102')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath_UPD",
            "content":"Any text_UPD",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1_UPD",
                    "status":"Private",
                    "system":false,
                    "urlPath":"urlPath1_UPD",
                    "content":"Any text1_UPD",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5')]

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A5"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Update 'URL Path' to 128 symbols

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName103",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName103"

        * def id = get[0] response[?(@.name == 'tagName103')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName103')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName103",
            "status":"Show on website",
            "system":false,
            "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A1",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def tagGroup = get[0] response[?(@.name == 'tagName103')]

        * match tagGroup contains
        """
        {       "id":"#(id)",
                "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A1"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Update existing tag group by notadmin with permissions: Hide-View-Print-Edit-Create-Delete

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        

#       >>>  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains "tagName500"
        And match response[*].name contains "tagName501"

#       <---->  Scenario have been finished. Now change back permissions and remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Update existing tag group by notadmin with permissions: Hide-View-Print-Edit-Create

#       <---->  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains "tagName500"
        And match response[*].name contains "tagName501"

#       <---->  Scenario have been finished. Now remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (+) Update existing tag group by notadmin with permissions: Hide-View-Print-Edit

#       <-->  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 204

#       >>> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains "tagName500"
        And match response[*].name contains "tagName501"

#       <---->  Scenario have been finished. Now change back permissions and remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update existing tag group by notadmin with permissions: Hide-View-Print

#       <-->  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 403

#       <---->  Scenario have been finished. Now change back permissions and remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update existing tag group by notadmin with permissions: Hide-View

#       <-->  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 403

#       <---->  Scenario have been finished. Now change back permissions and remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update existing tag group by notadmin with permissions: Hide

#       <-->  Add a new entity to update it and get id:
        Given path ishPath
        And request newTagGroupCreatedForNotadmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName500"

        * def id = get[0] response[?(@.name == 'tagName500')].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        

#       >>> Update entity:
        Given path ishPath + '/' + id
        And request newTagGroupUpdatedByNotadmin
        When method PUT
        Then status 403

#       <---->  Scenario have been finished. Now change back permissions and remove created object from DB
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update tag group to empty

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName104",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
            [{
                "name":"childTag1",
                "status":"Show on website",
                "urlPath":"urlPath1",
                "content":"Any text1",
                "weight":1
            }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName104"

        * def id = get[0] response[?(@.name == 'tagName104')].id

#       >>> Update entity:
        * def tagToUpdate = {"id":"#(id)"}

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Name should be set."

#       <----->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <----->



    Scenario: (-) Update root tag 'Name' to empty

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName105",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName105"

        * def id = get[0] response[?(@.name == 'tagName105')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName105')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath2",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Name should be set."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update child tag 'Name' to empty

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName106",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName106"

        * def id = get[0] response[?(@.name == 'tagName106')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName106')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName106",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath2",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Name should be set."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update root tag 'Name' to existing value

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName108",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName108"

        * def id = get[0] response[?(@.name == 'tagName108')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName108')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"Assessment method",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath2",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Name should be unique."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update child tag 'Name' to existing value

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName109",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                 },
                 {
                    "name":"childTag2",
                    "status":"Show on website",
                    "urlPath":"urlPath2",
                    "content":"Any text2",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName109"

        * def id = get[0] response[?(@.name == 'tagName109')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName109')].childTags[?(@.name == 'childTag1')].id
        * def chuldTagId2 = get[0] response[?(@.name == 'tagName109')].childTags[?(@.name == 'childTag2')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName109",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath2",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                },
                {   "id":"#(chuldTagId2)",
                        "name":"childTag1",
                        "status":"Show on website",
                        "system":false,
                        "urlPath":"urlPath2",
                        "content":"Any text2",
                        "weight":2,
                        "requirements":[],
                        "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "The tag name is not unique within its parent tag."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update root tag 'Name' to >512 symbols

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName110",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName110"

        * def id = get[0] response[?(@.name == 'tagName110')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName110')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A51",
            "status":"Private",
            "system":false,
            "urlPath":"urlPath_UPD",
            "content":"Any text_UPD",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1_UPD",
                    "status":"Private",
                    "system":false,
                    "urlPath":"urlPath1_UPD",
                    "content":"Any text1_UPD",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "The maximum length is 512."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update 'URL Path' to >128 symbols

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName111",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName111"

        * def id = get[0] response[?(@.name == 'tagName111')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName111')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName111",
            "status":"Private",
            "system":false,
            "urlPath":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A12",
            "content":"Any text_UPD",
            "weight":1,
            "requirements":[{"type":"Site","mandatory":false,"limitToOneTag":false,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1_UPD",
                    "status":"Private",
                    "system":false,
                    "urlPath":"urlPath1_UPD",
                    "content":"Any text1_UPD",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "The maximum length is 128."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update requirements to empty

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName106",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName106"

        * def id = get[0] response[?(@.name == 'tagName106')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName106')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName106",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath2",
            "content":"Any text",
            "weight":1,
            "requirements":[],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "At least one requirement should be set for root tag."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update requirements to not existing type

#       >>>  Add a new entity to update it and get id:
        * def newTagGroup =
        """
        {
            "name":"tagName107",
            "status":"Show on website",
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"Payslip","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{
                    "name":"childTag1",
                    "status":"Show on website",
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1
                }]
        }
        """

        Given path ishPath
        And request newTagGroup
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains "tagName107"

        * def id = get[0] response[?(@.name == 'tagName107')].id
        * def chuldTagId1 = get[0] response[?(@.name == 'tagName107')].childTags[?(@.name == 'childTag1')].id

#       >>> Update entity:
        * def tagToUpdate =
        """
        {   "id":"#(id)",
            "name":"tagName107",
            "status":"Show on website",
            "system":false,
            "urlPath":"urlPath",
            "content":"Any text",
            "weight":1,
            "requirements":[{"type":"notExisting","mandatory":true,"limitToOneTag":true,"system":false}],
            "childTags":
                [{  "id":"#(chuldTagId1)",
                    "name":"childTag1",
                    "status":"Show on website",
                    "system":false,
                    "urlPath":"urlPath1",
                    "content":"Any text1",
                    "weight":1,
                    "requirements":[],
                    "childTags":[]
                }]
        }
        """

        Given path ishPath + '/' + id
        And request tagToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Invalid requirement type."

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


