package ish.oncourse.aql.impl

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import static ish.oncourse.aql.impl.AntlrAqlServiceTest.MockEnum.ACTIVE
import static ish.oncourse.aql.impl.AntlrAqlServiceTest.MockEnum.EXPIRED
import static ish.oncourse.aql.impl.AntlrAqlServiceTest.MockEnum.SUCCESS
import static java.util.Arrays.asList
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Expression
import static org.apache.cayenne.exp.ExpressionFactory.exp
import org.apache.cayenne.exp.TraversalHandler
import org.apache.cayenne.exp.parser.ASTAnd
import org.apache.cayenne.exp.parser.SimpleNode
import org.apache.cayenne.map.DbAttribute
import org.apache.cayenne.map.DbEntity
import org.apache.cayenne.map.EntityResolver
import org.apache.cayenne.map.ObjAttribute
import org.apache.cayenne.map.ObjEntity
import org.apache.cayenne.map.ObjRelationship
import org.apache.cayenne.query.Select
import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.CoreMatchers.not
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AntlrAqlServiceTest {

    private AqlService service

    @Before
    void setUp() {
        service = new AntlrAqlService()
    }

    @Test
    void testEnumDbValueString() {
        CompilationResult result = service.compile("status == 'S' or status == 'E'"
                , null, getMockContext())
        assertValid(result)
    }

    @Test
    void testEnumDbValue() {
        CompilationResult result = service.compile("status == S or status == A"
                , null, getMockContext())
        assertValid(result)
    }

    @Test
    void testLongPath() {
        CompilationResult result = service.compile("contact.path = 1 and contact.address.area.name ~ 'name'"
                , null, getMockContext())

        assertValid("contact.path = 1 and contact.address.area.name likeIgnoreCase '%name%'", result)
    }

    @Test
    void testCurlyBracesExp() {
        CompilationResult result = service.compile("contact{path = 1}"
                , null, getMockContext())

        assertValid("contact.path = 1", result)
    }

    @Test
    void testNestedCurlyBracesExp() {
        CompilationResult result = service.compile("contact{address{area{name ~ 'c'}}}"
                , null, getMockContext())

        assertValid("contact.address.area.name likeIgnoreCase '%c%'", result)
    }

    @Test
    void testPathCurlyBraces() {
        CompilationResult result = service.compile("contact{code in (1,3,5)}.path = 1"
                // contact.created today and contact.path = 1
                , null, getMockContext())

        assertValid("contact.path = 1 and contact.code in (1,3,5) ", result)
    }

    @Test
    void testPathMultiCurlyBraces() {
        CompilationResult result = service.compile(
                "contact{created today}.address{city ~ 'city'}.area{name starts with 'a'}.name ends with 'c'"
                , null, getMockContext())

        assertValid(result)
    }

    @Test
    void testPathMultiCurlyBracesExp() {
        CompilationResult result = service.compile(
                "contact{path = 1 and created today}.address{area.name ~ 'test'}.area{name starts with 's'}"
                , null, getMockContext())

        assertValid(result)
    }

    @Test
    void testPathMultiCurlyBracesMixedExp() {
        CompilationResult result = service.compile(
                "contact{path = 1 or created today}.address{area.name ~ 'test'}.area{name starts with 's'}" +
                        " or contact{code in (1,2,3)}.path = 2"
                , null, getMockContext())

        assertValid(result)
    }

    @Test
    void testTag() {
        CompilationResult result = service.compile("#arts"
            , null, getMockContext("course"))

        assertValid("(arts.entityIdentifier = 1) and (arts.tag+.name = \"arts\")", result)
    }

    @Test
    void testChildTag() {
        CompilationResult result = service.compile("contact #arts"
            , null, getMockContext(Contact.class, "contact", "course"))

        assertValid("(contact.arts.entityIdentifier = 8) and (contact.arts.tag+.name = \"arts\")", result)
    }

    @Test
    void testLongTag() {
        CompilationResult result = service.compile("#'health_and_care'"
            , null, getMockContext("course"))

        assertValid("(health_and_care.entityIdentifier = 1) and (health_and_care.tag+.name = \"health_and_care\")", result)
    }

    @Test
    void testEqualExp() {
        CompilationResult result = service.compile("path = 10"
            , null, getMockContext())

        assertValid("path = 10", result)
    }

    @Test
    void testUnaryToday() {
        CompilationResult result = service.compile("created today"
            , null, getMockContext())

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        assertValid(exp('created between $a and $b', start, end), result)
    }

    @Test
    void testDateFormat() {
        CompilationResult result = service.compile("created in 2/01/1999 3:00 am - 2 days .. 2001-02-20 20:00 + 2 weeks"
            , null, getMockContext())

        LocalDateTime start = LocalDateTime
                .of(LocalDate.of(1998, 12, 31), LocalTime.of(3, 0))
        LocalDateTime end = LocalDateTime
                .of(LocalDate.of(2001, 3, 6), LocalTime.of(20, 0))
        assertValid(exp('created between $start and $end', start, end), result)
    }

    @Test
    void testDateIdentifiers() {
        CompilationResult result = service.compile("created after today - 2 days"
            , null, getMockContext())

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.MAX)
        assertValid(exp('created > $a', dateTime), result)
    }

    @Test
    void testDateIdentifiersMathEquals() {
        CompilationResult result = service.compile("created = today - 2 days"
            , null, getMockContext())

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.MIN)
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.MAX)
        assertValid(exp('(created >= $a) and (created <= $b)', todayStart, todayEnd), result)
    }

    @Test
    void testDateIdentifiersEquals() {
        CompilationResult result = service.compile("created = yesterday"
            , null, getMockContext())

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN)
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX)
        assertValid(exp('(created >= $a) and (created <= $b)', todayStart, todayEnd), result)
    }

    @Test
    void testWrongDateIdentifiersEquals() {
        CompilationResult result = service.compile("created last week"
            , null, getMockContext())

        LocalDate weekStart = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY)
        LocalDateTime start = LocalDateTime.of(weekStart, LocalTime.MIN)
        LocalDateTime end = LocalDateTime.of(weekStart.plusDays(6), LocalTime.MAX)
        assertValid(exp('created between $s and $e', start, end), result)
    }

    @Test
    void testUnaryThisYear() {
        CompilationResult result = service.compile("created this year"
            , null, getMockContext())

        LocalDateTime start = LocalDateTime.of(LocalDate.ofYearDay(LocalDate.now().getYear(), 1),
                LocalTime.MIN)
        LocalDateTime end = LocalDateTime.of(LocalDate.ofYearDay(LocalDate.now().getYear() + 1, 1).minusDays(1),
                LocalTime.MAX)
        assertValid(exp('created between $s and $e', start, end), result)
    }

    @Test
    void testUnaryMe() {
        CompilationResult result = service.compile("createdBy me"
            , null, getMockContext())

        assertValid('createdBy = $me', result)
    }

    @Test
    void testDateEquals() {
        CompilationResult result = service.compile("created = 2018-01-01"
            , null, getMockContext())

        LocalDateTime start = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN)
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MAX)
        assertValid(exp('created >= $a and created <= $b', start, end), result)
    }

    @Test
    void testDateAfter() {
        CompilationResult result = service.compile("created after 2018-01-01"
            , null, getMockContext())

        LocalDateTime date = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MAX)
        assertValid(exp('created > $d', date), result)
    }

    @Test
    void testDateBefore() {
        CompilationResult result = service.compile("created before 2018-01-01"
            , null, getMockContext())

        LocalDateTime date = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN)
        assertValid(exp('created < $d', date), result)
    }

    @Test
    void testDateInRange() {
        CompilationResult result = service.compile("created in 2018-01-01 .. 2018-01-02"
            , null, getMockContext())

        LocalDateTime start = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN)
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2018, 1, 2), LocalTime.MAX)
        assertValid(exp('created between $a and $b', start, end), result)
    }

    @Test
    void testDateInRangeOpenLeft() {
        CompilationResult result = service.compile("created in * .. 2018-01-02"
            , null, getMockContext())

        LocalDateTime date = LocalDateTime.of(LocalDate.of(2018, 1, 2), LocalTime.MAX)
        assertValid(exp('created <= $d', date), result)
    }

    @Test
    void testDateInRangeOpenRight() {
        CompilationResult result = service.compile("created in 2018-01-01 .. *"
            , null, getMockContext())

        LocalDateTime date = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN)
        assertValid(exp('created >= $d', date), result)
    }

    @Test
    void testDateExpEvaluation() {
        CompilationResult result = service.compile("created after (tomorrow - 2 days) - 1 week"
            , null, getMockContext())

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now().minusDays(1).minusWeeks(1), LocalTime.MAX)
        assertValid(exp('created > $d', dateTime), result)
    }

    @Test
    void testWrongUnit() {
        CompilationResult result = service.compile("created after today - 1 ear"
            , null, getMockContext())

        assertInvalid(result)
    }

    @Test
    void testEnumIn() {
        CompilationResult result = service.compile("status in SUCCESS, ACTIVE, EXPIRED"
            , null, getMockContext())

        assertValid(exp('status in $l', asList(SUCCESS, ACTIVE, EXPIRED)), result)
    }

    @Test
    void testAndWithDateEvaluation() {
        CompilationResult result = service.compile("status in SUCCESS, ACTIVE, EXPIRED and created after today - 7 days"
            , null, getMockContext())

        LocalDateTime date = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MAX)
        Expression exp = exp('status in $l and created > $d', asList(SUCCESS, ACTIVE, EXPIRED), date)
        assertValid(exp, result)
    }

    @Test
    void testComplexExp() {
        String aqlQuery = "status in SUCCESS, ACTIVE, EXPIRED \n" +
            "and not deleted \n" +
            "and (created after today - 7 days or created last year) \n" +
            "and (name contains John or name starts with 'a') \n" +
            "and (expires before 31/12/2018 11:59pm or expires in 2017-01-01 00:00 .. today - 1 month) \n" +
            "and (#art or #'health care') \n" +
            "and (field1 = 'abc' or field2 = 123)"

        CompilationResult result = service.compile(aqlQuery, null, getMockContext("Contact"))

        // TODO: actual exp
        assertValid(result)
    }


    @Test
    void testCustomField() {
        CompilationResult result = service
            .compile("field1 = 'abc' and field2 = 123"
                , null, getMockContext("Contact"))

        // create AND manually as exp() will flatten it and assertion will fail
        ASTAnd and = new ASTAnd()
        ExpressionUtil.addChild(and, exp("(__custom_field_alias__0.customFieldType+.key = 'field1') and (__custom_field_alias__0.value = 'abc')"), 0)
        ExpressionUtil.addChild(and, exp("(__custom_field_alias__1.customFieldType+.key = 'field2') and (__custom_field_alias__1.value = 123)"), 1)
        assertValid(and, result)
    }

    @Test
    void testCustomField2() {
        CompilationResult result = service
            .compile("someField1 value10"
                , null, getMockContext("Contact"))

        assertInvalid(result)
    }

    @Test
    void testStringOps() {
        CompilationResult result = service
            .compile("(name like 'abcd' or name not contains 'ab') and (name starts with 'a' or name not ends with 'd')"
                , null, getMockContext("Contact"))

        assertValid("(name likeIgnoreCase '%abcd%' or name not likeIgnoreCase '%ab%') " +
                "and (name likeIgnoreCase 'a%' or name not likeIgnoreCase '%d')", result)
    }

    @Test
    void testStringOps_NonStringType() {
        CompilationResult result = service
            .compile("created not contains 'ab' or created starts with 'a'"
                , null, getMockContext("Contact"))

        assertInvalid(result)
    }

    @Test
    void testModule() {
        CompilationResult result = service
                .compile("~ 'Test Mod'", null, getMockContext("Module"))
        assertValid("(title likeIgnoreCase 'Test Mod%') or (nationalCode likeIgnoreCase 'Test Mod%')", result)
    }

    @Test
    void testModuleOps() {
        CompilationResult result = service
                .compile("module = 'Test Module' or module ~ 'Tes' or module !~ 'Teq'"
                        , null, getMockContext(Site.class, "module", "module"))

        assertValid(result)
    }

    @Test
    void testSingleModuleOp() {
        CompilationResult result = service
                .compile("module = 'Test Module'"
                        , null, getMockContext(Site.class, "module", "module"))

        assertValid(result)
    }

    @Test
    void testModule_InvalidOps() {
        CompilationResult result = service
                .compile("module after 'Test Module' or module >= 'Test M'"
                        , null, getMockContext(Site.class, "module", "module"))

        assertInvalid(result)
    }

    @Test
    void testQualification() {
        CompilationResult result = service
                .compile("~ 'Test Qua'", null, getMockContext("Qualification"))
        assertValid(result)
    }

    @Test
    void testQualificationOps() {
        CompilationResult result = service
                .compile("qualification = 'Test Qualification' or qualification ~ 'Tes' or qualification !~ 'Teq'"
                        , null, getMockContext(Site.class, "qualification", "qualification"))

        assertValid(result)
    }

    @Test
    void testSingleQualificationOp() {
        CompilationResult result = service
                .compile("qualification = 'Test Qualification'"
                        , null, getMockContext(Site.class, "qualification", "qualification"))

        assertValid(result)
    }

    @Test
    void testQualification_InvalidOps() {
        CompilationResult result = service
                .compile("qualification after 'Test Qualification' or qualification >= 'Test Q'"
                        , null, getMockContext(Site.class, "qualification", "qualification"))

        assertInvalid(result)
    }

    @Test
    void testSite() {
        CompilationResult result = service
                .compile("~ 'Defau'", null, getMockContext("Site"))
        assertValid(result)
    }

    @Test
    void testSiteOps() {
        CompilationResult result = service
                .compile("site = 'Default Site' or site ~ 'De' or site !~ 'Qe'"
                        , null, getMockContext(Site.class, "site", "site"))

        assertValid(result)
    }

    @Test
    void testSingleSiteOp() {
        CompilationResult result = service
                .compile("site = 'Default Site'"
                        , null, getMockContext(Site.class, "site", "site"))

        assertValid(result)
    }

    @Test
    void testSite_InvalidOps() {
        CompilationResult result = service
                .compile("site after 'Default Site' or site >= 'Defa'"
                        , null, getMockContext(Site.class, "site", "site"))

        assertInvalid(result)
    }

    @Test
    void testRoom() {
        CompilationResult result = service
                .compile("~ 'Tes'", null, getMockContext("Room"))
        assertValid(result)
    }

    @Test
    void testRoomOps() {
        CompilationResult result = service
                .compile("room = 'Test Room' or room ~ 'Te' or room !~ 'Ye'"
                        , null, getMockContext(Site.class, "room", "room"))

        assertValid(result)
    }

    @Test
    void testSingleRoomOp() {
        CompilationResult result = service
                .compile("room = 'Test Room'"
                        , null, getMockContext(Site.class, "room", "room"))

        assertValid(result)
    }

    @Test
    void testRoom_InvalidOps() {
        CompilationResult result = service
                .compile("room after 'Test Room' or room >= 'Tes'"
                        , null, getMockContext(Site.class, "room", "room"))

        assertInvalid(result)
    }

    @Test
    void testContact() {
        CompilationResult result = service
            .compile("~ 'Lei Ste'", null, getMockContext("Contact"))
        assertValid("(lastName likeIgnoreCase 'Lei Ste%') or (firstName likeIgnoreCase 'Lei%' and lastName likeIgnoreCase 'Ste%')", result)
    }

    @Test
    void testContactComplex() {
        CompilationResult result = service
                .compile("deleted == true AND ~'Smith'", null, getMockContext("Contact"))
        assertValid("(deleted = true) and (lastName likeIgnoreCase 'Smith%')", result)
    }

    @Test
    void testContactOps() {
        CompilationResult result = service
            .compile("contact = 'John Smith' or contact ~ 'Jo' or contact !~ 'Sm,'"
                , null, getMockContext(Contact.class, "contact", "contact"))

        assertValid(result)
    }

    @Test
    void testSingleContactOp() {
        CompilationResult result = service
            .compile("contact = 'John Smith'"
                , null, getMockContext(Contact.class, "contact", "contact"))

        assertValid("(contact.lastName = 'John Smith') or (contact.firstName = 'John' and contact.lastName = 'Smith')", result)
    }

    @Test
    void testContact_InvalidOps() {
        CompilationResult result = service
            .compile("contact after 'John Smith' or contact >= 'Jo Sm'"
                , null, getMockContext(Contact.class, "contact", "contact"))

        assertInvalid(result)
    }

    @Test
    void testNull() {
        CompilationResult result = service
            .compile("path is null"
                , null, getMockContext())

        assertValid("path = null", result)
    }

    @Test
    void testIdSet() {
        CompilationResult result = service
                .compile("contact[1,2,3]"
                        , null, getMockContext())

        assertValid("contact in (1,2,3)", result)
    }

    @Test
    void testIdSetLongPath() {
        CompilationResult result = service
                .compile("contact.address.area[1,2,3]"
                        , null, getMockContext())

        assertValid("contact.address.area in (1,2,3)", result)
    }

    @Test
    void testNotIdSetLongPath() {
        CompilationResult result = service
                .compile("not contact.address.area[1,2,3]"
                        , null, getMockContext())

        assertValid("contact.address.area not in (1,2,3)", result)
    }

    @Test
    void testIdSetNested() {
        CompilationResult result = service
                .compile("contact{address[1,2,3]}.address.area.name ~ 'test'"
                        , null, getMockContext())

        assertValid("contact.address.area.name likeIgnoreCase '%test%' and contact.address in (1,2,3)", result)
    }

    @Test
    void testBankingCustomSearchLastWeek() {
        CompilationResult result = service
                .compile("~ last week"
                        , Banking.class, getMockContext("Banking"))
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(1)
        LocalDate end = start.plusDays(6)
        assertValid(exp('settlementDate between $a and $b', start, end), result)
    }

    @Test
    void testBankingQuickSearchThisMonth() {
        CompilationResult result = service
                .compile("~ \"%this month\""
                        , Banking.class, getMockContext("Banking"))
        LocalDate start = LocalDate.now().withDayOfMonth(1)
        LocalDate end = start.plusMonths(1).minusDays(1)
        assertValid(exp('settlementDate between $a and $b', start, end), result)
    }

    @Test
    void testBankingCustomSearchToday() {
        CompilationResult result = service
                .compile("~ today"
                        , Banking.class, getMockContext("Banking"))
        assertValid(exp('settlementDate = $d', LocalDate.now()), result)
    }

    @Test
    void testWhitespace() {
        CompilationResult result = service
                .compile("contact . address    .area.    name = \t'test'"
                        , null, getMockContext())
        assertValid("contact.address.area.name = 'test'", result)
    }

    @Test
    void testEmptyToMany() {
        CompilationResult result = service
                .compile("contact.address is empty", null, getMockContext())
        assertValid("contact.address = null", result)
    }

    @Test
    void testNegativeInt() {
        CompilationResult result = service
                .compile("contact.path = -1"
                        , null, getMockContext())
        assertValid(exp('contact.path = $i', -1), result)
    }

    @Test
    void testNegativeDouble() {
        CompilationResult result = service
                .compile("contact.path = -1.0"
                        , null, getMockContext())
        assertValid(exp('contact.path = $f', -1.0d), result)
    }

    @Test
    void testBooleanStringComparision() {
        CompilationResult result = service
                .compile("deleted = \"true\""
                        , null, getMockContext())
        assertValid("deleted = true", result)
    }

    @Test
    void testInvalidBooleanStringComparision() {
        CompilationResult result = service
                .compile("deleted = '123'"
                        , null, getMockContext())
        assertValid("deleted = false", result)
    }

    @Test
    void testEnumStringComparision() {
        CompilationResult result = service
                .compile("status = 'SUCCESS'"
                        , null, getMockContext())
        assertValid(exp('status = $s', SUCCESS), result)
    }

    @Test
    void testInvalidEnumStringComparision() {
        CompilationResult result = service
                .compile("status = 'SUCCESSS'"
                        , null, getMockContext())
        assertInvalid(result)
    }

    @Test
    void testIntegerStringComparision() {
        CompilationResult result = service
                .compile("path > '123'"
                        , null, getMockContext())
        assertValid(exp('path > $i', 123L), result)
    }

    @Test
    void testInvalidIntegerStringComparision() {
        CompilationResult result = service
                .compile("path = '123x'"
                        , null, getMockContext())
        assertInvalid(result)
    }

    @Test
    void testDateStringComparision() {
        CompilationResult result = service
                .compile("created = '2001-10-23'"
                        , null, getMockContext())
        assertInvalid(result)
    }

    private void assertValid(String cayenneExpExpected, CompilationResult result) {
        assertValid(exp(cayenneExpExpected), result)
    }

    private void assertValid(Expression cayenneExpExpected, CompilationResult result) {
        assertValid(result)
        assertEquals(cayenneExpExpected, result.getCayenneExpression().get())
    }

    private void assertValid(CompilationResult result) {
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        Expression exp = result.getCayenneExpression().get()
        // Validate expression:
        // 1. doesn't have Lazy nodes
        // 2. parents and children are linked consistently both ways
        exp.traverse(new TraversalHandler() {
            @Override
            void startNode(Expression node, Expression parentNode) {
                assertThat(node, instanceOf(SimpleNode.class))
                assertThat("Expression has unresolved lazy node", node, not(instanceOf(LazyExpressionNode.class)))

                SimpleNode simpleNode = (SimpleNode)node
                assertEquals("Wrong parent for node: " + simpleNode.getClass().getSimpleName() + '"' + simpleNode.toString() + '"',
                        simpleNode.jjtGetParent(), parentNode)
            }

            @Override
            void finishedChild(Expression node, int childIndex, boolean hasMoreChildren) {
            }

            @Override
            void endNode(Expression node, Expression parentNode) {
            }

            @Override
            void objectNode(Object leaf, Expression parentNode) {
            }
        })
    }

    private void assertInvalid(CompilationResult result) {
        assertFalse(result.getCayenneExpression().isPresent())
        assertFalse(result.getErrors().isEmpty())
    }

    private ObjectContext getMockContext() {
        return getMockContext(Contact.class, "contact", "test")
    }

    private ObjectContext getMockContext(String rootEntityName) {
        return getMockContext(Contact.class, "contact", rootEntityName)
    }

    @SuppressWarnings("unchecked")
    private ObjectContext getMockContext(Class relatedClass, String relationshipName, String rootEntityName) {
        DbEntity entity = new DbEntity("test")
        entity.addAttribute(new DbAttribute("path"))
        ObjEntity objEntity = new ObjEntity(rootEntityName)
        objEntity.setDbEntity(entity)

        ObjAttribute attribute = new ObjAttribute("status")
        attribute.setDbAttributePath("path")
        attribute.setType(MockEnum.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("path")
        attribute.setDbAttributePath("path")
        attribute.setType(Integer.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("created")
        attribute.setDbAttributePath("path")
        attribute.setType(LocalDate.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("createdBy")
        attribute.setDbAttributePath("path")
        attribute.setType(PersistentObject.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("name")
        attribute.setDbAttributePath("path")
        attribute.setType(String.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("expires")
        attribute.setDbAttributePath("path")
        attribute.setType(LocalDate.class.getName())
        objEntity.addAttribute(attribute)

        attribute = new ObjAttribute("deleted")
        attribute.setDbAttributePath("path")
        attribute.setType("boolean")
        objEntity.addAttribute(attribute)

        ObjEntity contact = new ObjEntity().with{ contact ->
            contact.setName("contact")
            contact.setClassName(relatedClass.getName())

            ObjRelationship contactRelationship = mock(ObjRelationship.class)
            when(contactRelationship.getName()).thenReturn("taggingRelations")
            when(contactRelationship.getSourceEntity()).thenReturn(contact)
            when(contactRelationship.getTargetEntity()).thenReturn(new ObjEntity("target2"))
            contact.addRelationship(contactRelationship)

            attribute = new ObjAttribute("path")
            attribute.setDbAttributePath("path")
            attribute.setType(Integer.class.getName())
            contact.addAttribute(attribute)

            attribute = new ObjAttribute("code")
            attribute.setDbAttributePath("code")
            attribute.setType(String.class.getName())
            contact.addAttribute(attribute)

            attribute = new ObjAttribute("created")
            attribute.setDbAttributePath("created")
            attribute.setType(LocalDate.class.getName())
            contact.addAttribute(attribute)

            ObjEntity address = new ObjEntity().with{ address ->
                address.setName("address")
                address.setClassName(Address.class.getName())

                attribute = new ObjAttribute("city")
                attribute.setDbAttributePath("city")
                attribute.setType(String.class.getName())
                address.addAttribute(attribute)

                attribute = new ObjAttribute("street")
                attribute.setDbAttributePath("street")
                attribute.setType(String.class.getName())
                address.addAttribute(attribute)

                ObjEntity area = new ObjEntity().with { area ->
                    area.setName("area")
                    area.setClassName(Address.class.getName())

                    attribute = new ObjAttribute("name")
                    attribute.setDbAttributePath("name")
                    attribute.setType(String.class.getName())
                    area.addAttribute(attribute)
                    area
                }

                ObjRelationship relationship = mock(ObjRelationship.class)
                when(relationship.getName()).thenReturn("area")
                when(relationship.getSourceEntity()).thenReturn(address)
                when(relationship.getTargetEntity()).thenReturn(area)
                address.addRelationship(relationship)
                address
            }

            ObjRelationship relationship = mock(ObjRelationship.class)
            when(relationship.getName()).thenReturn("address")
            when(relationship.getSourceEntity()).thenReturn(contact)
            when(relationship.getTargetEntity()).thenReturn(address)
            contact.addRelationship(relationship)
            contact
        }

        ObjRelationship relationship = mock(ObjRelationship.class)
        when(relationship.getName()).thenReturn("taggingRelations")
        when(relationship.getSourceEntity()).thenReturn(objEntity)
        when(relationship.getTargetEntity()).thenReturn(new ObjEntity("target"))
        objEntity.addRelationship(relationship)

        relationship = mock(ObjRelationship.class)
        when(relationship.getName()).thenReturn(relationshipName)
        when(relationship.getSourceEntity()).thenReturn(objEntity)
        when(relationship.getTargetEntity()).thenReturn(contact)
        objEntity.addRelationship(relationship)

        EntityResolver resolver = mock(EntityResolver.class)
        when(resolver.getObjEntity((Class<?>)any())).thenReturn(objEntity)

        ObjectContext context = mock(ObjectContext.class)
        when(context.getEntityResolver()).thenReturn(resolver)
        when(context.select(any(Select.class))).thenReturn(asList("field1", "field2"))

        return context
    }

    @SuppressWarnings("unused")
    enum MockEnum implements DisplayableExtendedEnumeration<String> {
        SUCCESS("S"), ACTIVE("A"), EXPIRED("E");

        private final String dbValue

        MockEnum(String dbValue) {
            this.dbValue = dbValue
        }

        @Override
        String getDisplayName() {
            return dbValue
        }

        @Override
        String getDatabaseValue() {
            return dbValue
        }
    }

    static class Banking extends PersistentObject {
    }

    static class Contact extends PersistentObject {
    }

    static class Address extends PersistentObject {
    }

    static class Site extends PersistentObject {
    }
}