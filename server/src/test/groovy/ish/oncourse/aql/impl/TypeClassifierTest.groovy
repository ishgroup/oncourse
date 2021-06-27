package ish.oncourse.aql.impl

import groovy.transform.CompileStatic
import org.apache.cayenne.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static ish.oncourse.aql.impl.TypeClassifier.*

@CompileStatic
class TypeClassifierTest {

    @Test
    void numericClassifier() {
        Assertions.assertEquals(NUMERIC, of(byte.class))
        Assertions.assertEquals(NUMERIC, of(char.class))
        Assertions.assertEquals(NUMERIC, of(short.class))
        Assertions.assertEquals(NUMERIC, of(int.class))
        Assertions.assertEquals(NUMERIC, of(long.class))
        Assertions.assertEquals(NUMERIC, of(float.class))
        Assertions.assertEquals(NUMERIC, of(double.class))
        Assertions.assertEquals(NUMERIC, of(Integer.class))
        Assertions.assertEquals(NUMERIC, of(Double.class))
        Assertions.assertEquals(NUMERIC, of(java.math.BigDecimal.class))
    }

    @Test
    void booleanClassifier() {
        Assertions.assertEquals(BOOLEAN, of(boolean.class))
        Assertions.assertEquals(BOOLEAN, of(Boolean.class))
    }

    @Test
    void stringClassifier() {
        Assertions.assertEquals(STRING, of(String.class))
        Assertions.assertEquals(STRING, of(CharSequence.class))
        Assertions.assertEquals(STRING, of(StringBuilder.class))
    }

    @Test
    void dateClassifier() {
        Assertions.assertEquals(DATE, of(java.util.Date.class))
        Assertions.assertEquals(DATE, of(java.sql.Time.class))
        Assertions.assertEquals(DATE, of(java.sql.Date.class))
        Assertions.assertEquals(DATE, of(java.sql.Timestamp.class))
        Assertions.assertEquals(DATE, of(java.time.LocalTime.class))
        Assertions.assertEquals(DATE, of(java.time.LocalDate.class))
        Assertions.assertEquals(DATE, of(java.time.LocalDateTime.class))
    }

    @Test
    void enumClassifier() {
        Assertions.assertEquals(ENUM, of(Enum1.class))
        Assertions.assertEquals(ENUM, of(Enum2.class))
        Assertions.assertEquals(UNKNOWN, of(SomeClass.class))
    }

    @Test
    void persistentClassifier() {
        Assertions.assertEquals(PERSISTENT, of(Persistent1.class))
        Assertions.assertEquals(PERSISTENT, of(Persistent2.class))
        Assertions.assertEquals(PERSISTENT, of(Persistent3.class))
        Assertions.assertEquals(UNKNOWN, of(SomeClass.class))
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