/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.queries

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRelation
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Test cases to check the correctness of expressions, mostly the complicated ones, related to multiple tags. <br/>
 * <br/>
 * <ul>
 * <li>Subjects (allowing tagging of Course entities)
 * <ul>
 * <li>Subjects_1
 * <ul>
 * <li>Subjects_1_1</li>
 * <li>Subjects_1_2</li>
 * <li>Subjects_1_3</li>
 * </ul>
 * </li>
 * </ul>
 * <ul>
 * <li>Subjects_2
 * <ul>
 * <li>Subjects_2_1
 * <ul>
 * <li>Subjects_2_1_1 (tagged records: Course GG)</li>
 * <li>Subjects_2_1_2</li>
 * </ul>
 * </li>
 * <li>Subjects_2_2 (tagged records: Course FF, Course HH)
 * <ul>
 * <li>Subjects_2_2_1</li>
 * <li>Subjects_2_2_2</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <ul>
 * <li>Subjects_3
 * <ul>
 * <li>Subjects_3_1</li>
 * <li>Subjects_3_2</li>
 * <li>Subjects_3_3</li>
 * <li>Subjects_3_4 (tagged records: Course DD)</li>
 * <li>Subjects_3_5 (tagged records: Course EE)</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * <ul>
 * <li>Regions (allows tagging of Course and Site entities)
 * <ul>
 * <li>Regions_1 (tagged records: Course AA, Site A)</li>
 * <li>Regions_2 (tagged records: Course BB, Course CC, Site B)</li>
 * <li>Regions_3 (tagged records: Course EE, Site C)</li>
 * <ul>
 * <li>Regions_3_1 (tagged records: Site D)</li>
 * <li>Regions_3_2 (tagged records: Site E)</li>
 * </ul>
 * </li>
 * <li>Regions_4 (tagged records: Course HH)</li>
 * </ul>
 * </li> </ul>
 *
 */
@CompileStatic
@DatabaseSetup(readOnly = true, value = "ish/queries/expressions-test.xml")
class ExpressionsTest extends CayenneIshTestCase {

    /**
     * tests a simple query. queries for Course records tagged with Regions_1 (there is one course meeting the criteria)
     */
    @Test
    void testSingleTag() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag = getRecordWithId(cayenneContext, Tag.class, 102L)

