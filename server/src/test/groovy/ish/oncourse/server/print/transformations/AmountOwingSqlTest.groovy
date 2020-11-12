package ish.oncourse.server.print.transformations

import org.junit.Test
import org.springframework.util.Assert

/**
 * Created by akoiro on 9/06/2016.
 */
class AmountOwingSqlTest {

    @Test
    void test() {
        AmountOwingSql amountOwingSql = new AmountOwingSql()
        Assert.notNull(amountOwingSql.sqlResult)
    }
}
