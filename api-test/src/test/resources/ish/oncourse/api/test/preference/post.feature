@parallel=false
Feature: Main feature for all POST requests with path 'preference/'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference'


    Scenario: (-) Edit 'readOnly' preferences

        * table readOnlyPreferences
            | name                       | code                      | value  |
            | 'LICENSE_ACCESS_CONTROL'   | 'license.access_control'  | 'true' |
            | 'LICENSE_SMS'              | 'license.sms'             | 'true' |
            | 'LICENSE_SCRIPTING'        | 'license.scripting'       | 'true' |




    Scenario: (+) Change "Automatically disable inactive accounts"

        Given path ishPath
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.auto.disable.inactive.account'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.auto.disable.inactive.account'
        And match response[0].valueString == 'false'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change "Number of allowed login attempts"

        Given path ishPath
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '10' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.number.login.attempts'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.number.login.attempts'
        And match response[0].valueString == '10'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '5' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change "Require better password"

        Given path ishPath
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.password.complexity'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.complexity'
        And match response[0].valueString == 'true'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change "Require password change every"

        Given path ishPath
        And request [{ uniqueKey: 'security.password.expiry.period', valueString: '60' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.password.expiry.period'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.expiry.period'
        And match response[0].valueString == '60'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.password.expiry.period', valueString: null }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change "Require two factor authentication change every"

        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.expiry.period', valueString: null }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.tfa.expiry.period'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.tfa.expiry.period'
        And match response[0].valueString == null

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.expiry.period', valueString: '16' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change 2FA from "Optional for all users" to "Required for admin users"

        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.admin' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.tfa.status'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.tfa.status'
        And match response[0].valueString == 'enabled.admin'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.status', valueString: 'disabled' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Change 2FA from "Optional for all users" to "Required for all users"

        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.all' }]
        When method POST
        Then status 204

        Given path ishPath
        And param search = 'security.tfa.status'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.tfa.status'
        And match response[0].valueString == 'enabled.all'

#       <-----> Scenario have been finished. Change value to default:
        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.status', valueString: 'disabled' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (-) Change Security settings by notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        Given path ishPath
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'false' }]
        When method POST
        Then status 403

        Given path ishPath
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '10' }]
        When method POST
        Then status 403

        Given path ishPath
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 403

        Given path ishPath
        And request [{ uniqueKey: 'security.password.expiry.period', valueString: 10 }]
        When method POST
        Then status 403

        Given path ishPath
        And request [{ uniqueKey: 'security.tfa.expiry.period', valueString: '5' }]
        When method POST
        Then status 403

        Given path 'preference'
        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.all' }]
        When method POST
        Then status 403
