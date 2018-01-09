package ish.oncourse.services.course;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetRandomNonRepeatableInBoundTest {

    @Test
    public void test() {
        List<Integer> indexes = GetRandomNonRepeatableInBound.valueOf(50, 50).get();
        assertEquals(50, indexes.size());
        Set<Integer> setForRemoveDuplicates = new HashSet<>(indexes);
        assertEquals(50, setForRemoveDuplicates.size());

        // special case: upper bound is less than count of indexes
        indexes = GetRandomNonRepeatableInBound.valueOf(2, 3).get();
        assertEquals("Max capacity of number sequence with lower bound 0 and upperBound of 2 are {0, 1}", 2, indexes.size());
        indexes.forEach(index -> {
            assertTrue( index >= 0 && index < 2);
        });
    }

    @Test
    public void zeroAndNegativeTest() {
        assertEquals("Indexes list should be empty if upper bound is 0", 0, GetRandomNonRepeatableInBound.valueOf(0, 1).get().size());
        assertEquals("Indexes list should be empty if upper bound < 0", 0, GetRandomNonRepeatableInBound.valueOf(-Integer.MAX_VALUE, 3).get().size());

        assertEquals("Indexes list should be empty if needed count is 0", 0, GetRandomNonRepeatableInBound.valueOf(1, 0).get().size());
        assertEquals("Indexes list should be empty if needed count < 0", 0, GetRandomNonRepeatableInBound.valueOf(3, -Integer.MAX_VALUE).get().size());

        assertEquals("Indexes list should be empty if upper bound and needed count < 0", 0, GetRandomNonRepeatableInBound.valueOf(-Integer.MAX_VALUE, -Integer.MAX_VALUE).get().size());
        assertEquals("Indexes list should be empty if upper bound and needed count are 0", 0, GetRandomNonRepeatableInBound.valueOf(0, 0).get().size());
    }
}
