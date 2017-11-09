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
                new SCourse().with {it.id = 1},
                new SCourse().with {it.id = 2},
                new SCourse().with {it.id = 2},
                new SCourse().with {it.id = 3},
                new SCourse().with {it.id = 3},
                new SCourse().with {it.id = 3},
                new SCourse().with {it.id = 4},
                new SCourse().with {it.id = 4},
                new SCourse().with {it.id = 4},
                new SCourse().with {it.id = 4},
                new SCourse().with {it.id = 4},
        ]


        Flowable.fromIterable(courses).groupBy({c -> println(c);c}, {c -> println(c);c}). subscribe({println(it)})
    }
}
