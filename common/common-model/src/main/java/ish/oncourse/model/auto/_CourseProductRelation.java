package ish.oncourse.model.auto;

import ish.oncourse.model.Course;
import ish.oncourse.model.EntityRelation;
import ish.oncourse.model.Product;
import org.apache.cayenne.exp.Property;

/**
 * Class _CourseProductRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseProductRelation extends EntityRelation {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String COURSE_PROPERTY = "course";
    @Deprecated
    public static final String PRODUCT_PROPERTY = "product";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Course> COURSE = new Property<Course>("course");
    public static final Property<Product> PRODUCT = new Property<Product>("product");

    public void setCourse(Course course) {
        setToOneTarget("course", course, true);
    }

    public Course getCourse() {
        return (Course)readProperty("course");
    }


    public void setProduct(Product product) {
        setToOneTarget("product", product, true);
    }

    public Product getProduct() {
        return (Product)readProperty("product");
    }


}
