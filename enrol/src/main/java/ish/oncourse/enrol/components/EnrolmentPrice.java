package ish.oncourse.enrol.components;

import ish.math.Country;
import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Preference;
import ish.oncourse.services.preference.IPreferenceService;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentPrice {

	@Inject
	private IPreferenceService preferenceService;

	@Parameter
	private Enrolment enrolment;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private int index;

	@Property
	private Format moneyFormat;

	@Property
	private Format numberFormat;

	@SetupRender
	void beforeRender() {
		Preference currencyPref = preferenceService.getPreferenceByKey("default.currency");
		Locale locale = null;
		if (currencyPref != null) {
			locale = Country.forCurrencySymbol(currencyPref.getValueString()).locale();
		} else {
			locale = Country.AUSTRALIA.locale();
		}
		moneyFormat = NumberFormat.getCurrencyInstance(locale);
		numberFormat = new DecimalFormat("0.00");
	}

	public String getFeeClass() {
		return "fee-" + (hasDiscountValue() ? "disabled" : "full");
	}

	public String getFeeDiscountedClass() {
		return "fee-discounted" + (!hasDiscountValue() ? " collapse" : "");
	}

	private boolean hasDiscountValue() {
		if (enrolment.getDiscount() == null) {
			return false;
		}
		return Money.ZERO.toBigDecimal().compareTo(enrolment.getDiscount().getDiscountAmount()) != 0;
	}

}
