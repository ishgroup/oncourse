@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/enrolment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/enrolment'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        

        * def enrolmentToDefault = {"id":2,"tags":[],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":false,"outcomeIdTrainingOrg":null,"studentIndustryANZSICCode":null,"vetClientID":null,"vetFundingSourceStateID":null,"vetIsFullTime":false,"vetTrainingContractID":null,"status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"Not stated","vetFeeExemptionType":"Not set","fundingSource":"Domestic full fee paying student","associatedCourseIdentifier":null,"vetInSchools":null,"suppressAvetmissExport":false,"vetPurchasingContractID":null,"cricosConfirmation":null,"vetFeeIndicator":false,"trainingPlanDeveloped":null,"feeCharged":700,"feeHelpAmount":0,"invoicesCount":1,"outcomesCount":1,"feeStatus":null,"attendanceType":"No information","creditOfferedValue":null,"creditUsedValue":null,"creditFOEId":null,"creditProvider":null,"creditProviderType":null,"creditTotal":null,"creditType":null,"creditLevel":null,"documents":[],"customFields":{}}



    Scenario: (+) Update Enrolment by admin

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "tags":[{"id":236,"name":"enrolments 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "studentContactId":2,
        "studentName":"stud1",
        "courseClassId":3,
        "courseClassName":"Course1 course1-3",
        "confirmationStatus":"Do not send",
        "eligibilityExemptionIndicator":true,
        "outcomeIdTrainingOrg":"qwe",
        "studentIndustryANZSICCode":null,
        "vetClientID":"qwerty",
        "vetFundingSourceStateID":"qwe",
        "vetIsFullTime":true,
        "vetTrainingContractID":"qwerty",
        "status":"Active",
        "displayStatus":"Active",
        "source":"office",
        "relatedFundingSourceId":5,
        "studyReason":"To get a job",
        "vetFeeExemptionType":"Yes (Y)",
        "fundingSource":"Commonwealth - specific",
        "associatedCourseIdentifier":"qwerty",
        "vetInSchools":true,
        "suppressAvetmissExport":true,
        "vetPurchasingContractID":"qwerty",
        "cricosConfirmation":"qwerty",
        "vetFeeIndicator":true,
        "trainingPlanDeveloped":true,
        "feeCharged":700.00,
        "feeHelpAmount":100.00,
        "invoicesCount":1,
        "outcomesCount":1,
        "feeStatus":"Restricted Access Arrangement",
        "attendanceType":"Part-time attendance",
        "creditOfferedValue":"qwer",
        "creditUsedValue":"qwer",
        "creditFOEId":"qwer",
        "creditProvider":"qwer",
        "creditProviderType":"TAFE",
        "creditTotal":"Unit of study is NOT an RPL unit of study",
        "creditType":"Other",
        "creditLevel":"Certificate IV",
        "documents":"#ignore",
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "assessments":[],
        "submissions":[],
        "feeHelpClass":false
        }
        """

#       <----->  Scenario have been finished. Now find and change back object:
        Given path ishPath + '/2'
        And request enrolmentToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Enrolment by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "tags":[{"id":236,"name":"enrolments 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "studentContactId":2,
        "studentName":"stud1",
        "courseClassId":3,
        "courseClassName":"Course1 course1-3",
        "confirmationStatus":"Do not send",
        "eligibilityExemptionIndicator":true,
        "outcomeIdTrainingOrg":"qwe",
        "studentIndustryANZSICCode":null,
        "vetClientID":"qwerty",
        "vetFundingSourceStateID":"qwe",
        "vetIsFullTime":true,
        "vetTrainingContractID":"qwerty",
        "status":"Active",
        "displayStatus":"Active",
        "source":"office",
        "relatedFundingSourceId":5,
        "studyReason":"To get a job",
        "vetFeeExemptionType":"Yes (Y)",
        "fundingSource":"Commonwealth - specific",
        "associatedCourseIdentifier":"qwerty",
        "vetInSchools":true,
        "suppressAvetmissExport":true,
        "vetPurchasingContractID":"qwerty",
        "cricosConfirmation":"qwerty",
        "vetFeeIndicator":true,
        "trainingPlanDeveloped":true,
        "feeCharged":700.00,
        "feeHelpAmount":100.00,
        "invoicesCount":1,
        "outcomesCount":1,
        "feeStatus":"Restricted Access Arrangement",
        "attendanceType":"Part-time attendance",
        "creditOfferedValue":"qwer",
        "creditUsedValue":"qwer",
        "creditFOEId":"qwer",
        "creditProvider":"qwer",
        "creditProviderType":"TAFE",
        "creditTotal":"Unit of study is NOT an RPL unit of study",
        "creditType":"Other",
        "creditLevel":"Certificate IV",
        "documents":"#ignore",
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "assessments":[],
        "submissions":[],
        "feeHelpClass":false
        }
        """

#       <----->  Scenario have been finished. Now find and change back object:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/2'
        And request enrolmentToDefault
        When method PUT
        Then status 204



    Scenario: (-) Update Enrolment Enrolment by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def enrolmentToUpdate = {}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update enrolment. Please contact your administrator"



    Scenario: (-) Update Enrolment vetClientID to wrong value more than 10 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"12345678901","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 10."



    Scenario: (-) Update Enrolment cricosConfirmation to wrong value more than 32 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"A3A5A7A9A12A15A18A21A24A27A30A33A","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 32."



    Scenario: (-) Update Enrolment vetPurchasingContractID to wrong value more than 12 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"A3A5A7A9A12A1","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 12."



    Scenario: (-) Update Enrolment outcomeIdTrainingOrg to wrong value more than 3 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"1234","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 3."



    Scenario: (-) Update Enrolment vetTrainingContractID to wrong value more than 10 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"A3A5A7A9A12","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 10."



    Scenario: (-) Update Enrolment vetFundingSourceStateID to wrong value more than 3 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe1","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 3."



    Scenario: (-) Update Enrolment associatedCourseIdentifier to wrong value more than 10 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"12345678901","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 10."



    Scenario: (-) Update Enrolment creditOfferedValue to wrong value more than 4 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer1","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 4."



    Scenario: (-) Update Enrolment creditUsedValue to wrong value more than 4 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer1","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 4."



    Scenario: (-) Update Enrolment creditFOEId to wrong value more than 4 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer1","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 4."



    Scenario: (-) Update Enrolment creditProvider to wrong value more than 4 chars

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer1","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The maximum length for this field is 4."



    Scenario: (-) Update Enrolment with vetClientID but without vetTrainingContractID

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"qwerty","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":null,"status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "If you enter data in either the Training Contract ID or the Training Contract Client ID, then you must fill out both."



    Scenario: (-) Update Enrolment with vetTrainingContractID but without vetClientID

        * def enrolmentToUpdate = {"id":2,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":null,"vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/2'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "If you enter data in either the Training Contract ID or the Training Contract Client ID, then you must fill out both."



    Scenario: (-) Update Enrolment not existing Enrolment

#       <----->  Add a new entity to update and define its id:
        * def enrolmentToUpdate = {"id":99999,"tags":[{"id":236}],"studentContactId":2,"studentName":"stud1","courseClassId":3,"courseClassName":"Course1 course1-3","confirmationStatus":"Do not send","eligibilityExemptionIndicator":true,"outcomeIdTrainingOrg":"qwe","studentIndustryANZSICCode":null,"vetClientID":"abc","vetFundingSourceStateID":"qwe","vetIsFullTime":true,"vetTrainingContractID":"qwerty","status":"Active","source":"office","relatedFundingSourceId":5,"studyReason":"To get a job","vetFeeExemptionType":"Yes (Y)","fundingSource":"Commonwealth - specific","associatedCourseIdentifier":"qwerty","vetInSchools":true,"suppressAvetmissExport":true,"vetPurchasingContractID":"qwerty","cricosConfirmation":"qwerty","vetFeeIndicator":true,"trainingPlanDeveloped":true,"feeCharged":700,"feeHelpAmount":100,"invoicesCount":1,"outcomesCount":1,"feeStatus":"Restricted Access Arrangement","attendanceType":"Part-time attendance","creditOfferedValue":"qwer","creditUsedValue":"qwer","creditFOEId":"qwer","creditProvider":"qwer","creditProviderType":"TAFE","creditTotal":"Unit of study is NOT an RPL unit of study","creditType":"Other","creditLevel":"Certificate IV","documents":[{"id":201}],"customFields":{}}

        Given path ishPath + '/99999'
        And request enrolmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



