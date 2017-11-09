package ish.oncourse.solr.reindex

import com.github.javafaker.Faker
import ish.oncourse.solr.functions.course.SCourseFunctions
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.model.STag
import org.apache.commons.lang3.time.DateUtils
import org.apache.solr.common.SolrDocumentList
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import java.util.concurrent.TimeUnit

class RealSolrReindexCoursesJobTest {

    private RealSolrReindexTemplate<STag> template

    @Before
    void before() {
        template = RealSolrReindexTemplate.valueOf('courses-local', getCourses, compareCollections)
    }

    @Ignore
    @Test
    void test() {

        long start = System.currentTimeMillis()

        int i = 0;

        SCourseFunctions.SolrCourses({template.runtime.newContext()}, DateUtils.addYears(new Date(), -40)).blockingSubscribe( {println(it.tutorId)})

        println(System.currentTimeMillis() - start)



//        List<SolrCourseClass> source = [
//                new SolrCourseClass().with { classCode = "1"; it },
//                new SolrCourseClass().with { classCode = "2"; it },
//                new SolrCourseClass().with { classCode = "3"; it }
//        ]
//
//        Observable.just(new SolrCourse())
//                .flatMap({
//                    SolrCourse c -> Observable.fromIterable(source).map({
//                        SolrCourseClass cc ->
//                        c.classStart.add(cc.classStart)
//                        c.classEnd.add(cc.classEnd)
//                        c.price.add(cc.price)
//                        c.classCode.add(cc.classCode)
//                            return c
//                    })
//        }).lastElement().subscribe({println it})

//        Observable
//                .just("a", "b")
//                .flatMap({ s ->
//            Observable.range(0, 100)
//                    .map({ i -> String.format("Here's an Integer(%s), with String(%s)", i, s)
//            })
//        }).subscribe({println it})

//        Observable.just(new SolrCourse()).flatMap(new Function<SolrCourse, ObservableSource<SolrCourseClass>>() {
//            @Override
//            ObservableSource<SolrCourseClass> apply(SolrCourse o) {
//                return
//            }
//        }).subscribe({
//            println it
//        })

//        Observable.fromIterable({ Functions.getCourses(template.runtime.newContext()) })
//                .flatMap({ c -> Observable.just(Functions.getSolrCourse(c)) })
//                .subscribe({ println it })

        //template.test()
    }

    @Ignore
    @Test
    void testFullIntegration() {
        template.testFullIntegration()
    }

    private static compareCollections = { SolrDocumentList c1, SolrDocumentList c2 -> }

    private static Closure<Iterator<SCourse>> getCourses = {
        Faker faker = new Faker()
        SCourse course = new SCourse().with {
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
    }

}
