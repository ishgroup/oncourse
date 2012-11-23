package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.utils.EnrolCoursesController;
import ish.oncourse.services.discount.IDiscountService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;

@Deprecated
public class EnrolmentPaymentProcessing {

    private static final Logger LOGGER = Logger.getLogger(EnrolmentPaymentProcessing.class);

	/**
	 * ish services
	 */

	@InjectComponent
	private EnrolmentPaymentResult result;

    @InjectPage
    private EnrolCourses enrolCourses;
    
    @Inject
    private IDiscountService discountService;

	/**
	 * The processHolder displays its content while this method is being
	 * performed and when it is finished, the
	 * {@link EnrolmentPaymentProcessing#result} component is shown instead the
	 * processHolder's content.
	 *
	 * @return the result block. {@see EnrolmentPaymentResult}
	 * @throws Exception
	 */
	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object performGateway() throws Exception {
        /**
         *  Workaround to exclude NullPointerException on context synchronize block. Unknown reason. (possible reason is expired session).
         */
        try {
            getController().processPayment(discountService.getPromotions());
        } catch (Exception e) {
            LOGGER.warn("Unexpected Exception", e);
            result.setUnexpectedException(e);
        }
        return result;
    }
	
	/**
	 * @return the controller
	 */
	EnrolCoursesController getController() {
		return enrolCourses.getController();
	}

}
