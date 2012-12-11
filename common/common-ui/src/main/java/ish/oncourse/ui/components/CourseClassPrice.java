package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.PotentialDiscountsPolicy;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.utils.DiscountFeeComparator;
import ish.oncourse.utils.DiscountUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CourseClassPrice {

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
	private Discount discountItem;

	@Property
	private List<Discount> potentialDiscounts;

	@Property
	private Money discountValue;

	private List<Discount> applicableDiscounts;

	@Property
	private Date discountExpiryDate;

	@Property
	private Format dateFormat;

	@Property
	private String appliedDiscountsTitle;
	
	private void fillAppliedDiscounts() {
		List<Discount> promotions = discountService.getPromotions();
		applicableDiscounts = courseClass.getDiscountsToApply(new PotentialDiscountsPolicy(promotions));
		discountedFee = courseClass.getDiscountedFeeIncTax(applicableDiscounts);
		discountValue = courseClass.getDiscountAmountIncTax(applicableDiscounts);
		discountExpiryDate = DiscountUtils.earliestExpiryDate(applicableDiscounts);
		fillAppliedDiscountsTooltip();
	}
	
	private void fillAppliedDiscountsTooltip() {
		dateFormat = FormatUtils.getShortDateFormat(courseClass.getCollege().getTimeZone());
		if (!applicableDiscounts.isEmpty()) {
			StringBuffer appliedDiscountsTitleBuf = new StringBuffer(applicableDiscounts.get(0).getName());
			if (applicableDiscounts.size() > 1) {
				appliedDiscountsTitleBuf.append(" combined with: ");
				for (int i = 1; i < applicableDiscounts.size(); i++) {
					appliedDiscountsTitleBuf.append(applicableDiscounts.get(i).getName());
					if (i != applicableDiscounts.size() - 1) {
						appliedDiscountsTitleBuf.append(", ");
					}
				}
			}
			if (discountExpiryDate != null) {
				appliedDiscountsTitleBuf.append(" expires ").append(dateFormat.format(discountExpiryDate));
			}
			appliedDiscountsTitle = appliedDiscountsTitleBuf.toString();
		}
	}
	
	private void fillPosibleDiscounts() {
		Expression notHiddenDiscounts = ExpressionFactory.matchExp(Discount.HIDE_ON_WEB_PROPERTY, false);
		potentialDiscounts = notHiddenDiscounts.filterObjects(DiscountUtils.getFilteredDiscounts(courseClass));
		Collections.sort(potentialDiscounts, new DiscountFeeComparator(courseClass));
	}

	@SetupRender
	public void beforeRender() {
		fillAppliedDiscounts();
		fillPosibleDiscounts();
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

    public boolean isPaymentGatewayEnabled() {
        return preferenceController.isPaymentGatewayEnabled();
    }

}
