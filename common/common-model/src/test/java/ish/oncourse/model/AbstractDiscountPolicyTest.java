package ish.oncourse.model;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Contains fields and tests common for {@link PotentialDiscountsPolicyTest} and {@link ish.oncourse.model.GetDiscountForEnrolment}
 * @author ksenia
 *
 */
public abstract class AbstractDiscountPolicyTest {
	protected static final Money FEE_EX_GST = new Money("100");

	protected static final Money FEE_GST = new Money("10");
	/**
	 * Combinable discount with amount=10.
	 */
	public static Discount combDiscountWithAmount;
	/**
	 * Not combinable discount with the rate 20% and zero restrictions.
	 */
	public static Discount singleDiscountWithRate;
	/**
	 * Combinable discount with the rate 20% and maximum 10.
	 */
	public static Discount combDiscountWithRateMax;
	/**
	 * Not combinable discount with the rate 10% and minimum 15.
	 */
	public static Discount singleDiscountWithRateMin;
	/**
	 * Combinable discount for $10 amount which has hideOnWeb property set to true
	 */
	public static Discount hiddenDiscountWithAmount;
	/**
	 * Combinable discount for $10 amount which has isAvailableOnWeb property set to false
	 */
	public static Discount nonAvailableDiscountWithAmount;
	/**
	 * Not combinable discount for $50 amount which has minEnrolment = 1 and minValue = $200
	 */
	public static Discount minValueConditionDiscount;
	/**
	 * Not combinable discount for $60 amount which has minEnrolment = 2 and minValue = $200
	 */
	public static Discount minCountAndValueConditionDiscount;
	
	/**
	 * Discount for  -$20 amount (surcharge)
	 */
	public static Discount negativeDollarDiscount;

	/**
	 * Discount for  -%30 amount (surcharge)
	 */
	public static Discount negativePercentDiscount;

	/**
	 * Fee override $200 discount (surcharge)
	 */
	public static Discount negativeFeeOverrideDiscount;

	public static CorporatePass corporatePass;
	public static CorporatePassDiscount corporatePassDiscount;
	public static Discount discountByCorporatePass;
	
