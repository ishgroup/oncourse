@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/document'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/document'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get existing document by admin

#        <---> Get existing Private document:
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
        "shared":true,
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#        <---> Get existing Public document:
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
        "shared":true,
        "created":"#ignore",
        "modified":"#ignore"
        }
        """



    Scenario: (+) Get existing public document by notadmin with access rights

        * table userWithRole

        | user                   |
        | 'UserWithRightsHide'   |
        | 'UserWithRightsView'   |
        | 'UserWithRightsPrint'  |
        | 'UserWithRightsEdit'   |
        | 'UserWithRightsCreate' |
        | 'UserWithRightsDelete' |

        * call read('getPublicDocumentWithRights.feature') userWithRole



    Scenario: (+) Get existing private document by notadmin with access rights

        * table userWithRole

        | user                   |
        | 'UserWithRightsHide'   |
        | 'UserWithRightsView'   |
        | 'UserWithRightsPrint'  |
        | 'UserWithRightsEdit'   |
        | 'UserWithRightsCreate' |
        | 'UserWithRightsDelete' |

        * call read('getPrivateDocumentWithRights.feature') userWithRole



#    Scenario: (-) Get existing private document by notadmin without access rights
#
#        * table userWithRole
#
#        | user                   |
#        | 'UserWithRightsHide'   |
#
#        * call read('getPrivateDocumentWithoutRights.feature') userWithRole




    Scenario: (-) Get not existing document by admin

        Given path ishPath + "/999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '999' doesn't exist."
