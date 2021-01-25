@parallel=false
Feature: Main feature for all GET requests with path 'preference/'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference'
        * def ishPathLogin = 'login'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get Security settings by admin

        Given path ishPath
        And param search = 'security.auto.disable.inactive.account'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.auto.disable.inactive.account'
        And match response[0].valueString == 'true'

        Given path ishPath
        And param search = 'security.password.complexity'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.complexity'
        And match response[0].valueString == 'false'

        Given path ishPath
        And param search = 'security.password.expiry.period'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.expiry.period'
        And match response[0].valueString == null

        Given path ishPath
        And param search = 'security.tfa.expiry.period'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.tfa.expiry.period'
        And match response[0].valueString == '16'

        Given path ishPath
        And param search = 'security.tfa.status'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.tfa.status'
        And match response[0].valueString == 'disabled'



    Scenario: (+) Get Security settings by notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPath
        And param search = 'security.auto.disable.inactive.account'
        When method GET
        Then status 403

        Given path ishPath
        And param search = 'security.password.complexity'
        When method GET
        Then status 403

        Given path ishPath
        And param search = 'security.password.expiry.period'
        When method GET
        Then status 403

        Given path ishPath
        And param search = 'security.tfa.expiry.period'
        When method GET
        Then status 403

        Given path ishPath
        And param search = 'security.tfa.status'
        When method GET
        Then status 403



    Scenario: (-) Get password value

        * table passwordPreferences
            | name                  | code                  |
            | 'LDAP_BIND_USER_PASS' | 'ldap.bind.user.pass' |
            | 'AUSKEY_PASSWORD'     | 'auskey.password'     |
            | 'EMAIL_POP3PASSWORD'  | 'email.pop3.password' |
            | 'AUSKEY_CERTIFICATE'  | 'auskey.certificate'  |
            | 'AUSKEY_PRIVATE_KEY'  | 'auskey.privatekey'   |
            | 'AUSKEY_SALT'         | 'auskey.salt'         |

         * call read('getPassword.feature') passwordPreferences





