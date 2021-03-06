@ignore
@parallel=false
Feature: Sign in as Admin

    Background: Authorize first
        * url 'https://127.0.0.1:8182'
        * configure headers = null
        * configure ssl = true
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'

        
    Scenario: logout any current user
        Given path 'a/v1/logout'
        And request {}
        When method PUT

    Scenario: Authorize as admin
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path 'a/v1/login'
        And request loginBody
        When method PUT
        Then status 200
