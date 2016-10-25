package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.VoucherProduct;

/**
 * Class _VoucherProductCourse was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _VoucherProductCourse extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_PROPERTY = "course";
    public static final String VOUCHER_PRODUCT_PROPERTY = "voucherProduct";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Course> COURSE = new Property<Course>("course");
    public static final Property<VoucherProduct> VOUCHER_PRODUCT = new Property<VoucherProduct>("voucherProduct");

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


    public void setCourse(Course course) {
        setToOneTarget("course", course, true);
    }

    public Course getCourse() {
        return (Course)readProperty("course");
    }


    public void setVoucherProduct(VoucherProduct voucherProduct) {
        setToOneTarget("voucherProduct", voucherProduct, true);
    }

    public VoucherProduct getVoucherProduct() {
        return (VoucherProduct)readProperty("voucherProduct");
    }


}
