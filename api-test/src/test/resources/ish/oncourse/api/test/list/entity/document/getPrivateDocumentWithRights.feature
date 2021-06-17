@ignore
@parallel=false
Feature: Re-usable feature to get Document with access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        * def userWithPermission = user

#       <--->  Login as notadmin
        * configure headers = { Authorization:  '#(user)'}

        
#       <--->

        Given path ishPath + "/200"
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "id":200,
        "name":"defaultPrivateDocument",
        "versionId":null,
        "added":"#ignore",
        "tags":[],
        "thumbnail":null,
        "versions":
            [{
            "id":200,
            "added":"#ignore",
            "createdBy":"onCourse Administrator",
            "fileName":"defaultPrivateDocument.txt",
            "mimeType":"text/plain",
            "size":"22 b",
            "url":null,
            "thumbnail":null
            }],
        "description":"Private description",
        "access":"Private",
        "shared":true
        }
        """