	public static List<Discount> promotions;
	public static DiscountPolicy discountPolicy;

	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void init() throws Exception {

		combDiscountWithAmount = new Discount();
		combDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		combDiscountWithAmount.setCombinationType(true);
		combDiscountWithAmount.setCode("code1");
		combDiscountWithAmount.setHideOnWeb(false);
		combDiscountWithAmount.setMinEnrolments(0);
		combDiscountWithAmount.setMinValue(Money.ZERO);

		singleDiscountWithRate = new Discount();
		singleDiscountWithRate.setDiscountRate(new BigDecimal(0.2));
		singleDiscountWithRate.setMaximumDiscount(Money.ZERO);
		singleDiscountWithRate.setMinimumDiscount(Money.ZERO);
		singleDiscountWithRate.setCombinationType(false);
		singleDiscountWithRate.setHideOnWeb(false);
		singleDiscountWithRate.setMinEnrolments(0);
		singleDiscountWithRate.setMinValue(Money.ZERO);

		combDiscountWithRateMax = new Discount();
		combDiscountWithRateMax.setDiscountRate(new BigDecimal(0.2));
		combDiscountWithRateMax.setMaximumDiscount(new Money(BigDecimal.TEN));
		combDiscountWithRateMax.setCombinationType(true);
		combDiscountWithRateMax.setCode("code2");
		combDiscountWithRateMax.setHideOnWeb(false);
		combDiscountWithRateMax.setMinEnrolments(0);
		combDiscountWithRateMax.setMinValue(Money.ZERO);

		singleDiscountWithRateMin = new Discount();
		singleDiscountWithRateMin.setDiscountRate(new BigDecimal(0.1));
		singleDiscountWithRateMin.setMinimumDiscount(new Money("15"));
		singleDiscountWithRateMin.setCombinationType(false);
		singleDiscountWithRateMin.setCode("code3");
		singleDiscountWithRateMin.setHideOnWeb(false);
		singleDiscountWithRateMin.setMinEnrolments(0);
		singleDiscountWithRateMin.setMinValue(Money.ZERO);
		
		hiddenDiscountWithAmount = new Discount();
		hiddenDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		hiddenDiscountWithAmount.setCombinationType(true);
		hiddenDiscountWithAmount.setHideOnWeb(true);
		hiddenDiscountWithAmount.setMinEnrolments(0);
		hiddenDiscountWithAmount.setMinValue(Money.ZERO);
		
		nonAvailableDiscountWithAmount = new Discount();
		nonAvailableDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		nonAvailableDiscountWithAmount.setCombinationType(true);
		nonAvailableDiscountWithAmount.setCode("unavail_code");
		nonAvailableDiscountWithAmount.setHideOnWeb(false);
		nonAvailableDiscountWithAmount.setIsAvailableOnWeb(false);
		nonAvailableDiscountWithAmount.setMinEnrolments(0);
		nonAvailableDiscountWithAmount.setMinValue(Money.ZERO);

		minValueConditionDiscount = new Discount();
		minValueConditionDiscount.setDiscountAmount(new Money("50"));
		minValueConditionDiscount.setCombinationType(false);
		minValueConditionDiscount.setHideOnWeb(false);
		minValueConditionDiscount.setIsAvailableOnWeb(true);
		minValueConditionDiscount.setMinEnrolments(1);
		minValueConditionDiscount.setMinValue(new Money("200"));

		minCountAndValueConditionDiscount = new Discount();
		minCountAndValueConditionDiscount.setDiscountAmount(new Money("60"));
		minCountAndValueConditionDiscount.setCombinationType(false);
		minCountAndValueConditionDiscount.setHideOnWeb(false);
		minCountAndValueConditionDiscount.setIsAvailableOnWeb(true);
		minCountAndValueConditionDiscount.setMinEnrolments(2);
		minCountAndValueConditionDiscount.setMinValue(new Money("200"));

		negativeDollarDiscount = new Discount();
		negativeDollarDiscount.setDiscountType(DiscountType.DOLLAR);
		negativeDollarDiscount.setDiscountAmount(new Money("-20"));
		negativeDollarDiscount.setCombinationType(false);
		negativeDollarDiscount.setHideOnWeb(false);
		negativeDollarDiscount.setIsAvailableOnWeb(true);
		negativeDollarDiscount.setMinEnrolments(0);
		negativeDollarDiscount.setMinValue(Money.ZERO);

		negativePercentDiscount = new Discount();
		negativePercentDiscount.setDiscountType(DiscountType.PERCENT);
		negativePercentDiscount.setDiscountRate(new BigDecimal(-0.3));
		negativePercentDiscount.setCombinationType(false);
		negativePercentDiscount.setHideOnWeb(false);
		negativePercentDiscount.setIsAvailableOnWeb(true);
		negativePercentDiscount.setMinEnrolments(0);
		negativePercentDiscount.setMinValue(Money.ZERO);

		negativeFeeOverrideDiscount = new Discount();
		negativeFeeOverrideDiscount.setDiscountType(DiscountType.FEE_OVERRIDE);
		negativeFeeOverrideDiscount.setDiscountAmount(new Money("200"));
		negativeFeeOverrideDiscount.setCombinationType(false);
		negativeFeeOverrideDiscount.setHideOnWeb(false);
		negativeFeeOverrideDiscount.setIsAvailableOnWeb(true);
		negativeFeeOverrideDiscount.setMinEnrolments(0);
		negativeFeeOverrideDiscount.setMinValue(Money.ZERO);

		ContextUtils.setupDataSources();

		ObjectContext context = ContextUtils.createObjectContext();

		discountByCorporatePass = context.newObject(Discount.class);
		discountByCorporatePass.setDiscountType(DiscountType.PERCENT);
		discountByCorporatePass.setDiscountRate(new BigDecimal("0.99"));
		discountByCorporatePass.setHideOnWeb(false);
		discountByCorporatePass.setIsAvailableOnWeb(true);
		discountByCorporatePass.setMinEnrolments(0);
		discountByCorporatePass.setMinValue(Money.ZERO);

		corporatePass = context.newObject(CorporatePass.class);
		
		corporatePassDiscount = context.newObject(CorporatePassDiscount.class);
		
		discountByCorporatePass.addToCorporatePassDiscounts(corporatePassDiscount);
		corporatePass.addToCorporatePassDiscounts(corporatePassDiscount);
		
		promotions = new ArrayList<>();
		promotions.add(combDiscountWithAmount);
		promotions.add(singleDiscountWithRateMin);
	}
}
