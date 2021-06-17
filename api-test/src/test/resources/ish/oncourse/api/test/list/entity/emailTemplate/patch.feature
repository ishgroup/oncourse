@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/emailTemplateToUpdate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplateToUpdate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update system EmailTemplate by admin

        Given path ishPathList
        And param entity = 'EmailTemplate'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['plain'])].id
        * print "id = " + id
