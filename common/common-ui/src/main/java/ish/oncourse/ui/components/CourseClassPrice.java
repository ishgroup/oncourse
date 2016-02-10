package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.PotentialDiscountsPolicy;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.utils.DiscountFeeComparator;
import ish.oncourse.utils.WebDiscountUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
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
	private Money discountValue;

	private DiscountCourseClass applicableDiscount;

	@Property
	private Date discountExpiryDate;

	@Property
	private Format dateFormat;

	@Property
	private String appliedDiscountsTitle;
	
	@Parameter
	private Money feeOverride;
	
	private void fillAppliedDiscounts() {
		List<Discount> promotions = discountService.getPromotions();
		applicableDiscount = courseClass.getDiscountToApply(new PotentialDiscountsPolicy(promotions));
		
		discountedFee = courseClass.getDiscountedFeeIncTax(applicableDiscount);
		discountValue = courseClass.getDiscountAmountIncTax(applicableDiscount);
		if (applicableDiscount != null) {
			discountExpiryDate = WebDiscountUtils.expiryDate(applicableDiscount.getDiscount(), courseClass);
		}
		fillAppliedDiscountsTooltip();
	}
	
	private void fillAppliedDiscountsTooltip() {
		dateFormat = FormatUtils.getShortDateFormat(courseClass.getCollege().getTimeZone());
		if (applicableDiscount != null) {
			StringBuilder appliedDiscountsTitleBuf = new StringBuilder(applicableDiscount.getDiscount().getName());
			if (discountExpiryDate != null) {
				appliedDiscountsTitleBuf.append(" expires ").append(dateFormat.format(discountExpiryDate));
			}
			appliedDiscountsTitle = appliedDiscountsTitleBuf.toString();
		}
	}

	private void fillPossibleDiscounts() {
		Expression notHiddenDiscounts = DiscountCourseClass.DISCOUNT.dot(Discount.HIDE_ON_WEB).isFalse();
		List<DiscountCourseClass> potentialDiscounts = notHiddenDiscounts.filterObjects(WebDiscountUtils.getFilteredDiscounts(courseClass));
		Collections.sort(potentialDiscounts, new DiscountFeeComparator(courseClass));

		Money money = null;
		DiscountItem discountItem = null;

		discountItems = new ArrayList<>();

		for (DiscountCourseClass discountCourseClass : potentialDiscounts) {
			Money dMoney = courseClass.getDiscountedFeeIncTax(discountCourseClass);
			if (money == null || !money.equals(dMoney))
			{
				money = courseClass.getDiscountedFeeIncTax(discountCourseClass);
				discountItem = new DiscountItem();
				discountItem.setFeeIncTax(money);
				discountItems.add(discountItem);
			}
			discountItem.addDiscount(discountCourseClass.getDiscount());
			discountItem.init();
		}
	}

	@SetupRender
	public void beforeRender() {
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
		return courseClass.getFeeIncGst().isGreaterThan(Money.ZERO);
	}

	public Money getFee() {
		Money feeIncGst = courseClass.getFeeIncGst();
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


	public static final class DiscountItem
	{
		private static final String DIVIDER = " / ";
		private List<Discount> discounts = new ArrayList<>();
		private Money feeIncTax;


		private Format feeFormat;
		private String title;

		public void init()
		{
			List<String> strings = new ArrayList<>();
			for (Discount  discount : discounts) {
				strings.add(discount.getName());
			}
			title = StringUtils.join(strings, DIVIDER);
			feeFormat = FormatUtils.chooseMoneyFormat(feeIncTax);
		}

		public void addDiscount(Discount discount)
		{
			discounts.add(discount);
		}

		public String getTitle()
		{
			return title;
		}

		public Money getFeeIncTax() {
			return feeIncTax;
		}

		public void setFeeIncTax(Money feeIncTax)
		{
			this.feeIncTax = feeIncTax;
		}

		public Format getFeeFormat()
		{
			return feeFormat;
		}

	}
}
