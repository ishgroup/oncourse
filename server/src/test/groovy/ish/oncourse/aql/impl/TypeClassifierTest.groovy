package ish.oncourse.aql.impl

import static ish.oncourse.aql.impl.TypeClassifier.BOOLEAN
import static ish.oncourse.aql.impl.TypeClassifier.DATE
import static ish.oncourse.aql.impl.TypeClassifier.ENUM
import static ish.oncourse.aql.impl.TypeClassifier.NUMERIC
import static ish.oncourse.aql.impl.TypeClassifier.PERSISTENT
import static ish.oncourse.aql.impl.TypeClassifier.STRING
import static ish.oncourse.aql.impl.TypeClassifier.UNKNOWN
import static ish.oncourse.aql.impl.TypeClassifier.of
import org.apache.cayenne.BaseDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.Persistent
import org.apache.cayenne.PersistentObject
import static org.junit.Assert.assertEquals
import org.junit.Test

class TypeClassifierTest {

    @Test
    void numericClassifier() {
        assertEquals(NUMERIC, of(byte.class))
        assertEquals(NUMERIC, of(char.class))
        assertEquals(NUMERIC, of(short.class))
        assertEquals(NUMERIC, of(int.class))
        assertEquals(NUMERIC, of(long.class))
        assertEquals(NUMERIC, of(float.class))
        assertEquals(NUMERIC, of(double.class))
        assertEquals(NUMERIC, of(Integer.class))
        assertEquals(NUMERIC, of(Double.class))
        assertEquals(NUMERIC, of(java.math.BigDecimal.class))
    }

    @Test
    void booleanClassifier() {
        assertEquals(BOOLEAN, of(boolean.class))
        assertEquals(BOOLEAN, of(Boolean.class))
    }

    @Test
    void stringClassifier() {
        assertEquals(STRING, of(String.class))
        assertEquals(STRING, of(CharSequence.class))
        assertEquals(STRING, of(StringBuilder.class))
    }

    @Test
    void dateClassifier() {
        assertEquals(DATE, of(java.util.Date.class))
        assertEquals(DATE, of(java.sql.Time.class))
        assertEquals(DATE, of(java.sql.Date.class))
        assertEquals(DATE, of(java.sql.Timestamp.class))
        assertEquals(DATE, of(java.time.LocalTime.class))
        assertEquals(DATE, of(java.time.LocalDate.class))
        assertEquals(DATE, of(java.time.LocalDateTime.class))
    }

    @Test
    void enumClassifier() {
        assertEquals(ENUM, of(Enum1.class))
        assertEquals(ENUM, of(Enum2.class))
        assertEquals(UNKNOWN, of(SomeClass.class))
    }

    @Test
    void persistentClassifier() {
        assertEquals(PERSISTENT, of(Persistent1.class))
        assertEquals(PERSISTENT, of(Persistent2.class))
        assertEquals(PERSISTENT, of(Persistent3.class))
        assertEquals(UNKNOWN, of(SomeClass.class))
    }

    // set of test classes
    private static class SomeClass {}
    private enum Enum1 {}
    private enum Enum2 {}
    private static class Persistent1 extends BaseDataObject {}
    private static class Persistent2 extends PersistentObject {}
    private static class Persistent3 implements Persistent {
        @Override
        ObjectId getObjectId() {
            return null
        }
        @Override
        void setObjectId(ObjectId id) {}
        @Override
        int getPersistenceState() {
            return 0
        }
        @Override
        void setPersistenceState(int state) {}
        @Override
        ObjectContext getObjectContext() {
            return null
        }
        @Override
        void setObjectContext(ObjectContext objectContext) {}
    }

}