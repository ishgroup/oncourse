package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.services.discount.*;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.utils.CourseContext;
import ish.oncourse.util.FormatUtils;
import ish.util.DiscountUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.Format;
import java.util.Date;
import java.util.List;

public class CourseClassPrice extends ISHCommon {

	@Inject
	private IDiscountService discountService;

    @Inject
    private PreferenceController preferenceController;

	@SuppressWarnings("all")
	@Inject
	private PreferenceController preferenceService;

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format feeFormat;

	private Money discountedFee;

	@Property
	private DiscountItem discountItem;

	@Property
	private List<DiscountItem> discountItems;

	@Property
	private Money discountValue = Money.ZERO;

	@Property
	private Date discountExpiryDate;

	@Property
	private Format dateFormat;

	@Property
	private String appliedDiscountsTitle;
	
	@Parameter
	private Money feeOverride;

	private void fillAppliedDiscounts() {
		CourseContext context = (CourseContext) request.getAttribute(CourseItem.COURSE_CONTEXT);
		List<Discount> promotions = context == null ? discountService.getPromotions() : context.getDiscounts();
		List<DiscountCourseClass> allApplicable = GetAppliedDiscounts.valueOf(courseClass, promotions, null).get();
		DiscountCourseClass bestDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(allApplicable, courseClass.getFeeExGst(), courseClass.getTaxRate());
		
		if (bestDiscount != null) {
			discountedFee = courseClass.getDiscountedFeeIncTax(bestDiscount, null);
			discountValue = courseClass.getFeeIncGst(null).subtract(discountedFee);
			fillAppliedDiscountsTooltip(bestDiscount);
		}
	}
	
	private void fillAppliedDiscountsTooltip(DiscountCourseClass applicableDiscount) {
		StringBuilder appliedDiscountsTitleBuf = new StringBuilder(applicableDiscount.getDiscount().getName());
		discountExpiryDate = WebDiscountUtils.expiryDate(applicableDiscount.getDiscount(), courseClass.getStartDate());
		if (discountExpiryDate != null) {
			appliedDiscountsTitleBuf.append(" expires ").append(dateFormat.format(discountExpiryDate));
		}
		appliedDiscountsTitle = appliedDiscountsTitleBuf.toString();
	}

	private void fillPossibleDiscounts() {
		List<DiscountCourseClass> potentialDiscounts = GetPossibleDiscounts.valueOf(courseClass).get();
		discountItems = WebDiscountUtils.sortByDiscountValue(potentialDiscounts, courseClass.getFeeExGst(), courseClass.getTaxRate());
	}

	@SetupRender
	public void beforeRender() {
		feeFormat = null;
		discountedFee = null;
		discountItems = null;
		discountValue = Money.ZERO;
		discountExpiryDate = null;
		dateFormat = null;
		appliedDiscountsTitle = null; 
		dateFormat = FormatUtils.getShortDateFormat(courseClass.getCollege().getTimeZone());
		fillAppliedDiscounts();
		fillPossibleDiscounts();
	}

	public boolean isHasFee() {
		return courseClass.getFeeExGst() != null;
	}

	public Format getFeeFormat() {
		return this.feeFormat;
	}

	public boolean isHasDiscountValue() {
		return !discountValue.isZero();
	}

	public boolean isTaxExempt() {
		return courseClass.isGstExempt();
	}

	public boolean isShowTax() {
		return courseClass.getFeeIncGst(null).isGreaterThan(Money.ZERO);
	}

	public Money getFee() {
		Money feeIncGst = courseClass.getFeeIncGst(null);
		feeFormat = FormatUtils.chooseMoneyFormat(feeIncGst);
		return feeIncGst;
	}
	
	public Money getFeeOverride() {
		feeFormat = FormatUtils.chooseMoneyFormat(feeOverride);
		if (isTaxExempt()) {
			return feeOverride;
		} else {
			return feeOverride.multiply(BigDecimal.ONE.add(courseClass.getTaxRate()));
		}
	}

	public boolean isOverridden() {
		return feeOverride != null;
	}
	
	public Money getDiscountedFee() {
		feeFormat = FormatUtils.chooseMoneyFormat(discountedFee);
		return discountedFee;
	}

    public boolean isPaymentGatewayEnabled() {
        return preferenceController.isPaymentGatewayEnabled();
    }
}
