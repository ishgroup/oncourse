@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/document'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/document'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Upload document by admin

#       <-----> Upload private document:
        * def someStream = read('testDoc2.txt')

        Given path ishPath
        And params {"name":"testDoc2","description":"some description","shared":true,"access":"Private","tags":"216","fileName":"testDoc2.txt"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def privateDocId = get[0] response.rows[?(@.values == ["testDoc2"])].id

        Given path ishPath + '/' + privateDocId
        When method GET
        Then status 200
        And match $.name == "testDoc2"
        And match $.tags[*].id == [216]
        And match $.description == "some description"
        And match $.access == "Private"
        And match $.shared == true
        And match $.versions[*].id == [203]

#       <-----> Upload public document:
        * def someStream = read('testDoc3.zip')

        Given path ishPath
        And params {"name":"testDoc3","description":"some description","shared":true,"access":"Public","tags":[],"fileName":"testDoc3.zip"}
        And header Content-Type = 'application/x-www-form-urlencoded'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def publicDocId = get[0] response.rows[?(@.values == ["testDoc3"])].id

        Given path ishPath + '/' + publicDocId
        When method GET
        Then status 200
        And match $.name == "testDoc3"
        And match $.description == "some description"
        And match $.access == "Public"
        And match $.shared == true



    Scenario: (+) Upload private document by notadmin with rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        

#       <-----> Upload private document:
        * def someStream = read('testDoc4.png')

        Given path ishPath
        And params {"name":"testDoc4","description":"some description","shared":true,"access":"Private","tags":[],"fileName":"testDoc4.png"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def privateDocId = get[0] response.rows[?(@.values == ["testDoc4"])].id

        Given path ishPath + '/' + privateDocId
        When method GET
        Then status 200
        And match $.name == "testDoc4"
        And match $.description == "some description"
        And match $.access == "Private"
        And match $.shared == true



    Scenario: (+) Upload public document by notadmin with rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        

#       <-----> Upload private document:
        * def someStream = read('testDoc5.csv')

        Given path ishPath
        And params {"name":"testDoc5","description":"some description","shared":true,"access":"Public","tags":[],"fileName":"testDoc5.csv"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200

        Given path ishPathPlain
        And param entity = 'Document'
        And param columns = 'name'
        When method GET
        Then status 200

        * def publicDocId = get[0] response.rows[?(@.values == ["testDoc5"])].id

        Given path ishPath + '/' + publicDocId
        When method GET
        Then status 200
        And match $.name == "testDoc5"
        And match $.description == "some description"
        And match $.access == "Public"
        And match $.shared == true



    Scenario: (-) Upload public document by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        

#       <-----> Upload private document:
        * def someStream = read('testDoc6.xml')

        Given path ishPath
        And params {"name":"testDoc6","description":"some description","shared":true,"access":"Public","tags":[],"fileName":"testDoc6.xml"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create Document. Please contact your administrator"



    Scenario: (-) Upload private document by notadmin without rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        

#       <-----> Upload private document:
        * def someStream = read('testDoc4.png')

        Given path ishPath
        And params {"name":"testDoc4","description":"some description","shared":true,"access":"Private","tags":[],"fileName":"testDoc4.png"}
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create Document. Please contact your administrator"
