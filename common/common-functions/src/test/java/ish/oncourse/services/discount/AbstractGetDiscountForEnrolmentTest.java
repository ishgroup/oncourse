package ish.oncourse.services.discount;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CorporatePassDiscount;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains fields and tests common for {@link ish.oncourse.services.discount.GetDiscountForEnrolment}
 * @author ksenia
 *
 */
public abstract class AbstractGetDiscountForEnrolmentTest {
	protected static final Money FEE_EX_GST = new Money("100");

	protected static final Money FEE_GST = new Money("10");
	/**
	 * Combinable discount with amount=10.
	 */
	public static DiscountCourseClass combDiscountWithAmount;
	/**
	 * Not combinable discount with the rate 20% and zero restrictions.
	 */
	public static DiscountCourseClass singleDiscountWithRate;
	/**
	 * Combinable discount with the rate 20% and maximum 10.
	 */
	public static DiscountCourseClass combDiscountWithRateMax;
	/**
	 * Not combinable discount with the rate 10% and minimum 15.
	 */
	public static DiscountCourseClass singleDiscountWithRateMin;
	/**
	 * Combinable discount for $10 amount which has hideOnWeb property set to true
	 */
	public static DiscountCourseClass hiddenDiscountWithAmount;
	/**
	 * Combinable discount for $10 amount which has isAvailableOnWeb property set to false
	 */
	public static DiscountCourseClass nonAvailableDiscountWithAmount;
	/**
	 * Not combinable discount for $50 amount which has minEnrolment = 1 and minValue = $200
	 */
	public static DiscountCourseClass minValueConditionDiscount;
	/**
	 * Not combinable discount for $60 amount which has minEnrolment = 2 and minValue = $200
	 */
	public static DiscountCourseClass minCountAndValueConditionDiscount;
	
	/**
	 * Discount for  -$20 amount (surcharge)
	 */
	public static DiscountCourseClass negativeDollarDiscount;

	/**
	 * Discount for  -%30 amount (surcharge)
	 */
	public static DiscountCourseClass negativePercentDiscount;

	/**
	 * Fee override $200 discount (surcharge)
	 */
	public static DiscountCourseClass negativeFeeOverrideDiscount;

	public static CorporatePass corporatePass;
	public static CorporatePassDiscount corporatePassDiscount;
	public static DiscountCourseClass discountByCorporatePass;
	
	public static List<Discount> promotions;

	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void init() throws Exception {
		ContextUtils.setupDataSources();
		ObjectContext context = ContextUtils.createObjectContext();

		Discount discount = context.newObject(Discount.class);
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(true);
		discount.setCode("code1");
		discount.setHideOnWeb(false);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		combDiscountWithAmount = context.newObject(DiscountCourseClass.class);
		combDiscountWithAmount.setDiscount(discount);
		discount.addToDiscountCourseClasses(combDiscountWithAmount);

		discount = new Discount();
		discount.setDiscountRate(new BigDecimal(0.2));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMaximumDiscount(Money.ZERO);
		discount.setMinimumDiscount(Money.ZERO);
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		singleDiscountWithRate = context.newObject(DiscountCourseClass.class);
		singleDiscountWithRate.setDiscount(discount);
		discount.addToDiscountCourseClasses(singleDiscountWithRate);

		discount = new Discount();
		discount.setDiscountRate(new BigDecimal(0.2));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMaximumDiscount(new Money(BigDecimal.TEN));
		discount.setCombinationType(true);
		discount.setCode("code2");
		discount.setHideOnWeb(false);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		combDiscountWithRateMax = context.newObject(DiscountCourseClass.class);
		combDiscountWithRateMax.setDiscount(discount);
		discount.addToDiscountCourseClasses(combDiscountWithRateMax);

		discount = new Discount();
		discount.setDiscountRate(new BigDecimal(0.1));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMinimumDiscount(new Money("15"));
		discount.setCombinationType(false);
		discount.setCode("code3");
		discount.setHideOnWeb(false);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		singleDiscountWithRateMin = context.newObject(DiscountCourseClass.class);
		singleDiscountWithRateMin.setDiscount(discount);
		discount.addToDiscountCourseClasses(singleDiscountWithRateMin);
		
		discount = new Discount();
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(true);
		discount.setHideOnWeb(true);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		hiddenDiscountWithAmount = context.newObject(DiscountCourseClass.class);
		hiddenDiscountWithAmount.setDiscount(discount);
		discount.addToDiscountCourseClasses(hiddenDiscountWithAmount);


		discount = new Discount();
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(true);
		discount.setCode("unavail_code");
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(false);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		nonAvailableDiscountWithAmount = context.newObject(DiscountCourseClass.class);
		nonAvailableDiscountWithAmount.setDiscount(discount);
		discount.addToDiscountCourseClasses(nonAvailableDiscountWithAmount);

		discount = new Discount();
		discount.setDiscountAmount(new Money("50"));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(1);
		discount.setMinValue(new Money("200"));
		minValueConditionDiscount = context.newObject(DiscountCourseClass.class);
		minValueConditionDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(minValueConditionDiscount);

		discount = new Discount();
		discount.setDiscountAmount(new Money("60"));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(2);
		discount.setMinValue(new Money("200"));
		minCountAndValueConditionDiscount = context.newObject(DiscountCourseClass.class);
		minCountAndValueConditionDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(minCountAndValueConditionDiscount);

		discount = new Discount();
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setDiscountAmount(new Money("-20"));
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		negativeDollarDiscount = context.newObject(DiscountCourseClass.class);
		negativeDollarDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(negativeDollarDiscount);

		discount = new Discount();
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal(-0.3));
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		negativePercentDiscount = context.newObject(DiscountCourseClass.class);
		negativePercentDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(negativePercentDiscount);

		discount = new Discount();
		discount.setDiscountType(DiscountType.FEE_OVERRIDE);
		discount.setDiscountAmount(new Money("200"));
		discount.setCombinationType(false);
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		negativeFeeOverrideDiscount = context.newObject(DiscountCourseClass.class);
		negativeFeeOverrideDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(negativeFeeOverrideDiscount);

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("0.99"));
		discount.setHideOnWeb(false);
		discount.setIsAvailableOnWeb(true);
		discount.setMinEnrolments(0);
		discount.setMinValue(Money.ZERO);
		discountByCorporatePass = context.newObject(DiscountCourseClass.class);
		discountByCorporatePass.setDiscount(discount);
		discount.addToDiscountCourseClasses(discountByCorporatePass);

		corporatePass = context.newObject(CorporatePass.class);
		
		corporatePassDiscount = context.newObject(CorporatePassDiscount.class);

		discount.addToCorporatePassDiscounts(corporatePassDiscount);
		corporatePass.addToCorporatePassDiscounts(corporatePassDiscount);
		
		promotions = new ArrayList<>();
		promotions.add(combDiscountWithAmount.getDiscount());
		promotions.add(singleDiscountWithRateMin.getDiscount());
	}
}
