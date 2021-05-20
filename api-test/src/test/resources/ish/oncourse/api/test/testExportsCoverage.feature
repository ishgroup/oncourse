@parallel=false
Feature: Feature which checks exports implementation and their coverage

    Background: Authorize first
        * call read('./signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/export'
        * def csvImportPath = 'template'
        * def xmlImportPath = 'template'
        * def pdfImportPath = 'pdf/template'
        * def ishPathLogin = 'login'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        * def JavaFunctions = Java.type('ish.oncourse.api.test.ExportsCoverageTest')
        * def excludedForCsvXml = excludedForCsvXml: ["contact", "articleProduct", "assessment", "definedTutorRole", "document", "emailTemplate", "exportTemplate", "membershipProduct", "outcome", "sales", "report", "reportOverlay", "note", "import", "priorLearning"]
        * def csvXmlEntities = JavaFunctions.getListEntitiesAndExclude(excludedForCsvXml)
        * print csvXmlEntities
        * def excludedForPdf = excludedForPdf: ["contact", "articleProduct", "assessment", "assessmentSubmission", "definedTutorRole", "document", "emailTemplate", "exportTemplate", "membershipProduct", "sales", "report", "reportOverlay", "note", "import", "priorLearning"]
        * def pdfEntities = JavaFunctions.getListEntitiesAndExclude(excludedForPdf)


    Scenario: (+) Check csv export coverage
    * call read('getCsv.feature') csvXmlEntities

    Scenario: (+) Check xml export coverage
    * call read('getXml.feature') csvXmlEntities

    Scenario: (+) Check pdf export coverage
    * call read('getPdf.feature') pdfEntities