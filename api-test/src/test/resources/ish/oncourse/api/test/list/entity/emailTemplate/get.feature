@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/emailTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Get list of all EmailTemplates by admin

        Given path ishPathList
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6"]



    Scenario: (+) Get list of all EmailTemplates by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6"]



    Scenario: (+) Get EmailTemplate by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $.id == 1



    Scenario: (+) Get EmailTemplate by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $.id == 2



    Scenario: (-) Get not existing EmailTemplate

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get EmailTemplate configs by admin

        Given path ishPath + '/config/1'
        When method GET
        Then status 200
        And match $ contains 'shortDescription: "A message template for inviting the student of a VET course to\\\n  \\ complete a course completion survey. Their answers will be available in the \'Student\\\n  \\ Feedback\' section of onCourse. Sent by an automation, or manually form the Class\\\n  \\ or Enrolments windows."\ndescription: "A message template for inviting the student of a VET course to complete\\\n  \\ a course completion survey. Their answers will be available in the \'Student Feedback\'\\\n  \\ section of onCourse. Sent by an automation, or manually form the Class or Enrolments\\\n  \\ windows."\ncategory: accreditation\nname: VET Course completion survey\noptions:'
        And match $ contains '\n- name: header_keycode\n  value: ish.email.header\n  dataType: TEXT'
        And match $ contains '\n- name: footer_keycode\n  value: ish.email.footer\n  dataType: TEXT'
        And match $ contains '\nentityClass: Enrolment\ntype: EMAIL\nsubject: VET Course completion survey'