        Expression expression = createExpressionForTagAndEntity(tag, TaggableClasses.COURSE)
        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals( 1, result.size(), "testSingleTag")
        Assertions.assertEquals(tag.getName(), result.get(0).getNotes(), "testSingleTag")
    }

    /**
     * tests a simple query. queries for Course records tagged with Regions_2 and code 'BB' (there is one course matching the criteria)
     */
    @Test
    void testSingleTagWithExtraExpression() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag = getRecordWithId(cayenneContext, Tag.class, 103L)

        Expression expression = createExpressionForTagAndEntity(tag, TaggableClasses.COURSE)
        expression = expression.andExp(ExpressionFactory.matchExp(Course.CODE_PROPERTY, "BB"))

        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals( 1, result.size(), "testSingleTag")
        Assertions.assertEquals(tag.getName(), result.get(0).getNotes(), "testSingleTag")
    }

    /**
     * queries for Course records tagged with Regions_2 OR Subjects_3_4 (there are 3 courses meeting the criteria)
     */
    @Test
    void testTwoTags() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 27L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 103L)

        Expression expression1 = createExpressionForTagAndEntity(tag1, TaggableClasses.COURSE)
        Expression expression2 = createExpressionForTagAndEntity(tag2, TaggableClasses.COURSE)

        Expression expression = expression1.orExp(expression2)

        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(3, result.size())
    }

    /**
     * queries for Course records tagged with Regions_3 OR Subjects_3_5 (there is one course tagged with both, query should return 1 record) <br/>
     * <br/>
     * this is not an example of what we are doing, as those tags belong to different tag trees the expression should contain AND
     */
    @Test
    void testOrQueryForOneRecordWithTwoTags() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 28L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 104L)

        Expression expression1 = createExpressionForTagAndEntity(tag1, TaggableClasses.COURSE)
        Expression expression2 = createExpressionForTagAndEntity(tag2, TaggableClasses.COURSE)

        Expression expression = expression1.orExp(expression2)

        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with Regions_4 AND Subjects_2_1_1 <br/>
     * <br/>
     * there is one course tagged with both, query should return 1 record. yet the AND does not allow to join two tag trees.
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsIncorrect() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 20L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        Expression expression1 = createExpressionForTagAndEntity(tag1, TaggableClasses.COURSE)
        Expression expression2 = createExpressionForTagAndEntity(tag2, TaggableClasses.COURSE)

        Expression expression = expression1.andExp(expression2)

        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(0, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with Regions_4 AND Subjects_2_1_1 (there is one course tagged with both, query should return 1 record)
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 20L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        Expression expression1 = ExpressionFactory.matchExp("alias1." + TagRelation.TAG_PROPERTY, tag1)
        expression1 = expression1.andExp(ExpressionFactory.matchExp("alias1." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression2 = ExpressionFactory.matchExp("alias2." + TagRelation.TAG_PROPERTY, tag2)
        expression2 = expression2.andExp(ExpressionFactory.matchExp("alias2." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression = expression1.andExp(expression2)

        query.setQualifier(expression)
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, "alias1", "alias2")

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with Regions_4 AND Subjects_2_1_2 (there is no course tagged with both)
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect2() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 21L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        Expression expression1 = ExpressionFactory.matchExp("alias1." + TagRelation.TAG_PROPERTY, tag1)
        expression1 = expression1.andExp(ExpressionFactory.matchExp("alias1." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression2 = ExpressionFactory.matchExp("alias2." + TagRelation.TAG_PROPERTY, tag2)
        expression2 = expression2.andExp(ExpressionFactory.matchExp("alias2." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression = expression1.andExp(expression2)

        query.setQualifier(expression)
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, "alias1", "alias2")

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(0, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with (Regions_4 AND (Subjects_2_1_2 OR Subjects_2_1_1)) (the query should return 1 record)
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect3() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag0 = getRecordWithId(cayenneContext, Tag.class, 20L)
        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 21L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        Expression expression0 = ExpressionFactory.matchExp("alias0." + TagRelation.TAG_PROPERTY, tag0)
        expression0 = expression0.andExp(ExpressionFactory.matchExp("alias0." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression1 = ExpressionFactory.matchExp("alias1." + TagRelation.TAG_PROPERTY, tag1)
        expression1 = expression1.andExp(ExpressionFactory.matchExp("alias1." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression2 = ExpressionFactory.matchExp("alias2." + TagRelation.TAG_PROPERTY, tag2)
        expression2 = expression2.andExp(ExpressionFactory.matchExp("alias2." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression = expression2.andExp(expression0.orExp(expression1))

        query.setQualifier(expression)
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, "alias0", "alias1", "alias2")

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with (Regions_4 AND (Subjects_2_1_2 OR Subjects_2_1_1)) (the query should return 1 record) <br/>
     * <br/>
     * (with reuse of aliases, shortening the JOINS section)
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect4() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag0 = getRecordWithId(cayenneContext, Tag.class, 20L)
        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 21L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        Expression expression0 = ExpressionFactory.matchExp("alias1." + TagRelation.TAG_PROPERTY, tag0)
        expression0 = expression0.andExp(ExpressionFactory.matchExp("alias1." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression1 = ExpressionFactory.matchExp("alias1." + TagRelation.TAG_PROPERTY, tag1)
        expression1 = expression1.andExp(ExpressionFactory.matchExp("alias1." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression2 = ExpressionFactory.matchExp("alias2." + TagRelation.TAG_PROPERTY, tag2)
        expression2 = expression2.andExp(ExpressionFactory.matchExp("alias2." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, TaggableClasses.COURSE))

        Expression expression = expression2.andExp(expression0.orExp(expression1))

        query.setQualifier(expression)
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, "alias0", "alias1", "alias2")

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with Subjects_2 or any subtags (there are 3 courses)
     */
    @Test
    void testTagTree() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Expression expression = createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 16L), TaggableClasses.COURSE)
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 17L), TaggableClasses.COURSE))
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 18L), TaggableClasses.COURSE))
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 19L), TaggableClasses.COURSE))
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 20L), TaggableClasses.COURSE))
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 21L), TaggableClasses.COURSE))
        expression = expression.orExp(createExpressionForTagAndEntity(getRecordWithId(cayenneContext, Tag.class, 22L), TaggableClasses.COURSE))

        query.setQualifier(expression)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(3, result.size(), "should return one result")
    }

    private Expression createExpressionForTagAndEntity(Tag tag, TaggableClasses entityIdentifier) {
        Expression result = ExpressionFactory.matchExp(Course.TAGGING_RELATIONS_PROPERTY + "+." + TagRelation.TAG_PROPERTY, tag)
        result = result.andExp(ExpressionFactory.matchExp(Course.TAGGING_RELATIONS_PROPERTY + "+." + TagRelation.ENTITY_IDENTIFIER_PROPERTY, entityIdentifier))
        return result
    }
}
