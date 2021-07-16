/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.queries

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.cayenne.TagUtil
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Tag
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
@DatabaseSetup(value = "ish/queries/expressions-test.xml")
class ExpressionsTest2 extends TestWithDatabase {

    /**
     * tests a simple query. queries for Course records tagged with Regions_1 (there is one course meeting the criteria)
     */
    @Test
    void testSingleTag() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag = getRecordWithId(cayenneContext, Tag.class, 102L)
        List<Tag> list = new ArrayList<>()
        list.add(tag)

        Expression expression = TagUtil.createExpressionForTagsWithinOneTagTree(tag.getRoot().hashCode() + "", list, TaggableClasses.COURSE)
        query.setQualifier(expression)
        String alias = "alias" + tag.getRoot().hashCode()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "testSingleTag")
        Assertions.assertEquals(tag.getName(), result.get(0).getNotes(), "testSingleTag")
    }

    /**
     * tests a simple query. queries for Course records tagged with Regions_2 and code 'BB' (there is one course matching the criteria)
     */
    @Test
    void testSingleTagWithExtraExpression() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag = getRecordWithId(cayenneContext, Tag.class, 103L)
        List<Tag> list = new ArrayList<>()
        list.add(tag)

        Expression expression = TagUtil.createExpressionForTagsWithinOneTagTree(tag.getRoot().hashCode() + "", list, TaggableClasses.COURSE)
        expression = expression.andExp(ExpressionFactory.matchExp(Course.CODE_PROPERTY, "BB"))
        query.setQualifier(expression)
        String alias = "alias" + tag.getRoot().hashCode()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "testSingleTag")
        Assertions.assertEquals(tag.getName(), result.get(0).getNotes(), "testSingleTag")
    }

    /**
     * queries for Course records tagged with Regions_2 OR Subjects_3_4 <br/>
     * it should fail, as the tags belong to different trees
     */
    @Test
    void testTwoTagsDifferentTagTrees() {
        Assertions.assertThrows(IllegalArgumentException.class, { ->

            Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 27L)
            Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 103L)
            List<Tag> list = new ArrayList<>()
            list.add(tag1)
            list.add(tag2)

            TagUtil.createExpressionForTagsWithinOneTagTree(tag1.getRoot().hashCode() + "", list, TaggableClasses.COURSE)
        })

    }

    /**
     * queries for Course records tagged with Regions_2 OR Subjects_3_4 <br/>
     * not really used in oncourse, as normally the two tag trees expressions are ANDed
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 27L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 103L)
        List<Tag> list1 = new ArrayList<>()
        list1.add(tag1)
        List<Tag> list2 = new ArrayList<>()
        list2.add(tag2)

        Expression expression1 = TagUtil.createExpressionForTagsWithinOneTagTree(tag1.getRoot().hashCode() + "", list1, TaggableClasses.COURSE)
        Expression expression2 = TagUtil.createExpressionForTagsWithinOneTagTree(tag2.getRoot().hashCode() + "", list2, TaggableClasses.COURSE)
        Expression expression = expression1.orExp(expression2)
        query.setQualifier(expression)
        String alias1 = "alias" + tag1.getRoot().hashCode()
        String alias2 = "alias" + tag2.getRoot().hashCode()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias1, alias2)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(3, result.size(), "testSingleTag")
    }

    /**
     * queries for Course records tagged with Regions_4 AND Subjects_2_1_2 (there is no course tagged with both)
     */
    @Test
    void testAndQueryForOneRecordWithTwoTagsCorrect2() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        Tag tag1 = getRecordWithId(cayenneContext, Tag.class, 21L)
        Tag tag2 = getRecordWithId(cayenneContext, Tag.class, 107L)

        List<Tag> list1 = new ArrayList<>()
        list1.add(tag1)
        List<Tag> list2 = new ArrayList<>()
        list2.add(tag2)

        Expression expression1 = TagUtil.createExpressionForTagsWithinOneTagTree(tag1.getRoot().hashCode() + "", list1, TaggableClasses.COURSE)
        Expression expression2 = TagUtil.createExpressionForTagsWithinOneTagTree(tag2.getRoot().hashCode() + "", list2, TaggableClasses.COURSE)
        Expression expression = expression1.andExp(expression2)
        query.setQualifier(expression)
        String alias1 = "alias" + tag1.getRoot().hashCode()
        String alias2 = "alias" + tag2.getRoot().hashCode()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias1, alias2)

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

        List<Tag> list1 = new ArrayList<>()
        list1.add(tag0)
        list1.add(tag1)
        List<Tag> list2 = new ArrayList<>()
        list2.add(tag2)

        Expression expression1 = TagUtil.createExpressionForTagsWithinOneTagTree(tag1.getRoot().hashCode() + "", list1, TaggableClasses.COURSE)
        Expression expression2 = TagUtil.createExpressionForTagsWithinOneTagTree(tag2.getRoot().hashCode() + "", list2, TaggableClasses.COURSE)
        Expression expression = expression1.andExp(expression2)
        query.setQualifier(expression)
        String alias1 = "alias" + tag1.getRoot().hashCode()
        String alias2 = "alias" + tag2.getRoot().hashCode()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias1, alias2)

        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(1, result.size(), "should return one result")
    }

    /**
     * queries for Course records tagged with Subjects_2 or any subtags (there are 3 courses)
     */
    @Test
    void testTagTree() {
        SelectQuery<Course> query = SelectQuery.query(Course.class)

        List<Tag> list1 = new ArrayList<>()
        list1.add(getRecordWithId(cayenneContext, Tag.class, 16L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 17L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 18L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 19L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 20L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 21L))
        list1.add(getRecordWithId(cayenneContext, Tag.class, 22L))
        Expression expression1 = TagUtil.createExpressionForTagsWithinOneTagTree(list1.get(0).getRoot().getName(), list1, TaggableClasses.COURSE)
        query.setQualifier(expression1)
        String alias1 = "alias" + list1.get(0).getRoot().getName()
        query.aliasPathSplits(Course.TAGGING_RELATIONS_PROPERTY, alias1)
        List<Course> result = cayenneContext.select(query)

        Assertions.assertEquals(3, result.size(), "should return one result")
    }
}
