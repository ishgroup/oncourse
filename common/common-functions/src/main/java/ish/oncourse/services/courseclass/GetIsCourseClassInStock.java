package ish.oncourse.services.courseclass;

import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.application.FindOfferedApplication;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.QueryCacheStrategy;

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION;

public class GetIsCourseClassInStock {

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private CourseClass cc;
    private Contact c;
    private boolean isPaymentGatewayEnabled;

    private GetIsCourseClassInStock() {}

    public static GetIsCourseClassInStock valueOf(ObjectContext context, QueryCacheStrategy strategy, CourseClass courseClass, Contact contact, boolean isPaymentGatewayEnabled) {
        GetIsCourseClassInStock obj = new GetIsCourseClassInStock();
        obj.context = context;
        obj.strategy = strategy;
        obj.cc = courseClass;
        obj.c = contact;
        obj.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
        return obj;
    }

    public boolean get() {
        boolean isFinished = !cc.isCancelled() && cc.hasEnded();
        boolean isInStock = ((!isFinished && !cc.isCancelled() && cc.isHasAvailableEnrolmentPlaces()) || cc.getIsDistantLearningCourse()) && isPaymentGatewayEnabled;

        if (!isInStock) {
            if (ENROLMENT_BY_APPLICATION == cc.getCourse().getEnrolmentType()) {
                if (c != null && c.getStudent() != null) {
                    Application application = new FindOfferedApplication(cc.getCourse(), c.getStudent(), context, strategy).get();
                    if (application != null) {
                        isInStock = false;
                    }
                } else {
                    isInStock = true;
                }
            }
        }

        return isInStock;
    }
}
