package ish.oncourse.enrol.components.checkout;

import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.List;


public class EnrolmentItem {
    static final Logger logger = LogManager.getLogger();

    @Parameter(required = true)
    @Property
    private Enrolment enrolment;

    @Parameter(required = true)
    @Property
    private Integer contactIndex;

    @Parameter(required = true)
    @Property
    private Integer enrolmentIndex;

    @Parameter(required = true)
    @Property
    private EnrolmentItemDelegate delegate;

    @Property
    @Parameter(required = true)
    private Boolean checked;

    @Property
    @Parameter(required = false)
    private String error;

    @Property
    private DateFormat dateFormat;

    @Inject
    private Request request;

    @Parameter(required = false)
    private Block blockToRefresh;

    @SetupRender
    void beforeRender() {
        dateFormat = FormatUtils.getDateFormat("EEE d MMM yy h:mm a", enrolment.getCourseClass().getTimeZone());
    }

    public Integer[] getActionContext() {
        return new Integer[]{contactIndex, enrolmentIndex};
    }

    public String getEnrolmentClass() {
        return checked ? StringUtils.EMPTY: "disabled";
    }


    @OnEvent(value = "selectEnrolmentEvent")
    public Object selectEnrolment(Integer contactIndex, Integer enrolmentIndex) {
        if (!request.isXHR())
            return null;
        if (delegate != null) {
            delegate.onChange(contactIndex, enrolmentIndex);
            if (blockToRefresh != null)
                return blockToRefresh;
        }
        return null;
    }

    public boolean isCommenced() {
        return enrolment.getCourseClass().hasStarted() && !enrolment.getCourseClass().hasEnded();
    }


    public static interface EnrolmentItemDelegate {
        public void onChange(Integer contactIndex, Integer enrolmentIndex);
        public List<Discount> getDiscounts();
    }
}
