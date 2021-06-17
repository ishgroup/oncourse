@ignore
@parallel=false
Feature: Re-usable feature to get Document with access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        * def userWithPermission = user

#       <--->  Login as notadmin
        * configure headers = { Authorization:  '#(user)'}

        
#       <--->

        Given path ishPath + "/201"
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":201,
        "name":"defaultPublicDocument",
        "versionId":null,
        "added":"#ignore",
        "tags":[],
        "thumbnail":null,
        "versions":
            [{
            "id":201,
            "added":"#ignore",
            "createdBy":"onCourse Administrator",
            "fileName":"defaultPublicDocument.txt",
            "mimeType":"text/plain",
            "size":"21 b",
            "url":null,
            "thumbnail":null
            }],
        "description":"Public description",
        "access":"Public",
        "shared":true
        }
        """

