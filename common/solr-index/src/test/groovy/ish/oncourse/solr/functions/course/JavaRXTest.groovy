package ish.oncourse.solr.functions.course

import io.reactivex.Flowable
import ish.oncourse.solr.model.SCourse
import org.junit.Test

/**
 * User: akoiro
 * Date: 4/11/17
 */
class JavaRXTest {

    @Test
    void test_groupBy() {
        List<SCourse> courses = [
                new SCourse().with { it.id = 1; it },
                new SCourse().with { it.id = 2; it },
                new SCourse().with { it.id = 2; it },
                new SCourse().with { it.id = 3; it },
                new SCourse().with { it.id = 3; it },
                new SCourse().with { it.id = 3; it },
                new SCourse().with { it.id = 4; it },
                new SCourse().with { it.id = 4; it },
                new SCourse().with { it.id = 4; it },
                new SCourse().with { it.id = 4; it },
                new SCourse().with { it.id = 4; it },
        ]

        Flowable.fromIterable(courses).groupBy({ c -> c.id }, { c -> c })
                .flatMap { it.firstElement() }
                .subscribe({ println(it) })
    }

    @Test
    void test_window() {
        List<SCourse> courses = [
                new SCourse().with { it.id = 1; it },
                new SCourse().with { it.id = 2; it },
                new SCourse().with { it.id = 3; it },
                new SCourse().with { it.id = 4; it },
                new SCourse().with { it.id = 5; it },
                new SCourse().with { it.id = 6; it },
                new SCourse().with { it.id = 7; it },
                new SCourse().with { it.id = 8; it },
                new SCourse().with { it.id = 9; it },
                new SCourse().with { it.id = 10; it },
                new SCourse().with { it.id = 11; it },
        ]

        Flowable.fromIterable(courses)
                .window(2)
                .flatMap({ it.doOnTerminate({println("1")}) }).subscribe({ println it })

    }
}
