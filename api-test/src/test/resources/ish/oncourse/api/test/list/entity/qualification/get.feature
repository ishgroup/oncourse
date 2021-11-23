@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/qualification'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/qualification'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get list of all Qualifications by admin

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $ contains
        """
            {
                "entity":"Qualification",
                "search":null,
                "pageSize":4,
                "offset":0,
                "sort":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                        {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]}
                    ],
                "rows":
                    [
                        {"id":"3","values":["10218NAT","Aboriginal Language/s v2","Certificate I in",null,"true"]},
                        {"id":"2","values":["21364VIC","Workforce Re-entry Skills","Course in","0","false"]},
                        {"id":"4","values":["90946NSW","Building Studies, Technology","Advanced Diploma of","0","true"]},
                        {"id":"1","values":["UEE30807","Electrotechnology Electrician","Certificate III in",null,"false"]}
                    ],
                "filteredCount":null,
                "layout":"Three column",
                "filterColumnWidth":200,
                "tagsOrder": [],
            }
        """



    Scenario: (+) Get list of all Qualifications by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $ contains
        """
            {
                "entity":"Qualification",
                "search":null,
                "pageSize":4,
                "offset":0,
                "sort":[{"attribute":"nationalCode","ascending":true,"complexAttribute":[]}],
                "columns":
                    [
                        {"title":"Code","attribute":"nationalCode","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Title","attribute":"title","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Level","attribute":"level","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Hours","attribute":"nominalHours","type":null,"sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]},
                        {"title":"Offered","attribute":"isOffered","type":"Boolean","sortable":true,"visible":true,"system":null,"width":100,"sortFields":[],"prefetches":[]}
                    ],
                "rows":
                    [
                        {"id":"3","values":["10218NAT","Aboriginal Language/s v2","Certificate I in",null,"true"]},
                        {"id":"2","values":["21364VIC","Workforce Re-entry Skills","Course in","0","false"]},
                        {"id":"4","values":["90946NSW","Building Studies, Technology","Advanced Diploma of","0","true"]},
                        {"id":"1","values":["UEE30807","Electrotechnology Electrician","Certificate III in",null,"false"]}
                    ],
                "filteredCount":null,
                "layout":"Three column",
                "filterColumnWidth":200,
                "tagsOrder": [],
            }
        """



    Scenario: (+) Get qualifications by admin

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
                {
                    "anzsco":"341111",
                    "fieldOfEducation":"0313",
                    "id":1,
                    "isCustom":false,
                    "isOffered":false,
                    "nationalCode":"UEE30807",
                    "newApprenticeship":null,
                    "nominalHours":null,
                    "qualLevel":"Certificate III in",
                    "reviewDate":null,
                    "specialization":null,
                    "title":"Electrotechnology Electrician"
                }
            """



    Scenario: (+) Get qualifications by notadmin user

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
                {
                    "anzsco":"341111",
                    "fieldOfEducation":"0313",
                    "id":1,
                    "isCustom":false,
                    "isOffered":false,
                    "nationalCode":"UEE30807",
                    "newApprenticeship":null,
                    "nominalHours":null,
                    "qualLevel":"Certificate III in",
                    "reviewDate":null,
                    "specialization":null,
                    "title":"Electrotechnology Electrician"
                }
            """