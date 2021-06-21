@parallel=false
Feature: Main feature for all GET requests with path 'preference/'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference'
        * def ishPathLogin = 'login'




    Scenario: (+) Get Security settings by admin

        Given path ishPath
        And param search = 'security.auto.disable.inactive.account'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.auto.disable.inactive.account'
        And match response[0].valueString == 'true'

        Given path ishPath
        And param search = 'security.number.login.attempts'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.number.login.attempts'
        And match response[0].valueString == '5'

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
        * configure headers = { Authorization:  'UserWithRightsDelete'}


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



    Scenario: (-) Admin should not be able to get password fields

        * configure headers = { Authorization: 'admin' }

        * table passwordPreferences
            | name                  | code                  |
            | 'LDAP_BIND_USER_PASS' | 'ldap.bind.user.pass' |
            | 'EMAIL_POP3PASSWORD'  | 'email.pop3.password' |

        * call read('getPassword.feature') passwordPreferences





