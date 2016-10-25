package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;

/**
 * Class _CourseClassPaymentPlanLine was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseClassPaymentPlanLine extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String AMOUNT_PROPERTY = "amount";
    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DAY_OFFSET_PROPERTY = "dayOffset";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Money> AMOUNT = new Property<Money>("amount");
    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Integer> DAY_OFFSET = new Property<Integer>("dayOffset");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<CourseClass> COURSE_CLASS = new Property<CourseClass>("courseClass");

    public void setAmount(Money amount) {
        writeProperty("amount", amount);
    }
    public Money getAmount() {
        return (Money)readProperty("amount");
    }

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDayOffset(Integer dayOffset) {
        writeProperty("dayOffset", dayOffset);
    }
    public Integer getDayOffset() {
        return (Integer)readProperty("dayOffset");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget("courseClass", courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty("courseClass");
    }


}
