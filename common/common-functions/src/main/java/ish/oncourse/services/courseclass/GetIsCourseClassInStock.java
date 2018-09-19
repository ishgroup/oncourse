package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;

public class GetIsCourseClassInStock {

    private CourseClass cc;
    private boolean isPaymentGatewayEnabled;

    private GetIsCourseClassInStock() {}

    public static GetIsCourseClassInStock valueOf(CourseClass courseClass, boolean isPaymentGatewayEnabled) {
        GetIsCourseClassInStock obj = new GetIsCourseClassInStock();
        obj.cc = courseClass;
        obj.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
        return obj;
    }

    public boolean get() {
        boolean isFinished = !cc.isCancelled() && cc.hasEnded();
        boolean isInStock = !isFinished && !cc.isCancelled() && cc.isHasAvailableEnrolmentPlaces() && isPaymentGatewayEnabled;
        return isInStock;
    }
}
