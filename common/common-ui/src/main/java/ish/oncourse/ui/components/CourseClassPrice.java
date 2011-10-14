package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.PotentialDiscountsPolicy;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.ui.utils.FormatUtils;
import ish.oncourse.utils.DiscountUtils;
import ish.persistence.CommonPreferenceController;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassPrice {

	@Inject
	private IDiscountService discountService;

	@Inject
	private CommonPreferenceController preferenceService;

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format feeFormat;

	private Money discountedFee;

	@Property
	private Discount discountItem;

	@Property
	private List<Discount> discountsWithConcessions;

	@Property
	private Money discountValue;

	private List<Discount> applicableDiscounts;

	@Property
	private Date discountExpiryDate;

	@Property
	private String discountEligibility;

	@Property
	private Format dateFormat;

	@Property
	private String appliedDiscountsTitle;

	@SetupRender
	public void beforeRender() {

		List<Discount> promotions = discountService.getPromotions();
		applicableDiscounts = courseClass.getDiscountsToApply(new PotentialDiscountsPolicy(promotions));
		discountedFee = courseClass.getDiscountedFeeIncTax(applicableDiscounts);
		discountValue = courseClass.getDiscountAmountIncTax(applicableDiscounts);
		discountExpiryDate = DiscountUtils.earliestExpiryDate(applicableDiscounts);
		discountEligibility = DiscountUtils.getEligibilityConditions(applicableDiscounts);

		if (!applicableDiscounts.isEmpty()) {
			StringBuffer appliedDiscountsTitleBuf = new StringBuffer(applicableDiscounts.get(0).getName());
			if (applicableDiscounts.size() > 1) {
				appliedDiscountsTitleBuf.append(" combined with: ");
				for (int i = 1; i < applicableDiscounts.size(); i++) {
					appliedDiscountsTitleBuf.append(applicableDiscounts.get(i));
					if (i != applicableDiscounts.size() - 1) {
						appliedDiscountsTitleBuf.append(", ");
					}
				}
			}
			appliedDiscountsTitle = appliedDiscountsTitleBuf.toString();
		}

		discountsWithConcessions = courseClass.getConcessionDiscounts();

		Ordering ordering = new Ordering(Discount.NAME_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(discountsWithConcessions);

		dateFormat = FormatUtils.getShortDateFormat(courseClass.getCollege().getTimeZone());
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

	public Money getDiscountItemFeeIncTax() {
		ArrayList<Discount> disc = new ArrayList<Discount>(1);
		disc.add(discountItem);
		Money discountedFeeIncTax = courseClass.getDiscountedFeeIncTax(disc);
		feeFormat = FormatUtils.chooseMoneyFormat(discountedFeeIncTax);
		return discountedFeeIncTax;
	}

	public Money getFee() {
		Money feeIncGst = courseClass.getFeeIncGst();
		feeFormat = FormatUtils.chooseMoneyFormat(feeIncGst);
		return feeIncGst;
	}

	public Money getDiscountedFee() {
		feeFormat = FormatUtils.chooseMoneyFormat(discountedFee);
		return discountedFee;
	}

}
