@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/module'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/module'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get list of all Modules by admin

        Given path ishPathList
        And param entity = 'Module'
        And request {}
        When method POST
        Then status 200
        And match $ contains
        """
            {
                "entity":"Module",
                "search":null,
                "pageSize":6,
                "offset":0,
                "sort":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                        {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100, "sortFields":[], "prefetches":[]},
                        {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100, "sortFields":[], "prefetches":[]},
                        {"title":"Is offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"system":null,"width":100, "sortFields":[], "prefetches":[]},
                        {"title":"Credit points","attribute":"creditPoints","type":null,"sortable":true,"visible":false,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Expiry days","attribute":"expiryDays","type":null,"sortable":true,"visible":false,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                    ],
                "rows":
                    [
                        {"id":"3","values":["AUM1001A","Manage personal career goals","true"]},
                        {"id":"4","values":["AUM1002A","Select and use tools and equipment in an automotive manufacturing environment","true"]},
                        {"id":"2","values":["AUM1503A","Create new product designs","true"]},
                        {"id":"1","values":["AUM1602A","Install plant, equipment or systems - Advanced","true"]},
                        {"id":"6","values":["VU21318","Identify community options","false"]},
                        {"id":"5","values":["VU21349","Identify the Australian electoral system","false"]}
                    ],
                "filteredCount":6,
                "layout":"Three column",
                "filterColumnWidth":200,
                "tagsOrder":[]
            }

        """



    Scenario: (+) Get list of all Modules by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Module'
        And request {}
        When method POST
        Then status 200
        And match $ contains
        """
            {
                "entity":"Module",
                "search":null,
                "pageSize":6,
                "offset":0,
                "sort":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                        {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Is offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Credit points","attribute":"creditPoints","type":null,"sortable":true,"visible":false,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Expiry days","attribute":"expiryDays","type":null,"sortable":true,"visible":false,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                    ],
                "rows":
                    [
                        {"id":"3","values":["AUM1001A","Manage personal career goals","true"]},
                        {"id":"4","values":["AUM1002A","Select and use tools and equipment in an automotive manufacturing environment","true"]},
                        {"id":"2","values":["AUM1503A","Create new product designs","true"]},
                        {"id":"1","values":["AUM1602A","Install plant, equipment or systems - Advanced","true"]},
                        {"id":"6","values":["VU21318","Identify community options","false"]},
                        {"id":"5","values":["VU21349","Identify the Australian electoral system","false"]}
                    ],
                "filteredCount":6,
                "layout":"Three column",
                "filterColumnWidth":200,
                "tagsOrder":[]
            }

        """


    Scenario: (+) Get module by admin

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
            {
                "creditPoints":null,
                "creditPointsStatus":"Active",
                "expiryDays":null,
                "fieldOfEducation":"030701",
                "id":1,
                "isCustom":false,
                "isOffered":true,
                "nationalCode":"AUM1602A"
                ,"nominalHours":22.0,
                "specialization":null,
                "title":"Install plant, equipment or systems - Advanced"
            }
            """



    Scenario: (+) Get module by notadmin user

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
            {
                "creditPoints":null,
                "creditPointsStatus":"Active",
                "expiryDays":null,
                "fieldOfEducation":"030701",
                "id":1,
                "isCustom":false,
                "isOffered":true,
                "nationalCode":"AUM1602A"
                ,"nominalHours":22.0,
                "specialization":null,
                "title":"Install plant, equipment or systems - Advanced"
            }
            """