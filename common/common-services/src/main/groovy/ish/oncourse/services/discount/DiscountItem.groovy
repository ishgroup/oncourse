package ish.oncourse.services.discount

import ish.math.Money
import ish.oncourse.model.Discount
import ish.oncourse.util.FormatUtils
import org.apache.commons.lang.StringUtils

import java.text.Format

public  class DiscountItem
{
	private static final String DIVIDER = " / ";
	private List<Discount> discounts;
	private Money feeIncTax;


	private Format feeFormat;
	private String title;

	def DiscountItem init()
	{
		List<String> strings = new ArrayList<>();
		for (Discount  discount : discounts) {
			strings.add(discount.getName());
		}
		title = StringUtils.join(strings, DIVIDER);
		feeFormat = FormatUtils.chooseMoneyFormat(feeIncTax);
		return this
	}

	def String getTitle()
	{
		return title;
	}

	def Money getFeeIncTax() {
		return feeIncTax;
	}


	def Format getFeeFormat()
	{
		return feeFormat;
	}

}