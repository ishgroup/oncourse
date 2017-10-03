package ish.oncourse.solr.reindex

import com.github.javafaker.Faker
import ish.oncourse.solr.model.SolrCourse
import ish.oncourse.solr.model.SolrTag
import org.apache.solr.common.SolrDocumentList
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import java.util.concurrent.TimeUnit

class RealSolrReindexCoursesJobTest {

    private RealSolrReindexTemplate<SolrTag> template

    @Before
    void before() {
        template = RealSolrReindexTemplate.valueOf('courses-local', getCourses, compareCollections)
    }

    @Ignore
    @Test
    void test() {
        template.test()
    }

    @Ignore
    @Test
    void testFullIntegration() {
        template.testFullIntegration()
    }

    private static compareCollections = { SolrDocumentList c1, SolrDocumentList c2 -> }

    private static Closure<Iterator<SolrCourse>> getCourses = {
        Faker faker = new Faker()
        SolrCourse course = new SolrCourse().with {
            id = faker.number().randomNumber()
            collegeId = 299
            name = faker.book().title()
            detail = faker.book().title()
            code = faker.bothify(name)
            startDate = faker.date().future(1, TimeUnit.DAYS)
            price = [faker.commerce().price(), faker.commerce().price(), faker.commerce().price()]
            classCode = [faker.commerce().promotionCode(), faker.commerce().promotionCode(), faker.commerce().promotionCode()]
            classEnd = [faker.date().future(5, TimeUnit.DAYS), faker.date().future(6, TimeUnit.DAYS), faker.date().future(7, TimeUnit.DAYS)]
            classStart = [faker.date().future(1, TimeUnit.DAYS), faker.date().future(2, TimeUnit.DAYS), faker.date().future(3, TimeUnit.DAYS)]
            tutorId = [faker.number().randomNumber(), faker.number().randomNumber(), faker.number().randomNumber()]
            tutor = [faker.name().fullName(), faker.name().fullName(), faker.name().fullName()]
            when = ["saturday weekend evening",
                    "sunday weekend evening",
                    "friday weekday evening",
                    "tuesday weekday evening",
                    "wednesday weekday evening"]
            siteId = [faker.number().randomNumber(), faker.number().randomNumber(), faker.number().randomNumber()]
            location = ["-34.92862120,138.59995940", "-34.92862120,138.59995940", "-34.92862120,138.59995940"]
            postcode = ["5000", "2000", "2001"]
            suburb = ["Adelaide", "Sydney", "Sydney"]
            tagId = [faker.number().randomNumber(), faker.number().randomNumber(), faker.number().randomNumber()]
            it
        }
        return Collections.singleton(course).iterator()
    } c

}
