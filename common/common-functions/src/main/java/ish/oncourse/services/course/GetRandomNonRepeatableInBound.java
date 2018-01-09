package ish.oncourse.services.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetRandomNonRepeatableInBound {

    private int upperBound;
    private int count;

    private GetRandomNonRepeatableInBound() {}

    public static GetRandomNonRepeatableInBound valueOf(int upperBound, int count) {
        GetRandomNonRepeatableInBound getter = new GetRandomNonRepeatableInBound();
        getter.upperBound = upperBound;
        getter.count = count;
        return getter;
    }

    public List<Integer> get() {
        List<Integer> indexes = new ArrayList<>();
        if (upperBound > 0) {
            Random gen = new Random();
            while (indexes.size() < count) {
                int randomIndex = gen.nextInt(upperBound);
                if (!indexes.contains(randomIndex)) {
                    indexes.add(randomIndex);
                }
                if (upperBound == indexes.size()) {
                    break;
                }
            }
        }
        return indexes;
    }
}
